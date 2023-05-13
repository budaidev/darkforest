package com.pm.mentor.darkforest.ui;

import com.loxon.javachallenge.challenge.game.model.WormHole;
import java.util.Collection;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.loxon.javachallenge.challenge.game.event.action.ActionResponse;
import com.loxon.javachallenge.challenge.game.event.action.GameAction;
import com.pm.mentor.darkforest.ai.model.AIPlanet;
import com.pm.mentor.darkforest.ui.dto.GameDto;

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

    public void setMyObject(GameDto myObject) {
        this.myObject = myObject;
        eventPublisher.publishEvent(new GameStateChangeEvent(this, myObject));
    }

    public void setConnectionStatus(String connectionStatus) {
        this.myObject.setConnectionStatus(connectionStatus);
        eventPublisher.publishEvent(new GameStateChangeEvent(this, myObject));
    }

    public void updatePlanetStatus(List<AIPlanet> aiplanets) {
        this.myObject.setPlanets(aiplanets);
        eventPublisher.publishEvent(new GameStateChangeEvent(this, myObject));
    }

    public void updateWormholes(List<WormHole> wormholes) {
        this.myObject.setWormHoles(wormholes);
        eventPublisher.publishEvent(new GameStateChangeEvent(this, myObject));
    }
    
    public void setActions(Collection<GameAction> initiated, Collection<ActionResponse> active) {
    	this.myObject.setInitiatedActions(initiated.stream().toList());
    	this.myObject.setActiveActions(active.stream().toList());
    	
    	eventPublisher.publishEvent(new GameStateChangeEvent(this, myObject));
    }
}
