package com.loxon.javachallenge.challenge.game.event;

/**
 * A játékhoz való csatlakozás lehet sikeres és sikertelen.
 * Ennek a két esetnek különböző okait tárolja ez az enum.
 */
public enum ConnectionResultType {

	/**
	 * Sikeres csatlakozás.
	 */
	SUCCESS("Succesful connection."),

	/**
	 * Sikertelen csatlakozás érvénytelen GameId miatt.
	 */
	INVALID_GAMEID("Invalid GameId was provided."),

	/**
	 * Sikertelen csatlakozás érvénytelen GameKey miatt.
	 */
	INVALID_GAMEKEY("Invalid GameKey was provided."),

	/**
	 * Hibás URL-lel történő sikertelen csatlakozás.
	 */
	WRONG_CONNECTION_URL("Missing URL parts. "
            + "The last bits of the URL should look something like this: "
            + "/game?gameId=xxx&gameKey=xxx&connectionType=[control|visualization]. "
            + "To enable privileged mode, weld this optional piece of string into the URL: "
            + "&mode=privileged as it will let you see the whole map, provided you have the rights.");

	/**
	 * A csatlakozáshoz kapcsolt állapotüzenet.
	 */
	private String message;

	/**
	 * Az enum konstruktora.
	 * @param message	A csatlakozáshoz kapcsolt állapotüzenet.
	 */
	private ConnectionResultType(String message) {
		this.message = message;
	}

	/**
	 * Az állapotot lekérdező getter.
	 * @return
	 */
	public String getMessage() {
		return message;
	}


}
