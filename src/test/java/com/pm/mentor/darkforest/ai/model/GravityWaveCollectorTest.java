package com.pm.mentor.darkforest.ai.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.loxon.javachallenge.challenge.game.event.actioneffect.GravityWaveCrossing;
import com.loxon.javachallenge.challenge.game.model.GravityWaveCause;
import com.loxon.javachallenge.challenge.game.model.Planet;
import com.loxon.javachallenge.challenge.game.model.Player;
import com.loxon.javachallenge.challenge.game.settings.GameSettings;
import com.pm.mentor.darkforest.util.Point;
import com.pm.mentor.darkforest.util.Vector;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class GravityWaveCollectorTest {

    private GravityWaveCollector gravityWaveCollector;

    public Planet createPlanet(int id, int x, int y) {
        return Planet.builder().id(id).x(x).y(y).build();
    }

    public Planet createPlanet(int id, Point point) {
        return createPlanet(id, point.getX(), point.getY());
    }

    private GameSettings initSettings() {
        GameSettings gameSettings = new GameSettings();
        gameSettings.setPassivityFleshPrecision(5);
        gameSettings.setGravityWaveSourceLocationPrecision(5);
        return gameSettings;
    }

    private List<Player> initPlayers() {
        List<Player> players = new ArrayList<>();
        players.add(Player.builder().id(1).teamName("test1").build());
        players.add(Player.builder().id(2).teamName("test2").build());
        return players;
    }

    private GravityWaveCrossing createEffect(int time, double dir, int affectedId) {
        return GravityWaveCrossing.builder()
                .sourceId(0)
                .cause(GravityWaveCause.SPACE_MISSION)
                .direction(dir)
                .affectedMapObjectId(affectedId)
                .inflictingPlayer(2)
                .time(time)
                .build();
    }

    private PlanetAndEffect createReceiverPlanets(Point source, double dir, double dist, int planetId, int lightspeed){

        Point p = source.move(dir, dist);
        Planet planet = createPlanet(planetId, p);
        GravityWaveCrossing effect = createEffect((int)(dist*lightspeed), dir, planetId);

        return new PlanetAndEffect(planet, effect);
    }

    private PlanetAndEffect createReceiverPlanets(Point source, Point target, int planetId, int lightspeed){
        Vector v = target.minus(source);
        double dir = v.angleToNorth().rad;
        double dist = v.magnitude;
        Planet planet = createPlanet(planetId, target);
        GravityWaveCrossing effect = createEffect((int)(dist*lightspeed), dir, planetId);

        return new PlanetAndEffect(planet, effect);
    }

    static class PlanetAndEffect {
        Planet planet;
        GravityWaveCrossing effect;

        public PlanetAndEffect(Planet planet, GravityWaveCrossing effect) {
            this.planet = planet;
            this.effect = effect;
        }
    }

    @Test
    public void sameDirectionTest() {

        GameSettings settings = initSettings();
        List<Planet> planets = new ArrayList<>();

        Point source = new Point(30, 30);

        Planet planet = createPlanet(10000, source);
        planets.add(planet);

        PlanetAndEffect pae1 = createReceiverPlanets(source, Math.PI/2, 40, 10001, 40);
        PlanetAndEffect pae2 = createReceiverPlanets(source, Math.PI/2, 50, 10002, 40);

        planets.add(pae1.planet);
        planets.add(pae2.planet);

        planets.add(createPlanet(10005, new Point(10, 10)));
        planets.add(createPlanet(10005, new Point(10, 50)));

        gravityWaveCollector = new GravityWaveCollector(
                initPlayers(), planets, 112, 60, 40, 1, settings
        );

        System.out.println(gravityWaveCollector.filterPossiblePlanets(planets, pae1.effect, pae2.effect, 5));

        CollectResult res1 = gravityWaveCollector.collect(pae1.effect);
        CollectResult res2 = gravityWaveCollector.collect(pae2.effect);
        assertFalse(res1.isSuccessful());
        assertTrue(res2.isSuccessful());
        assertEquals(30, res2.getPossibleSource().getX());
        assertEquals(30, res2.getPossibleSource().getY());
    }

    @Test
    public void _90_degrees_test() {

        GameSettings settings = initSettings();
        List<Planet> planets = new ArrayList<>();

        Point source = new Point(30, 30);

        Planet planet = createPlanet(10000, source);
        planets.add(planet);

        PlanetAndEffect pae1 = createReceiverPlanets(source, new Point(70,30), 10001, 40);
        PlanetAndEffect pae2 = createReceiverPlanets(source, new Point(30,80), 10002, 40);

        planets.add(pae1.planet);
        planets.add(pae2.planet);

        gravityWaveCollector = new GravityWaveCollector(
                initPlayers(), planets, 112, 60, 40, 1, settings
        );

        CollectResult res1 = gravityWaveCollector.collect(pae1.effect);
        CollectResult res2 = gravityWaveCollector.collect(pae2.effect);
        assertFalse(res1.isSuccessful());
        assertTrue(res2.isSuccessful());
        assertEquals(30, res2.getPossibleSource().getX());
        assertEquals(30, res2.getPossibleSource().getY());
    }

    @Test
    public void _45_degrees_test() {

        GameSettings settings = initSettings();
        List<Planet> planets = new ArrayList<>();

        Point source = new Point(30, 30);

        Planet planet = createPlanet(10000, source);
        planets.add(planet);

        PlanetAndEffect pae1 = createReceiverPlanets(source, new Point(40, 40), 10001, 40);
        PlanetAndEffect pae2 = createReceiverPlanets(source, new Point(30, 60), 10002, 40);

        planets.add(pae1.planet);
        planets.add(pae2.planet);

        gravityWaveCollector = new GravityWaveCollector(
                initPlayers(), planets, 112, 60, 40, 1, settings
        );

        CollectResult res1 = gravityWaveCollector.collect(pae1.effect);
        CollectResult res2 = gravityWaveCollector.collect(pae2.effect);
        assertFalse(res1.isSuccessful());
        assertTrue(res2.isSuccessful());
        assertEquals(30, res2.getPossibleSource().getX());
        assertEquals(30, res2.getPossibleSource().getY());
    }
}
