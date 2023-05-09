package com.pm.mentor.darkforest.service;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import com.pm.mentor.darkforest.contoller.ConnectionState;
import com.pm.mentor.darkforest.contoller.ConnectionStateHolder;
import com.pm.mentor.darkforest.ui.PlanetWebSocketHandler;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ShutDownHandler implements DisposableBean {

    private final GameHttpAdapter gameHttpAdapter;
    private final ConnectionStateHolder connectionStateHolder;
    private final AIContainer aiContainer;
    private final GameWebSocketAdapter gameWebSocketAdapter;
    private final PlanetWebSocketHandler planetWebSocketHandler;

    public ShutDownHandler(GameHttpAdapter gameHttpAdapter, 
    		ConnectionStateHolder connectionStateHolder, 
    		AIContainer aiContainer, 
    		GameWebSocketAdapter gameWebSocketAdapter,
    		PlanetWebSocketHandler planetWebSocketHandler) {
        this.gameHttpAdapter = gameHttpAdapter;
        this.connectionStateHolder = connectionStateHolder;
        this.aiContainer = aiContainer;
        this.gameWebSocketAdapter = gameWebSocketAdapter;
        this.planetWebSocketHandler = planetWebSocketHandler;
    }

    @Override
    public void destroy() throws Exception {
        // Code to run when application is shutting down
    	log.info("Application is shutting down...");
    	
    	aiContainer.shutdown();
    	gameWebSocketAdapter.shutdown();
    	planetWebSocketHandler.shutdown();

        connectionStateHolder.getAllGames().forEach(System.out::println);

        for (ConnectionState state : connectionStateHolder.getLiveGames()) {
        	log.info("Stopping game with game key " + state.getGameId() + " game id " + state.getGameId() );
        	gameHttpAdapter.stopGame(state.getGameKey(), state.getGameId());
        }
    }
}
