package com.pm.mentor.darkforest.ai.manual.actionevents;

import com.loxon.javachallenge.challenge.game.event.action.SpaceMissionAction;
import org.springframework.context.ApplicationEvent;

public class SpaceMissionEvent extends ApplicationEvent {

    private final SpaceMissionAction event;

    public SpaceMissionEvent(Object source, SpaceMissionAction event) {
        super(source);
        this.event = event;
    }

    public SpaceMissionAction getMyObject() {
        return event;
    }
}
