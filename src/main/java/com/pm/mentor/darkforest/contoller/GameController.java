package com.pm.mentor.darkforest.contoller;

import com.loxon.javachallenge.challenge.game.rest.BotDefinition;
import com.loxon.javachallenge.challenge.game.rest.GameConfig;
import com.loxon.javachallenge.challenge.game.rest.GameCreated;
import com.loxon.javachallenge.challenge.game.rest.GameKey;
import com.pm.mentor.darkforest.service.AIContainer;
import com.pm.mentor.darkforest.service.GameHttpAdapter;
import com.pm.mentor.darkforest.service.GameWebSocketAdapter;

import java.util.List;
import lombok.val;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {

    private final GameHttpAdapter gameHttpAdapter;
    private final GameWebSocketAdapter gameWebSocketAdapter;
    private final AIContainer container;
    private final ConnectionStateHolder connectionStateHolder;

    public GameController(GameHttpAdapter gameHttpAdapter,
    					  GameWebSocketAdapter gameWebSocketAdapter,
    					  AIContainer container,
                          ConnectionStateHolder connectionStateHolder) {
        this.gameHttpAdapter = gameHttpAdapter;
        this.gameWebSocketAdapter = gameWebSocketAdapter;
        this.container = container;
        this.connectionStateHolder = connectionStateHolder;
    }

    @GetMapping("/getGameKey")
    public GameKey getGameKey() {
        return gameHttpAdapter.getGameKey();
    }

    @PostMapping("/createGame/{gameKey}")
    public GameCreated createGame(@PathVariable String gameKey, @RequestBody GameConfig gameConfig) {
        GameCreated result = gameHttpAdapter.createGame(gameKey, gameConfig);
        if(result != null){
            connectionStateHolder.addConnection(new ConnectionState(gameKey, result.getGameId()));
        }
        
        container.create();
        
        return result;
    }
    
    @GetMapping("/connect/{gameId}/{gameKey}")
    public void connectControlWebSocket(@PathVariable String gameId, @PathVariable String gameKey) {
    	gameWebSocketAdapter.connect(gameId, gameKey);
        connectionStateHolder.getConnection(gameId, gameKey).ifPresent(x -> x.setConnected(true));
    }

    @GetMapping("/startGame/{gameId}/{gameKey}")
    public String startGame(@PathVariable String gameId, @PathVariable String gameKey) {
        connectionStateHolder.getConnection(gameId, gameKey).ifPresent(x -> x.setStarted(true));
        return gameHttpAdapter.startGame(gameId, gameKey);

    }

    @GetMapping("/stopGame/{gameId}/{gameKey}")
    public String stopGame(@PathVariable String gameId, @PathVariable String gameKey) {
        connectionStateHolder.getConnection(gameId, gameKey).ifPresent(x -> x.setStopped(true));
        return gameHttpAdapter.stopGame(gameId, gameKey);
    }

    @GetMapping("/bots")
    public List<BotDefinition> getBots() {
        return gameHttpAdapter.getBots();
    }


}
