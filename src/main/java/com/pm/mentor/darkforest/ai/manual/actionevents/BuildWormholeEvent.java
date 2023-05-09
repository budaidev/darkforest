package com.pm.mentor.darkforest.ai.manual.actionevents;

import com.loxon.javachallenge.challenge.game.event.action.BuildWormHoleAction;
import com.loxon.javachallenge.challenge.game.event.action.SpaceMissionAction;
import org.springframework.context.ApplicationEvent;

public class BuildWormholeEvent extends ApplicationEvent {

    private final BuildWormHoleAction event;

    public BuildWormholeEvent(Object source, BuildWormHoleAction event) {
        super(source);
        this.event = event;
    }

    public BuildWormHoleAction getMyObject() {
        return event;
    }
}
