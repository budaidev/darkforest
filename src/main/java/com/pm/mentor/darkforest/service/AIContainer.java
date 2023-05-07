package com.pm.mentor.darkforest.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.loxon.javachallenge.challenge.game.event.GameEvent;
import com.pm.mentor.darkforest.ai.AIRunner;
import com.pm.mentor.darkforest.ai.GameActionApi;
import com.pm.mentor.darkforest.ai.SampleAI;

@Component
public class AIContainer {
	
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private AIRunner runner;
	
	private GameActionApi gameActionApi;
	
	public void create() {
		runner = new AIRunner();
		runner.init(new SampleAI(gameActionApi));
		
		scheduler.scheduleAtFixedRate(runner, 0, 50, TimeUnit.MILLISECONDS);
	}
	
	public void receiveGameEvent(GameEvent event) {
		runner.receiveEvent(event);
		scheduler.execute(runner);
	}

	public void attachGameActionApi(GameActionApi api) {
		gameActionApi = api;
	}
	
	public void shutDown() {
		try {
			scheduler.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
