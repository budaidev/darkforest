package com.loxon.javachallenge.challenge.game.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A játékost a  játékhoz való csatlakozás erdményével összekötő osztály.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionResult {
	/**
	 * A csatlakozó játékos azonosítója.
	 */
	private int playerId;

	/**
	 * A csatlakozás eredménye.
	 */
	private ConnectionResultType connectionResultType;
}
