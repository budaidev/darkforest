package com.loxon.javachallenge.challenge.game.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * MapObjectekről tárol információkat. Belőle származnak le a bolygó és a féreglyuk adatait tároló osztályok.
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(
		  use = JsonTypeInfo.Id.NAME, 
		  include = JsonTypeInfo.As.PROPERTY, 
		  property = "type")
		@JsonSubTypes({
		  @Type(value = WormHole.class, name = "WormHole"),
		  @Type(value = Planet.class, name = "Planet"),
		})
public class MapObject {
	/**
	 * Egyedi azonosító
	 */
	private int id;
	/**
	 * Az objektum vízszintes tengely szerinti koordinátája. WormHole esetén az A pont x kooridátája.
	 */
	private long x;
	/**
	 * Az objektum függőleges tengely szerinti koordinátája. WormHole esetén az A pont y kooridátája.
	 */
	private long y;
	/**
	 * Az objektumot birtokló csapat azonosítója (id). 0 ha gazdátlan az objektum.
	 */
	private int player;

	/**
	 * Kiszámolja az objektum távolságát egy adott másik objektumtól.
	 * Ha az objetum egy Féreglyuk, akkor az A végétől vett a távolság.
	 *
	 * @param mapObject másik objektum
	 * @return távolság
	 */
	public double distance(MapObject mapObject) {
		double px = mapObject.getX() - this.getX();
		double py = mapObject.getY() - this.getY();
		return Math.sqrt(px * px + py * py);
	}
}
