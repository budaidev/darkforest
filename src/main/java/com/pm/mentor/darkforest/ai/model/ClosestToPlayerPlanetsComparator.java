package com.pm.mentor.darkforest.ai.model;

import java.util.Comparator;
import java.util.List;

import com.pm.mentor.darkforest.util.PointToPointDistanceCache;

import lombok.val;

public class ClosestToPlayerPlanetsComparator implements Comparator<AIPlanet> {
	
	private final List<AIPlanet> playerPlanets;
	
	public ClosestToPlayerPlanetsComparator(List<AIPlanet> playerPlanets) {
		this.playerPlanets = playerPlanets;
	}

	@Override
	public int compare(AIPlanet lhs, AIPlanet rhs) {
		// calculate distance to closest player planet
		val leftDistance = playerPlanets.stream()
			.mapToInt(p -> (int)PointToPointDistanceCache.distance(p.getPos(), lhs.getPos()))
			.min();
		
		val rightDistance = playerPlanets.stream()
			.mapToInt(p -> (int)PointToPointDistanceCache.distance(p.getPos(), rhs.getPos()))
			.min();
		
		if (leftDistance.isPresent() && rightDistance.isPresent()) {
			return leftDistance.getAsInt() - rightDistance.getAsInt();
		}
		
		return 0;
	}

}
