package com.pm.mentor.darkforest.ai;

import com.loxon.javachallenge.challenge.game.event.action.EntryPointIndex;
import com.loxon.javachallenge.challenge.game.event.action.GameAction;

public interface GameActionApi {
	GameAction spaceMission(int sourcePlanet, int targetPlanet);
	GameAction spaceMissionWithWormHole(int sourcePlanet, int targetPlanet, int wormHole, EntryPointIndex wormHoleSide);
	GameAction buildWormHole(long xa, long ya, long xb, long yb);
	GameAction erectShield(int targetPlanet);
	GameAction shootMBH(int sourcePlanet, int targetPlanet);
}
