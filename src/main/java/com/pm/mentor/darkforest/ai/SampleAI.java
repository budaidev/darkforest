package com.pm.mentor.darkforest.ai;

import java.util.stream.Collectors;

import com.loxon.javachallenge.challenge.game.event.GameEvent;
import com.pm.mentor.darkforest.ai.model.AIPlanet;
import com.pm.mentor.darkforest.ai.model.GameState;

import lombok.Getter;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SampleAI implements AI {

	private GameState gameState;

	private int wormholeNumber = 0;
	private int startingPlanetId = 0;
	
	@Getter
	private boolean running = false;
	
	@Override
	public void init(GameState gameState) {
		log.info("AI initialized");
		this.gameState = gameState;
		
		running = true;

		wormholeNumber = 0;
		startingPlanetId = gameState.getPlayerPlanets().get(0).getId();

		log.info("My starting planet id is {}", startingPlanetId);
	}

	@Override
	public void receiveEvent(GameEvent event) {
		log.trace(String.format("AI received a game event: %s", event.getEventType()));
		
		doStuff();
	}

	@Override
	public void heartBeat() {
		if (!running) {
			return;
		}
		
		log.trace("AI received a heartbeat");
		
		doStuff();
	}
	
	@Override
	public void stop() {
		running = false;
	}
	
	private void doStuff() {
		if (!running) {
			gameState.clearActions();
			
			return;
		}

		log.trace("dostuff");

		if (!gameState.hasFreeAction()) {
			log.trace(String.format("No free actions. Initiated: %d, Active: %d, Calculated: %d, Max: %d",
					gameState.getInitiatedActions().size(), gameState.getActiveActions().size(), gameState.activeActionCount(), gameState.getMaxConcurrentActionCount()));
			
			return;
		}

		if(gameState.getPlayerPlanets().size() > 1) {
			AIPlanet planet = gameState.getPlayerPlanets().stream()
				.filter(p -> p.getId() != startingPlanetId)
					.findFirst().get();
			buildWormhole(planet);
		}

		
		sendMissionsToNearbyPlanets();
	}

	private void buildWormhole(AIPlanet startPlanet) {
		if(wormholeNumber < 1) {
			log.trace("buildWormhole from " + startPlanet.getId() + " to center");

			int x_center = gameState.getSettings().getWidth() / 2;
			int y_center = gameState.getSettings().getHeight() / 2;

			gameState.buildWormHole(startPlanet.getPos().getX(), startPlanet.getPos().getY(), x_center, y_center);

			wormholeNumber++;
		}

	}
	
	private void sendMissionsToNearbyPlanets() {
		log.trace("sendMissionsToNearbyPlanets");
		
		val playerPlanets = gameState.getPlayerPlanets();
		log.trace(String.format("number of player planets: %d", playerPlanets.size()));
		val closestUnknownPlanets = gameState.getUnknownPlanets((AIPlanet lhs, AIPlanet rhs) -> {
			// calculate distance to closest player planet
			val leftDistance = playerPlanets.stream()
				.mapToInt(p -> (int)p.getPos().distance(lhs.getPos()))
				.min();
			
			val rightDistance = playerPlanets.stream()
				.mapToInt(p -> (int)p.getPos().distance(rhs.getPos()))
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
					.sorted((AIPlanet lhs, AIPlanet rhs) -> {
						val leftDistance = (int)target.getPos().distance(lhs.getPos());
						val rightDistance = (int)target.getPos().distance(rhs.getPos());
						
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
