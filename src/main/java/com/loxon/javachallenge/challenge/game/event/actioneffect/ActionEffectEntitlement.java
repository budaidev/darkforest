package com.loxon.javachallenge.challenge.game.event.actioneffect;

/**
 * Az <code>ActionEffect</code> hatásköre.
 */
public enum ActionEffectEntitlement {

    /**
     * Akciót Kezdeményező enum típus.
     */
    INITIATOR,

    /**
     * Akciót fogadó enum típus.
     */
    RECEIVER,

    /**
     * Egyszerre <code>INITIATOR</code> és <code>RECEIVER</code> módot jelető enum típus.
     */
    BOTH
}
