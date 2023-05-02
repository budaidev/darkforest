package com.loxon.javachallenge.challenge.game.rest;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Tárolja az újonnan létrehozott játékok adatait.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameCreated {
    /**
     * A létrehozott játék azonosítója.
     */
    @ApiModelProperty(value="Id of the created game.")
    private String gameId;
    /**
     * A létrehozott játékhoz tartozó konfigurációk.
     */
    @ApiModelProperty(value="Configuration object of the game.")
    private GameConfig gameConfig;
}
