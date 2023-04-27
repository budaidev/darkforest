package com.loxon.javachallenge.challenge.game.event.actioneffect;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

/**
 * Akciók hatását leíró osztály.
 */
@Data
@JsonTypeInfo(
		  use = JsonTypeInfo.Id.NAME, 
		  include = JsonTypeInfo.As.PROPERTY, 
		  property = "type")
		@JsonSubTypes({ 
		  @Type(value = ActionEffect.class, name = "AE"),
		  @Type(value = GravityWaveCrossing.class, name = "GWCE"),
		  @Type(value = WormHoleBuiltEffect.class, name = "WHBE"),
		})
public class ActionEffect {

	/**
	 * Az akcióhoz kapcsolódó hatások listája.<br>
	 * Az elküldött JSON üzenetben "ec" kulccsal szerepel.
	 * @see ActionEffectType
	 */
	@JsonProperty("ec") private List<ActionEffectType> effectChain;

	/**
	 * A hatást kiváltó játékos azonosítója.<br>
	 * Az elküldött JSON üzenetben "p" kulccsal szerepel.
	 */
	@JsonProperty("p") private int inflictingPlayer;

	/**
	 * A hatást elszenvedő objektum azonosítója.<br>
	 * Az elküldött JSON üzenetben "id" kulccsal szerepel.
	 */
	@JsonProperty("id") private int affectedMapObjectId;

	/**
	 * Az akció hatás bekövetkezésének időpontja.<br>
	 * Az elküldött JSON üzenetben "ti" kulccsal szerepel.
	 * A szerver órája szerinti, mili-szekundumokban kifejezett idő (EPOH time)
	 */
	@JsonProperty("ti") private long time;
	
}
