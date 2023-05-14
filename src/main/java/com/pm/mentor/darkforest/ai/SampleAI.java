package com.pm.mentor.darkforest.ai;

import java.util.stream.Collectors;

import com.loxon.javachallenge.challenge.game.event.GameEvent;
import com.pm.mentor.darkforest.ai.model.AIPlanet;
import com.pm.mentor.darkforest.ai.model.ClosestToGivenPlanetComparator;
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
		log.trace(String.format("Actions: Initiated: %d, Active: %d, Calculated: %d, Max: %d",
				gameState.getInitiatedActions().size(), gameState.getActiveActions().size(), gameState.activeActionCount(), gameState.getMaxConcurrentActionCount()));

		if (!gameState.hasFreeAction()) {
			log.trace("No free actions");
			
			return;
		}

		if(gameState.getPlayerPlanets().size() > 1) {
			AIPlanet planet = gameState.getPlayerPlanets().stream()
				.filter(p -> p.getId() != startingPlanetId)
					.findFirst().get();
			buildWormhole(planet);
		}

		
		sendMissionsToNearbyPlanets();
		missileShower();
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
		val closestUnknownPlanets = gameState.getColonizablePlanets(gameState.createClosestToPlayerPlanetComparator());
		
		log.trace(String.format("Number of unknown planets: %d", closestUnknownPlanets.size()));
		
		if (closestUnknownPlanets.size() > 0) {
			val targetPlanets = closestUnknownPlanets.stream()
				.filter(gameState.createTargetedPlanetFilter())
				.limit(gameState.availableActionCount())
				.collect(Collectors.toList());
			
			log.trace(String.format("Selected %d planets as mission targets", targetPlanets.size()));
			
			for (val target : targetPlanets) {
				val closestPlayerPlanet = playerPlanets.stream()
					.sorted(new ClosestToGivenPlanetComparator(target))
					.findFirst();
				
				if (closestPlayerPlanet.isPresent()) {
					val playerPlanet = closestPlayerPlanet.get();
					
					log.trace(String.format("Sending mission from %d to %d", playerPlanet.getId(), target.getId()));
					gameState.spaceMission(playerPlanet, target);
				}
			}
		}
	}
	
	private void missileShower() {
		if (!gameState.hasFreeAction() ) {
			return;
		}
		
		log.trace("missileShower");
		
		val nonDestroyedPlanets = gameState.getDestroyablePlanets()
				.stream()
				.sorted(gameState.createClosestToPlayerPlanetComparator())
				.collect(Collectors.toList());
		
		if (nonDestroyedPlanets.size() > 0) {
			val targetPlanets = nonDestroyedPlanets.stream()
					.filter(gameState.createTargetedPlanetFilter())
					.limit(gameState.availableActionCount())
					.collect(Collectors.toList());
				
				log.trace(String.format("Selected %d planets as missile shower targets", targetPlanets.size()));

				val playerPlanets = gameState.getPlayerPlanets();
				
				for (val target : targetPlanets) {
					val closestPlayerPlanet = playerPlanets.stream()
						.sorted(new ClosestToGivenPlanetComparator(target))
						.findFirst();
					
					if (closestPlayerPlanet.isPresent()) {
						val playerPlanet = closestPlayerPlanet.get();
						
						log.trace(String.format("Sending mission from %d to %d", playerPlanet.getId(), target.getId()));
						gameState.shootMBH(playerPlanet, target);
					}
				}
		}
	}
}
