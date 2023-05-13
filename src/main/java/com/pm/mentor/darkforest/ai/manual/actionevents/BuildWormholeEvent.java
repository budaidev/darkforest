package com.pm.mentor.darkforest.ai.manual.actionevents;

import org.springframework.context.ApplicationEvent;

import com.loxon.javachallenge.challenge.game.event.action.BuildWormHoleAction;

public class BuildWormholeEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;
	private final BuildWormHoleAction event;

    public BuildWormholeEvent(Object source, BuildWormHoleAction event) {
        super(source);
        this.event = event;
    }

    public BuildWormHoleAction getMyObject() {
        return event;
    }
}
