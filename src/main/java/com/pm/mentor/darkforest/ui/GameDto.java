package com.pm.mentor.darkforest.ui;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.loxon.javachallenge.challenge.game.model.Planet;
import com.loxon.javachallenge.challenge.game.model.WormHole;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class GameDto {

    private long width;
    /**
     * Az univerzum magassága 2D-ben.
     */
    private long height;
    /**
     * Tárolja az univerzumban található bolygókat.
     */
    private List<Planet> planets = new ArrayList<Planet>();
    /**
     * Tárolja az univerumban található féreglyukakat.
     */
    @JsonIgnore
    private List<WormHole> wormHoles = new ArrayList<WormHole>();
}
