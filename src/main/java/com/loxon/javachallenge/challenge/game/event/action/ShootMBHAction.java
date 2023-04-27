package com.loxon.javachallenge.challenge.game.event.action;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Az MBH kilövés akciója.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ShootMBHAction extends GameAction {
    /**
     * A kilövés forrásának meghatározására szolgáló mező.
     * Csak birtokolt bolygó lehet.
     */
    private int originId;

    /**
     * A konstruktor, amiben az akció típusa beállítódik.
     */
    public ShootMBHAction() {
        setType(GameActionType.SHOOT_MBH);
    }
}
