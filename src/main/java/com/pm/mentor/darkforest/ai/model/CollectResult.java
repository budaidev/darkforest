package com.pm.mentor.darkforest.ai.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CollectResult {

    private boolean successful;
    private AIPlanet possibleSource;
}
