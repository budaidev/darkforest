package com.pm.mentor.darkforest.ai.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.loxon.javachallenge.challenge.game.event.GameEvent;
import com.loxon.javachallenge.challenge.game.event.action.ActionResponse;
import com.loxon.javachallenge.challenge.game.event.action.EntryPointIndex;
import com.loxon.javachallenge.challenge.game.event.action.GameAction;
import com.loxon.javachallenge.challenge.game.event.action.GameActionType;
import com.loxon.javachallenge.challenge.game.event.actioneffect.ActionEffect;
import com.loxon.javachallenge.challenge.game.model.Game;
import com.loxon.javachallenge.challenge.game.model.Planet;
import com.loxon.javachallenge.challenge.game.settings.GameSettings;
import com.pm.mentor.darkforest.ai.GameActionApi;

import lombok.Getter;
import lombok.Synchronized;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class GameState {

	private final int playerId;
	private final GameSettings settings;
	private final List<Planet> planets;
	private final List<Planet> destroyedPlayerPlanets = new ArrayList<>();
	
	private final GameActionApi actionApi;
	private Map<Integer, GameAction> initiatedActions = new HashMap<>();
	private Map<Integer, ActionResponse> activeActions = new HashMap<>();
	
	public GameState(Game game, int playerId, GameActionApi gameActionApi) {
		this.playerId = playerId;
		settings = game.getSettings();
		planets = game.getWorld().getPlanets();
		actionApi = gameActionApi;
	}
	
	public int getMaxConcurrentActionCount() {
		return settings.getMaxConcurrentActions();
	}
	
	@Synchronized
	public List<Planet> getPlayerPlanets() {
		return planets.stream()
			.filter(p -> p.getPlayer() == playerId)
			.collect(Collectors.toList());
	}
	
	@Synchronized
	public List<Planet> getUnknownPlanets() {
		return planets.stream()
			.filter(p -> p.getPlayer() == 0 && p.isDestroyed() == false)
			.collect(Collectors.toList());
	}
	
	@Synchronized
	public List<Planet> getUnknownPlanets(Comparator<? super Planet> comparator) {
		return planets.stream()
			.filter(p -> p.getPlayer() == 0 && p.isDestroyed() == false)
			.sorted(comparator)
			.collect(Collectors.toList());
	}

	@Synchronized
	public void spaceMissionSuccessful(int affectedMapObjectId) {
		tryFindPlayerPlanet(affectedMapObjectId)
			.ifPresent(p -> {
				p.setClassM(true);
				p.setPlayer(playerId);
			});
	}

	@Synchronized
	public void spaceMissionFailed(int affectedMapObjectId) {
		tryFindPlayerPlanet(affectedMapObjectId)
			.ifPresent(p -> {
				// mark the planet as destroyed and inhabitable, however it can still be owned by an other player!
				p.setClassM(false);
				p.setDestroyed(true);
			});
	}

	@Synchronized
	public void planetDestroyed(int affectedId) {
		tryFindPlayerPlanet(affectedId)
			.ifPresent(p -> {
				log.trace(String.format("Player planet %d destroyed.", affectedId));
				p.setClassM(false);
				p.setDestroyed(true);
				planets.remove(p);
				destroyedPlayerPlanets.add(p);
			});
	}
	
	@Synchronized
	public Optional<Planet> tryFindPlayerPlanet(int planetId) {
		return planets.stream()
			.filter(p -> p.getId() == planetId)
			.findFirst();
	}
	
	public void spaceMission(Planet sourcePlanet, Planet targetPlanet) {
		spaceMission(sourcePlanet.getId(), targetPlanet.getId());
	}
	
	public void spaceMission(int sourcePlanet, int targetPlanet) {
		val action = actionApi.spaceMission(sourcePlanet, targetPlanet);
		
		initiatedActions.put(action.getRefId(), action);
	}
		
	public void spaceMissionWithWormHole(int sourcePlanet, int targetPlanet, int wormHole, EntryPointIndex wormHoleSide) {
		val action = actionApi.spaceMissionWithWormHole(sourcePlanet, targetPlanet, wormHole, wormHoleSide);
		
		initiatedActions.put(action.getRefId(), action);
	}
	
	public void buildWormHole(int xa, int ya, int xb, int yb) {
		val action = actionApi.buildWormHole(xa, ya, xb, yb);
		
		initiatedActions.put(action.getRefId(), action);
	}
	
	public void erectShield(int targetPlanet) {
		val action = actionApi.erectShield(targetPlanet);
		
		initiatedActions.put(action.getRefId(), action);
	}
	
	public void shootMBH(int sourcePlanet, int targetPlanet) {
		val action = actionApi.shootMBH(sourcePlanet, targetPlanet);
		
		initiatedActions.put(action.getRefId(), action);
	}
	
	public boolean isEffectPlayerRelated(ActionEffect effect) {
		return effect.getInflictingPlayer() == getPlayerId();
	}
	
	public Optional<GameAction> tryFindOriginalPlayerAction(ActionEffect effect) {
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
	
	public void handlePlayerActionFallout(GameAction action, ActionEffect effect) {
		// try remove the action from the active action list
		if (activeActions.containsKey(action.getRefId())) {
			activeActions.remove(action.getRefId());
		}
	}
	
	public void handleAttributeChange(GameEvent event) {
		val changes = event.getChanges();
		
		boolean actionNumberChanged = false; 
		
		for (val change : changes.getChanges()) {
			if (changes.isForPlayer()) {
				switch (change.getName()) {
				case "numberOfRemainingActions":
					actionNumberChanged = true;
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
					spaceMissionFailed(action.getTargetId());
					activeActions.remove(action.getRefId());
				}
			}
		}
	}

	public boolean hasFreeAction() {
		return activeActionCount() < getMaxConcurrentActionCount();
	}
	
	public int activeActionCount() {
		return activeActions.size() + initiatedActions.size();
	}
	
	public int availableActionCount() {
		return getMaxConcurrentActionCount() - activeActionCount();
	}
}
