package com.pm.mentor.darkforest.ai.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.loxon.javachallenge.challenge.game.event.actioneffect.ActionEffect;
import com.loxon.javachallenge.challenge.game.event.actioneffect.ActionEffectType;
import com.loxon.javachallenge.challenge.game.event.actioneffect.GravityWaveCrossing;
import com.loxon.javachallenge.challenge.game.model.GravityWaveCause;
import com.loxon.javachallenge.challenge.game.model.Player;
import com.loxon.javachallenge.challenge.game.settings.GameSettings;
import com.pm.mentor.darkforest.util.Angle;
import com.pm.mentor.darkforest.util.MathUtil;
import com.pm.mentor.darkforest.util.Point;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GravityWaveCollector {
    // events older than this period are not considered during source calculation
    private final double maxEventLifetime;
    private final double lightSpeed;
    private final Map<Integer, PlayerActionEffectCollector> playerEffectCollectors = new HashMap<>();
    private final List<AIPlanet> planets;
    private final int playerId;
    private final GameSettings settings;

    public GravityWaveCollector(List<Player> availablePlayers, List<AIPlanet> planets, int playerId, GameSettings settings) {
    	val mapHeight = settings.getHeight();
    	val mapWidth = settings.getWidth();

        for (val player : availablePlayers) {
            playerEffectCollectors.put(player.getId(), new PlayerActionEffectCollector());
        }

        this.planets = planets;
        this.playerId = playerId;

        lightSpeed = settings.getTimeOfOneLightYear();
        maxEventLifetime = Math.sqrt(mapWidth * mapWidth + mapHeight * mapHeight) * lightSpeed;

        this.settings = settings;
    }

    public CollectResult collect(GravityWaveCrossing effect) {
        return playerEffectCollectors.get(effect.getInflictingPlayer())
                .collect(effect);
    }

    private double distanceBetween(AIPlanet a, AIPlanet b) {
        return a.getPos().distance(b.getPos());
    }

    private AIPlanet findPlanet(int id) {
    	try {
	        return planets.stream()
	                .filter(p -> p.getId() == id)
	                .findAny()
	                .get();
    	} catch (NoSuchElementException e) {
    		log.error(String.format("Unable to find planet: %d", id));
    		
    		throw e;
    	}
    }

    public List<AIPlanet> filterPossiblePlanets(List<AIPlanet> planets,
                                              GravityWaveCrossing pastEffect,
                                              GravityWaveCrossing currentEffect,
                                              int precision) {
        AIPlanet planetA = findPlanet(pastEffect.getAffectedMapObjectId());
        AIPlanet planetB = findPlanet(currentEffect.getAffectedMapObjectId());

        double error = precision / 100.0 * 2 * Math.PI;

        double past_dir_center = pastEffect.getDirection();
        double past_dir_up = pastEffect.getDirection() + error;
        double past_dir_down = pastEffect.getDirection() - error;

        double current_dir_center = currentEffect.getDirection();
        double current_dir_up = currentEffect.getDirection() + error;
        double current_dir_down = currentEffect.getDirection() - error;

        Point pA = planetA.getPos();
        Point pB = planetB.getPos();

        return planets.stream().filter(
                        x -> MathUtil.isInsideTheTriangle(
                                pA,
                                pA.move(past_dir_up, settings.getWidth()),
                                pA.move(past_dir_down, settings.getWidth()),
                                x.getPos()))
                .filter(
                        x -> MathUtil.isInsideTheTriangle(
                                pB,
                                pB.move(current_dir_up, settings.getWidth()),
                                pB.move(current_dir_down, settings.getWidth()),
                                x.getPos()))
                .collect(Collectors.toList());
    }

    private class PlayerActionEffectCollector {
        private final Map<GravityWaveCause, EffectCollector> effectCollectors = new HashMap<>();

        public CollectResult collect(GravityWaveCrossing effect) {
            if (!effectCollectors.containsKey(effect.getCause())) {
                effectCollectors.put(effect.getCause(), new EffectCollector(effect.getCause()));
            }

            return effectCollectors.get(effect.getCause())
                    .collect(effect);
        }
    }

    private class EffectCollector {
        private final List<GravityWaveCrossing> observedEffects = new ArrayList<>();

        private final double error;

        public EffectCollector(GravityWaveCause cause) {
            error = cause == GravityWaveCause.PASSIVITY
                    ? 360 * settings.getPassivityFleshPrecision() / 100
                    : 360 * settings.getGravityWaveSourceLocationPrecision() / 100;
        }

        public CollectResult collect(GravityWaveCrossing currentEffect) {
            val currentTime = currentEffect.getTime();

            CollectResult result = iterateEffectsBackwards(pastEffect -> {
                val timeBetweenEffects = currentTime - pastEffect.getTime();
                // no need to continue, the other effects are too old to be correlated with the current one
                if (maxEventLifetime <= timeBetweenEffects) {
                    // continue -> false
                    return new CollectResult(true, null);
                }

                val planetA = findPlanet(pastEffect.getAffectedMapObjectId());
                val planetB = findPlanet(currentEffect.getAffectedMapObjectId());

                // the planets are the same so these effects must come from different sources
                if (planetA.getId() == planetB.getId()) {
                    return new CollectResult(false, null);
                }

                for (val potentialSource : planets) {
                    if (shallSkipPlanet(potentialSource)) {
                        continue;
                    }

                    // skip the planets observing the events ()
                    if (potentialSource.getId() == planetA.getId() || potentialSource.getId() == planetB.getId()) {
                        continue;
                    }

                    val distanceToA = distanceBetween(potentialSource, planetA);
                    val distanceToB = distanceBetween(potentialSource, planetB);

                    val distanceDifference = Math.abs(distanceToB - distanceToA);
                    val expectedObservationTimeDifference = distanceDifference * getEffectSpeed(currentEffect);

                    if (isCloseEnough(expectedObservationTimeDifference, timeBetweenEffects)) {
                        log.trace(String.format("considering potential source planet: %s", potentialSource.toString()));

                        val sourcePoint = potentialSource.getPos();
                        val pointA = planetA.getPos();
                        val pointB = planetB.getPos();

                        val expectedAngleA = pointA.minus(sourcePoint).angleToNorth().deg;
                        val actualAngleA = new Angle(pastEffect.getDirection()).deg;

                        if (Math.abs(expectedAngleA - actualAngleA) > error) {
                            log.trace(String.format("expected angle A: %f, actual: %f", expectedAngleA, actualAngleA));
                            continue;
                        }

                        val expectedAngleB = pointB.minus(sourcePoint).angleToNorth().deg;
                        val actualAngleB = new Angle(currentEffect.getDirection()).deg;

                        if (Math.abs(expectedAngleB - actualAngleB) > error) {
                            log.trace(String.format("expected angle B: %f, actual: %f", expectedAngleB, actualAngleB));
                            continue;
                        }

                        log.trace(String.format("This effect is originated from planet %d", potentialSource.getId()));
                        potentialSource.blameEffect(currentEffect);
                        return new CollectResult(true, potentialSource);
                    }
                }

                return new CollectResult(false, null);
            });

            observedEffects.add(currentEffect);

            return result;
        }


        private CollectResult iterateEffectsBackwards(Function<GravityWaveCrossing, CollectResult> action) {
            val backwardsIterator = observedEffects.listIterator(observedEffects.size());

            while (backwardsIterator.hasPrevious()) {
                CollectResult res = action.apply(backwardsIterator.previous());
                if (res.isSuccessful()) {
                    if (res.getPossibleSource() != null) {
                        return res;
                    } else {
                        return unsuccessfulResult();
                    }

                }
            }
            return unsuccessfulResult();
        }
    }

    private CollectResult unsuccessfulResult() {
        return new CollectResult(false, null);
    }

    private boolean shallSkipPlanet(AIPlanet planet) {
        if (planet.isDestroyed()) {
        	return true;
        }

        if (planet.getOwner() == playerId) {
            return true;
        }

        return false;
    }

    private boolean isCloseEnough(double a, double b) {
        return Math.abs(a - b) < 1;
    }

    private double getEffectSpeed(ActionEffect effect) {
    	if (effect.getEffectChain().contains(ActionEffectType.SPACE_MISSION_GRAWITY_WAVE_PASSING)) {
    		return lightSpeed * 2;
    	}
    	
    	return lightSpeed;
    }
}
