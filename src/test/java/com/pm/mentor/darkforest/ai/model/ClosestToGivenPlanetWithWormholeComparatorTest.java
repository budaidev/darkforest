package com.pm.mentor.darkforest.ai.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.loxon.javachallenge.challenge.game.model.Planet;
import com.loxon.javachallenge.challenge.game.model.WormHole;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ClosestToGivenPlanetWithWormholeComparatorTest {

    @Test
    public void wormholeStartFromPlanet_wormholeDistanceShorter_shouldReturnWormholePath() {
        Planet planet = Planet.builder().x(30).y(30).id(1).player(1).build();
        AIPlanet target = new AIPlanet(planet);
        WormHole wormHole = WormHole.builder().x(30).y(30).xb(80).yb(80).player(1).id(100).build();

        ClosestToGivenPlanetWithWormholeComparator comparator =
                new ClosestToGivenPlanetWithWormholeComparator(target, wormHole);


        List<AIPlanet> planets = new ArrayList<>();
        planets.add(new AIPlanet(Planet.builder().x(20).y(20).id(2).player(1).build()));
        planets.add(new AIPlanet(Planet.builder().x(85).y(85).id(3).player(1).build()));

        planets.sort(comparator);

        assertEquals(3, planets.get(0).getId());
    }

    @Test
    public void wormholeStartFromPlanet_wormholeDistanceLonger_shouldReturnNormalPath() {
        Planet planet = Planet.builder().x(30).y(30).id(1).player(1).build();
        AIPlanet target = new AIPlanet(planet);
        WormHole wormHole = WormHole.builder().x(30).y(30).xb(80).yb(80).player(1).id(100).build();

        ClosestToGivenPlanetWithWormholeComparator comparator =
                new ClosestToGivenPlanetWithWormholeComparator(target, wormHole);


        List<AIPlanet> planets = new ArrayList<>();
        planets.add(new AIPlanet(Planet.builder().x(20).y(20).id(2).player(1).build()));
        planets.add(new AIPlanet(Planet.builder().x(95).y(95).id(3).player(1).build()));

        planets.sort(comparator);

        assertEquals(2, planets.get(0).getId());
    }

    @Test
    public void wormholeStartNearThePlanet_wormholeDistanceShorter_shouldReturnWormholePath() {
        Planet planet = Planet.builder().x(30).y(30).id(1).player(1).build();
        AIPlanet target = new AIPlanet(planet);
        WormHole wormHole = WormHole.builder().x(35).y(35).xb(80).yb(80).player(1).id(100).build();

        ClosestToGivenPlanetWithWormholeComparator comparator =
                new ClosestToGivenPlanetWithWormholeComparator(target, wormHole);


        List<AIPlanet> planets = new ArrayList<>();
        planets.add(new AIPlanet(Planet.builder().x(19).y(19).id(2).player(1).build()));
        planets.add(new AIPlanet(Planet.builder().x(85).y(85).id(3).player(1).build()));

        planets.sort(comparator);

        assertEquals(3, planets.get(0).getId());
    }

}
