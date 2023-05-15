export class GameEvent {

    /**
     * @param {number} width 
     * @param {number} height 
     * @param {Array<Planet>} planets 
     * @param {Array<Wormhole>} wormHoles
     * @param {string} eventType
     */
    constructor(width, height, planets, wormHoles, eventType) {
        this.width = width;
        this.height = height;
        this.planets = planets;
        this.wormHoles = wormHoles;
        this.eventType = eventType;
    }
}

export class Planet {

    /**
     * @param {number} id
     * @param {Position} pos
     * @param {string} color 
     * @param {boolean} destroyed
     * @param {boolean} spaceMissionPossible
     * @param {number} owner
     * @param {boolean} alreadyShot
     * @param {Object} effectsEmitted
     */
    constructor(id, pos, color, destroyed, spaceMissionPossible, owner, alreadyShot, effectsEmitted) {
        this.id = id;
        this.pos = pos;
        this.color = color;
        this.destroyed = destroyed;
        this.spaceMissionPossible = spaceMissionPossible;
        this.owner = owner;
        this.alreadyShot = alreadyShot
        this.effectsEmitted = effectsEmitted;
    }
}

export class Wormhole {

    /**
     * @param {string} name 
     * @param {number} x1 
     * @param {number} y1 
     * @param {number} x2 
     * @param {number} y2 
     * @param {string} color 
     * @param {string} info 
     */
    constructor(name, x1, y1, x2, y2, color, info) {
        this.name = name;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.color = color;
        this.info = info;
    }
}