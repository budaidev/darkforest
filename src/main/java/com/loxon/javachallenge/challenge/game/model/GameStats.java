package com.loxon.javachallenge.challenge.game.model;

import lombok.Data;

/**
 * Statisztikai szempontból alapvető osztály, amelynek relevanciája az eredményhírdetéskor mutatkozik meg.
 */
@Data
public class GameStats {
	/**
	 * A játékot azonosítja, amiről a statisztika készült.
	 */
	private String gameId;
	/**
	 * A győztes csapat neve.
	 */
	private String winnerTeam;
	/**
	 * A győztes csapathoz tartozó pontszám.
	 */
	private int winnerScore;
	/**
	 * A bolygók száma a játékmezőn.
	 */
	private int numAllPlanets;
	/**
	 * A lakható bolygók száma a játék végén.
	 */
	private int numMPlanets;
	/**
	 * Az elpusztított bolygók száma a játék végén.
	 */
	private int numDestroyedPlanets;
	/**
	 * A kivédett MBH-k száma a játék végén.
	 */
	private int numDeflectedMBH;
}
