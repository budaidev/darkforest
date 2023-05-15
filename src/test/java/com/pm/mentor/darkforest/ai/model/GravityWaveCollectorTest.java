package com.pm.mentor.darkforest.ai.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.loxon.javachallenge.challenge.game.event.actioneffect.ActionEffectType;
import com.loxon.javachallenge.challenge.game.event.actioneffect.GravityWaveCrossing;
import com.loxon.javachallenge.challenge.game.model.GravityWaveCause;
import com.loxon.javachallenge.challenge.game.model.Planet;
import com.loxon.javachallenge.challenge.game.model.Player;
import com.loxon.javachallenge.challenge.game.settings.GameSettings;
import com.pm.mentor.darkforest.util.Point;
import com.pm.mentor.darkforest.util.Vector;

import lombok.val;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GravityWaveCollectorTest {

	private GameSettings settings;

    private GameState gameState;
	
    private GravityWaveCollector gravityWaveCollector;
    
    @BeforeEach
    public void setup() {

        settings = initSettings();
        gameState = new GameState(settings, 1);

    }

    @Test
    public void sameDirectionTest() {

        List<AIPlanet> planets = new ArrayList<>();

        Point source = new Point(30, 30);

        AIPlanet planet = createPlanet(10000, source);
        planets.add(planet);

        PlanetAndEffect pae1 = createReceiverPlanets(source, Math.PI/2, 40, 10001);
        PlanetAndEffect pae2 = createReceiverPlanets(source, Math.PI/2, 50, 10002);

        planets.add(pae1.planet);
        planets.add(pae2.planet);

        planets.add(createPlanet(10005, new Point(10, 10)));
        planets.add(createPlanet(10005, new Point(10, 50)));

        gravityWaveCollector = new GravityWaveCollector(
                initPlayers(), planets, 1, gameState
        );

        System.out.println(gravityWaveCollector.filterPossiblePlanets(planets, pae1.effect, pae2.effect, 5));

        CollectResult res1 = gravityWaveCollector.collect(pae1.effect);
        CollectResult res2 = gravityWaveCollector.collect(pae2.effect);
        assertFalse(res1.isSuccessful());
        assertTrue(res2.isSuccessful());
        assertEquals(30, res2.getPossibleSource().getPos().getX());
        assertEquals(30, res2.getPossibleSource().getPos().getY());
    }

    @Test
    public void _90_degrees_test() {

        List<AIPlanet> planets = new ArrayList<>();

        Point source = new Point(30, 30);

        AIPlanet planet = createPlanet(10000, source);
        planets.add(planet);

        PlanetAndEffect pae1 = createReceiverPlanets(source, new Point(70,30), 10001);
        PlanetAndEffect pae2 = createReceiverPlanets(source, new Point(30,80), 10002);

        planets.add(pae1.planet);
        planets.add(pae2.planet);

        gravityWaveCollector = new GravityWaveCollector(
                initPlayers(), planets, 1, gameState
        );

        CollectResult res1 = gravityWaveCollector.collect(pae1.effect);
        CollectResult res2 = gravityWaveCollector.collect(pae2.effect);
        assertFalse(res1.isSuccessful());
        assertTrue(res2.isSuccessful());
        assertEquals(30, res2.getPossibleSource().getPos().getX());
        assertEquals(30, res2.getPossibleSource().getPos().getY());
    }

    @Test
    public void _45_degrees_test() {

        List<AIPlanet> planets = new ArrayList<>();

        Point source = new Point(30, 30);

        AIPlanet planet = createPlanet(10000, source);
        planets.add(planet);

        PlanetAndEffect pae1 = createReceiverPlanets(source, new Point(40, 40), 10001);
        PlanetAndEffect pae2 = createReceiverPlanets(source, new Point(30, 60), 10002);

        planets.add(pae1.planet);
        planets.add(pae2.planet);

        gravityWaveCollector = new GravityWaveCollector(
                initPlayers(), planets, 1, gameState
        );

        CollectResult res1 = gravityWaveCollector.collect(pae1.effect);
        CollectResult res2 = gravityWaveCollector.collect(pae2.effect);
        assertFalse(res1.isSuccessful());
        assertTrue(res2.isSuccessful());
        assertEquals(30, res2.getPossibleSource().getPos().getX());
        assertEquals(30, res2.getPossibleSource().getPos().getY());
    }
    
    @Test
    public void sourcePlusTwoObservations_ShouldSucceed() {
    	// source planet: 
    	// 2273, pos=(13, 34), emitted at: 1683997776591
    	
    	// observers
    	// 2302, pos(18, 26), observed at: 1683997777345 angle: 0.7653616315096966
    	// 2306, pos(20, 33), observed at: 1683997777156 angle: 1.6454067077991554
    	
    	val sourcePlanet = createPlanet(2273, new Point(13, 34));
    	val observer1 = createPlanet(2302, new Point(18, 26));
    	val observer2 = createPlanet(2306, new Point(20, 33));
    	
    	List<AIPlanet> planets = new ArrayList<>();
    	planets.add(sourcePlanet);
    	planets.add(observer1);
    	planets.add(observer2);

    	gravityWaveCollector = new GravityWaveCollector(initPlayers(), planets, 57, gameState);
    	
    	val res1 = gravityWaveCollector.collect(createSpaceMissionPassingEffect(1683997777156L, 1.6454067077991554, 2306));
    	val res2 = gravityWaveCollector.collect(createSpaceMissionPassingEffect(1683997777345L, 0.7653616315096966, 2302));
    	
    	assertFalse(res1.isSuccessful());
    	assertTrue(res2.isSuccessful());
    	assertEquals(2273, res2.getPossibleSource().getId());
    }
    
    @Test
    public void sourcePlusTwoObservations2_ShouldSucceed() {
    	// source planet: 
    	// 50231, pos=(78, 17), emitted at: 1684176405134
    	
    	// observers
    	// 50227, pos(79, 12), observed at: 1684176405541 angle: 0.019701476948111335
    	// 50268, pos(88, 17), observed at: 1684176405934 angle: 1.5372053955231917
    	
    	val sourcePlanet = createPlanet(50231, new Point(78, 17));
    	val observer1 = createPlanet(50227, new Point(78, 12));
    	val observer2 = createPlanet(50268, new Point(88, 17));
    	
    	List<AIPlanet> planets = new ArrayList<>();
    	planets.add(sourcePlanet);
    	planets.add(observer1);
    	planets.add(observer2);

    	gravityWaveCollector = new GravityWaveCollector(initPlayers(), planets, 57, gameState);
    	
    	val res1 = gravityWaveCollector.collect(createSpaceMissionPassingEffect(1684176405541L, 0.019701476948111335, 50227));
    	val res2 = gravityWaveCollector.collect(createSpaceMissionPassingEffect(1684176405934L, 1.5372053955231917, 50268));
    	
    	assertFalse(res1.isSuccessful());
    	assertTrue(res2.isSuccessful());
    	assertEquals(50231, res2.getPossibleSource().getId());
    }
    
    @Test
    public void sourcePlusTwoObservations3_ShouldSucceed() {
    	// source planet: 
    	// 69077, pos=(105, 9), emitted at: 1684180220804
    	
    	// observers
    	// 69080, pos(105, 17), observed at: 1684180221444 angle: 3.0728263891480956
    	// 69035, pos(96, 6), observed at: 1684180221562 angle: 5.114998205541819
    	
    	val sourcePlanet = createPlanet(69077, new Point(105, 9));
    	val observer1 = createPlanet(69080, new Point(105, 17));
    	val observer2 = createPlanet(69035, new Point(96, 6));
    	
    	List<AIPlanet> planets = new ArrayList<>();
    	planets.add(sourcePlanet);
    	planets.add(observer1);
    	planets.add(observer2);

    	gravityWaveCollector = new GravityWaveCollector(initPlayers(), planets, 57, gameState);
    	
    	val res1 = gravityWaveCollector.collect(createSpaceMissionPassingEffect(1684180221444L, 3.0728263891480956, 69080));
    	val res2 = gravityWaveCollector.collect(createSpaceMissionPassingEffect(1684180221562L, 5.114998205541819, 69035));
    	
    	assertFalse(res1.isSuccessful());
    	assertTrue(res2.isSuccessful());
    	assertEquals(69077, res2.getPossibleSource().getId());
    }
    
    private AIPlanet createPlanet(int id, int x, int y) {
    	return new AIPlanet(Planet.builder().id(id).x(x).y(y).build());
    }

    private AIPlanet createPlanet(int id, Point point) {
        return createPlanet(id, point.getX(), point.getY());
    }

    private GameSettings initSettings() {
        GameSettings gameSettings = new GameSettings();
        gameSettings.setPassivityFleshPrecision(5);
        gameSettings.setGravityWaveSourceLocationPrecision(5);
        gameSettings.setWidth(112);
        gameSettings.setHeight(60);
        gameSettings.setTimeOfOneLightYear(40);

        return gameSettings;
    }

    private List<Player> initPlayers() {
        List<Player> players = new ArrayList<>();
        players.add(Player.builder().id(1).teamName("test1").build());
        players.add(Player.builder().id(2).teamName("test2").build());

        return players;
    }

    private GravityWaveCrossing createSpaceMissionPassingEffect(long time, double dir, int affectedId) {
        return GravityWaveCrossing.builder()
                .sourceId(0)
                .cause(GravityWaveCause.SPACE_MISSION)
                .direction(dir)
                .affectedMapObjectId(affectedId)
                .inflictingPlayer(2)
                .time(time)
                .effectChain(List.of(ActionEffectType.SPACE_MISSION_GRAWITY_WAVE_PASSING))
                .build();
    }

    private PlanetAndEffect createReceiverPlanets(Point source, double dir, double dist, int planetId) {

    	val lightSpeed = settings.getTimeOfOneLightYear();
        Point p = source.move(dir, dist);
        AIPlanet planet = createPlanet(planetId, p);
        GravityWaveCrossing effect = createSpaceMissionPassingEffect((int)(dist*lightSpeed*2), dir, planetId);

        return new PlanetAndEffect(planet, effect);
    }

    private PlanetAndEffect createReceiverPlanets(Point source, Point target, int planetId) {
    	val lightSpeed = settings.getTimeOfOneLightYear();
        Vector v = target.minus(source);
        double dir = v.angleToNorth().rad;
        double dist = v.magnitude;
        AIPlanet planet = createPlanet(planetId, target);
        GravityWaveCrossing effect = createSpaceMissionPassingEffect((int)(dist*lightSpeed*2), dir, planetId);

        return new PlanetAndEffect(planet, effect);
    }

    static class PlanetAndEffect {
        AIPlanet planet;
        GravityWaveCrossing effect;

        public PlanetAndEffect(AIPlanet planet, GravityWaveCrossing effect) {
            this.planet = planet;
            this.effect = effect;
        }
    }
}
