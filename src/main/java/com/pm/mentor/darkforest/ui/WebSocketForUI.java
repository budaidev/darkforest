package com.pm.mentor.darkforest.ui;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSocket
public class WebSocketForUI implements WebSocketConfigurer {

    private final GameStateHolder holder;
    private final ObjectMapper mapper;

    public WebSocketForUI(GameStateHolder holder, ObjectMapper mapper) {
        this.holder = holder;
        this.mapper = mapper;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new PlanetWebSocketHandler(holder, mapper), "/planets").setAllowedOrigins("*");
    }
}
