package com.pm.mentor.darkforest.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.mentor.darkforest.ui.GameStateChangeEvent;
import com.pm.mentor.darkforest.ui.PlanetWebSocketHandler;
import io.swagger.annotations.Authorization;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

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

