package com.loxon.javachallenge.challenge.game.rest;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Tárolja a játék konfigurációját.
 */
@Data
public class GameConfig {
    /**
     * A játék típusa.
     */
    @ApiModelProperty(value="Type of the game.")
    private GameType gameType;
    /**
     * A játékban résztvevő bot definíciók ID-jai. Ugyanaz a BOT definíció kétszer is szerepelhet, 
     * ez esetben két azonos BOT fog szerepelni az ellenfelek között.
     */
    @ApiModelProperty(value="List of available bot definition ids.")
    private List<Integer> bots;
}
