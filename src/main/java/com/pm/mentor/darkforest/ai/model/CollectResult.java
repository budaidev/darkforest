package com.pm.mentor.darkforest.ai.model;

import com.loxon.javachallenge.challenge.game.model.Planet;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CollectResult {

    private boolean successful;
    private Planet possibleSource;
}
