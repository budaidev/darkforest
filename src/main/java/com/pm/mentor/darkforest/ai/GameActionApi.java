package com.pm.mentor.darkforest.ai;

import com.loxon.javachallenge.challenge.game.event.action.EntryPointIndex;

public interface GameActionApi {
	int spaceMission(int sourcePlanet, int targetPlanet);
	int spaceMissionWithWormHole(int sourcePlanet, int targetPlanet, int wormHole, EntryPointIndex wormHoleSide);
	int buildWormHole(int xa, int ya, int xb, int yb);
	int erectShield(int targetPlanet);
	int shootMBH(int sourcePlanet, int targetPlanet);
}
