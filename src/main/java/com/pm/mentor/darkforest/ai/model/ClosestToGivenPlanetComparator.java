package com.pm.mentor.darkforest.ai.model;

import java.util.Comparator;

import com.pm.mentor.darkforest.util.PointToPointDistanceCache;

import lombok.val;

public class ClosestToGivenPlanetComparator implements Comparator<AIPlanet> {
	
	private final AIPlanet target;
	
	public ClosestToGivenPlanetComparator(AIPlanet target) {
		this.target = target;
	}

	@Override
	public int compare(AIPlanet lhs, AIPlanet rhs) {
		val leftDistance = (int)PointToPointDistanceCache.distance(target.getPos(), lhs.getPos());
		val rightDistance = (int)PointToPointDistanceCache.distance(target.getPos(), rhs.getPos());
		
		return leftDistance - rightDistance;
	}

}
