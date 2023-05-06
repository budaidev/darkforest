package com.pm.mentor.darkforest.contoller;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConnectionState {

    private String gameKey;
    private String gameId;
    private boolean isConnected;
    private boolean isStarted;
    private boolean isStopped;

    public ConnectionState(String gameKey, String gameId){
        this.gameKey = gameKey;
        this.gameId = gameId;

    }
}
