package com.loxon.javachallenge.challenge.game.model;

/**
 * A játéknak 3 lehetséges állapota van, amelyeket itt tárolunk.
 * Ezek az állapotok befolyásolják a játékmechaniát.
 */
public enum GameStatus {
    /**
     * Kezdeti állapot, a játék ebben az állapotban marad az indításig vagy a törlésig.
     */
    CREATED,

    /**
     * A játék futás közben lévő állapota.
     * Kizárólag ebben az állapotban képes a játék Action-t fogadni.
     */
    PLAYING,

    /**
     * A játék futást követő állapota.
     * Ez jelzi a játék végét.
     */
    ENDED;
}
