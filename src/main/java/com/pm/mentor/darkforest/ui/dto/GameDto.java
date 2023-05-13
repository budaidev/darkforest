package com.pm.mentor.darkforest.ui.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.loxon.javachallenge.challenge.game.event.EventType;
import com.loxon.javachallenge.challenge.game.event.actioneffect.ActionEffect;
import com.loxon.javachallenge.challenge.game.model.Player;
import com.loxon.javachallenge.challenge.game.model.WormHole;
import com.pm.mentor.darkforest.ai.model.AIPlanet;

import lombok.Data;;

@Data
public class GameDto {

    private String connectionStatus = "Not connected";

    private List<Player> players = new ArrayList<>();

    private EventType eventType;

    private ActionEffect actionEffect;

    private long width;
    /**
     * Az univerzum magassága 2D-ben.
     */
    private long height;
    /**
     * Tárolja az univerzumban található bolygókat.
     */
    private List<AIPlanet> planets = new ArrayList<AIPlanet>();
    /**
     * Tárolja az univerumban található féreglyukakat.
     */
    @JsonIgnore
    private List<WormHole> wormHoles = new ArrayList<WormHole>();

    /**
     * Űrmisszió/féreglyuk építés/MBH becsapódás forrásának az irányának a pontossága.
     */
    private int gravityWaveSourceLocationPrecision;

    /**
     * Becsapódott MBH lövés irányának pontossága.
     */
    private int mbhShootOriginPrecision;

    /**
     * Passzivitásból eredő forrás irányának pontossága.
     */
    private int passivityFleshPrecision;
}
