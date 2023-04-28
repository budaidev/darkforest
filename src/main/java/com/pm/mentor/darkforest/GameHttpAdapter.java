package com.pm.mentor.darkforest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.loxon.javachallenge.challenge.game.rest.GameConfig;
import com.loxon.javachallenge.challenge.game.rest.GameCreated;
import com.loxon.javachallenge.challenge.game.rest.GameKey;

import lombok.SneakyThrows;
import lombok.val;

@Component
public class GameHttpAdapter {
	
	private final String RootUrl = "http://javachallenge.loxon.eu:8081";
	
	private final JsonSerializationService serializationService;
	
	public GameHttpAdapter(JsonSerializationService serializationService) {
		this.serializationService = serializationService;
	}
	
	public GameKey getGameKey() {
		val sign = Signer.sign();

		val url = RootUrl + "/game_api/getGameKey"
            + "?email=" + URLEncoder.encode(Signer.Email, StandardCharsets.UTF_8)
            + "&team=" + URLEncoder.encode(Signer.Team, StandardCharsets.UTF_8)
            + "&ts=" + sign.getTimeStamp()
            + "&signature=" + sign.getSignature();
		
		val response = httpGet(url);
		
		val gameKey = serializationService.readGameKey(response);
		
		System.out.println(String.format("Game key fetched: %s", gameKey));
		
		return gameKey;
	}
	
	public GameCreated createGame(String gameId, String gameKey, GameConfig gameConfig) {
		val url = RootUrl + "/game/create/" + gameKey;
		val configString = serializationService.writeGameConfig(gameConfig);
		
		val response = httpPost(url, Optional.of(configString));
		val gameCreated = serializationService.readGameCreated(response);
		
		System.out.println(String.format("Game created: %s", gameCreated));
		
		return gameCreated;
	}
	
	public String startGame(String gameId, GameKey gameKey) {
		val url = String.format("%s/game/start/%s/%s", RootUrl, gameId, gameKey);
		
		val response = httpPost(url, Optional.empty());
		
		System.out.println("Game creation result: " + response);
		
		return response;
	}
	
	public String stopGame(String gameId, GameKey gameKey) {
		val url = String.format("%s/game/stop/%s/%s", RootUrl, gameId, gameKey);
		
		val response = httpPost(url, Optional.empty());
		
		System.out.println("Game creation result: " + response);
		
		return response;
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
	        try (OutputStream os = connection.getOutputStream()) {
	            byte[] input = payload.get().getBytes("utf-8");
	            os.write(input, 0, input.length);			
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
        }
	}
}
