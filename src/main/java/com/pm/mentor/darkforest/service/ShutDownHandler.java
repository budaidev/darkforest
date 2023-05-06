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

    public ShutDownHandler(GameHttpAdapter gameHttpAdapter, ConnectionStateHolder connectionStateHolder){
        this.gameHttpAdapter = gameHttpAdapter;
        this.connectionStateHolder = connectionStateHolder;
    }

    @Override
    public void destroy() throws Exception {
        // Code to run when application is shutting down
    	log.info("Application is shutting down...");

        connectionStateHolder.getAllGames().forEach(System.out::println);

        for (ConnectionState state : connectionStateHolder.getLiveGames()) {
        	log.info("Stopping game with game key " + state.getGameId() + " game id " + state.getGameId() );
        	gameHttpAdapter.stopGame(state.getGameKey(), state.getGameId());
        }
    }
}
