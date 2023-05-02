package com.loxon.javachallenge.challenge.game.event.attibute;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Egy <code>Player</code>, vagy <code>Planet</code> változójával kapcsolatos változást nyilvántartó osztály.
 */
@Data
@NoArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
public class AttributeChange {

	/**
	 * A változó neve.
	 */
	private String name;

	/**
	 * A változó új értéke.
	 */
	private String value;

	/**
	 * Az <code>AttributeChange</code> osztály konstruktora.
	 * @param name	A változó neve
	 * @param value	A változó új értéke
	 */
	public AttributeChange( String name, String value ) {
		this.name = name;
		this.value = value;
	}
}
