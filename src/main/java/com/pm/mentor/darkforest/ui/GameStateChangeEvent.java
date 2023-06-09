package com.pm.mentor.darkforest.ui;

import com.pm.mentor.darkforest.ui.dto.GameDto;
import org.springframework.context.ApplicationEvent;

public class GameStateChangeEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;
    
	private final GameDto game;

    public GameStateChangeEvent(Object source, GameDto game) {
        super(source);
        this.game = game;
    }

    public GameDto getMyObject() {
        return game;
    }
}
