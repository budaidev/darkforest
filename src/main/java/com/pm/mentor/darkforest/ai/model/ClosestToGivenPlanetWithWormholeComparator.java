package com.pm.mentor.darkforest.ai.model;

import java.util.Comparator;

import com.loxon.javachallenge.challenge.game.model.WormHole;
import com.pm.mentor.darkforest.util.Point;
import com.pm.mentor.darkforest.util.PointToPointDistanceCache;

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
		
		double d1 = PointToPointDistanceCache.distance(target.getPos(), wormhole.getX(), wormhole.getY());
		double d2 = PointToPointDistanceCache.distance(target.getPos(), wormhole.getXb(), wormhole.getYb());

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
		val leftDistance = (int)PointToPointDistanceCache.distance(target.getPos(), lhs.getPos());
		val leftDistanceWithWormhole = (int)(PointToPointDistanceCache.distance(further, lhs.getPos()) + closerDistance);
		val rightDistance = (int)PointToPointDistanceCache.distance(target.getPos(), rhs.getPos());
		val rightDistanceWithWormhole = (int)(PointToPointDistanceCache.distance(further, rhs.getPos()) + closerDistance);

		return Math.min(leftDistance, leftDistanceWithWormhole) - Math.min(rightDistance,rightDistanceWithWormhole);
	}

}
