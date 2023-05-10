package com.pm.mentor.darkforest.ai;

import com.pm.mentor.darkforest.ai.model.GameState;
import java.util.concurrent.LinkedBlockingQueue;

import com.loxon.javachallenge.challenge.game.event.ConnectionResultType;
import com.loxon.javachallenge.challenge.game.event.EventType;
import com.loxon.javachallenge.challenge.game.event.GameEvent;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AIRunner implements Runnable {
	
	private final LinkedBlockingQueue<GameEvent> commandQueue = new LinkedBlockingQueue<>();
	
	private AI aiImplementation;
	private long lastExecution = 0;

	@Override
	public void run() {
		val timeStarted = System.currentTimeMillis();
		
		var needHeartBeat = shouldSendHeartBeat();
		lastExecution = System.currentTimeMillis();
		
		while (!commandQueue.isEmpty()) {
			needHeartBeat = false;
			aiImplementation.receiveEvent(commandQueue.poll());
		}
		
		if (needHeartBeat) {
			aiImplementation.heartBeat();
		}
		
		if (aiImplementation.isRunning()) {
			val elapsed = System.currentTimeMillis() - timeStarted;
			
			if (elapsed > 20) {
				log.warn(String.format("AI logic execution took: %d ms", elapsed));
			} else {
				log.trace(String.format("AI logic execution took: %d ms", elapsed));
			}
		}
	}

	public void init(AI impl) {
		aiImplementation = impl;
	}

	public void startGame(GameState gameState) {
		aiImplementation.init(gameState);
	}

	public void receiveEvent(GameEvent event) {
		
		commandQueue.add(event);
	}
	
	private boolean shouldSendHeartBeat() {
		// send a heart beat to the AI if last execution was more than 45ms ago
		return System.currentTimeMillis() - lastExecution >= 45;
	}
}
