package com.pm.mentor.darkforest.ai;

import java.util.concurrent.LinkedBlockingQueue;

import com.loxon.javachallenge.challenge.game.event.GameEvent;

public class AIRunner implements Runnable {
	
	private final LinkedBlockingQueue<GameEvent> commandQueue = new LinkedBlockingQueue<>();
	
	private AI aiImplementation;
	private long lastExecution = 0;

	@Override
	public void run() {
		var needHeartBeat = shouldSendHeartBeat();
		lastExecution = System.currentTimeMillis();
		
		while (!commandQueue.isEmpty()) {
			needHeartBeat = false;
			aiImplementation.receiveEvent(commandQueue.poll());
		}
		
		if (needHeartBeat) {
			aiImplementation.heartBeat();
		}
	}

	public void init(AI impl, GameActionApi gameActionApi) {
		aiImplementation = impl;
		aiImplementation.init(gameActionApi);
	}

	public void receiveEvent(GameEvent event) {
		commandQueue.add(event);
	}
	
	private boolean shouldSendHeartBeat() {
		// send a heart beat to the AI if last execution was more than 45ms ago
		return lastExecution - System.currentTimeMillis() >= 45;
	}
}
