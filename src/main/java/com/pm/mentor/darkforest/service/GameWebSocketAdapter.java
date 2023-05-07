package com.pm.mentor.darkforest.service;

import com.loxon.javachallenge.challenge.game.event.EventType;
import com.loxon.javachallenge.challenge.game.model.Game;
import com.loxon.javachallenge.challenge.game.model.Planet;
import com.loxon.javachallenge.challenge.game.model.World;
import com.pm.mentor.darkforest.ui.GameDtoMapper;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import com.loxon.javachallenge.challenge.game.event.GameEvent;
import com.loxon.javachallenge.challenge.game.event.action.BuildWormHoleAction;
import com.loxon.javachallenge.challenge.game.event.action.EntryPointIndex;
import com.loxon.javachallenge.challenge.game.event.action.ErectShieldAction;
import com.loxon.javachallenge.challenge.game.event.action.GameAction;
import com.loxon.javachallenge.challenge.game.event.action.ShootMBHAction;
import com.loxon.javachallenge.challenge.game.event.action.SpaceMissionAction;
import com.pm.mentor.darkforest.ai.GameActionApi;
import com.pm.mentor.darkforest.config.ClientConfiguration;
import com.pm.mentor.darkforest.ui.GameStateHolder;

import lombok.SneakyThrows;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GameWebSocketAdapter implements WebSocketHandler, GameActionApi {

	private static AtomicInteger commandRefSequence = new AtomicInteger(1000);

	private final JsonSerializationService serializationService;
	private final String RootUrl;
	private final AIContainer aiContainer;

	private final GameStateHolder gameStateHolder;

	private WebSocketSession clientSession;

	public GameWebSocketAdapter(JsonSerializationService serializationService,
								ClientConfiguration clientConfiguration,
								AIContainer aiContainer,
								GameStateHolder gameStateHolder) {
		this.serializationService = serializationService;
		aiContainer.attachGameActionApi(this);
		this.aiContainer = aiContainer;
		this.gameStateHolder = gameStateHolder;

		this.RootUrl = String.format("ws://%s:%s", clientConfiguration.getUrl(), clientConfiguration.getPort());
	}

	@SneakyThrows
	public void connect(String gameId, String gameKey) {
		val url = String.format("%s/game?gameId=%s&gameKey=%s&connectionType=control", RootUrl, gameId, gameKey);
		WebSocketClient client = new StandardWebSocketClient();
		clientSession = new ConcurrentWebSocketSessionDecorator(client.execute(this, url).get(), 10000, 1024*1024);
	}

	@SneakyThrows
	public void stop() {
		if (clientSession != null && clientSession.isOpen()) {
			clientSession.close();
		}
	}

	@SneakyThrows
	public void send(GameAction gameAction) {
		val message = serializationService.writeGameAction(gameAction);

		clientSession.sendMessage(new TextMessage(message));
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		log.info("control connection established");
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {

		String payload = (String)message.getPayload();

		log.info(payload);

		GameEvent gameEvent = serializationService.readGameEvent(payload);

		gameStateHolder.setMyObject(GameDtoMapper.toGameDto(gameEvent));

		aiContainer.receiveGameEvent(gameEvent);
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) {
		System.err.println("An error occured on control connection:");

		exception.printStackTrace();
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
		log.info("control connection closed");
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}

	@Override
	public int spaceMission(int sourcePlanet, int targetPlanet) {
		val action = new SpaceMissionAction();

		action.setOriginId(sourcePlanet);
		action.setTargetId(targetPlanet);

		return setActionIdAndSend(action);
	}

	@Override
	public int spaceMissionWithWormHole(int sourcePlanet, int targetPlanet, int wormHole, EntryPointIndex wormHoleSide) {
		val action = new SpaceMissionAction();

		action.setOriginId(sourcePlanet);
		action.setTargetId(targetPlanet);
		action.setWormHoleId(wormHole);
		action.setEntryPointIndex(wormHoleSide);

		return setActionIdAndSend(action);
	}

	@Override
	public int buildWormHole(int xa, int ya, int xb, int yb) {
		val action = new BuildWormHoleAction();

		action.setXa(xa);
		action.setYa(ya);
		action.setXb(xb);
		action.setYb(yb);

		return setActionIdAndSend(action);
	}

	@Override
	public int erectShield(int targetPlanet) {
		val action = new ErectShieldAction();

		action.setTargetId(targetPlanet);

		return setActionIdAndSend(action);
	}

	@Override
	public int shootMBH(int sourcePlanet, int targetPlanet) {
		val action = new ShootMBHAction();

		action.setOriginId(sourcePlanet);
		action.setTargetId(targetPlanet);

		return setActionIdAndSend(action);
	}

	private int setActionIdAndSend(GameAction action) {
		val id = commandRefSequence.incrementAndGet();
		action.setRefId(id);

		send(action);

		return id;
	}

	public void testUi(){
		GameEvent g1 = new GameEvent();
		g1.setGame(GameEvent.builder()
				.eventType(EventType.GAME_STARTED)
				.game(Game.builder()
						.world(World.builder()
								.width(800)
								.height(600)
								.planets(
										List.of(
												Planet.builder()
														.id(0)
														.x(100)
														.y(100)
														.player(1)
														.shieldErectedAt(0)
														.classM(false)
														.destroyed(false)
														.shieldRemovedAt(0)
														.build()))
								.build())
						.build()).build().getGame());

		gameStateHolder.setMyObject(GameDtoMapper.toGameDto(g1));


	}
}
