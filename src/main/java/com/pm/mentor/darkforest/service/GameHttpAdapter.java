package com.pm.mentor.darkforest.service;

import com.loxon.javachallenge.challenge.game.rest.BotDefinition;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.loxon.javachallenge.challenge.game.rest.GameConfig;
import com.loxon.javachallenge.challenge.game.rest.GameCreated;
import com.loxon.javachallenge.challenge.game.rest.GameKey;
import com.pm.mentor.darkforest.config.ClientConfiguration;
import com.pm.mentor.darkforest.util.Signer;

import lombok.SneakyThrows;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GameHttpAdapter {

	private final String RootUrl;

	private final JsonSerializationService serializationService;

	private final ClientConfiguration clientConfiguration;

	private final Signer signer;

	public GameHttpAdapter(JsonSerializationService serializationService,
						   ClientConfiguration clientConfiguration,
						   Signer signer) {
		this.serializationService = serializationService;
		this.clientConfiguration = clientConfiguration;
		this.signer = signer;
		this.RootUrl = String.format("http://%s:%s", clientConfiguration.getUrl(), clientConfiguration.getPort());
	}

	public GameKey getGameKey() {
		val sign = signer.sign();

		val url = RootUrl + "/game_api/getGameKey"
            + "?email=" + URLEncoder.encode(clientConfiguration.getEmail(), StandardCharsets.UTF_8)
            + "&team=" + URLEncoder.encode(clientConfiguration.getTeam(), StandardCharsets.UTF_8)
            + "&ts=" + sign.getTimeStamp()
            + "&signature=" + sign.getSignature();

		val response = httpGet(url);

		val gameKey = serializationService.readGameKey(response);

		log.info(String.format("Game key fetched: %s", gameKey));

		return gameKey;
	}

	public GameCreated createGame(String gameKey, GameConfig gameConfig) {
		val url = RootUrl + "/game/create/" + gameKey;
		val configString = serializationService.writeGameConfig(gameConfig);

		val response = httpPost(url, Optional.of(configString));
		val gameCreated = serializationService.readGameCreated(response);

		log.info(String.format("Game created: %s", gameCreated));

		return gameCreated;
	}

	public String startGame(String gameId, String gameKey) {
		val url = String.format("%s/game/start/%s/%s", RootUrl, gameId, gameKey);

		val response = httpPost(url, Optional.empty());

		log.info("Game start result: " + response);

		return response;
	}

	public String stopGame(String gameId, String gameKey) {
		val url = String.format("%s/game/stop/%s/%s", RootUrl, gameId, gameKey);

		val response = httpPost(url, Optional.empty());

		log.info("Game stop result: " + response);

		return response;
	}

	public List<BotDefinition> getBots() {
		val url = String.format("%s/game/bots", RootUrl);

		val response = httpGet(url);

		val bots = serializationService.readBotDefinition(response);

		return bots;
	}

	@SneakyThrows
	private String httpGet(String url) {
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        return readResponse(connection);
	}

	@SneakyThrows
	private String httpPost(String url, Optional<String> payload) {
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        
        if (payload.isPresent()) {
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);
			try (PrintWriter writer = new PrintWriter(connection.getOutputStream())) {
				writer.print(payload.get());
			}
        }

        connection.connect();
        
        return readResponse(connection);
	}

	@SneakyThrows
	private String readResponse(HttpURLConnection connection) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			StringBuilder response = new StringBuilder();

			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}

			return response.toString();
        } catch (java.io.IOException e) {
        	log.warn("Dumping connection headers...");
        	
        	for (val headerField : connection.getHeaderFields().entrySet()) {
        		val headerName = headerField.getKey();
        		val headerValue = String.join(", ", headerField.getValue());
        		
        		log.warn(String.format("%s: %s", headerName, headerValue));
        	}

			InputStream errorStream = connection.getErrorStream();
			if (errorStream != null) {
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream))) {
					StringBuilder response = new StringBuilder();

					String line;
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}

					log.warn("Error response: " + response.toString());
					return response.toString();
				}
			}

        	
        	throw e;
        }
	}
}
