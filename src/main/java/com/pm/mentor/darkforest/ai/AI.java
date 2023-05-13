package com.pm.mentor.darkforest.ai;

import com.loxon.javachallenge.challenge.game.event.GameEvent;
import com.pm.mentor.darkforest.ai.model.GameState;

public interface AI {

	void init(GameState state);
	
	void receiveEvent(GameEvent event);

	void heartBeat();
	
	boolean isRunning();

	void stop();
}
