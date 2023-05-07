package com.pm.mentor.darkforest.ai.model;

import java.util.List;

import com.loxon.javachallenge.challenge.game.model.Game;
import com.loxon.javachallenge.challenge.game.model.Planet;
import com.loxon.javachallenge.challenge.game.settings.GameSettings;

import lombok.Getter;

@Getter
public class GameState {

	private final int playerId;
	private final GameSettings settings;
	private final List<Planet> planets;
	
	public GameState(Game game, int playerId) {
		this.playerId = playerId;
		settings = game.getSettings();
		planets = game.getWorld().getPlanets();
	}
}
