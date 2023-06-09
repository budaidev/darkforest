package com.pm.mentor.darkforest.ai.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.loxon.javachallenge.challenge.game.model.WormHole;
import com.pm.mentor.darkforest.util.Point;
import com.pm.mentor.darkforest.util.PointToPointDistanceCache;

import lombok.val;

public class ClosestToPlayerPlanetsWithWormholeComparator implements Comparator<AIPlanet> {

	private final List<AIPlanet> playerPlanets;
	private final List<WormHole> wormholes;

	public ClosestToPlayerPlanetsWithWormholeComparator(List<AIPlanet> playerPlanets, List<WormHole> wormholes) {
		this.playerPlanets = new ArrayList<>(playerPlanets);
		this.wormholes = wormholes;
		setVirtualPlanets();
	}

	public void setVirtualPlanets() {
		for (WormHole wormhole : wormholes) {
			AIPlanet virtualPlanet = AIPlanet.createVirtualPlanetFromWormHole(new Point(wormhole.getX(), wormhole.getY()));
			AIPlanet virtualPlanet2 = AIPlanet.createVirtualPlanetFromWormHole(new Point(wormhole.getXb(), wormhole.getYb()));
			playerPlanets.add(virtualPlanet);
			playerPlanets.add(virtualPlanet2);
		}
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
