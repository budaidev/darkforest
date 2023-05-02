package com.loxon.javachallenge.challenge.game.event.action;

/**
 * Egy akció kimenetele változhat, lehet sikeres és sikertelen.
 * Ennek a két esetnek különböző okait tárolja ez az enum.
 */
public enum ActionResult {
    /**
     * Az az eset, amikor egy akció sikeresen teljesítve van.
     */
    SUCCESS("Action successfully completed.", false),
    /**
     * Az az eset, amikor egy akciót nem lehet teljesíteni, mert a játékos kifogyott az indítható akciókból.
     */
    INSUFFICIENT_ACTIONS("You ran out of concurrent actions to initiate", true),
    /**
     * Az az eset, amikor egy akciót nem lehet teljesíteni, mert a játékos nem definiált célbolygót,
     * vagy nem létező bolygóazonosítót adott meg vagy pajzsfelhúzás esetén nem a saját bolygóját adta meg.
     */
    TARGET_PLANET_INVALID("You forgot to specify the target planet.", true),
    /**
     * Az az eset, amikor egy akciót nem lehet teljesíteni, mert a játékos nem definiált kiindulási bolygót
     * vagy nem létező bolybóazonosítót adott meg.
     */
    ORIGIN_PLANET_INVALID("You forgot to specify the origin planet.", true),
    /**
     * Az az eset, amikor egy akciót nem lehet teljesíteni, mert a játékos pajzsa blokkolja.
     */
    SHIELD_BLOCKED_ACTION("You cannot perform that action with shields activated.", true),
    /**
     * Az az eset, amikor egy akciót nem lehet teljesíteni, mert a játékos rossz féreglyuk ID-t definiált.
     */
    INVALID_WORM_HOLE_ID("The worm hole id you defined is invalid.", true),
    /**
     * Az az eset, amikor egy akciót nem lehet teljesíteni, mert a játékos rossz féreglyuk bemeneteli pontot definiált.
     */
    INVALID_ENTRY_POINT("The entry point of signed wormhole is invalid", true),
    /**
     * Az az eset, amikor a játékos kifogyott a számára elérhető építhető féreglyuk mennyiségből.
     */
    MAXIMUM_WORM_HOLES_REACHED("You have reached the maximum amount of worm holes available per civilization.", true),
    /**
     * Az az eset, amikor egy akciót nem lehet teljesíteni, mert a játékos már elpusztított bolygót adott meg célpontknak.
     */
    DESTROYED_PLANET("The planet you specified has already been destroyed.", true),
    /**
     * Az az eset, amikor a játékos egy nem létező játékhoz szeretne csatlakozni.
     */
    GAME_DOES_NOT_EXISTS("The game does not exist anymore.", true),
    /**
     * Az az eset, amikor a játék még nem fut de a játékos már cselekedne.
     */
    GAME_HAS_NOT_BEEN_STARTED("The game has not been started yet.", true),
    /**
     * Az az eset, amikor a játék már nem fut.
     */
    GAME_IS_ENDED("The game has ended.", true),
    /**
     * Ez az eset, amikor hibás formátumban próbál a játékos GameAction-t futtatni.
     */
    WRONG_MESSAGE_FORMAT("Unable to parse message to a GameAction object.", true);

    /**
     * Az akció kimenetelének szöveges üzenete.
     */
    private final String message;
    /**
     * Egy bináris változó annak eldöntésére, hogy a kimenetel sikeres vagy sikertelen.
     */
    private final boolean error;

    /**
     * A konstruktora ennek az enum-nak.
     *
     * @param message indok
     * @param error   hiba vagy sem
     */
    ActionResult(String message, boolean error) {
        this.message = message;
        this.error = error;
    }

    /**
     * Indok lekérdezése külső osztályoknak.
     *
     * @return indok
     */
    public String getMessage() {
        return message;
    }

    /**
     * Kimenetel komolyságának lekérdezés külső osztályoknak.
     *
     * @return hiba vagy sem
     */
    public boolean isError() {
        return error;
    }
}
