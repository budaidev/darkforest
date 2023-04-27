package com.loxon.javachallenge.challenge.game.rest;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Egy BOT definíciót leíró osztály.
 */
@Data
public class BotDefinition {
    /**
     * A bot definíció azonosítója.
     */
    @ApiModelProperty(value="Identifier of the bot definition.")
    private int id;
    /**
     * A bot definíció neve.
     */
    @ApiModelProperty(value="Name of bot definition.")
    private String name;
    /**
     * A bot definíció leírása.
     */
    private String description;

    /**
     * A bot tulajdonságot létrehozó paraméter nélküli konstruktora.
     */
    public BotDefinition() {
    }

    /**
     * A bot tulajdonságot létrehozó teljesen felparaméterezett konstruktora.
     */
    public BotDefinition(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
