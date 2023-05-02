package com.loxon.javachallenge.challenge.game.event.action;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

/**
 * A játék akcióról információkat tároló absztrakt osztály.
 */
@Data
@JsonTypeInfo(
		  use = JsonTypeInfo.Id.NAME,
		  include = JsonTypeInfo.As.PROPERTY,
		  property = "type")
		@JsonSubTypes({
		  @Type(value = ShootMBHAction.class, name = "SHOOT_MBH"),
		  @Type(value = SpaceMissionAction.class, name = "SPACE_MISSION"),
		  @Type(value = ErectShieldAction.class, name = "ERECT_SHIELD"),
		  @Type(value = BuildWormHoleAction.class, name = "BUILD_WORM_HOLE")
		})
public abstract class GameAction {
    /**
     * Az akció típusa.
     */
	@JsonIgnore private GameActionType type;
	/**
	 * A felhasználó által generált egyedi azonosító. Célja, hogy a BOT könnyen beazonosíthassa, 
	 * hogy melyik üzenetére kapta az ActionResponse-t.
	 */
	private int refId;
	/**
	 * Űrmisszó és MBH kilövés esetén a célbavett bolygó azonosítója.
	 * Pajzsfelhúzás esetén a megvédeni kívánt bolygó azonosítoja.
	 * Féreglyuk építés esetén az új féreglyuk generált azonosítója, ezesetben nem kell kitölteni.
	 */
	private int targetId;
}
