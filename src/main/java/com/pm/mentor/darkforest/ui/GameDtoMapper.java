package com.pm.mentor.darkforest.ui;

import com.loxon.javachallenge.challenge.game.event.GameEvent;
import com.pm.mentor.darkforest.ui.dto.GameDto;

public class GameDtoMapper {

    public static GameDto toGameDto(GameEvent gameEvent){
        GameDto gameDto = new GameDto();
        if(gameEvent.getGame() != null) {
            gameDto.setPlanets(gameEvent.getGame().getWorld().getPlanets());
            gameDto.setWormHoles(gameEvent.getGame().getWorld().getWormHoles());
            gameDto.setWidth(gameEvent.getGame().getWorld().getWidth());
            gameDto.setHeight(gameEvent.getGame().getWorld().getHeight());
        }
        return gameDto;
    }
}
