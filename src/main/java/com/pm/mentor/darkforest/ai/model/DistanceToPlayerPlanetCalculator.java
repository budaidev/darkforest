package com.pm.mentor.darkforest.ai.model;

import com.loxon.javachallenge.challenge.game.model.WormHole;
import com.pm.mentor.darkforest.util.Point;
import com.pm.mentor.darkforest.util.PointToPointDistanceCache;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DistanceToPlayerPlanetCalculator {

	private final List<AIPlanet> playerPlanets;
	private final List<WormHole> wormholes;

	public DistanceToPlayerPlanetCalculator(List<AIPlanet> playerPlanets) {
		this.playerPlanets = new ArrayList<>(playerPlanets);
		this.wormholes = new ArrayList<>();
	}

	public DistanceToPlayerPlanetCalculator(List<AIPlanet> playerPlanets, List<WormHole> wormholes) {
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

	public Map<AIPlanet, Double> calculateDistances(List<AIPlanet> planets) {
		Map<AIPlanet, Double> distances = new HashMap<>();
		for (AIPlanet planet : planets) {
			playerPlanets.stream().mapToDouble(
					p -> PointToPointDistanceCache.distance(p.getPos(), planet.getPos())
			).min().ifPresent(minDistance ->
					distances.put(planet, minDistance)
			);
		}
		return distances;
	}

}
