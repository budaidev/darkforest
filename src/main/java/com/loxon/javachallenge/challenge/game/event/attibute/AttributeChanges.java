package com.loxon.javachallenge.challenge.game.event.attibute;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * A <code>Player</code> és <code>Planet</code> példányok játék közben történő változásait nyilvántartó osztály.
 */
@Data
public class AttributeChanges {

    /**
     * Az érintett <code>Player</code>, vagy <code>Planet</code> példányok id-ja.
     */
    private int affectedId;

    /**
     * Az érintett objektum típusa.<br>
     * Jelenleg 2 értékkel dolgozik az API:
     * <ul>
     *     <li>Player</li>
     *     <li>Planet</li>
     * </ul>
     */
    private String affectedType;

    /**
     * Az attribútumok változását nyilvántartó lista.
     * @see AttributeChange
     */
    private List<AttributeChange> changes = new ArrayList<>();

    /**
     *
     * @param id
     * @param type
     * @return
     */
    public static AttributeChanges createChanges(int id, String type){
        AttributeChanges changes = new AttributeChanges();
        changes.setAffectedId(id);
        changes.setAffectedType(type);
        return changes;
    }
}
