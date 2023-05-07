package com.loxon.javachallenge.challenge.game.model;

import java.util.ArrayList;
import java.util.List;

import com.loxon.javachallenge.challenge.game.rest.GameType;
import com.loxon.javachallenge.challenge.game.settings.GameSettings;

import lombok.Builder;
import lombok.Data;

/**
 * A játékmechanikához szükséges mezőket tárolja.
 */
@Data
@Builder
public class Game {
    /**
     * A játékhoz tartozó egyedi azonosító.
     */
    private String gameId;
    /**
     * Tárolja a játékban résztvevő játékosok listáját.
     */
    private List<Player> players = new ArrayList<>();
    /**
     * Tárolja a vizualizáció szempontjából releváns kliens csatlakozásokat.
     */
    private List<Player> spectators = new ArrayList<>();
    /**
     * Tárolja a generált világnak az adatait.
     */
    private World world;
    /**
     * Tárolja a játék státuszát.
     */
    private GameStatus status;
    /**
     * Tárolja a játék beállításait.
     */
    private GameSettings settings;
    /**
     * Tárolja a játék típusát.
     */
    private GameType type;
}
