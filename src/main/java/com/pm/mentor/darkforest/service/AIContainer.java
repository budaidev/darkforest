package com.pm.mentor.darkforest.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.loxon.javachallenge.challenge.game.event.ConnectionResultType;
import com.loxon.javachallenge.challenge.game.event.GameEvent;
import com.loxon.javachallenge.challenge.game.event.action.ActionResult;
import com.loxon.javachallenge.challenge.game.event.action.BuildWormHoleAction;
import com.loxon.javachallenge.challenge.game.event.action.GameActionType;
import com.loxon.javachallenge.challenge.game.event.actioneffect.ActionEffectType;
import com.loxon.javachallenge.challenge.game.event.actioneffect.WormHoleBuiltEffect;
import com.loxon.javachallenge.challenge.game.model.WormHole;
import com.pm.mentor.darkforest.ai.AI;
import com.pm.mentor.darkforest.ai.AIRunner;
import com.pm.mentor.darkforest.ai.GameActionApi;
import com.pm.mentor.darkforest.ai.SampleAI;
import com.pm.mentor.darkforest.ai.manual.ManualAI;
import com.pm.mentor.darkforest.ai.model.GameState;
import com.pm.mentor.darkforest.ui.GameStateHolder;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AIContainer {
	
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private AIRunner runner;
	private ScheduledFuture<?> scheduledTask;
	
	private GameActionApi gameActionApi;
	private GameState gameState;
	private GameStateHolder gameStateHolder;

	private AI ai;

	private int playerId;

	private List<WormHole> wormholeToBuild = new ArrayList<>();
	private List<WormHole> wormholeHaveBuilt = new ArrayList<>();
	
	private long longestGameEventReceiveExecution = 0;

	public AIContainer(ManualAI ai, GameStateHolder gameStateHolder) {

		this.ai = ai;
		this.gameStateHolder = gameStateHolder;
	}
	
	public void create() {
		if (scheduledTask != null && !scheduledTask.isDone()) {
			scheduledTask.cancel(true);
		}

		runner = new AIRunner();
		ai = new SampleAI();
		runner.init(ai);
		
		scheduledTask = scheduler.scheduleAtFixedRate(runner, 0, 1000, TimeUnit.MILLISECONDS);
	}
	
	public void receiveGameEvent(GameEvent event) {
		try {
			val timeStarted = System.currentTimeMillis();
			
			handleGameEvent(event);
			runner.receiveEvent(event);
			scheduler.execute(runner);
			
			val elapsed = System.currentTimeMillis() - timeStarted;
			
			if (elapsed > longestGameEventReceiveExecution) {
				longestGameEventReceiveExecution = elapsed;
			}
			
			if (elapsed > 20) {
				log.warn(String.format("AIContainer.receiveGameEvent execution took: %d ms", elapsed));
			} else {
				log.trace(String.format("AIContainer.receiveGameEvent execution took: %d ms", elapsed));
			}
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			
			e.printStackTrace(pw);
			String sStackTrace = sw.toString();
			
			log.error(sStackTrace);
		}
	}

	public void handleGameEvent(GameEvent event) {
		switch (event.getEventType()) {
			case ACTION:
				val actionResponse = event.getAction();
				
				if (actionResponse.getResult() != ActionResult.SUCCESS) {
					log.warn(String.format("Cannot execute action: %s", actionResponse.toString()));
					
					break;
				}
				
				val action = actionResponse.getAction();
				
				log.info(String.format("ActionResponse received: %s", actionResponse));
				
				// action response for an action we did not send?
				if (!gameState.getInitiatedActions().containsKey(action.getRefId())) {
					log.warn(String.format("Response for non-existent action received: %s", actionResponse.toString()));
					
					return;
				}

				if(action.getType() == GameActionType.BUILD_WORM_HOLE) {
					BuildWormHoleAction wormholeAction = (BuildWormHoleAction) action;
					WormHole hole = WormHole.builder()
							.id(wormholeAction.getTargetId())
							.x(wormholeAction.getXa())
							.y(wormholeAction.getYa())
							.xb(wormholeAction.getXb())
							.yb(wormholeAction.getYb())
							.build();
					wormholeToBuild.add(hole);
				}
				
				// remove the action from the initiated actions list
				gameState.getInitiatedActions().remove(action.getRefId());
				
				// add to active actions list
				gameState.getActiveActions().put(action.getRefId(), actionResponse);
				
				break;

			case CONNECTION_RESULT:
				val result = event.getConnectionResult();

				if (result.getConnectionResultType() == ConnectionResultType.SUCCESS) {
					playerId = result.getPlayerId();
				} else {
					throw new RuntimeException(String.format("Failed connecting to game: %s", result.getConnectionResultType()));
				}

				break;

			case GAME_STARTED:
				gameState = new GameState(event.getGame(), playerId, gameActionApi);
				runner.startGame(gameState);
				longestGameEventReceiveExecution = 0;

				break;

			case ACTION_EFFECT:
				val actionEffect = event.getActionEffect();
				log.info(String.format("ActionEffect received: %s", actionEffect.toString()));

				if (actionEffect.getEffectChain().contains(ActionEffectType.SPACE_MISSION_SUCCESS)) {
					gameState.spaceMissionSuccessful(actionEffect.getAffectedMapObjectId());
				} else if (actionEffect.getEffectChain().contains(ActionEffectType.WORM_HOLE_BUILT)) {
					WormHoleBuiltEffect effect = (WormHoleBuiltEffect) actionEffect;
					Optional<WormHole> op = wormholeToBuild.stream().filter(x -> x.getId() == effect.getWormHoleId()).findFirst();
					if (op.isPresent()) {
						WormHole hole = op.get();
						wormholeHaveBuilt.add(hole);
						wormholeToBuild.remove(hole);
						gameState.wormHoleBuilt(hole);
						gameStateHolder.updateWormholes(gameState.getWormHoles());
					}
				}
				
				if (gameState.isEffectPlayerRelated(actionEffect) ) {
					val originalAction = gameState.tryFindOriginalPlayerAction(actionEffect);
					
					originalAction.ifPresent(origAction -> gameState.handlePlayerActionFallout(origAction, actionEffect));
				} else {
					gameState.nonPlayerEffectArrived(actionEffect);
				}

				gameStateHolder.updatePlanetStatus(gameState.getPlanets());
				
				break;
				
			case ATTRIBUTE_CHANGE:
				val changes = event.getChanges();

				for (val change : changes.getChanges()) {
					if (changes.isForPlanet()) {
						switch (change.getName()) {
							case "destroyed":
								gameState.planetDestroyed(changes.getAffectedId());
								gameStateHolder.updatePlanetStatus(gameState.getPlanets());

								break;
						}
					}
				}
				
				log.info(String.format("AttributeChange received: %s", event.getChanges().toString()));
				
				gameState.handleAttributeChange(event);

				break;

			case GAME_ENDED:
				ai.stop();
				runner.signalStop();
				
				log.info(String.format("Longest AIContainer.receiveGameEvent execution took %d ms", longestGameEventReceiveExecution));

				break;
		}
		
		if (gameState != null) {
			gameState.purgeStuckActions(event.getEventTime());
			
			gameStateHolder.setActions(gameState.getInitiatedActions().values(), gameState.getActiveActions().values());
		}
	}

	public void attachGameActionApi(GameActionApi api) {
		gameActionApi = api;
	}

	public boolean isCreated() {
		return runner != null;
	}
	
	public void shutdown() {
		if (scheduledTask != null && !scheduledTask.isDone()) {
			scheduledTask.cancel(true);
		}
		
		try {
			scheduler.shutdown();

			if (!scheduler.awaitTermination(2, TimeUnit.SECONDS)) {
				scheduler.shutdownNow();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
