package com.pm.mentor.darkforest.ai;

import java.util.stream.Collectors;

import com.loxon.javachallenge.challenge.game.event.GameEvent;
import com.loxon.javachallenge.challenge.game.model.Planet;
import com.pm.mentor.darkforest.ai.model.GameState;

import lombok.Getter;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SampleAI implements AI {

	private GameState gameState;
	
	@Getter
	private boolean running = false;
	
	@Override
	public void init(GameState gameState) {
		log.info("AI initialized");
		this.gameState = gameState;
		
		running = true;
	}

	@Override
	public void receiveEvent(GameEvent event) {
		log.trace(String.format("AI received a game event: %s", event.getEventType()));
		
		doStuff();
	}

	@Override
	public void heartBeat() {
		log.trace("AI received a heartbeat");
		if (!running) {
			return;
		}
		
		doStuff();
	}
	
	private void doStuff() {
		log.trace("dostuff");

		if (!gameState.hasFreeAction()) {
			log.trace(String.format("No free actions. Initiated: %d, Active: %d, Calculated: %d, Max: %d",
					gameState.getInitiatedActions().size(), gameState.getActiveActions().size(), gameState.activeActionCount(), gameState.getMaxConcurrentActionCount()));
			
			return;
		}
		
		sendMissionsToNearbyPlanets();
	}
	
	private void sendMissionsToNearbyPlanets() {
		log.trace("sendMissionsToNearbyPlanets");
		
		val playerPlanets = gameState.getPlayerPlanets();
		log.trace(String.format("number of player planets: %d", playerPlanets.size()));
		val closestUnknownPlanets = gameState.getUnknownPlanets((Planet lhs, Planet rhs) -> {
			// calculate distance to closest player planet
			val leftDistance = playerPlanets.stream()
				.mapToInt(p -> (int)p.distance(lhs))
				.min();
			
			val rightDistance = playerPlanets.stream()
				.mapToInt(p -> (int)p.distance(rhs))
				.min();
			
			if (leftDistance.isPresent() && rightDistance.isPresent()) {
				return leftDistance.getAsInt() - rightDistance.getAsInt();
			}
			
			return 0;
		});
		
		log.trace(String.format("Number of unknown planets: %d", closestUnknownPlanets.size()));
		
		if (closestUnknownPlanets.size() > 0) {
			val targetPlanets = closestUnknownPlanets.stream()
				.filter(planet -> {
					val initiatedActionExists = gameState.getInitiatedActions().values()
						.stream()
						.anyMatch(action -> action.getTargetId() == planet.getId());
					
					val activeActionExists = gameState.getActiveActions().values()
						.stream()
						.anyMatch(actionResponse -> actionResponse.getAction().getTargetId() == planet.getId());
					
					return !(initiatedActionExists || activeActionExists);
				})
				.limit(gameState.availableActionCount())
				.collect(Collectors.toList());
			
			log.trace(String.format("Selected %d planets as mission targets", targetPlanets.size()));
			
			for (val target : targetPlanets) {
				val closestPlayerPlanet = playerPlanets.stream()
					.sorted((Planet lhs, Planet rhs) -> {
						val leftDistance = (int)target.distance(lhs);
						val rightDistance = (int)target.distance(rhs);
						
						return leftDistance - rightDistance;
					})
					.findFirst();
				
				if (closestPlayerPlanet.isPresent()) {
					val playerPlanet = closestPlayerPlanet.get();
					
					log.trace(String.format("Sending mission from %d to %d", playerPlanet.getId(), target.getId()));
					gameState.spaceMission(playerPlanet, target);
				}
			}
		}
	}
}
