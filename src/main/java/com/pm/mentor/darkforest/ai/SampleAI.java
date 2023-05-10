package com.pm.mentor.darkforest.ai;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.loxon.javachallenge.challenge.game.event.GameEvent;
import com.loxon.javachallenge.challenge.game.event.action.ActionResponse;
import com.loxon.javachallenge.challenge.game.event.action.ActionResult;
import com.loxon.javachallenge.challenge.game.event.action.EntryPointIndex;
import com.loxon.javachallenge.challenge.game.event.action.GameAction;
import com.loxon.javachallenge.challenge.game.event.action.GameActionType;
import com.loxon.javachallenge.challenge.game.event.actioneffect.ActionEffect;
import com.loxon.javachallenge.challenge.game.event.actioneffect.ActionEffectType;
import com.loxon.javachallenge.challenge.game.model.Game;
import com.loxon.javachallenge.challenge.game.model.Planet;
import com.pm.mentor.darkforest.ai.model.GameState;

import lombok.Getter;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SampleAI implements AI {
	
	private final GameActionApi actionApi;
	private GameState gameState;
	private Map<Integer, GameAction> initiatedActions = new HashMap<>();
	private Map<Integer, ActionResponse> activeActions = new HashMap<>();
	
	private AtomicInteger actionCounter = new AtomicInteger(0);
	
	@Getter
	private boolean running = false;
	
	public SampleAI(GameActionApi gameActionApi) {
		actionApi = gameActionApi;
	}
	
	@Override
	public void init(Game game, int playerId) {
		log.info("AI initialized");
		gameState = new GameState(game, playerId);
		
		running = true;
	}

	@Override
	public void receiveEvent(GameEvent event) {
		log.trace(String.format("AI received a game event: %s", event.getEventType()));

		switch (event.getEventType()) {
		case ACTION:
			val actionResponse = event.getAction();
			val action = actionResponse.getAction();
			
			log.info(String.format("ActionResponse received: %s", actionResponse));
			
			// action response for an action we did not send?
			if (!initiatedActions.containsKey(action.getRefId())) {
				log.warn(String.format("Response for non-existent action received: %s", actionResponse.toString()));
				
				return;
			}
			
			// remove the action from the initiated actions list
			initiatedActions.remove(action.getRefId());
			
			// add to active actions list
			if (actionResponse.getResult() == ActionResult.SUCCESS) {
				activeActions.put(action.getRefId(), actionResponse);
			}  else {
				log.warn(String.format("Cannot execute action: %s", actionResponse.toString()));
			}
			
			break;
			
		case CONNECTION_RESULT:
		case GAME_STARTED:
			// ignore
			break;

		case ACTION_EFFECT:
			val actionEffect = event.getActionEffect();

			log.info(String.format("ActionEffect received: %s", actionEffect.toString()));
			
			if (isEffectPlayerRelated(actionEffect) ) {
				val originalAction = tryFindOriginalPlayerAction(actionEffect);
				
				originalAction.ifPresent(origAction -> handlePlayerActionFallout(origAction, actionEffect));
			}
			
			break;
		case ATTRIBUTE_CHANGE:
			log.info(String.format("AttributeChange received: %s", event.getChanges().toString()));
			
			handleAttributeChange(event);
			
			break;
		case GAME_ENDED:
			running = false;
			break;
		}
		
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

		if (!hasFreeAction()) {
			log.trace(String.format("No free actions. Initiated: %d, Active: %d, Calculated: %d, Max: %d",
					initiatedActions.size(), activeActions.size(), activeActionCount(), gameState.getMaxConcurrentActionCount()));
			
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
					val initiatedActionExists = initiatedActions.values()
						.stream()
						.anyMatch(action -> action.getTargetId() == planet.getId());
					
					val activeActionExists = activeActions.values()
						.stream()
						.anyMatch(actionResponse -> actionResponse.getAction().getTargetId() == planet.getId());
					
					return !(initiatedActionExists || activeActionExists);
				})
				.limit(availableActionCount())
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
					spaceMission(playerPlanet, target);
				}
			}
		}
	}
	
	private boolean isEffectPlayerRelated(ActionEffect effect) {
		return effect.getInflictingPlayer() == gameState.getPlayerId();
	}
	
	private Optional<GameAction> tryFindOriginalPlayerAction(ActionEffect effect) {
		// NOTE this strategy does not work if multiple actions target the same object!
		var maybeAction = activeActions.values()
			.stream()
			.map(response -> response.getAction())
			.filter(a -> a.getTargetId() == effect.getAffectedMapObjectId())
			.findAny();
		
		if (!maybeAction.isPresent()) {
			maybeAction = initiatedActions.values()
				.stream()
				.filter(a -> a.getTargetId() == effect.getAffectedMapObjectId())
				.findAny();
		}
		
		return maybeAction;
	}
	
	private void handlePlayerActionFallout(GameAction action, ActionEffect effect) {
		// try remove the action from the active action list
		if (activeActions.containsKey(action.getRefId())) {
			activeActions.remove(action.getRefId());
		}
		
		// we only receive an effect notification about successful space missions (for missions we launched)
		if (effect.getEffectChain().contains(ActionEffectType.SPACE_MISSION_SUCCESS)) {
			gameState.spaceMissionSuccessful(effect.getAffectedMapObjectId());
		}
	}
	
	private void handleAttributeChange(GameEvent event) {
		val changes = event.getChanges();
		
		boolean actionNumberChanged = false; 
		
		for (val change : changes.getChanges()) {
			if (changes.isForPlayer()) {
				switch (change.getName()) {
				case "numberOfRemainingActions":
					actionCounter.set(gameState.getMaxConcurrentActionCount() - Integer.parseInt(change.getValue()));
					actionNumberChanged = true;
					break;
				}
			} else if (changes.isForPlanet()) {
				switch (change.getName()) {
				case "destroyed":
					gameState.planetDestroyed(changes.getAffectedId());
					break;
				}
			}
		}
		
		if (actionNumberChanged) {
			val pastActionResponses = activeActions.values()
				.stream()
				.filter(action -> action.getActionEndTime() <= event.getEventTime())
				.collect(Collectors.toList());
			
			for (val actionResponse : pastActionResponses) {
				val action = actionResponse.getAction();
				if (action.getType() == GameActionType.SPACE_MISSION) {
					gameState.spaceMissionFailed(action.getTargetId());
					activeActions.remove(action.getRefId());
				}
			}
		}
	}

	private boolean hasFreeAction() {
		return activeActionCount() < gameState.getMaxConcurrentActionCount();
	}
	
	private int activeActionCount() {
		// return actionCounter.get() + initiatedActions.size();
		return activeActions.size() + initiatedActions.size();
	}
	
	private int availableActionCount() {
		return gameState.getMaxConcurrentActionCount() - activeActionCount();
	}
	
	private void spaceMission(Planet sourcePlanet, Planet targetPlanet) {
		spaceMission(sourcePlanet.getId(), targetPlanet.getId());
	}
	
	private void spaceMission(int sourcePlanet, int targetPlanet) {
		val action = actionApi.spaceMission(sourcePlanet, targetPlanet);
		
		initiatedActions.put(action.getRefId(), action);
	}
		
	private void spaceMissionWithWormHole(int sourcePlanet, int targetPlanet, int wormHole, EntryPointIndex wormHoleSide) {
		val action = actionApi.spaceMissionWithWormHole(sourcePlanet, targetPlanet, wormHole, wormHoleSide);
		
		initiatedActions.put(action.getRefId(), action);
	}
	
	private void buildWormHole(int xa, int ya, int xb, int yb) {
		val action = actionApi.buildWormHole(xa, ya, xb, yb);
		
		initiatedActions.put(action.getRefId(), action);
	}
	
	private void erectShield(int targetPlanet) {
		val action = actionApi.erectShield(targetPlanet);
		
		initiatedActions.put(action.getRefId(), action);
	}
	
	private void shootMBH(int sourcePlanet, int targetPlanet) {
		val action = actionApi.shootMBH(sourcePlanet, targetPlanet);
		
		initiatedActions.put(action.getRefId(), action);
	}
}
