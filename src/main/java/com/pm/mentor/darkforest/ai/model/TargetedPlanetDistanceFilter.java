package com.pm.mentor.darkforest.ai.model;

import java.util.function.Predicate;
import lombok.val;

public class TargetedPlanetDistanceFilter implements Predicate<PlanetDistance> {

	private final GameState gameState;

	public TargetedPlanetDistanceFilter(GameState gameState) {
		this.gameState = gameState;
	}
	
	@Override
	public boolean test(PlanetDistance planet) {
		val initiatedActionExists = gameState.getInitiatedActions().values()
			.stream()
			.anyMatch(action -> action.getTargetId() == planet.getPlanet().getId());
		
		val activeActionExists = gameState.getActiveActions().values()
			.stream()
			.anyMatch(actionResponse -> actionResponse.getAction().getTargetId() == planet.getPlanet().getId());
		
		return !(initiatedActionExists || activeActionExists);
	}
}
