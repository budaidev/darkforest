package com.pm.mentor.darkforest.ai.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.loxon.javachallenge.challenge.game.model.Planet;
import com.loxon.javachallenge.challenge.game.model.WormHole;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ClosestToPlayerPlanetsWithWormholeComparatorTest {

    private AIPlanet createPlanet(int x, int y, int id, int player) {
        Planet planet = Planet.builder().x(x).y(y).id(id).player(player).build();
        return new AIPlanet(planet);
    }

    @Test
    public void test(){

        AIPlanet p1 = createPlanet(30, 30, 1, 1);
        WormHole wormHole = WormHole.builder().x(30).y(30).xb(80).yb(80).player(1).id(100).build();

        List<AIPlanet> playerPlanets =  List.of(p1);
        List<WormHole> wormHoles = List.of(wormHole);

        ClosestToPlayerPlanetsWithWormholeComparator comparator = new ClosestToPlayerPlanetsWithWormholeComparator(
                playerPlanets, wormHoles
        );

        List<AIPlanet> planets = new ArrayList<>();
        planets.add(createPlanet(85, 85, 2, 0));
        planets.add(createPlanet(40, 40, 3, 0));

        planets.sort(comparator);

        assertEquals(2, planets.get(0).getId());
    }
}
