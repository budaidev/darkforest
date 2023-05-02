package com.loxon.javachallenge.challenge.game.event;

/**
 * A játék során bekövetkezett események lehetséges típusait felsoroló enum.
 */
public enum EventType {

	/**
	 * Valamilyen akció történt (pl: űrmisszió)
	 */
	ACTION,

	/**
	 * Akció hatására valamilyen esemény bekövetkezése (pl: GravityWave indult)
	 */
	ACTION_EFFECT,

	/**
	 * Valamilyen attribútum megváltozott a játékon belül.
	 */
	ATTRIBUTE_CHANGE,

	/**
	 * Játék vége.
	 */
	GAME_ENDED,

	/**
	 * Elindult egy játék.
	 */
	GAME_STARTED,

	/**
	 * Kapcsolódás történt a játékhoz.
	 */
	CONNECTION_RESULT;
}
