package com.loxon.javachallenge.challenge.game.model;

import lombok.Data;

/**
 * Tárolja a játékoshoz (csapat) tartozó statisztikákat, metaadatokat.
 */
@Data
public class Player {

    /**
     * A játékos beazonosítására szolgáló egyedi azonosító.
     */
    private int id;
    /**
     * A csapat neve.
     */
    private String teamName;
    /**
     * Tárolja a játékos által újonnan kolonizált bolygók számát.
     */
    private int numOfColinizedPlanets;
    /**
     * Tárolja a játékos tulajdonában lévő, nem elpusztított bolygók számát.
     */
    private int numOfOwnedPlanets = 1;
    /**
     * Tárolja a játékos által elpusztított bolygók számát.
     */
    private int numOfDestroyedPlanets;
    /**
     * Tárolja a játékos által elpusztított ellenséges bolygók számát.
     */
    private int numOfDestroyedHostilePlanets;
    /**
     * Tárolja a játékos által elvesztett bolygók számát.
     */
    private int numOfLostPlanets;
    /**
     * Tárolja a játékos által épített féreglyukak számát.
     */
    private int numOfWormHoles;
    /**
     * Tárolja a játékos bolygóin felhúzott pajzsok által kivédett MBH-k számát.
     */
    private int numOfDeflectedMBH;
    /**
     * Tárolja a csapat színét meghatározó indexet.
     */
    private int colorIndex;
    /**
     * Tárolja a csapat aktuális pontszámát.
     */
    private int score;
    /**
     * Tárolja a csapat adott időpillanatban még felhasználható akcióinak számát.
     */
    private int numberOfRemainingActions;
    /**
     * Tárolja, hogy a csapat kihalt vagy sem.
     */
    private boolean extinct;
}
