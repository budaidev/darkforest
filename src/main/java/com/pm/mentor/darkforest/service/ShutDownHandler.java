package com.pm.mentor.darkforest.service;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import com.pm.mentor.darkforest.contoller.ConnectionState;
import com.pm.mentor.darkforest.contoller.ConnectionStateHolder;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ShutDownHandler implements DisposableBean {

    private final GameHttpAdapter gameHttpAdapter;
    private final ConnectionStateHolder connectionStateHolder;
    private final AIContainer aiContainer;

    public ShutDownHandler(GameHttpAdapter gameHttpAdapter, ConnectionStateHolder connectionStateHolder, AIContainer aiContainer) {
        this.gameHttpAdapter = gameHttpAdapter;
        this.connectionStateHolder = connectionStateHolder;
        this.aiContainer = aiContainer;
    }

    @Override
    public void destroy() throws Exception {
        // Code to run when application is shutting down
    	log.info("Application is shutting down...");
    	
    	aiContainer.shutDown();

        connectionStateHolder.getAllGames().forEach(System.out::println);

        for (ConnectionState state : connectionStateHolder.getLiveGames()) {
        	log.info("Stopping game with game key " + state.getGameId() + " game id " + state.getGameId() );
        	gameHttpAdapter.stopGame(state.getGameKey(), state.getGameId());
        }
    }
}
