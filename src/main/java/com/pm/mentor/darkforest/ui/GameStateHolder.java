package com.pm.mentor.darkforest.ui;

import com.pm.mentor.darkforest.ui.dto.GameDto;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class GameStateHolder {

    private GameDto myObject;
    private final ApplicationEventPublisher eventPublisher;

    public GameStateHolder(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        this.myObject = new GameDto();
    }

    public GameDto getMyObject() {
        return myObject;
    }

    public void  setMyObject(GameDto myObject) {
        this.myObject = myObject;
        eventPublisher.publishEvent(new GameStateChangeEvent(this, myObject));
    }

    public void setConnectionStatus(String connectionStatus) {
        this.myObject.setConnectionStatus(connectionStatus);
        eventPublisher.publishEvent(new GameStateChangeEvent(this, myObject));
    }
}
