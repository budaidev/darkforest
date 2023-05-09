package com.pm.mentor.darkforest.ai.manual.actionevents;

import com.loxon.javachallenge.challenge.game.event.action.ShootMBHAction;
import org.springframework.context.ApplicationEvent;

public class ShootMBHEvent extends ApplicationEvent {

    private final ShootMBHAction event;

    public ShootMBHEvent(Object source, ShootMBHAction event) {
        super(source);
        this.event = event;
    }

    public ShootMBHAction getMyObject() {
        return event;
    }
}
