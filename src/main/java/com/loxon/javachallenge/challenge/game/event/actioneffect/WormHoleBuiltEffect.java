package com.loxon.javachallenge.challenge.game.event.actioneffect;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * WormHole építésekor keletkező ActionEffect osztály.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WormHoleBuiltEffect extends ActionEffect {

	/**
	 * Az elkészült WormHole azonosítója.
	 */
	@JsonProperty("whId") private int wormHoleId;

	/**
	 * Az osztály konstruktora.<br>
	 * A WormHole építésekor keletkező megfelelő ActionEffecteket is beállítja.
	 */
	public WormHoleBuiltEffect() {
		setEffectChain(List.of(ActionEffectType.WORM_HOLE_BUILT, ActionEffectType.WORM_HOLE_BUILT_GRAWITY_WAVE_START));
	}
}
