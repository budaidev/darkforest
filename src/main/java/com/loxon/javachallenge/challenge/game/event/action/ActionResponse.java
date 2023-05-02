package com.loxon.javachallenge.challenge.game.event.action;

import lombok.Data;

/**
 * Ez az osztály az akciók hatásaira vonatkozó információkat tárolja.
 */
@Data
public class ActionResponse {
    /**
     * Akció eredményességét írja le és annak okát.
     */
    private ActionResult result;
    /**
     * Az érintett játékos ID-ja.
     */
    private int player;
    /**
     * Az akció kezdésének időpontja. A szerver órája szerinti, mili-szekundumokban kifejezett idő (EPOH time)
     */
    private long actionStartTime;
    /**
     * Az akció befelyezésének várható időpontja. A szerver órája szerinti, mili-szekundumokban kifejezett idő (EPOH time)
     */
    private long actionEndTime;
    /**
     * A kiváltó akció.
     */
    private GameAction action;
}
