package com.loxon.javachallenge.challenge.game.event.actioneffect;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Akciók hatásának konkrét típusát adj meg ez az enum.
 */
public enum ActionEffectType {

    /**
     * MBH becsapódás.
     */
    MBH_HIT("MH", ActionEffectEntitlement.INITIATOR),

    /**
     * Felhúzott pajzs lerombolása.
     */
    SHIELD_DESTROYED("SD", ActionEffectEntitlement.BOTH),

    /**
     * Megsemmisült egy bolygó.
     */
    PLANET_DESTROYED("PD", ActionEffectEntitlement.BOTH),

    /**
     * MBH találat hatására elindult egy gravitációs hullám.
     */
    MBH_HIT_GRAWITY_WAVE_START("MGS", ActionEffectEntitlement.INITIATOR),

    /**
     * MBH találat hatására elindult gravitációs hullám éppen áthalad egy, a játékos által birtokolt objektumon keresztül.
     */
    MBH_HIT_GRAWITY_WAVE_PASSING("MGP", ActionEffectEntitlement.RECEIVER),

    /**
     * Sikeres űrmisszió.
     */
    SPACE_MISSION_SUCCESS("SMS", ActionEffectEntitlement.INITIATOR),

    /**
     * Sikertelen űrmisszió
     */
    SPACE_MISSION_DESTROYED("SMD", ActionEffectEntitlement.INITIATOR),

    /**
     * Űrmisszió hatására kibocsájtott gravitációs hullám.
     */
    SPACE_MISSION_GRAWITY_WAVE_START("SGS", ActionEffectEntitlement.INITIATOR),

    /**
     * Űrmisszió hatására kibocsájtott gravitációs hullám éppen áthalad egy, a játékos által birtokolt objektumon keresztül.
     */
    SPACE_MISSION_GRAWITY_WAVE_PASSING("SGP", ActionEffectEntitlement.RECEIVER),

    /**
     * Felépült egy féreglyuk.
     */
    WORM_HOLE_BUILT("WB", ActionEffectEntitlement.INITIATOR),

    /**
     * Egy felépített féreglyuk hatására kibocsájtott gravitációs hullám.
     */
    WORM_HOLE_BUILT_GRAWITY_WAVE_START("WGS", ActionEffectEntitlement.INITIATOR),

    /**
     * Egy felépített féreglyuk hatására kibocsájtott gravitációs hullám éppen áthalad egy, a játékos által birtokolt objektumon keresztül.
     */
    WORM_HOLE_BUILT_GRAWITY_WAVE_PASSING("WGP", ActionEffectEntitlement.RECEIVER),

    /**
     * Egy felépített pajzs automatikus eltűnésének jelzése.
     */
    SHIELD_TIMEOUT("ST", ActionEffectEntitlement.RECEIVER),

    /**
     * Inaktivitásból eredő gravitációs hullám.
     */
    INACTIVITY_FLARE_START("IFS", ActionEffectEntitlement.INITIATOR),

    /**
     * Inaktivitásból eredő gravitációs hullám éppen áthalad egy, a játékos által birtokolt objektumon keresztül.
     */
    INACTIVITY_FLARE_PASSING("IFP", ActionEffectEntitlement.RECEIVER);

    /**
     * Az enum neve.
     */
    private String shortName;

    /**
     * Az enum hatásköre
     * @see ActionEffectEntitlement
     */
    private ActionEffectEntitlement entitlement;

    /**
     * Az enum konstruktora.
     * @param shortName     Enum neve
     * @param entitlement   Enum hatásköre
     */
    private ActionEffectType(String shortName, ActionEffectEntitlement entitlement) {
        this.shortName = shortName;
        this.entitlement = entitlement;
    }

    /**
     * Az akció nevét lekérdező metódus.
     * @return
     */
    @JsonValue
    public String shortName() {
        return shortName;
    }

    /**
     * Akció hatáskörének a lekérdező metódusa.
     * @return  Az akció beállított hatésköre.
     */
    public ActionEffectEntitlement getEntitlement() {
        return entitlement;
    }
}
