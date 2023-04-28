package com.pm.mentor.darkforest.service;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.loxon.javachallenge.challenge.game.rest.GameConfig;
import com.loxon.javachallenge.challenge.game.rest.GameCreated;
import com.loxon.javachallenge.challenge.game.rest.GameKey;

import lombok.SneakyThrows;

@Component
public class JsonSerializationService {
	private final JsonMapper mapper = new JsonMapper();
	
	@SneakyThrows
	public GameKey readGameKey(String payload) {
		return mapper.readValue(payload, GameKey.class);
	}
	
	@SneakyThrows
	public String writeGameKey(GameKey gameKey) {
		return mapper.writeValueAsString(gameKey);
	}
	
	@SneakyThrows
	public GameCreated readGameCreated(String payload) {
		return mapper.readValue(payload, GameCreated.class);
	}
	
	@SneakyThrows
	public String writeGameConfig(GameConfig gameConfig) {
		return mapper.writeValueAsString(gameConfig);
	}
}
