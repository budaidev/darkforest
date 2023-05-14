package com.pm.mentor.darkforest.ai.model;

import java.util.List;

import com.loxon.javachallenge.challenge.game.event.actioneffect.ActionEffect;
import com.loxon.javachallenge.challenge.game.model.Planet;
import com.pm.mentor.darkforest.util.Point;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class AIPlanet {
	
	@Getter
	private final int id;
	@Getter
	private final Point pos;
	
	@Getter
	private int owner;
	@Getter
	private boolean destroyed;
	
	private boolean visitedBySpaceMission;

	@Getter
	private boolean alreadyShot;

	@Getter
	@Setter
	private boolean virtualPlanet = false; // used for wormhole comparison
	@Getter
	@Setter
	private double distanceToClosest = 0; // used for wormhole comparison
	
	@Getter
	private List<ActionEffect> effectsEmitted;
	
	public AIPlanet(Planet p) {
		id = p.getId();
		pos = new Point(p.getX(), p.getY());
		owner = p.getPlayer();
		destroyed = p.isDestroyed();
		visitedBySpaceMission = owner != 0;
		alreadyShot = false;
	}

	private AIPlanet(Point p) {
		id = -1;
		this.pos = p;
		destroyed = false;
		virtualPlanet = true;
	}

	public static AIPlanet createVirtualPlanetFromWormHole(Point p) {
		return new AIPlanet(p);
	}

	public boolean isSpaceMissionPossible() {
		return !destroyed && !visitedBySpaceMission;
	}
	
	public void playerSettled(int playerId) {
		owner = playerId;
		visitedBySpaceMission = true;
	}
	
	public void destoryed() {
		this.destroyed = true;
	}
	
	public void spaceMissionFailed() {
		visitedBySpaceMission = true;
	}

	public void shoot() {
		alreadyShot = true;
	}
	
	public void blameEffect(ActionEffect effect) {
		effectsEmitted.add(effect);
	}
}
