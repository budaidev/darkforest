package com.pm.mentor.darkforest.config;

import java.io.IOException;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketForUI implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new PlanetWebSocketHandler(), "/planets").setAllowedOrigins("*");
    }

    private static class PlanetWebSocketHandler extends TextWebSocketHandler {

        @Override
        public void handleTextMessage(WebSocketSession session, TextMessage message) {
            if (message.getPayload().equals("getJson")) {
                // Retrieve the JSON data and send it to the client
                String jsonData = retrieveJsonData();
                try {
                    session.sendMessage(new TextMessage(jsonData));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private String retrieveJsonData() {
            // Retrieve the JSON data from a database or other data source
            // and return it as a string
            return "{}";
        }
    }
}

