package com.loxon.javachallenge.challenge.game.event.action;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A féreglyuk építés akciója.
 */
@Data
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper = true)
public class BuildWormHoleAction extends GameAction {

    /**
     * Az építeni kívánt Féreglyuk A és B végének vízszintes(x) és függőleges(y) tengely szerinti koordinátái
     */
    private long xa, ya, xb, yb;

    /**
     * A konstruktor, amiben az akció típusa beállítódik.
     */
    public BuildWormHoleAction() {
        setType(GameActionType.BUILD_WORM_HOLE);
    }
}
