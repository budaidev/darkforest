package com.loxon.javachallenge.challenge.game.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class WormHole extends MapObject {
	/**
	 * A Féreglyuk B végének vízszintes tengely szerinti koordinátája
	 */
	private long xb;
	/**
	 * A Féreglyuk B végének függőleges tengely szerinti koordinátája
	 */
	private long yb;

	/**
	 * Kiszámolja egy adott objektum távolságát a féreglyuk B végétől
	 *
	 * @param mapObject az adott objektum
	 * @return távolság
	 */
	public double distanceFromOutput(MapObject mapObject) {
		double px = mapObject.getX() - this.getXb();
		double py = mapObject.getY() - this.getYb();
		return Math.sqrt(px * px + py * py);
	}
}
