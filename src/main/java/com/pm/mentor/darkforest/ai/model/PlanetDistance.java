package com.pm.mentor.darkforest.ai.model;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class PlanetDistance {
    private final AIPlanet planet;
    private final double distance;
}
