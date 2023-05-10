package com.pm.mentor.darkforest.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.loxon.javachallenge.challenge.game.event.GameEvent;
import com.pm.mentor.darkforest.ai.AI;
import com.pm.mentor.darkforest.ai.AIRunner;
import com.pm.mentor.darkforest.ai.GameActionApi;
import com.pm.mentor.darkforest.ai.SampleAI;
import com.pm.mentor.darkforest.ai.manual.ManualAI;

@Component
public class AIContainer {
	
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private AIRunner runner;
	private ScheduledFuture<?> scheduledTask;
	
	private GameActionApi gameActionApi;

	private AI ai;

	public AIContainer(ManualAI ai) {
		this.ai = ai;
	}
	
	public void create() {
		if (scheduledTask != null && !scheduledTask.isDone()) {
			scheduledTask.cancel(true);
		}

		runner = new AIRunner();
		ai = new SampleAI(gameActionApi);
		// ai.init(gameActionApi);
		runner.init(ai);
		
		scheduledTask = scheduler.scheduleAtFixedRate(runner, 0, 1000, TimeUnit.MILLISECONDS);
	}
	
	public void receiveGameEvent(GameEvent event) {
		runner.receiveEvent(event);
		scheduler.execute(runner);
	}

	public void attachGameActionApi(GameActionApi api) {
		gameActionApi = api;
	}

	public boolean isCreated() {
		return runner != null;
	}
	
	public void shutdown() {
		if (scheduledTask != null && !scheduledTask.isDone()) {
			scheduledTask.cancel(true);
		}
		
		try {
			scheduler.shutdown();
			scheduler.awaitTermination(2, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
