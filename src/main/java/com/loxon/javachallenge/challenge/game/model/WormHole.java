package com.loxon.javachallenge.challenge.game.model;

import com.pm.mentor.darkforest.util.Point;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper=true)
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
		double px = mapObject.getX() - xb;
		double py = mapObject.getY() - yb;
		return Math.sqrt(px * px + py * py);
	}

	public Point getPointA(){
		return new Point(getX(), getY());
	}

	public Point getPointB(){
		return new Point(getXb(), getYb());
	}
}
