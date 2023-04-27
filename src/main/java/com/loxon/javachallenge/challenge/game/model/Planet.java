package com.loxon.javachallenge.challenge.game.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Tárol olyan információkat, amelyek elengedhetetlenek egy bolygó leírásához.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Planet extends MapObject {
	/**
	 * Egy bináris mező annak mérlegelésére, hogy az égitest lakható vagy sem.
	 */
	private boolean classM;
	/**
	 * Egy bináris mező annak mérlegelésére, hogy az égitest megsemmisült vagy sem.
	 */
	private boolean destroyed;
	/**
	 * A pajzs aktiválási időpontjának tárolására szolgáló milliszekundumos érték.
	 */
	private long shieldErectedAt; // EPOCH time in milliseconds. E.g.: System.currentTimeInMillis();
	/**
	 * A pajzs lejárati időpontjának tárolására szolgáló milliszekundumos érték.
	 */
	private long shieldRemovedAt; // EPOCH time in milliseconds. E.g.: System.currentTimeInMillis();

}