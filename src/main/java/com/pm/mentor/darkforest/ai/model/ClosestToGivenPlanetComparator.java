package com.pm.mentor.darkforest.ai.model;

import java.util.Comparator;

import lombok.val;

public class ClosestToGivenPlanetComparator implements Comparator<AIPlanet> {
	
	private final AIPlanet target;
	
	public ClosestToGivenPlanetComparator(AIPlanet target) {
		this.target = target;
	}

	@Override
	public int compare(AIPlanet lhs, AIPlanet rhs) {
		val leftDistance = (int)target.getPos().distance(lhs.getPos());
		val rightDistance = (int)target.getPos().distance(rhs.getPos());
		
		return leftDistance - rightDistance;
	}

}
