package com.loxon.javachallenge.challenge.game.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Tárol olyan információkat, amelyek elengedhetetlenek egy bolygó leírásához.
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
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
	
	@JsonIgnore
	public boolean hasShield() {
		return shieldErectedAt > 0;
	}

}