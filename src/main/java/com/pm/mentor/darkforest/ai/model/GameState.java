package com.pm.mentor.darkforest.ai.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.loxon.javachallenge.challenge.game.model.Game;
import com.loxon.javachallenge.challenge.game.model.Planet;
import com.loxon.javachallenge.challenge.game.settings.GameSettings;

import lombok.Getter;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class GameState {

	private final int playerId;
	private final GameSettings settings;
	private final List<Planet> planets;
	private final List<Planet> destroyedPlayerPlanets = new ArrayList<>();
	
	public GameState(Game game, int playerId) {
		this.playerId = playerId;
		settings = game.getSettings();
		planets = game.getWorld().getPlanets();
	}
	
	public int getMaxConcurrentActionCount() {
		return settings.getMaxConcurrentActions();
	}
	
	@Synchronized
	public List<Planet> getPlayerPlanets() {
		return planets.stream()
			.filter(p -> p.getPlayer() == playerId)
			.collect(Collectors.toList());
	}
	
	@Synchronized
	public List<Planet> getUnknownPlanets() {
		return planets.stream()
			.filter(p -> p.getPlayer() == 0 && p.isDestroyed() == false)
			.collect(Collectors.toList());
	}
	
	@Synchronized
	public List<Planet> getUnknownPlanets(Comparator<? super Planet> comparator) {
		return planets.stream()
			.filter(p -> p.getPlayer() == 0 && p.isDestroyed() == false)
			.sorted(comparator)
			.collect(Collectors.toList());
	}

	@Synchronized
	public void spaceMissionSuccessful(int affectedMapObjectId) {
		tryFindPlayerPlanet(affectedMapObjectId)
			.ifPresent(p -> {
				p.setClassM(true);
				p.setPlayer(playerId);
			});
	}

	@Synchronized
	public void spaceMissionFailed(int affectedMapObjectId) {
		tryFindPlayerPlanet(affectedMapObjectId)
			.ifPresent(p -> {
				// mark the planet as destroyed and inhabitable, however it can still be owned by an other player!
				p.setClassM(false);
				p.setDestroyed(true);
			});
	}

	@Synchronized
	public void planetDestroyed(int affectedId) {
		tryFindPlayerPlanet(affectedId)
			.ifPresent(p -> {
				log.trace(String.format("Player planet %d destroyed.", affectedId));
				p.setClassM(false);
				p.setDestroyed(true);
				planets.remove(p);
				destroyedPlayerPlanets.add(p);
			});
	}
	
	@Synchronized
	public Optional<Planet> tryFindPlayerPlanet(int planetId) {
		return planets.stream()
			.filter(p -> p.getId() == planetId)
			.findFirst();
	}
}
