package com.pm.mentor.darkforest.contoller;

import com.loxon.javachallenge.challenge.game.rest.GameConfig;
import com.loxon.javachallenge.challenge.game.rest.GameCreated;
import com.loxon.javachallenge.challenge.game.rest.GameKey;
import com.pm.mentor.darkforest.service.GameHttpAdapter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {

    private final GameHttpAdapter gameHttpAdapter;

    public GameController(GameHttpAdapter gameHttpAdapter) {
        this.gameHttpAdapter = gameHttpAdapter;
    }

    @GetMapping("/getGameKey")
    public GameKey getGameKey() {
        return gameHttpAdapter.getGameKey();
    }

    public GameCreated createGame(String gameId, String gameKey, GameConfig gameConfig) {
        return gameHttpAdapter.createGame(gameId, gameKey, gameConfig);
    }

    public String startGame(String gameId, GameKey gameKey) {
        return gameHttpAdapter.startGame(gameId, gameKey);
    }

    public String stopGame(String gameId, GameKey gameKey) {
        return gameHttpAdapter.stopGame(gameId, gameKey);
    }

}
