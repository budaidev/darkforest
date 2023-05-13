package com.pm.mentor.darkforest.ai.manual.actionevents;

import org.springframework.context.ApplicationEvent;

import com.loxon.javachallenge.challenge.game.event.action.ErectShieldAction;

public class ErectShieldEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;
	private final ErectShieldAction event;

    public ErectShieldEvent(Object source, ErectShieldAction event) {
        super(source);
        this.event = event;
    }

    public ErectShieldAction getMyObject() {
        return event;
    }
}
