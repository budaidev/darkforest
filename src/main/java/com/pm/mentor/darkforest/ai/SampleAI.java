package com.pm.mentor.darkforest.ai;

import java.util.HashMap;
import java.util.Map;

import com.loxon.javachallenge.challenge.game.event.GameEvent;
import com.loxon.javachallenge.challenge.game.event.action.ActionResult;
import com.loxon.javachallenge.challenge.game.event.action.EntryPointIndex;
import com.loxon.javachallenge.challenge.game.event.action.GameAction;
import com.loxon.javachallenge.challenge.game.model.Game;
import com.pm.mentor.darkforest.ai.model.GameState;

import lombok.Getter;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SampleAI implements AI {
	
	private final GameActionApi actionApi;
	private GameState gameState;
	private Map<Integer, GameAction> initiatedActions = new HashMap<>();
	private Map<Integer, GameAction> activeActions = new HashMap<>();
	
	@Getter
	private boolean running = false;
	
	public SampleAI(GameActionApi gameActionApi) {
		actionApi = gameActionApi;
	}
	
	@Override
	public void init(Game game, int playerId) {
		gameState = new GameState(game, playerId);
		
		running = true;
	}

	@Override
	public void receiveEvent(GameEvent event) {
		switch (event.getEventType()) {
		case ACTION:
			val actionResponse = event.getAction();
			val action = actionResponse.getAction();
			
			// action response for an action we did not send?
			if (!initiatedActions.containsKey(action.getRefId())) {
				log.warn(String.format("Response for non-existent action received: %s", actionResponse.toString()));
				
				return;
			}
			
			// remove the action from the initiated actions list
			initiatedActions.remove(action.getRefId());
			
			// add to active actions list
			if (actionResponse.getResult() == ActionResult.SUCCESS) {
				activeActions.put(action.getRefId(), action);
			}  else {
				log.warn(String.format("Cannot execute action: %s", actionResponse.toString()));
			}
			
			break;
			
		case CONNECTION_RESULT:
		case GAME_STARTED:
			// ignore
			break;

		case ACTION_EFFECT:
			log.info(String.format("ActionEffect received: %s", event.getActionEffect().toString()));
			
			break;
		case ATTRIBUTE_CHANGE:
			log.info(String.format("AttributeChange received: %s", event.getChanges().toString()));
			
			break;
		case GAME_ENDED:
			running = false;
			break;
		}
	}

	@Override
	public void heartBeat() {
		if (!running) {
			return;
		}
		
		// TODO Auto-generated method stub
		
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
