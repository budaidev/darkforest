package com.loxon.javachallenge.challenge.game.rest;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * A játékkulcs kapcsán alapvető információkat tároló osztály.
 */
@Data
public class GameKey {
    /**
     * Http-státuszkód mezője a http hívás sikerességének jelzésére.
     */
    @ApiModelProperty(value="HTTP status code to check the http call's status.")
    private int httpStatusCode;
    /**
     * A konkrét kulcs.
     */
    @ApiModelProperty(value="Value of the game key.")
    private String key;
    /**
     * GameKey generáláskor kapott üzenet.
     */
    @ApiModelProperty(value="Message received during game key generation.")
    private String message;
}
