package com.pm.mentor.darkforest.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.loxon.javachallenge.challenge.game.rest.BotDefinition;
import java.util.List;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.loxon.javachallenge.challenge.game.event.GameEvent;
import com.loxon.javachallenge.challenge.game.event.action.GameAction;
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
	public List<BotDefinition> readBotDefinition(String payload) {
		return mapper.readValue(payload, new TypeReference<>() {
		});
	}
	
	@SneakyThrows
	public String writeGameConfig(GameConfig gameConfig) {
		return mapper.writeValueAsString(gameConfig);
	}
	
	@SneakyThrows
	public GameEvent readGameEvent(String payload) {
		return mapper.readValue(payload, GameEvent.class);
	}

	@SneakyThrows
	public String writeGameAction(GameAction gameAction) {
		return mapper.writeValueAsString(gameAction);
	}
}
