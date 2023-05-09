class GameEvent {

    /**
     * @param {number} width 
     * @param {number} height 
     * @param {Array<Planet>} planets 
     * @param {Array<Wormhole>} wormholes 
     */
    constructor(width, height, planets, wormholes) {
        this.width = width;
        this.height = height;
        this.planets = planets;
        this.wormholes = wormholes;
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

    #container;

    #mapWidth = undefined;
    #mapHeight = undefined;

    #containerWidth;
    #containerHeight;

    #resizeObserver;

    #planetElements;
    #wormholeElements;
    #planets = [];
    #wormholes = [];

    /**
     * @param {string} containerId 
     */
    constructor(containerId) {
        this.#container = document.getElementById(containerId);

        this.#containerWidth = this.#container.clientWidth;
        this.#containerHeight = this.#container.clientHeight;

        this.#planetElements = new Map();
        this.#wormholeElements = new Map();
        this.#planets = new Map();
        this.#wormholes = new Map();

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

        if (gameEvent.planets && gameEvent.planets.length > 0) {
            this.#renderPlanets(gameEvent.planets);
        }

        if (gameEvent.wormholes && gameEvent.wormholes.length > 0) {
            this.#renderWormholes(gameEvent.wormholes);
        }
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
        const backgroundColor = planet.color ?? 'red';
        const renderedPosition = this.#calculateRenderedPosition(planet.x, planet.y, UIController.#PlanetSize);

        planetDiv.className = 'planet';
        planetDiv.style.width = UIController.#PlanetSize + 'px';
        planetDiv.style.height = UIController.#PlanetSize + 'px';
        planetDiv.style.backgroundColor = backgroundColor;
        planetDiv.style.left = renderedPosition.x + 'px';
        planetDiv.style.top = renderedPosition.y + 'px';

        const planetPopup = document.createElement('div');
        planetPopup.className = 'planet-popup';
        planetPopup.innerHTML = `<strong>${displayedName}</strong><br>${displayedInfo}`;

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
        wormholes.forEach(wormhole => {

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
        })
    }
}