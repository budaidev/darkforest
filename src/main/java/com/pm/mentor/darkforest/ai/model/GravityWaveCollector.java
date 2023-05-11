package com.pm.mentor.darkforest.ai.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.loxon.javachallenge.challenge.game.event.actioneffect.GravityWaveCrossing;
import com.loxon.javachallenge.challenge.game.model.GravityWaveCause;
import com.loxon.javachallenge.challenge.game.model.Planet;
import com.loxon.javachallenge.challenge.game.model.Player;

import lombok.val;

public class GravityWaveCollector {
	// events older than this period are not considered during source calculation
	private final double maxEventLifetime;
	private final double lightSpeed;
	private final Map<Integer, PlayerActionEffectCollector> playerEffectCollectors = new HashMap<>();
	private final List<Planet> planets;
	
	public GravityWaveCollector(List<Player> availablePlayers, List<Planet> planets, int mapWidth, int mapHeight, double lightSpeed) {
		for (val player : availablePlayers) {
			playerEffectCollectors.put(player.getId(), new PlayerActionEffectCollector(player));
		}
		
		this.planets = planets;
		
		maxEventLifetime = Math.sqrt(mapWidth*mapWidth + mapHeight*mapHeight) / lightSpeed;
		this.lightSpeed = lightSpeed;
	}
	
	public void collect(GravityWaveCrossing effect) {
		playerEffectCollectors.get(effect.getInflictingPlayer())
			.collect(effect);
	}
	
	private double distanceBetween(Planet a, Planet b) {
		return a.distance(b);
	}
	
	private Planet findPlanet(int id) {
		return planets.stream()
			.filter(p -> p.getId() == id)
			.findAny()
			.get();
	}
	
	private class PlayerActionEffectCollector {
		private final Player owner;
		private final Map<GravityWaveCause, EffectCollector> effectCollectors = new HashMap<>();
		
		public PlayerActionEffectCollector(Player owner) {
			this.owner = owner;
		}
		
		public void collect(GravityWaveCrossing effect) {
			if (!effectCollectors.containsKey(effect.getCause())) {
				effectCollectors.put(effect.getCause(), new EffectCollector());
			}
			
			effectCollectors.get(effect.getCause())
				.collect(effect);
		}
	}
	
	private class EffectCollector {
		private final List<GravityWaveCrossing> observedEffects = new ArrayList<>();
		
		public void collect(GravityWaveCrossing currentEffect) {
			val currentTime = currentEffect.getTime();
			
			iterateEffectsBackwards(pastEffect -> {
				val timeBetweenEffects = currentTime - pastEffect.getTime(); 
				// no need to continue, the other effects are too old to be correlated with the current one
				if (timeBetweenEffects <= maxEventLifetime) {
					// continue -> false
					return false;
				}
				
				val planetA = findPlanet(pastEffect.getAffectedMapObjectId());
				val planetB = findPlanet(currentEffect.getAffectedMapObjectId());
				
				// the planets are the same so these effects must come from different sources
				if (planetA.getId() == planetB.getId()) {
					// continue -> true
					return true;
				}
				
				val distanceBetweenPlanets = distanceBetween(planetA, planetB);
				
				// TODO calculate source here
				
				
				return true;
			});
			
			observedEffects.add(currentEffect);
		}
		
		
		private void iterateEffectsBackwards(Function<GravityWaveCrossing, Boolean> action) {
			val backwardsIterator = observedEffects.listIterator(observedEffects.size());
			boolean shallContinue = true;
			
			while (shallContinue && backwardsIterator.hasPrevious()) {
				shallContinue = action.apply(backwardsIterator.previous());
			}
		}
	}
}
