package com.loxon.javachallenge.challenge.game.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;

/**
 * Ez tárolja a világot, vagyis azt az univerzumot, amelyben a játék zajlik.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class World {
    /**
     * Az univerzum szélessége 2D-ben.
     */
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
    @JsonIgnore private List<WormHole> wormHoles = new ArrayList<WormHole>();
}
