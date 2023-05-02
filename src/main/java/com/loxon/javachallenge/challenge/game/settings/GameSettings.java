package com.loxon.javachallenge.challenge.game.settings;

import lombok.Data;

/**
 * A játék paraméterezését tároló osztály.
 */
@Data
public class GameSettings {
	/**
	 * A játékpálya szélessége.
	 */
	private int width;

	/**
	 * A játékpálya magassága.
	 */
	private int height;

	/**
	 * 1 fényév megtételéhez szükséges egységnyi idő. (miliszekundumban kifejezve)
	 */
	private int timeOfOneLightYear;

	/**
	 * Párhuzamosan egyszerre elvégezhető akciók száma.
	 */
	private int maxConcurrentActions;

	/**
	 * 1 pajzs felépítéséhez szükséges idő.
	 */
	private int timeToBuildShild;

	/**
	 * Pajzs fennmaradásának hossza.
	 */
	private int shildDuration;

	/**
	 * Időtartam, mely után passzivitás GravityWave indul, amennyiben a játékos inaktív volt.
	 */
	private int passivityTimeTreshold;

	/**
	 * Az egyes játékosok által maximálisan építhető WormHole-ok száma.
	 */
	private int maxWormHolesPerPlayer;

	/**
	 * Űrmisszió/féreglyuk építés/MBH becsapódás forrásának az irányának a pontossága.
	 */
	private int gravityWaveSourceLocationPrecision;

	/**
	 * Becsapódott MBH lövés irányának pontossága.
	 */
	private int mbhShootOriginPrecision;

	/**
	 * Passzivitásból eredő forrás irányának pontossága.
	 */
	private int passivityFleshPrecision;

	/**
	 * WormHole építéséhez szükséges idő.
	 */
	private int timeOfBuildWormHole;

	/**
	 * 1 elfoglalt bolygért járó pontszám.
	 */
	private int pointsPerColonizedPlanets;

	/**
	 * 1 elfoglalt bolygó elpusztásáért járó pontszám.
	 */
	private int pointsPerDerstroyedHostilePlanets;

	/**
	 * 1 MBH kivédéséért járó pontszám.
	 */
	private int pointsPerMbhsDeflected;

}
