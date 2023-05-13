package com.pm.mentor.darkforest.ui;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.pm.mentor.darkforest.ai.model.AIPlanet;
import com.pm.mentor.darkforest.ui.dto.GameDto;

import lombok.val;

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

    public void updatePlanetStatus(List<AIPlanet> aiplanets) {
    	val planets = aiplanets.stream()
    			.map(p -> p.getOriginalPlanet())
    			.collect(Collectors.toList());
    	
        this.myObject.setPlanets(planets);
        eventPublisher.publishEvent(new GameStateChangeEvent(this, myObject));
    }
}
