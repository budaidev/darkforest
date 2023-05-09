package com.pm.mentor.darkforest.ai.manual.actionevents;

import com.loxon.javachallenge.challenge.game.event.action.ErectShieldAction;
import com.loxon.javachallenge.challenge.game.event.action.SpaceMissionAction;
import org.springframework.context.ApplicationEvent;

public class ErectShieldEvent extends ApplicationEvent {

    private final ErectShieldAction event;

    public ErectShieldEvent(Object source, ErectShieldAction event) {
        super(source);
        this.event = event;
    }

    public ErectShieldAction getMyObject() {
        return event;
    }
}
