package com.pm.mentor.darkforest.ai;

import com.loxon.javachallenge.challenge.game.event.action.EntryPointIndex;
import com.loxon.javachallenge.challenge.game.model.WormHole;
import com.pm.mentor.darkforest.ai.model.AIPlanet;
import com.pm.mentor.darkforest.ai.model.ClosestToGivenPlanetWithWormholeComparator;
import com.pm.mentor.darkforest.ai.model.PlanetDistance;
import com.pm.mentor.darkforest.util.Point;
import com.pm.mentor.darkforest.util.PointToPointDistanceCache;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.loxon.javachallenge.challenge.game.event.GameEvent;
import com.pm.mentor.darkforest.ai.model.ClosestToGivenPlanetComparator;
import com.pm.mentor.darkforest.ai.model.GameState;

import lombok.Getter;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SampleAI implements AI {

	private GameState gameState;

	private int wormholeNumber = 0;
	private int startingPlanetId = 0;

	@Getter
	private boolean running = false;

	private long currentTime;

	@Override
	public void init(GameState gameState) {
		log.info("AI initialized");
		this.gameState = gameState;

		running = true;

		wormholeNumber = 0;
		startingPlanetId = gameState.getPlayerPlanets().get(0).getId();

		log.info("My starting planet id is {}", startingPlanetId);
	}

	@Override
	public void receiveEvent(GameEvent event) {
		log.trace(String.format("AI received a game event: %s", event.getEventType()));
		currentTime = event.getEventTime();

		doStuff();
	}

	@Override
	public void heartBeat() {
		if (!running) {
			return;
		}

		log.trace("AI received a heartbeat");

		doStuff();
	}

	@Override
	public void stop() {
		running = false;
	}

	private void doStuff() {
		if (!running) {
			if (gameState != null) {
				gameState.clearActions();
			}

			return;
		}

		log.trace("dostuff");
		log.trace(String.format("Actions: Initiated: %d, Active: %d, Calculated: %d, Max: %d",
				gameState.getInitiatedActions().size(), gameState.getActiveActions().size(), gameState.activeActionCount(), gameState.getMaxConcurrentActionCount()));

		if (!gameState.hasFreeAction()) {
			log.trace("No free actions");

			return;
		}

		if(gameState.getPlayerPlanets().size() > 1) {
			AIPlanet planet = gameState.getPlayerPlanets().stream()
					.filter(p -> p.getId() != startingPlanetId)
					.findFirst().get();
			buildWormhole(planet);
		}

		val playerPlanets = gameState.getPlayerPlanets();

		int avaibleActionNumber = gameState.availableActionCount();

        /*
		val closestUnknownPlanets = gameState.getColonizablePlanets(gameState.createClosestToPlayerPlanetComparatorWithWormholes()).subList(0, avaibleActionNumber);
		val closestEnemyPlanets = gameState.getEnemyPlanet(gameState.createClosestToPlayerPlanetComparator()).subList(0, avaibleActionNumber);;
		sendMissionsToNearbyPlanets(playerPlanets, closestUnknownPlanets);
		missileShower(closestEnemyPlanets);
		 */

		//missionAndMissileTactic(playerPlanets, avaibleActionNumber);
		passiveShieldTactic(playerPlanets, avaibleActionNumber);
		if(gameState.hasFreeAction()){
			log.info("We did not use all the actions!");
		}
	}

	private void passiveShieldTactic(List<AIPlanet> playerPlanets, int avaibleActionNumber) {
		Map<AIPlanet, Double> colonizablePlanetsMap = gameState.getColonizablePlanets();

		List<PlanetDistance> colonizablePlanets = colonizablePlanetsMap.entrySet().stream()
				.sorted(Map.Entry.comparingByValue())
				.map(e -> new PlanetDistance(e.getKey(), e.getValue()))
				.filter(gameState.createTargetedPlanetDistanceFilter()).limit(avaibleActionNumber).toList();

		int actionNumber = 0;
		if (!colonizablePlanets.isEmpty() && colonizablePlanets.get(0).getDistance() < 30){
			while (actionNumber < avaibleActionNumber) {
				spaceMissionToTarget(playerPlanets, colonizablePlanets.get(actionNumber).getPlanet());
				actionNumber++;
			}
		} else {
			val planetsWithoutShield = playerPlanets.stream()
					.filter(gameState.createTargetedPlanetFilter())
					.filter(p -> !p.hasShield())
					.collect(Collectors.toList());
			Collections.shuffle(planetsWithoutShield);
			while (actionNumber < avaibleActionNumber && actionNumber < planetsWithoutShield.size()) {
				gameState.erectShield(planetsWithoutShield.get(actionNumber).getId());
				actionNumber++;
			}

		}

		// if we have remaining action we should just stay
		if(actionNumber < avaibleActionNumber) {
			Map<AIPlanet, Double> enemyPlanetMap = gameState.getEnemyPlanet();
			List<PlanetDistance> enemyPlanets = enemyPlanetMap.entrySet().stream()
					.sorted(Map.Entry.comparingByValue())
					.map(e -> new PlanetDistance(e.getKey(), e.getValue()))
					.filter(x -> !x.getPlanet().isAlreadyShot())
					.filter(gameState.createTargetedPlanetDistanceFilter())
					.limit(avaibleActionNumber)
					.toList();
			for (PlanetDistance dist : enemyPlanets) {
				shootFromClosestPlanet(playerPlanets, dist.getPlanet());
				actionNumber++;
			}
		}




	}

	private void missionAndMissileTactic(List<AIPlanet> playerPlanets, int avaibleActionNumber) {
		Map<AIPlanet, Double> colonizablePlanetsMap = gameState.getColonizablePlanets();
		Map<AIPlanet, Double> enemyPlanetMap = gameState.getEnemyPlanet();

		List<PlanetDistance> colonizablePlanets = colonizablePlanetsMap.entrySet().stream()
				.sorted(Map.Entry.comparingByValue())
				.map(e -> new PlanetDistance(e.getKey(), e.getValue()))
				.filter(gameState.createTargetedPlanetDistanceFilter()).limit(avaibleActionNumber).toList();
		List<PlanetDistance> enemyPlanets = enemyPlanetMap.entrySet().stream()
				.sorted(Map.Entry.comparingByValue())
				.map(e -> new PlanetDistance(e.getKey(), e.getValue()))
				.filter(x -> !x.getPlanet().isAlreadyShot())
				.filter(gameState.createTargetedPlanetDistanceFilter())
				.limit(avaibleActionNumber)
				.toList();

		log.info("colonizablePlanets: " + colonizablePlanets);
		log.info("enemyPlanets: " + enemyPlanets);

		int p = 0;
		int e = 0;
		int actionNumber = 0;

		if(!colonizablePlanets.isEmpty() || !enemyPlanets.isEmpty()) {
			while (actionNumber < avaibleActionNumber) {
				double d1 = colonizablePlanets.get(p).getDistance();
				double d2 = Double.MAX_VALUE;
				if (!enemyPlanets.isEmpty()) {
					d2 = enemyPlanets.get(e).getDistance();
				}


				if (d1 < d2) {
					//do space mission
					spaceMissionToTarget(playerPlanets, colonizablePlanets.get(p).getPlanet());
					p++;
					actionNumber++;
				} else {
					//do missile shoot
					//TODO: check if close enough shot
					if (actionNumber + 2 <= avaibleActionNumber) {
						doubleShootFromClosestPlanet(playerPlanets, enemyPlanets.get(e).getPlanet());
						actionNumber += 2;
					} else {
						shootFromClosestPlanet(playerPlanets, enemyPlanets.get(e).getPlanet());
						actionNumber++;
					}
					e++;
				}

			}
		} else {
			log.info("double shoot the rest of planets");
			enemyPlanets = enemyPlanetMap.entrySet().stream()
					.sorted(Map.Entry.comparingByValue())
					.map(x -> new PlanetDistance(x.getKey(), x.getValue()))
					.filter(gameState.createTargetedPlanetDistanceFilter())
					.limit(avaibleActionNumber)
					.toList();
			int cnt = 0;
			while (actionNumber < avaibleActionNumber && cnt < enemyPlanets.size()) {
				if (actionNumber + 2 <= avaibleActionNumber) {
					doubleShootFromClosestPlanet(playerPlanets, enemyPlanets.get(cnt).getPlanet());
					actionNumber += 2;
					cnt++;
				}
			}
		}
	}

	private void buildWormhole(AIPlanet startPlanet) {
		if(wormholeNumber < 1) {
			log.trace("buildWormhole from " + startPlanet.getId() + " to center");

			int w = gameState.getSettings().getWidth();
			int h = gameState.getSettings().getHeight();

			//four corner point
			Point upLeft = new Point(w/4, h/4);
			Point upRight = new Point(w/2 + w/4 , h/4);
			Point downLeft = new Point(w/2, h/2 + h/4);
			Point downRight = new Point(w/2 + w/4, h/2 + h/4);



			double maxDistance = startPlanet.getPos().distance(upLeft);
			Point maxPoint = upLeft;
			for(Point p : List.of(upRight, downLeft, downRight)) {
				double distance = startPlanet.getPos().distance(p);
				if(distance > maxDistance) {
					maxDistance = distance;
					maxPoint = p;
				}
			}

			if(maxPoint == upRight || maxPoint == downLeft) {
				gameState.buildWormHole(downRight.getX(), downRight.getY(), upLeft.getX(), upLeft.getY());
			} else {
				gameState.buildWormHole(downLeft.getX(), downLeft.getY(), upRight.getX(), upRight.getY());
			}


			gameState.buildWormHole(startPlanet.getPos().getX(), startPlanet.getPos().getY(), maxPoint.getX(), maxPoint.getY());

			wormholeNumber++;
		}

	}

	private void sendMissionsToNearbyPlanets(
			List<AIPlanet> playerPlanets,
			List<AIPlanet> closestUnknownPlanets) {

		if (closestUnknownPlanets.size() > 0) {
			val targetPlanets = closestUnknownPlanets.stream()
					.filter(gameState.createTargetedPlanetFilter())
					.limit(gameState.availableActionCount())
					.toList();

			log.trace(String.format("Selected %d planets as mission targets", targetPlanets.size()));

			for (val target : targetPlanets) {

				spaceMissionToTarget(playerPlanets, target);
			}
		}
	}

	private void spaceMissionToTarget(List<AIPlanet> playerPlanets, AIPlanet target) {
		if(gameState.getWormHoles().size() == 0){
			val closestPlayerPlanet = playerPlanets.stream()
					.sorted(new ClosestToGivenPlanetComparator(target))
					.findFirst();

			if (closestPlayerPlanet.isPresent()) {
				val playerPlanet = closestPlayerPlanet.get();
				log.trace(String.format("Sending mission from %d to %d", playerPlanet.getId(), target.getId()));
				gameState.spaceMission(playerPlanet, target);
			}
		} else {

			double minDistance = Double.MAX_VALUE;
			WormHole closestWormhole = null;
			for(WormHole wormHole : gameState.getWormHoles()){
				double distanceA = wormHole.getPointA().distance(target.getPos());
				double distanceB = wormHole.getPointB().distance(target.getPos());
				if(distanceA < minDistance){
					minDistance = distanceA;
					closestWormhole = wormHole;
				}
				if(distanceB < minDistance){
					minDistance = distanceB;
					closestWormhole = wormHole;
				}

			}

			val closestPlayerPlanet = playerPlanets.stream().min(new ClosestToGivenPlanetWithWormholeComparator(target, closestWormhole));

			if (closestPlayerPlanet.isPresent()) {
				val playerPlanet = closestPlayerPlanet.get();

				spaceMission(playerPlanet, target, closestWormhole);
			}
		}
	}

	public void spaceMission(AIPlanet from, AIPlanet to, WormHole w) {
		double distanceWithoutWormhole = PointToPointDistanceCache.distance(from.getPos(), to.getPos());

		double distanceFromAPoint =
				PointToPointDistanceCache.distance(from.getPos(), new Point(w.getX(), w.getY())) +
						PointToPointDistanceCache.distance(to.getPos(), new Point(w.getXb(), w.getYb()));

		double distanceFromBPoint =
				PointToPointDistanceCache.distance(from.getPos(), new Point(w.getXb(), w.getYb())) +
						PointToPointDistanceCache.distance(to.getPos(), new Point(w.getX(), w.getY()));

		double minDistance = List.of(distanceWithoutWormhole, distanceFromAPoint, distanceFromBPoint)
				.stream()
				.min(Double::compare)
				.get();

		if (minDistance == distanceWithoutWormhole) {
			log.trace(String.format("Sending mission from %d to %d", from.getId(), to.getId()));
			gameState.spaceMission(from, to);
		} else if(minDistance == distanceFromAPoint) {
			log.trace(String.format("Sending mission from %d to %d through wh %d with startpoint %s",
					from.getId(), to.getId(), w.getId(), EntryPointIndex.A));
			log.trace("Start point: " + from + " to " + to + " wormhole " + w + " " + EntryPointIndex.A);
			gameState.spaceMissionWithWormHole(from.getId(), to.getId(), w.getId(), EntryPointIndex.A);
		} else {
			log.trace(String.format("Sending mission from %d to %d through wh %d with startpoint %s",
					from.getId(), to.getId(), w.getId(), EntryPointIndex.B));
			gameState.spaceMissionWithWormHole(from.getId(), to.getId(), w.getId(), EntryPointIndex.B);
			log.trace("Start point: " + from + " to " + to + " wormhole " + w + " " + EntryPointIndex.B);
		}


	}

	private void missileShower(List<AIPlanet> closestEnemyPlanets) {
		if (!gameState.hasFreeAction() ) {
			return;
		}

		log.trace("missileShower");

		val nonDestroyedPlanets = closestEnemyPlanets
				.stream()
				.filter(x -> !x.isAlreadyShot())
				.sorted(gameState.createClosestToPlayerPlanetComparator())
				.collect(Collectors.toList());

		if (nonDestroyedPlanets.size() > 0) {
			val targetPlanets = nonDestroyedPlanets.stream()
					.filter(gameState.createTargetedPlanetFilter())
					.limit(gameState.availableActionCount())
					.collect(Collectors.toList());

			log.trace(String.format("Selected %d planets as missile shower targets", targetPlanets.size()));

			val playerPlanets = gameState.getPlayerPlanets();

			for (val target : targetPlanets) {
				shootFromClosestPlanet(playerPlanets, target);
			}
		}
	}

	private void shootFromClosestPlanet(List<AIPlanet> playerPlanets, AIPlanet target) {
		val closestPlayerPlanet = playerPlanets.stream()
				.sorted(new ClosestToGivenPlanetComparator(target))
				.findFirst();

		if (closestPlayerPlanet.isPresent()) {
			val playerPlanet = closestPlayerPlanet.get();

			log.trace(String.format("Shoot mbh from %d to %d", playerPlanet.getId(), target.getId()));
			gameState.shootMBH(playerPlanet, target);
		}
	}

	private void doubleShootFromClosestPlanet(List<AIPlanet> playerPlanets, AIPlanet target) {
		val closestPlayerPlanet = playerPlanets.stream()
				.sorted(new ClosestToGivenPlanetComparator(target)).limit(2).toList();

		val playerPlanet = closestPlayerPlanet.get(0);
		log.trace(String.format("Shoot mbh from %d to %d", playerPlanet.getId(), target.getId()));
		gameState.shootMBH(playerPlanet, target);

		val secondPlayerPlanet = closestPlayerPlanet.get(1);
		log.trace(String.format("Shoot mbh from %d to %d", secondPlayerPlanet.getId(), target.getId()));
		gameState.shootMBH(secondPlayerPlanet, target);

	}
}
