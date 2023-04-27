package com.loxon.javachallenge.challenge.game.rest;

/**
 * Tárolja a játékok 3 típusát.
 */
public enum GameType {
    /**
     * Az egyjátékos módhoz tartozó enum.
     */
    SINGLE_PLAYER,
    /**
     * A többjátékos módhoz tartozó enum.
     */
    MULTI_PLAYER,
    /**
     * Kvalifikációhoz használt játékmódhoz tartozó enum.
     */
    QUALIFYING;
}
