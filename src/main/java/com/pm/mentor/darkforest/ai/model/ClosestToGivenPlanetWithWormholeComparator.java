package com.pm.mentor.darkforest.ai.model;

import com.pm.mentor.darkforest.util.Point;
import java.sql.SQLOutput;
import java.util.Comparator;
import java.util.List;

import com.loxon.javachallenge.challenge.game.model.WormHole;

import lombok.val;

public class ClosestToGivenPlanetWithWormholeComparator implements Comparator<AIPlanet> {

	private final AIPlanet target;
	private final WormHole wormhole;
	private final Point closer;
	private final Point further;
	private final double closerDistance;


	public  ClosestToGivenPlanetWithWormholeComparator(AIPlanet target, WormHole wormhole) {
		this.target = target;
		this.wormhole = wormhole;
		double d1 = target.getPos().distance(wormhole.getX(), wormhole.getY());
		double d2 = target.getPos().distance(wormhole.getXb(), wormhole.getYb());

		if(d1 < d2){
			closer = new Point(wormhole.getX(), wormhole.getY());
			further = new Point(wormhole.getXb(), wormhole.getYb());
			closerDistance = d1;
		} else {
			closer = new Point(wormhole.getXb(), wormhole.getYb());
			further = new Point(wormhole.getX(), wormhole.getY());
			closerDistance = d2;
		}
	}

	@Override
	public int compare(AIPlanet lhs, AIPlanet rhs) {
		val leftDistance = (int)target.getPos().distance(lhs.getPos());
		val leftDistanceWithWormhole = (int)(further.distance(lhs.getPos()) + closerDistance);
		val rightDistance = (int)target.getPos().distance(rhs.getPos());
		val rightDistanceWithWormhole = (int)(further.distance(rhs.getPos()) + closerDistance);
		return Math.min(leftDistance,leftDistanceWithWormhole) - Math.min(rightDistance,rightDistanceWithWormhole);
	}

}
