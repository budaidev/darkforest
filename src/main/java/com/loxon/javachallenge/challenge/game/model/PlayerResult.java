package com.loxon.javachallenge.challenge.game.model;

import lombok.Data;

@Data
public class PlayerResult {
	private int playerId;
	private int score;
	private int numOfColinizedPlanets;
	private int numOfDestroyedHostilePlanets;
	private int numOfDeflectedMBH;
}
