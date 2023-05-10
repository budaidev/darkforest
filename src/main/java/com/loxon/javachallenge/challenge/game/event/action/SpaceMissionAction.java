package com.loxon.javachallenge.challenge.game.event.action;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper = true)
public class SpaceMissionAction extends GameAction {
    /**
     * A kiindulási bolygó egyedi azonosítója. Csak birtokolt bolygó lehet.
     */
    private int originId;
    /**
     * A misszióhoz használni kívánt Féreglyuk egyedi azonosítója.
     * 0 érték esetén a misszió nem fog féreglyukat használni.
     */
    private int wormHoleId;
    /**
     * Megadja, hogy A vagy B végén akarunk bemenni a Féreglyukba
     */
    private EntryPointIndex entryPointIndex;

    /**
     * A konstruktor, amiben az akció típusa beállítódik.
     */
    public SpaceMissionAction() {
        setType(GameActionType.SPACE_MISSION);
    }
}
