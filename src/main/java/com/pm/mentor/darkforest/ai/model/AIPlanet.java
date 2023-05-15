package com.pm.mentor.darkforest.ai.model;

import com.loxon.javachallenge.challenge.game.event.actioneffect.ActionEffect;
import com.loxon.javachallenge.challenge.game.event.actioneffect.ActionEffectType;
import com.loxon.javachallenge.challenge.game.model.Planet;
import com.pm.mentor.darkforest.util.Point;
import com.pm.mentor.darkforest.util.PointToPointDistanceCache;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@ToString
@Slf4j
public class AIPlanet {

    @Getter
    private final int id;
    @Getter
    private final Point pos;

    @Getter
    private int owner;
    @Getter
    private boolean destroyed;

    private boolean visitedBySpaceMission;

    @Getter
    private boolean alreadyShot;

    @Getter
    @Setter
    private boolean virtualPlanet = false; // used for wormhole comparison
    @Getter
    @Setter
    private double distanceToClosest = 0; // used for wormhole comparison

    @Getter
    private final List<ActionEffect> effectsEmitted;

    @Getter
    private long shieldedUntil = 0;

    private boolean hasShield = false;

    public AIPlanet(Planet p) {
        id = p.getId();
        pos = new Point(p.getX(), p.getY());
        owner = p.getPlayer();
        destroyed = p.isDestroyed();
        visitedBySpaceMission = owner != 0;
        alreadyShot = false;
        effectsEmitted = new ArrayList<>();
    }

    private AIPlanet(Point p) {
        id = -1;
        this.pos = p;
        destroyed = false;
        virtualPlanet = true;
        effectsEmitted = new ArrayList<>();
    }

    public static AIPlanet createVirtualPlanetFromWormHole(Point p) {
        return new AIPlanet(p);
    }

    public boolean isSpaceMissionPossible() {
        return !destroyed && !visitedBySpaceMission;
    }

    public void playerSettled(int playerId) {
        owner = playerId;
        visitedBySpaceMission = true;
    }

    public void destoryed() {
        this.destroyed = true;
    }

    public void spaceMissionFailed() {
        visitedBySpaceMission = true;
    }

    public void shoot() {
        alreadyShot = true;
    }

    public void shield(long time, long duration) {
        shieldedUntil = time + duration;
    }

    public void blameEffect(ActionEffect effect, GameState state) {
        if (effect.getEffectChain().stream()
                .anyMatch(x -> x.equals(ActionEffectType.SPACE_MISSION_GRAWITY_WAVE_PASSING))) {
            visitedBySpaceMission = true;
            owner = effect.getInflictingPlayer();
            if (destroyed) {
                log.warn("!!!Space mission on destroyed planet: {}!!!", this);
            }
        }

        effectsEmitted.add(effect);

        if (!destroyed) {
            List<Integer> effectsTime = effectsEmitted.stream()
            		.filter(x -> x.getEffectChain().stream()
                            .anyMatch(y -> y.equals(ActionEffectType.MBH_HIT_GRAWITY_WAVE_PASSING)))
                    .map(action -> (int)(action.getTime() - getActionTime(state, action))) // cast to int so distinct can be applied despite imprecision in floating point calculations
                    .distinct() // filter out different observations from the same event
                    .sorted((x1, x2) -> x2.compareTo(x1)) // sort descending as there is no guarantee that effects are observed in the order they happened 
                    .toList();

            if (effectsTime.size() >= 2) {
                alreadyShot = true;

                log.trace("Effects time: {}", effectsTime);

                for (int i = 1; i < effectsTime.size(); i++) {
                    long diff = effectsTime.get(i) - effectsTime.get(i - 1);
                    if (diff < state.getSettings().getShildDuration() + state.getSettings().getTimeToBuildShild()) {
                        destroyed = true;
                        log.info("Planet {} has got double hit!", id);
                        break;
                    }
                }
            }
        }
    }

    private long getActionTime(GameState state, ActionEffect action) {
    	val playerPlanet = state.tryFindPlayerPlanet(action.getAffectedMapObjectId()).get();
    	val distanceToAffected = PointToPointDistanceCache.distance(playerPlanet.pos, this.pos);
    	
        return (long) (distanceToAffected * state.getSettings().getTimeOfOneLightYear());
    }

    public void disableShield() {
        hasShield = false;
    }

    public boolean hasShield() {
        return hasShield;
    }
}
