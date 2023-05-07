package com.pm.mentor.darkforest.ai;

import com.loxon.javachallenge.challenge.game.event.GameEvent;
import com.loxon.javachallenge.challenge.game.model.Game;

public interface AI {

	void init(Game game, int playerId);
	
	void receiveEvent(GameEvent event);

	void heartBeat();
	
	boolean isRunning();
}
