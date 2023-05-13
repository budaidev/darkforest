package com.pm.mentor.darkforest.ai.model;

import java.util.function.Predicate;

import lombok.val;

public class TargetedPlanetFilter implements Predicate<AIPlanet> {

	private final GameState gameState;
	
	public TargetedPlanetFilter(GameState gameState) {
		this.gameState = gameState;
	}
	
	@Override
	public boolean test(AIPlanet planet) {
		val initiatedActionExists = gameState.getInitiatedActions().values()
			.stream()
			.anyMatch(action -> action.getTargetId() == planet.getId());
		
		val activeActionExists = gameState.getActiveActions().values()
			.stream()
			.anyMatch(actionResponse -> actionResponse.getAction().getTargetId() == planet.getId());
		
		return !(initiatedActionExists || activeActionExists);
	}
}
