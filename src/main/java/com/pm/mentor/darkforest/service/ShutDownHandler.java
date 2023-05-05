package com.pm.mentor.darkforest.service;

import com.pm.mentor.darkforest.contoller.ConnectionState;
import com.pm.mentor.darkforest.contoller.ConnectionStateHolder;
import com.pm.mentor.darkforest.contoller.GameController;
import com.pm.mentor.darkforest.ui.GameStateHolder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

@Component
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
        System.out.println("Application is shutting down...");

        connectionStateHolder.getAllGames().forEach(System.out::println);

        for (ConnectionState state : connectionStateHolder.getLiveGames()){
            System.out.println("Stopping game with game key " + state.getGameId() + " game id " + state.getGameId() );
            System.out.println(gameHttpAdapter.stopGame(state.getGameKey(), state.getGameId()));
        }
    }
}
