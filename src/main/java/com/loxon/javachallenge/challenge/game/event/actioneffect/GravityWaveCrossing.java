package com.loxon.javachallenge.challenge.game.event.actioneffect;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.loxon.javachallenge.challenge.game.model.GravityWaveCause;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Egy GravityWave adatait leíró osztály.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GravityWaveCrossing extends ActionEffect {

	/**
	 * A GravityWave-et kiváltó ok.
	 * @see GravityWaveCause
	 */
	@JsonProperty("c") private GravityWaveCause cause;

	/**
	 * A GravityWave-et kiváltó objektum forrásának azonosítója.
	 */
	@JsonProperty("sId") private int sourceId;

	/**
	 * MBH esetén: a GravityWave-et kiváltó lövés forrásának iránya.
	 * Űrmisszió esetén: az űrmisszió az érkezési bolygó irány-szöge valamennyi bizonytalansággal.
	 * Féreglyuk elkészülés esetén: az építő játékos, féreglyukhoz legközelebb álló bolygójának az iránya. 
	 * Passzivitás esetén: a passzív játékos egy véletlenszerűen kiválasztott bolygójának az irányszöge, valamennyi bizonytalansággal.
	 * 
	 */
	@JsonProperty("dir") private double direction;
}
