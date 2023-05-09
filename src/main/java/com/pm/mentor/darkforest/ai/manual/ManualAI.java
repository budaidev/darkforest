package com.pm.mentor.darkforest.ai.manual;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loxon.javachallenge.challenge.game.event.GameEvent;
import com.loxon.javachallenge.challenge.game.event.action.ActionResult;
import com.loxon.javachallenge.challenge.game.event.action.BuildWormHoleAction;
import com.loxon.javachallenge.challenge.game.event.action.EntryPointIndex;
import com.loxon.javachallenge.challenge.game.event.action.ErectShieldAction;
import com.loxon.javachallenge.challenge.game.event.action.GameAction;
import com.loxon.javachallenge.challenge.game.event.action.ShootMBHAction;
import com.loxon.javachallenge.challenge.game.event.action.SpaceMissionAction;
import com.loxon.javachallenge.challenge.game.model.Game;
import com.pm.mentor.darkforest.ai.AI;
import com.pm.mentor.darkforest.ai.GameActionApi;
import com.pm.mentor.darkforest.ai.manual.actionevents.BuildWormholeEvent;
import com.pm.mentor.darkforest.ai.manual.actionevents.ErectShieldEvent;
import com.pm.mentor.darkforest.ai.manual.actionevents.ShootMBHEvent;
import com.pm.mentor.darkforest.ai.manual.actionevents.SpaceMissionEvent;
import com.pm.mentor.darkforest.ai.model.GameState;
import com.pm.mentor.darkforest.ui.GameStateChangeEvent;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Component
public class ManualAI implements AI {

	private GameActionApi actionApi;
	private GameState gameState;
	private Map<Integer, GameAction> initiatedActions = new HashMap<>();
	private Map<Integer, GameAction> activeActions = new HashMap<>();
	private ObjectMapper mapper = new ObjectMapper();

	@Getter
	private boolean running = false;

	public void init(GameActionApi gameActionApi) {
		actionApi = gameActionApi;
	}

	@Override
	public void init(Game game, int playerId) {
		gameState = new GameState(game, playerId);

		running = true;
	}

	@Override
	public void receiveEvent(GameEvent event) {
		try {
			log.info("Received event: " + mapper.writeValueAsString(event));
		} catch (JsonProcessingException e) {
		}
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

	private void buildWormHole(long xa, long ya, long xb, long yb) {
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

	@EventListener
	public void handleBuildWormhole(BuildWormholeEvent event) {
		BuildWormHoleAction action = event.getMyObject();
		buildWormHole(action.getXa(), action.getYa(), action.getXb(), action.getYb());
	}

	@EventListener
	public void handleErectShield(ErectShieldEvent event) {
		log.info("ErectShieldEvent received with {}",
				event.getMyObject().getTargetId());
		ErectShieldAction action = event.getMyObject();
		erectShield(action.getTargetId());
	}

	@EventListener
	public void handleShootMbh(ShootMBHEvent event) {
		log.info("Shoot event received with {} => {}",
				event.getMyObject().getOriginId(), event.getMyObject().getTargetId());
		ShootMBHAction action = event.getMyObject();
		shootMBH(action.getOriginId(), action.getTargetId());
	}

	@EventListener
	public void handleSpaceMission(SpaceMissionEvent event) {
		log.info("SpaceMissionEvent received with {} => {}",
				event.getMyObject().getOriginId(), event.getMyObject().getTargetId());
		SpaceMissionAction action = event.getMyObject();
		spaceMission(action.getOriginId(), action.getTargetId());
	}
}
