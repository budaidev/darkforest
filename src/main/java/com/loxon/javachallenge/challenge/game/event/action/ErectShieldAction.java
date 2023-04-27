package com.loxon.javachallenge.challenge.game.event.action;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A pajzsemelés akciója.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ErectShieldAction extends GameAction {

    public ErectShieldAction() {
        setType(GameActionType.ERECT_SHIELD);
    }
}
