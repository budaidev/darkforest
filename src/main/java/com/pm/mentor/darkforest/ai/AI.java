package com.pm.mentor.darkforest.ai;

import com.loxon.javachallenge.challenge.game.event.GameEvent;

public interface AI {

	void init(GameActionApi gameActionApi);
	
	void receiveEvent(GameEvent event);

	void heartBeat();
}
