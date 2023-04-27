package com.loxon.javachallenge.challenge.game.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * A gravitációs hullám kibocsátásának 4 oka lehet, amelyek itt tárolódnak.
 */
public enum GravityWaveCause {
	/**
	 * MBH becsapódás okozta robbanással kiváltott gravitációs hullám jelzője.
	 */
	EXPLOSION("E"),
	/**
	 * Sikeres űrmisszióval kiváltott gravitációs hullám jelzője.
	 */
	SPACE_MISSION("SM"),
	/**
	 * Éppen megépített Féreglyuk által kiváltott gravitációs hullám jelzője.
	 */
	WORMHOLE("W"),
	/**
	 * Passzivitással kiváltott gravitációs hullám jelzője.
	 */
	PASSIVITY("P");

	/**
	 * Ezeknek a folyamatoknak a rövidítése, hogy gyorsabban haladjanak a hálózaton keresztül.
	 */
	private String shortName;

	/**
	 * @param shortName rövidítés
	 */
	GravityWaveCause(String shortName) {
		this.shortName = shortName;
	}

	/**
	 * Ezzel a függvénnyel kinyerhető a kiváltó akció rövidített neve külső osztályok számára.
	 *
	 * @return a rövidített név
	 */
	@JsonValue
	public String shortName() {
		return shortName;
	}
}
