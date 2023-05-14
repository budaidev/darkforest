package com.pm.mentor.darkforest.ai.model;

import com.loxon.javachallenge.challenge.game.model.WormHole;
import com.pm.mentor.darkforest.util.Point;
import java.util.Comparator;
import java.util.List;
import lombok.val;

public class ClosestToGivenPlanetWithWormholeComparator implements Comparator<AIPlanet> {
	
	private final AIPlanet target;
	private final List<WormHole> wormholes;

	public ClosestToGivenPlanetWithWormholeComparator(AIPlanet target, List<WormHole> wormholes) {
		this.target = target;
		this.wormholes = wormholes;
	}

	@Override
	public int compare(AIPlanet lhs, AIPlanet rhs) {
		val leftDistance = (int)target.getPos().distance(lhs.getPos());
		val rightDistance = (int)target.getPos().distance(rhs.getPos());
		
		return leftDistance - rightDistance;
	}

}
