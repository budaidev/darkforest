package com.pm.mentor.darkforest.contoller;

import com.loxon.javachallenge.challenge.game.rest.GameConfig;
import com.loxon.javachallenge.challenge.game.rest.GameCreated;
import com.loxon.javachallenge.challenge.game.rest.GameKey;
import com.pm.mentor.darkforest.service.GameHttpAdapter;
import com.pm.mentor.darkforest.service.GameWebSocketAdapter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {

    private final GameHttpAdapter gameHttpAdapter;
    private final GameWebSocketAdapter gameWebSocketAdapter;

    public GameController(GameHttpAdapter gameHttpAdapter,
    					  GameWebSocketAdapter gameWebSocketAdapter) {
        this.gameHttpAdapter = gameHttpAdapter;
        this.gameWebSocketAdapter = gameWebSocketAdapter;
    }

    @GetMapping("/getGameKey")
    public GameKey getGameKey() {
        return gameHttpAdapter.getGameKey();
    }

    @PostMapping("/createGame/{gameKey}")
    public GameCreated createGame(@PathVariable String gameKey, @RequestBody GameConfig gameConfig) {
        return gameHttpAdapter.createGame(gameKey, gameConfig);
    }
    
    @GetMapping("/connect/{gameId}/{gameKey}")
    public void connectControlWebSocket(@PathVariable String gameId, @PathVariable String gameKey) {
    	gameWebSocketAdapter.connect(gameId, gameKey);
    }

    @GetMapping("/startGame/{gameId}/{gameKey}")
    public String startGame(@PathVariable String gameId, @PathVariable String gameKey) {
        return gameHttpAdapter.startGame(gameId, gameKey);
    }

    @GetMapping("/stopGame/{gameId}/{gameKey}")
    public String stopGame(@PathVariable String gameId, @PathVariable String gameKey) {
        return gameHttpAdapter.stopGame(gameId, gameKey);
    }

}
