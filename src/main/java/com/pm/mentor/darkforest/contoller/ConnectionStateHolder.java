package com.pm.mentor.darkforest.contoller;

import com.loxon.javachallenge.challenge.game.rest.GameCreated;
import com.loxon.javachallenge.challenge.game.rest.GameKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ConnectionStateHolder {

    private List<ConnectionState> connections;

    public ConnectionStateHolder(){
        connections = new ArrayList<>();
    }

    public void addConnection(ConnectionState connection){
        connections.add(connection);
    }

    public Optional<ConnectionState> getConnection(String gameKey, String gameCreated){
        return connections.stream().filter(x -> x.getGameKey().equals(gameKey) && x.getGameId().equals(gameCreated)).findFirst();
    }

    public List<ConnectionState> getLiveGames() {
        return connections.stream().filter(x -> x.isStarted() && !x.isStopped()).collect(Collectors.toList());
    }

    public List<ConnectionState> getAllGames() {
        return connections;
    }

}
