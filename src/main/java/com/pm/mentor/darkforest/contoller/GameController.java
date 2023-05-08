package com.pm.mentor.darkforest.contoller;

import java.util.List;

import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.loxon.javachallenge.challenge.game.rest.BotDefinition;
import com.loxon.javachallenge.challenge.game.rest.GameConfig;
import com.loxon.javachallenge.challenge.game.rest.GameCreated;
import com.loxon.javachallenge.challenge.game.rest.GameKey;
import com.pm.mentor.darkforest.service.AIContainer;
import com.pm.mentor.darkforest.service.GameHttpAdapter;
import com.pm.mentor.darkforest.service.GameKeyRepository;
import com.pm.mentor.darkforest.service.GameKeyRepository.TimeStampedString;
import com.pm.mentor.darkforest.service.GameWebSocketAdapter;

@RestController
public class GameController {

    private final GameHttpAdapter gameHttpAdapter;
    private final GameWebSocketAdapter gameWebSocketAdapter;
    private final AIContainer container;
    private final ConnectionStateHolder connectionStateHolder;
    private final GameKeyRepository gameKeyRepository;

    public GameController(GameHttpAdapter gameHttpAdapter,
                          GameWebSocketAdapter gameWebSocketAdapter,
                          AIContainer container,
                          ConnectionStateHolder connectionStateHolder,
                          GameKeyRepository gameKeyRepository) {
        this.gameHttpAdapter = gameHttpAdapter;
        this.gameWebSocketAdapter = gameWebSocketAdapter;
        this.container = container;
        this.connectionStateHolder = connectionStateHolder;
        this.gameKeyRepository = gameKeyRepository;
    }

    @GetMapping("/getGameKey")
    public GameKey getGameKey() {
        GameKey gameKey = gameHttpAdapter.getGameKey();
        
        gameKeyRepository.newGameKeyCreated(gameKey.getKey(), System.currentTimeMillis());
        
        return gameKey;
    }

    @PostMapping("/createGame/{gameKey}")
    public GameCreated createGame(@PathVariable String gameKey, @RequestBody GameConfig gameConfig) {
        GameCreated result = gameHttpAdapter.createGame(gameKey, gameConfig);
        if (result != null) {
            connectionStateHolder.addConnection(new ConnectionState(gameKey, result.getGameId()));
            gameKeyRepository.newGameCreated(gameKey, result.getGameId(), System.currentTimeMillis());
        }
        
        container.create();
        
        return result;
    }
    
    @GetMapping("/connect/{gameId}/{gameKey}")
    public void connectControlWebSocket(@PathVariable String gameId, @PathVariable String gameKey) {
        if(!container.isCreated()){
            container.create();
        }
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

    @GetMapping("/stopAllGames")
    public void stopAllGames() throws InterruptedException {
        for(GameKeyRepository.TimeStampedGameKeyAndGameId gameKeyAndGameId : gameKeyRepository.getAllCombined()) {
            try {
                stopGame(gameKeyAndGameId.getGameId(), gameKeyAndGameId.getGameKey());
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @GetMapping("/bots")
    public List<BotDefinition> getBots() {
        return gameHttpAdapter.getBots();
    }


    @GetMapping("/test")
    public String startTestGame() {
        gameWebSocketAdapter.testUi();
        return "test";
    }

    @GetMapping("/gameKeyHistory")
    public List<TimeStampedString> getGameKeyHistory() {
            return gameKeyRepository.getAllGameKeys();
    }
    
    @GetMapping("/gameIdHistory")
    public List<TimeStampedString> getGameIdHistory() {
            return gameKeyRepository.getAllGameIds();
    }
}
