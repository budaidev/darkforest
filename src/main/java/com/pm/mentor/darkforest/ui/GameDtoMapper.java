package com.pm.mentor.darkforest.ui;

import java.util.stream.Collectors;

import com.loxon.javachallenge.challenge.game.event.EventType;
import com.loxon.javachallenge.challenge.game.event.GameEvent;
import com.pm.mentor.darkforest.ai.model.AIPlanet;
import com.pm.mentor.darkforest.ui.dto.GameDto;

import lombok.val;

public class GameDtoMapper {

    public static GameDto toGameDto(GameEvent gameEvent){
        GameDto gameDto = new GameDto();
        gameDto.setEventType(gameEvent.getEventType());
        if(gameEvent.getEventType() == EventType.GAME_STARTED) {
            gameDto.setPlayers(gameEvent.getGame().getPlayers());
        }
        if(gameEvent.getEventType() == EventType.ACTION_EFFECT) {
            gameDto.setActionEffect(gameEvent.getActionEffect());
        }
        if(gameEvent.getGame() != null) {
        	val planets = gameEvent.getGame().getWorld().getPlanets()
        			.stream()
        			.map(p -> new AIPlanet(p))
        			.collect(Collectors.toList());

            gameDto.setPlanets(planets);
            gameDto.setWormHoles(gameEvent.getGame().getWorld().getWormHoles());
            gameDto.setWidth(gameEvent.getGame().getWorld().getWidth());
            gameDto.setHeight(gameEvent.getGame().getWorld().getHeight());
            gameDto.setMbhShootOriginPrecision(gameEvent.getGame().getSettings().getMbhShootOriginPrecision());
            gameDto.setPassivityFleshPrecision(gameEvent.getGame().getSettings().getPassivityFleshPrecision());
            gameDto.setGravityWaveSourceLocationPrecision(gameEvent.getGame().getSettings().getGravityWaveSourceLocationPrecision());
        }
        return gameDto;
    }
}
