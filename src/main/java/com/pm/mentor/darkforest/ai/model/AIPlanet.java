package com.pm.mentor.darkforest.ai.model;

import com.loxon.javachallenge.challenge.game.model.Planet;
import com.pm.mentor.darkforest.util.Point;

import lombok.Getter;
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
	
	private boolean visitedBySpaceMission = false;
	
	public AIPlanet(Planet p) {
		id = p.getId();
		pos = new Point(p.getX(), p.getY());
		owner = p.getPlayer();
		destroyed = p.isDestroyed();
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
}
