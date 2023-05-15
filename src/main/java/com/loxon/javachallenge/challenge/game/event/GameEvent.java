package com.loxon.javachallenge.challenge.game.event;

import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.loxon.javachallenge.challenge.game.event.action.ActionResponse;
import com.loxon.javachallenge.challenge.game.event.actioneffect.ActionEffect;
import com.loxon.javachallenge.challenge.game.event.attibute.AttributeChanges;
import com.loxon.javachallenge.challenge.game.model.Game;
import com.loxon.javachallenge.challenge.game.model.GameStats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;


/**
 * Játékbeli esemény(eke)t leíró osztály.
 * */
@Getter
@EqualsAndHashCode
@Builder
@AllArgsConstructor
public class GameEvent {

	private static AtomicInteger idGenerator = new AtomicInteger();
	
	/**
	 * Az esemény egyedi azonosítója.
	 */
	private int id;
	
	/**
	 * Az eseményhez társított játék (generált) azonosítója.
	 */
	@JsonIgnore private String gameId;

	/**
	 * Az esemény típusa.
	 * @see EventType
	 */
	private EventType eventType;

	/**
	 * Az esemény bekövetkezésének ideje.<br>
	 * Az elküldött JSON üzenetben "et" néven szerepel.
	 */
	@JsonProperty(value = "et", required = false) private long eventTime;

	/**
	 * Az eseményhez köthető pályaelemek változásait tárolja.<br>
	 * Az elküldött JSON üzenetben "cs" néven szerepel.
	 * @see AttributeChanges
	 */
	@JsonInclude(Include.NON_NULL) @JsonProperty(value ="cs", required = false) private AttributeChanges changes;

	/**
	 * Az eseményre küldött válasz objektum.<br>
	 * Az elküldött JSON üzenetben "a" kulccsal szerepel.
	 * @see ActionResponse
	 */
	@JsonInclude(Include.NON_NULL) @JsonProperty(value ="a", required = false) private ActionResponse action;

	/**
	 * Az esemény bekövetkezésekor indított ActionEffect-eket tárolja.<br>
	 * Az elküldött JSON üzenetben "ae" kulccsal szerepel.
	 * @see ActionEffect
	 */
	@JsonInclude(Include.NON_NULL) @JsonProperty(value ="ae", required = false) private ActionEffect actionEffect;

	/**
	 * A játékteret tárolja.<br>
	 * Az elküldött JSON üzenetben "g" kulccsal szerepel.
	 * @see Game
	 */
	@JsonInclude(Include.NON_NULL) @JsonProperty(value ="g", required = false) private Game game;

	/**
	 * A játékkal kapcsolatos statisztikákat tárolja.<br>
	 * Az elküldött JSON üzenetben "gameStats" kulccsal szerepel.
	 * @see GameStats
	 */
	@JsonInclude(Include.NON_NULL) @JsonProperty(value = "gameStats", required = false) private GameStats gameStats;

	/**
	 * A játékhoz való kapcsolódás eredményét tárolja.<br>
	 * Az elküldött JSON üzenetben "connectionResult" kulccsal szerepel.
	 * @see ConnectionResult
	 */
	@JsonInclude(Include.NON_NULL) @JsonProperty(value = "connectionResult", required = false) private ConnectionResult connectionResult;

	public GameEvent() {
	}
	
	/**
	 * Privát konstruktor, a GameEvent példányok létrehozásáért felel.<br>
	 * Beállítja az eventTime paramétert az aktuális időpontnak megfelelő long értékre.
	 * @param gameId    adott játék id-ja, amelyhez hozzá szeretnénk kötni a GameEvent példányt
	 */
	private GameEvent(String gameId) {
		eventTime = System.currentTimeMillis();
		id = idGenerator.getAndAdd(1);
		this.gameId = gameId;
	}

	/**
	 * A changes változó setter metódusa.
	 * @param changes    Erre változtatja meg a changes aktuális értékét.
	 */
	public void setChanges(AttributeChanges changes) {
		if (changes != null) {
			eventType = EventType.ATTRIBUTE_CHANGE;
			this.changes = changes;
		}
	}

	/**
	 * Az action változó setter metódusa.<br>
	 * Az eventType értéke <code>EventType.ACTION</code> lesz.
	 * @param action    Erre változtatja meg az action aktuális értékét.
	 */
	public void setAction(ActionResponse action) {
		if (action != null) {
			eventType = EventType.ACTION;
			this.action = action;
		}
	}


	/**
	 * Az actionEffect változó setter metódusa.<br>
	 * Az eventType értéke <code>EventType.ACTION_EFFECT</code> lesz.
	 * @param actionEffectChain    Erre változtatja meg az actionEffect aktuális értékét.
	 */
	public void setActionEffectChain(ActionEffect actionEffectChain) {
		if (actionEffectChain != null) {
			eventType = EventType.ACTION_EFFECT;
			this.actionEffect = actionEffectChain;
		}
	}

	/**
	 * A game változó setter metódusa.<br>
	 * Az eventType értéke <code>EventType.GAME_STARTED</code> lesz.
	 * @param game    Erre változtatja meg a game aktuális értékét.
	 */
	public void setGame(Game game) {
		if (game != null) {
			eventType = EventType.GAME_STARTED;
			this.game = game;
		}
	}

	/**
	 * A gameStats változó setter metódusa.<br>
	 * Az eventType értéke <code>EventType.GAME_ENDED</code> lesz.
	 * @param gameStats    Erre változtatja meg a gameStats aktuális értékét.
	 */
	public void setGameStats(GameStats gameStats) {
		if (gameStats != null) {
			eventType = EventType.GAME_ENDED;
			this.gameStats = gameStats;
		}
	}

	/**
	 * A connectionResult változó setter metódusa.<br>
	 * Az eventType értéke <code>EventType.CONNECTION_RESULT</code> lesz.
	 * @param connectionResult    Erre változtatja meg a connectionResult aktuális értékét.
	 */
	public void setConnectionResult(ConnectionResult connectionResult) {
		if (connectionResult != null) {
			eventType = EventType.CONNECTION_RESULT;
			this.connectionResult = connectionResult;
		}
	}

	/**
	 * Az adatbázisba való logoláshoz szükséges adatot adja vissza.
	 */
	@JsonIgnore
	public Object getPayload() {
		switch (eventType) {
			case ACTION: return action;
			case ACTION_EFFECT: return actionEffect;
			case ATTRIBUTE_CHANGE: return changes;
			case GAME_STARTED: return game;
			case GAME_ENDED: return gameStats;
			case CONNECTION_RESULT: return connectionResult;
		}
		return null;
	}

	/**
	 * <code>GameEvent</code>-et állít elő a paraméterezés alapján.
	 * @param gameId	A társított játék azonosítója.
	 * @param o			Dinamikus paraméter, ami alapján létrejön a megfelelő <code>GameEvent</code> példány.
	 * @return			A generált <code>GameEvent</code> példány.
	 */
	public static GameEvent wrapEvent(String gameId, Object o) {
        GameEvent event = new GameEvent(gameId);

        if (o instanceof AttributeChanges) {
            AttributeChanges changes = (AttributeChanges) o;
            event.setChanges(changes);
        } else if (o instanceof ActionResponse) {
            ActionResponse action = (ActionResponse) o;
            event.setAction(action);
        } else if (o instanceof ActionEffect) {
			ActionEffect effect = (ActionEffect) o;
			event.setActionEffectChain(effect);
        } else if (o instanceof Game) {
			event.setGame((Game) o);
        } else if (o instanceof GameStats) {
			event.setGameStats((GameStats) o);
        } else if (o instanceof ConnectionResult) {
			event.setConnectionResult((ConnectionResult) o);
        } else {
            throw new RuntimeException("Unknown eventType:" + o.getClass().getSimpleName());
        }

        return event;
    }
	
	public static GameEvent wrapEvent(Game game, Object o) {
		return wrapEvent(game.getGameId(), o);
	}

}
