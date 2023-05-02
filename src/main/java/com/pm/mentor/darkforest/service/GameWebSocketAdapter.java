package com.pm.mentor.darkforest.service;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import com.loxon.javachallenge.challenge.game.event.action.BuildWormHoleAction;
import com.loxon.javachallenge.challenge.game.event.action.EntryPointIndex;
import com.loxon.javachallenge.challenge.game.event.action.ErectShieldAction;
import com.loxon.javachallenge.challenge.game.event.action.GameAction;
import com.loxon.javachallenge.challenge.game.event.action.ShootMBHAction;
import com.loxon.javachallenge.challenge.game.event.action.SpaceMissionAction;
import com.pm.mentor.darkforest.ai.GameActionApi;
import com.pm.mentor.darkforest.config.ClientConfiguration;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

import lombok.SneakyThrows;
import lombok.val;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class GameWebSocketAdapter implements WebSocketHandler, GameActionApi {
	
	private static AtomicInteger commandRefSequence = new AtomicInteger(1000);
	
	private final JsonSerializationService serializationService;
	private final String RootUrl;
	private final AIContainer aiContainer;
	
	private WebSocketSession clientSession;
	
	public GameWebSocketAdapter(JsonSerializationService serializationService,
								ClientConfiguration clientConfiguration,
								AIContainer aiContainer) {
		this.serializationService = serializationService;
		aiContainer.attachGameActionApi(this);
		this.aiContainer = aiContainer;
		
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
		System.out.println("control connection established");
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
		String payload = (String)message.getPayload();
		
		val gameEvent = serializationService.readGameEvent(payload);
		
		aiContainer.receiveGameEvent(gameEvent);
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) {
		System.err.println("An error occured on control connection:");

		exception.printStackTrace();
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
		System.out.println("control connection closed");
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
}
