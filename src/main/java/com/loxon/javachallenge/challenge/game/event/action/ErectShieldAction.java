package com.loxon.javachallenge.challenge.game.event.action;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A pajzsemelés akciója.
 */
@Data
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper = true)
public class ErectShieldAction extends GameAction {

    public ErectShieldAction() {
        setType(GameActionType.ERECT_SHIELD);
    }
}
