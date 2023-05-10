class GameEvent {

    /**
     * @param {number} width 
     * @param {number} height 
     * @param {Array<Planet>} planets 
     * @param {Array<Wormhole>} wormholes
     * @param {string} eventType
     */
    constructor(width, height, planets, wormholes, eventType) {
        this.width = width;
        this.height = height;
        this.planets = planets;
        this.wormholes = wormholes;
        this.eventType = eventType;
    }
}

class Planet {

    /**
     * @param {string} name 
     * @param {number} id
     * @param {number} x 
     * @param {number} y 
     * @param {number} radius 
     * @param {string} color 
     * @param {string} info 
     */
    constructor(name, id, x, y, radius, color, info) {
        this.name = name;
        this.id = id;
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;
        this.info = info;
    }
}

class Wormhole {

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

class Position {
    /**
     * @param {number} x 
     * @param {number} y 
     */
    constructor(x, y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @param {number} offset 
     * @returns {Position}
     */
    translate(offset) {
        return new Position(this.x + offset, this.y + offset);
    }
}

class UIController {
    static #PlanetSize = 25;
    static #Colors = ["gray", "red", "blue", "green", "yellow", "orange", "purple", "pink", "brown"];

    #container;

    #mapWidth = undefined;
    #mapHeight = undefined;

    #containerWidth;
    #containerHeight;

    #resizeObserver;

    #planetElements;
    #wormholeElements;
    #planets;
    #wormholes;

    #playerDict = {
        0: 0
    };

    #playerId = 57;

    /**
     * @param {string} containerId 
     */
    constructor(containerId) {
        this.#container = document.getElementById(containerId);

        this.#containerWidth = this.#container.clientWidth;
        this.#containerHeight = this.#container.clientHeight;

        this.#reset();

        this.#resizeObserver = new ResizeObserver(() => {
            this.#containerWidth = this.#container.clientWidth;
            this.#containerHeight = this.#container.clientHeight;

            if (this.#mapWidth !== undefined && this.#mapHeight !== undefined) {
                this.#renderPlanets(this.#planets.values());
                this.#renderWormholes(this.#wormholes.values());
            }
        });

        this.#resizeObserver.observe(this.#container);
    }

    /**
     * @param {GameEvent} gameEvent
     */
    receiveGameEvent(gameEvent) {
        this.#setMapSize(gameEvent);

        if (gameEvent.eventType === 'GAME_STARTED') {
            this.#reset();

            const players = gameEvent.players;

            for (let i = 0; i < players.length; i++) {
                this.#playerDict[players[i].id] = i+1;
            }
        } else if (gameEvent.eventType === 'ACTION_EFFECT') {
            const actionEffect = gameEvent.actionEffect;

            if (actionEffect.p !== this.#playerId) {
                const planet = this.#planets.get(actionEffect.id);
                const planetPosition = this.#calculateRenderedPosition(planet.x, planet.y, UIController.#PlanetSize);

                this.#drawLine(planetPosition.x, planetPosition.y, 5*10, actionEffect.dir);
            }
        } else if (gameEvent.eventType === 'CONNECTION_RESULT') {
            // this.#playerId = gameEvent.connectionResult.playerId;
        }

        if (gameEvent.planets && gameEvent.planets.length > 0) {
            this.#renderPlanets(gameEvent.planets);
        }

        if (gameEvent.wormholes && gameEvent.wormholes.length > 0) {
            this.#renderWormholes(gameEvent.wormholes);
        }
    }

    /**
     * @returns {void}
     */
    #reset() {
        this.#container.innerHTML = '';

        this.#planetElements = new Map();
        this.#wormholeElements = new Map();
        this.#planets = new Map();
        this.#wormholes = new Map();

        this.#playerDict = {
            0: 0
        };
    }

    /**
     * @param {GameEvent} gameEvent 
     */
    #setMapSize(gameEvent) {
        if (this.#mapWidth === undefined || this.#mapHeight === undefined) {
            if (gameEvent.width && gameEvent.height) {
                this.#mapWidth = gameEvent.width;
                this.#mapHeight = gameEvent.height;
            }
        }
    }

    /**
     * @param {Array<Planet>} planets 
     */
    #renderPlanets(planets) {
        for (const planet of planets) {
            if (this.#planetElements.has(planet.id)) {
                this.#updatePlanetElement(planet, this.#planetElements.get(planet.id));
            } else {
                const planetDiv = this.#createPlanetElement(planet);

                this.#planetElements.set(planet.id, planetDiv);
                this.#planets.set(planet.id, planet);
                this.#container.appendChild(planetDiv);
            }
        }
    }

    /**
     * @param {Planet} planet 
     * @returns {HTMLDivElement}
     */
    #createPlanetElement(planet) {
        const planetDiv = document.createElement('div');
        
        this.#updatePlanetElement(planet, planetDiv);

        return planetDiv;
    }

    /**
     * @param {Planet} planet 
     * @param {HTMLDivElement} planetDiv 
     * @returns {void}
     */
    #updatePlanetElement(planet, planetDiv) {
        const displayedName = planet.name ?? planet.id;
        const displayedInfo = `pos=(${planet.x}, ${planet.y})`;
        const backgroundColor = UIController.#Colors[this.#playerDict[planet.player]] ?? 'red';
        const renderedPosition = this.#calculateRenderedPosition(planet.x, planet.y, UIController.#PlanetSize);

        planetDiv.className = 'planet';
        planetDiv.id = 'planet' + planet.id
        planetDiv.style.width = UIController.#PlanetSize + 'px';
        planetDiv.style.height = UIController.#PlanetSize + 'px';
        planetDiv.style.backgroundColor = backgroundColor;
        planetDiv.style.left = renderedPosition.x + 'px';
        planetDiv.style.top = renderedPosition.y + 'px';

        const planetPopup = document.createElement('div');
        planetPopup.className = 'planet-popup';
        planetPopup.innerHTML = `<strong>${displayedName}</strong><br>${displayedInfo}<br>player: ${planet.player}`;

        planetDiv.innerHTML = '';
        planetDiv.appendChild(planetPopup);
    }

    /**
     * @param {number} x 
     * @param {number} y 
     * @param {number} sizeToOffset
     * @returns {Position}
     */
    #calculateRenderedPosition(x, y, sizeToOffset = 0) {
        const widthRatio = this.#containerWidth / this.#mapWidth;
        const heightRatio = this.#containerHeight / this.#mapHeight;

        const renderedPosition = new Position(Math.round(x * widthRatio), Math.round(y * heightRatio));
        const offset = Math.floor(sizeToOffset / 2);

        return renderedPosition.translate(-offset);
    }

    /**
     * @param {Array<Wormhole>} wormholes
     * @returns {HTMLDivElement}
     */
    #renderWormholes(wormholes) {
        for (const wormhole of wormholes) {

            const dx = wormhole.x2 - wormhole.x1;
            const dy = wormhole.y2 - wormhole.y1;
            const angle = Math.atan2(dy, dx) * 180 / Math.PI;
            const length = Math.sqrt(dx * dx + dy * dy);

            const wormholeDiv = document.createElement('div');
            wormholeDiv.className = 'wormhole';
            wormholeDiv.style.width = length + 'px';
            wormholeDiv.style.left = wormhole.x1 + 'px';
            wormholeDiv.style.top = wormhole.y1 - 5 + 'px';
            wormholeDiv.style.transform = 'rotate(' + angle + 'deg)';
            wormholeDiv.style.backgroundColor = wormhole.color;

            const wormholePopup = document.createElement('div');
            wormholePopup.className = 'wormhole-popup';
            wormholePopup.innerHTML = '<strong>' + wormhole.name + '</strong><br>' + wormhole.info;

            wormholeDiv.appendChild(wormholePopup);
            document.getElementById('container').appendChild(wormholeDiv);
        }
    }

    #drawLine(x, y, len, angleRad) {
        // Convert angle from degrees to radians
        // Calculate the end point of the line
        const endX = x + len * Math.cos(angleRad);
        const endY = y + len * Math.sin(angleRad);

        // Create a new div element to represent the line
        const line = document.createElement("div");

        // Set the position and dimensions of the line
        line.className = "effect-line"
        line.style.top = y + "px";
        line.style.left = x + "px";
        line.style.width = len + "px";

        // Rotate the line to match the angle
        line.style.transform = `rotate(${angleRad + Math.PI/2}rad)`;

        // Add the line to the DOM
        this.#container.appendChild(line);
    }
}