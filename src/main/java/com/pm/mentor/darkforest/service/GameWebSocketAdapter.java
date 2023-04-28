package com.pm.mentor.darkforest.service;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import com.loxon.javachallenge.challenge.game.event.action.GameAction;
import com.pm.mentor.darkforest.config.ClientConfiguration;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

import lombok.SneakyThrows;
import lombok.val;

@Component
public class GameWebSocketAdapter implements WebSocketHandler {
	
	private final JsonSerializationService serializationService;
	private final String RootUrl;
	
	private WebSocketSession clientSession;
	
	public GameWebSocketAdapter(JsonSerializationService serializationService,
								ClientConfiguration clientConfiguration) {
		this.serializationService = serializationService;
		
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
		
		// TODO forward event...
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
}
