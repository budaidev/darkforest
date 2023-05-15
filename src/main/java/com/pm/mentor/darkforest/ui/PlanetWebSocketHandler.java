package com.pm.mentor.darkforest.ui;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;
import lombok.val;

@Component
public class PlanetWebSocketHandler extends TextWebSocketHandler {

    private GameStateHolder gameStateHolder;
    private ObjectMapper objectMapper;

    private final CopyOnWriteArrayList<WebSocketSession> sessions;

    public PlanetWebSocketHandler(GameStateHolder gameStateHolder, ObjectMapper objectMapper) {
        this.gameStateHolder = gameStateHolder;
        this.objectMapper = objectMapper;
        this.sessions = new CopyOnWriteArrayList<>();
    }

    @Override
    @SneakyThrows
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws JsonProcessingException {
        if (message.getPayload().equals("getJson")) {
            // Retrieve the JSON data and send it to the client
            String jsonData = retrieveJsonData();
            try {
                session.sendMessage(new TextMessage(jsonData));
            } catch (IOException e) {
                e.printStackTrace();
				
				throw e;
            }
        }
    }

    private String retrieveJsonData() throws JsonProcessingException {
        // Retrieve the JSON data from a database or other data source
        // and return it as a string
        return objectMapper.writeValueAsString(gameStateHolder.getMyObject());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Add the new session to the sessions collection
        super.afterConnectionEstablished(session);
        sessions.add(session);

        // Send the current state of MyObject to the client when the WebSocket connection is established
        String json = objectMapper.writeValueAsString(gameStateHolder.getMyObject());
        session.sendMessage(new TextMessage(json));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Remove the closed session from the sessions collection
        sessions.remove(session);
    }

    @EventListener
    public void handleObjectChangedEvent(GameStateChangeEvent event) throws Exception {
        // Send the updated MyObject to all connected clients when the object is changed
        String json = objectMapper.writeValueAsString(event.getMyObject());
        for (WebSocketSession session : sessions) {
            session.sendMessage(new TextMessage(json));
        }
    }

    @SneakyThrows
	public void shutdown() {
		for (val session : sessions) {
			if (session.isOpen()) {
				session.close();
			}
		}
	}
}