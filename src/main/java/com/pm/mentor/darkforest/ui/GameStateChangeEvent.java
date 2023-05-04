package com.pm.mentor.darkforest.ui;

import org.springframework.context.ApplicationEvent;

public class GameStateChangeEvent extends ApplicationEvent {

    private final GameDto game;

    public GameStateChangeEvent(Object source, GameDto game) {
        super(source);
        this.game = game;
    }

    public GameDto getMyObject() {
        return game;
    }
}
