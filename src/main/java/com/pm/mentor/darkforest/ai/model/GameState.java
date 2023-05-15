package com.pm.mentor.darkforest.ai.model;

import com.loxon.javachallenge.challenge.game.model.Player;
import com.pm.mentor.darkforest.util.PointToPointDistanceCache;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.loxon.javachallenge.challenge.game.event.GameEvent;
import com.loxon.javachallenge.challenge.game.event.action.ActionResponse;
import com.loxon.javachallenge.challenge.game.event.action.EntryPointIndex;
import com.loxon.javachallenge.challenge.game.event.action.GameAction;
import com.loxon.javachallenge.challenge.game.event.action.GameActionType;
import com.loxon.javachallenge.challenge.game.event.actioneffect.ActionEffect;
import com.loxon.javachallenge.challenge.game.event.actioneffect.GravityWaveCrossing;
import com.loxon.javachallenge.challenge.game.model.Game;
import com.loxon.javachallenge.challenge.game.model.Planet;
import com.loxon.javachallenge.challenge.game.model.WormHole;
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
	private final List<AIPlanet> planets;
	private final List<Planet> destroyedPlayerPlanets = new ArrayList<>();
	
	private final GameActionApi actionApi;
	private Map<Integer, GameAction> initiatedActions = new HashMap<>();
	private Map<Integer, ActionResponse> activeActions = new HashMap<>();
	
	private final GravityWaveCollector actionEffectCollector;

	List<WormHole> wormHoles = new ArrayList<>();

	PointToPointDistanceCache distanceCache;
	
	public GameState(Game game, int playerId, GameActionApi gameActionApi) {
		this.playerId = playerId;
		settings = game.getSettings();
		planets = game.getWorld().getPlanets()
				.stream()
				.map(p -> new AIPlanet(p))
				.collect(Collectors.toList());
		actionApi = gameActionApi;
		
		actionEffectCollector = new GravityWaveCollector(game.getPlayers(), planets, playerId, this);
		distanceCache = new PointToPointDistanceCache();
	}

	public GameState(GameSettings settings, int playerId) {
		this.settings = settings;
		this.playerId = playerId;
		this.planets = new ArrayList<>();
		this.actionApi = null;
		actionEffectCollector = new GravityWaveCollector(List.of(new Player()), planets, playerId, this);
		distanceCache = new PointToPointDistanceCache();
	}
	
	public int getMaxConcurrentActionCount() {
		return settings.getMaxConcurrentActions();
	}
	
	@Synchronized
	public List<AIPlanet> getPlayerPlanets() {
		return planets.stream()
			.filter(p -> p.getOwner() == playerId && !p.isDestroyed())
			.collect(Collectors.toList());
	}
	
	@Synchronized
	public List<AIPlanet> getUnknownPlanets() {
		return planets.stream()
			.filter(p -> p.isSpaceMissionPossible())
			.collect(Collectors.toList());
	}

	@Synchronized
	public Optional<WormHole> getCloserWormhole(AIPlanet target){
		if (wormHoles.isEmpty()) {
			return Optional.empty();
		}

		WormHole min = wormHoles.get(0);
		double distance = PointToPointDistanceCache.distance(target.getPos(), wormHoles.get(0).getX(), wormHoles.get(0).getY());
		for(WormHole wormhole : wormHoles) {
			double d1 = PointToPointDistanceCache.distance(target.getPos(), wormhole.getX(), wormhole.getY());
			double d2 = PointToPointDistanceCache.distance(target.getPos(), wormhole.getXb(), wormhole.getYb());

			if(d1 < distance){
				distance = d1;
				min = wormhole;
			}

			if(d2 < distance){
				distance = d2;
				min = wormhole;
			}
		}

		return Optional.of(min);
	}

	@Synchronized
	public Map<AIPlanet, Double> getColonizablePlanets() {
		DistanceToPlayerPlanetCalculator calculator = getDistanceCalculatorWithWormholes();
		return calculator.calculateDistances(planets.stream()
				.filter(AIPlanet::isSpaceMissionPossible)
				.collect(Collectors.toList()));
	}

	@Synchronized
	public Map<AIPlanet, Double> getEnemyPlanet() {
		DistanceToPlayerPlanetCalculator calculator = getDistanceCalculator();
		return calculator.calculateDistances(planets.stream()
				.filter(p -> p.getOwner() != playerId && p.getOwner() != 0 && !p.isDestroyed())
				.collect(Collectors.toList()));
	}


	@Synchronized
	private DistanceToPlayerPlanetCalculator getDistanceCalculator(){
		return new DistanceToPlayerPlanetCalculator(getPlayerPlanets(), distanceCache);
	}

	@Synchronized
	private DistanceToPlayerPlanetCalculator getDistanceCalculatorWithWormholes(){
		return new DistanceToPlayerPlanetCalculator(getPlayerPlanets(), getWormHoles(),distanceCache);
	}
	
	@Synchronized
	public List<AIPlanet> getColonizablePlanets(Comparator<? super AIPlanet> comparator) {
		return planets.stream()
			.filter(p -> p.isSpaceMissionPossible())
			.sorted(comparator)
			.collect(Collectors.toList());
	}

	@Synchronized
	public List<AIPlanet> getEnemyPlanet(Comparator<? super AIPlanet> comparator) {
		return planets.stream()
				.filter(p -> p.getOwner() != playerId && !p.isDestroyed())
				.sorted(comparator)
				.collect(Collectors.toList());
	}

	@Synchronized
	public void spaceMissionSuccessful(int affectedMapObjectId) {
		tryFindPlayerPlanet(affectedMapObjectId)
			.ifPresent(p -> p.playerSettled(playerId));
	}

	@Synchronized
	public void wormHoleBuilt(WormHole wormhole) {
		wormHoles.add(wormhole);
	}

	@Synchronized
	public void spaceMissionFailed(int affectedMapObjectId) {
		tryFindPlayerPlanet(affectedMapObjectId)
			.ifPresent(p -> p.spaceMissionFailed());
	}

	@Synchronized
	public void planetDestroyed(int affectedId) {
		tryFindPlayerPlanet(affectedId)
			.ifPresent(p -> {
				log.trace(String.format("Planet %d destroyed.", affectedId));
				p.destoryed();
			});
	}
	
	@Synchronized
	public Optional<AIPlanet> tryFindPlayerPlanet(int planetId) {
		return planets.stream()
			.filter(p -> p.getId() == planetId)
			.findFirst();
	}
	
	@Synchronized
	public void spaceMission(AIPlanet sourcePlanet, AIPlanet targetPlanet) {
		spaceMission(sourcePlanet.getId(), targetPlanet.getId());
	}
	
	@Synchronized
	public void spaceMission(int sourcePlanet, int targetPlanet) {
		val action = actionApi.spaceMission(sourcePlanet, targetPlanet);
		
		initiatedActions.put(action.getRefId(), action);
	}
	
	@Synchronized
	public void spaceMissionWithWormHole(int sourcePlanet, int targetPlanet, int wormHole, EntryPointIndex wormHoleSide) {
		val action = actionApi.spaceMissionWithWormHole(sourcePlanet, targetPlanet, wormHole, wormHoleSide);
		
		initiatedActions.put(action.getRefId(), action);
	}
	
	@Synchronized
	public void buildWormHole(long xa, long ya, long xb, long yb) {
		val action = actionApi.buildWormHole(xa, ya, xb, yb);
		
		initiatedActions.put(action.getRefId(), action);
	}
	
	@Synchronized
	public void erectShield(int targetPlanet) {
		val action = actionApi.erectShield(targetPlanet);
		
		initiatedActions.put(action.getRefId(), action);
	}
	
	@Synchronized
	public void shootMBH(int sourcePlanet, int targetPlanet) {
		val action = actionApi.shootMBH(sourcePlanet, targetPlanet);
		
		initiatedActions.put(action.getRefId(), action);
	}
	
	@Synchronized
	public void shootMBH(AIPlanet playerPlanet, AIPlanet target) {
		shootMBH(playerPlanet.getId(), target.getId());
	}
	
	@Synchronized
	public boolean isEffectPlayerRelated(ActionEffect effect) {
		return effect.getInflictingPlayer() == getPlayerId();
	}
	
	@Synchronized
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
	
	@Synchronized
	public void handlePlayerActionFallout(GameAction action, ActionEffect effect) {
		log.trace(String.format("Received an effect (affectedId=%d) for a previous active action (ref=%d)!", effect.getAffectedMapObjectId(), action.getRefId()));

		// try remove the action from the active action list
		if (activeActions.containsKey(action.getRefId())) {
			activeActions.remove(action.getRefId());
		}
	}
	
	@Synchronized
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
			val pastActionResponses = getActionsCompletedBefore(event.getEventTime(), 0);
			
			for (val actionResponse : pastActionResponses) {
				val action = actionResponse.getAction();
				if (action.getType() == GameActionType.SPACE_MISSION) {
					log.trace(String.format("Space mission (ref=%d) should have been completed but no info received. Assuming failure.", action.getRefId()));
					spaceMissionFailed(action.getTargetId());
					activeActions.remove(action.getRefId());
				}
			}
		}
	}

	@Synchronized
	public boolean hasFreeAction() {
		return activeActionCount() < getMaxConcurrentActionCount();
	}
	
	@Synchronized
	public int activeActionCount() {
		return activeActions.size() + initiatedActions.size();
	}
	
	@Synchronized
	public int availableActionCount() {
		return getMaxConcurrentActionCount() - activeActionCount();
	}

	@Synchronized
	public void nonPlayerEffectArrived(ActionEffect actionEffect) {
		if (actionEffect.getClass().getSimpleName().equals("GravityWaveCrossing")) {
			actionEffectCollector.collect((GravityWaveCrossing)actionEffect);
		}
	}

	@Synchronized
	public void purgeStuckActions(long eventTime) {
		val pastActionResponses = getActionsCompletedBefore(eventTime, 500);
		
		for (val actionResponse : pastActionResponses) {
			val action = actionResponse.getAction();
			
			activeActions.remove(action.getRefId());
			
			log.warn(String.format("Removing forgotten action response from active actions list: %s", actionResponse.toString()));
		}
	}

	@Synchronized
	public void clearActions() {
		initiatedActions.clear();
		activeActions.clear();
	}

	@Synchronized
	public List<AIPlanet> getDestroyablePlanets() {
		return planets.stream()
				.filter(p -> p.getOwner() != playerId && !p.isDestroyed())
				.collect(Collectors.toList());
	}
	
	public Comparator<AIPlanet> createClosestToPlayerPlanetComparator() {
		return new ClosestToPlayerPlanetsComparator(getPlayerPlanets());
	}

	public Comparator<AIPlanet> createClosestToPlayerPlanetComparatorWithWormholes() {
		return new ClosestToPlayerPlanetsWithWormholeComparator(getPlayerPlanets(), getWormHoles());
	}
	
	public Predicate<AIPlanet> createTargetedPlanetFilter() {
		return new TargetedPlanetFilter(this);
	}

	public Predicate<PlanetDistance> createTargetedPlanetDistanceFilter() {
		return new TargetedPlanetDistanceFilter(this);
	}

	private List<ActionResponse> getActionsCompletedBefore(long timestamp, int safetyThreshold) {
		return activeActions.values()
			.stream()
			.filter(action -> action.getActionEndTime() + safetyThreshold <= timestamp)
			.collect(Collectors.toList());
	}

	public void shieldTimeout(int affectedMapObjectId) {
		tryFindPlayerPlanet(affectedMapObjectId)
			.ifPresent(AIPlanet::disableShield);
	}
}
