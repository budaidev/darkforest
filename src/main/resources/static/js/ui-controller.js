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
     * @param {number} id
     * @param {Position} pos
     * @param {string} color 
     * @param {boolean} destroyed
     * @param {boolean} spaceMissionPossible
     * @param {number} owner
     */
    constructor(id, pos, color, destroyed, spaceMissionPossible, owner) {
        this.id = id;
        this.pos = pos;
        this.color = color;
        this.destroyed = destroyed;
        this.spaceMissionPossible = spaceMissionPossible;
        this.owner = owner;
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

class SVGFactory {
    static #XMLNS = 'http://www.w3.org/2000/svg';

    /**
     * @param {Object} properties 
     * @returns {Element}
     */
    static createViewBox(width, height) {
        const svgElement = document.createElementNS(SVGFactory.#XMLNS, 'svg');

        const properties = {
            viewBox: `0 0 ${width} ${height}`,
            preserveAspectRatio: 'none'
        };

        SVGFactory.applyProperties(svgElement, properties);

        return svgElement;
    }

    /**
     * @param {Object} properties 
     * @returns {Element}
     */
    static createElement(name, properties) {
        const svgElement = document.createElementNS(SVGFactory.#XMLNS, name);
        SVGFactory.applyProperties(svgElement, properties);

        return svgElement;
    }

    /**
     * @param {Element} element 
     * @param {Object | undefined} properties 
     * @returns {void}
     */
    static applyProperties(element, properties = undefined) {
        if (properties) {
            for (const propName in properties) {
                const propValue = properties[propName];

                if (propValue !== undefined) {
                    element.setAttributeNS(null, propName, propValue);
                } else {
                    element.removeAttributeNS(null, propName);
                }
            }
        }
    }
}

/**
 * @param {string} elementId 
 */
const show = (elementId) => {
    SVGFactory.applyProperties(document.getElementById(elementId), {
        visibility: undefined
    });
}

/**
 * @param {string} elementId 
 */
const hide = (elementId) => {
    SVGFactory.applyProperties(document.getElementById(elementId), {
        visibility: 'hidden'
    });
}

class UISVGController {
    static #PlanetSize = 12.5;
    static #Colors = ["gray", "red", "blue", "green", "yellow", "orange", "purple", "pink", "brown"];
    static #PopupFontSize = 14;
    static #PopupPadding = 5;

    static #AnimationIntervalMs = 1000;
    static #RemoveWavesAfterMs = 6000;

    static #ShowPlayerActionEffects = true;

    #container;
    #svgContainer;

    #mapWidth = undefined;
    #mapHeight = undefined;

    #containerWidth;
    #containerHeight;

    #resizeObserver;

    #planets;
    #planetElements;
    #planetPopupElements;
    #waveElements;

    #allowAnimation = true;

    #playerDict = {
        0: 0
    };

    #playerId = 57;

    /**
     * @param {string} containerId 
     */
    constructor(containerId) {
        this.#container = document.getElementById(containerId);

        this.#reset();

        this.#resizeObserver = new ResizeObserver(() => {
            this.#containerWidth = this.#container.clientWidth;
            this.#containerHeight = this.#container.clientHeight;

            SVGFactory.applyProperties(this.#svgContainer, {
                viewBox: `0 0 ${this.#containerWidth} ${this.#containerHeight}`,
                preserveAspectRatio: 'none'
            });

            if (this.#mapWidth !== undefined && this.#mapHeight !== undefined) {
                this.#renderPlanets(this.#planets.values());
            }
        });

        this.#resizeObserver.observe(this.#container);
        setInterval(this.#animate.bind(this), UISVGController.#AnimationIntervalMs);
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

            if (UISVGController.#ShowPlayerActionEffects || actionEffect.p !== this.#playerId) {
                const planet = this.#planets.get(actionEffect.id);
                const planetPosition = this.#calculateRenderedPosition(planet.pos.x, planet.pos.y);
                const color = UISVGController.#Colors[this.#playerDict[actionEffect.p]]

                this.#drawLine(planetPosition.x, planetPosition.y, 50, actionEffect.dir, color);
            }
        } else if (gameEvent.eventType === 'CONNECTION_RESULT') {
            // this.#playerId = gameEvent.connectionResult.playerId;
        }

        if (gameEvent.planets && gameEvent.planets.length > 0) {
            this.#renderPlanets(gameEvent.planets);
        }

        if(gameEvent.wormholes && gameEvent.wormholes.length > 0) {
            this.#renderWormholes(gameEvent.wormholes);
        }
    }

    enableAnimation() {
        this.#allowAnimation = true;
    }

    disableAnimation() {
        this.#allowAnimation = false;
    }

    /**
     * @returns {void}
     */
    #reset() {
        this.#container.innerHTML = '';
        this.#svgContainer = SVGFactory.createViewBox(this.#containerWidth, this.#containerHeight);
        this.#container.appendChild(this.#svgContainer);

        this.#planetElements = new Map();
        this.#planetPopupElements = new Map();
        this.#waveElements = [];
        this.#planets = new Map();

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
        const popups = [];
        for (const planet of planets) {
            if (this.#planetElements.has(planet.id)) {
                this.#updatePlanetElement(planet, this.#planetElements.get(planet.id));
                this.#updatePlanetPopupElement(planet, this.#planetPopupElements.get(planet.id));
            } else {
                const planetDiv = this.#createPlanetElement(planet);

                this.#planetElements.set(planet.id, planetDiv);
                this.#planets.set(planet.id, planet);
                this.#svgContainer.appendChild(planetDiv);

                const planetPopup = this.#createPlanetPopup(planet);
                this.#planetPopupElements.set(planet.id, planetPopup);

                SVGFactory.applyProperties(planetDiv, {
                    onmouseenter: `show('planet-popup-${planet.id}')`,
                    onmouseleave: `hide('planet-popup-${planet.id}')`,
                });

                popups.push(planetPopup);
            }
        }

        popups.forEach(planetPopup => this.#svgContainer.appendChild(planetPopup));
    }

    /**
     * @param {Planet} planet 
     * @returns {HTMLDivElement}
     */
    #createPlanetElement(planet) {
        const planetDiv = SVGFactory.createElement('circle');
        planetDiv.id = 'planet' + planet.id

        SVGFactory.applyProperties(planetDiv, {
            r: UISVGController.#PlanetSize
        });
        
        this.#updatePlanetElement(planet, planetDiv);

        return planetDiv;
    }

    /**
     * @param {Planet} planet 
     * @param {HTMLDivElement} planetDiv 
     * @returns {void}
     */
    #updatePlanetElement(planet, planetDiv) {
        const backgroundColor = UISVGController.#Colors[this.#playerDict[planet.owner]] ?? 'red';
        const renderedPosition = this.#calculateRenderedPosition(planet.pos.x, planet.pos.y);

        planetDiv.style.fill = backgroundColor;

        if (planet.destroyed === true) {
            planetDiv.style.fill = 'black';
        }

        if (planet.owner !== this.#playerId && planet.spaceMissionPossible === false) {
            planetDiv.style.fill = 'darkgrey';
        }

        SVGFactory.applyProperties(planetDiv, {
            cx: renderedPosition.x,
            cy: renderedPosition.y,
        });
    }

    #createPlanetPopup(planet) {
        const renderedPosition = this.#calculateRenderedPosition(planet.pos.x, planet.pos.y);
        const displayedName = planet.id;

        const rectX = renderedPosition.x + UISVGController.#PlanetSize;
        const rectY = renderedPosition.y - UISVGController.#PlanetSize;

        const infoList = this.#createPlanetInfo(planet);
        const textLength = infoList.reduce((acc, current) => Math.max(acc, current.length), `${planet.id}`.length);

        const rect = SVGFactory.createElement('rect', {
            x: rectX,
            y: rectY,
            width: textLength*8 + UISVGController.#PopupPadding*2 + 2,
            height: (infoList.length+1) * UISVGController.#PopupFontSize + 2 + UISVGController.#PopupPadding*2,
            fill: 'white'
        });

        const titleText = SVGFactory.createElement('text', {
            x: rectX + UISVGController.#PopupPadding,
            y: rectY + UISVGController.#PopupFontSize + UISVGController.#PopupPadding,
            'font-size': UISVGController.#PopupFontSize,
            'font-weight': 'bold',
            'font-family': 'Courier'
        });

        titleText.textContent = displayedName;

        const group = SVGFactory.createElement('g', {
            visibility: 'hidden'
        });
        group.id = `planet-popup-${planet.id}`
        group.appendChild(rect);
        group.appendChild(titleText);

        for (let i = 0; i < infoList.length; i++) {
            const infoText = SVGFactory.createElement('text', {
                x: rectX + UISVGController.#PopupPadding,
                y: rectY + (i+2) * UISVGController.#PopupFontSize + UISVGController.#PopupPadding,
                'font-size': UISVGController.#PopupFontSize,
                'font-weight': 'normal',
                'font-family': 'Courier'
            });

            infoText.textContent = infoList[i];
            group.appendChild(infoText);
        }

        return group;
    }

    #updatePlanetPopupElement(planet, popupElement) {
        const renderedPosition = this.#calculateRenderedPosition(planet.pos.x, planet.pos.y);

        const rectX = renderedPosition.x + UISVGController.#PlanetSize;
        const rectY = renderedPosition.y - UISVGController.#PlanetSize;

        const infoList = this.#createPlanetInfo(planet);
        const textLength = infoList.reduce((acc, current) => Math.max(acc, current.length), `${planet.id}`.length);

        SVGFactory.applyProperties(popupElement.children[0], {
            x: rectX,
            y: rectY,
            width: textLength*8 + UISVGController.#PopupPadding*2 + 2,
        });

        SVGFactory.applyProperties(popupElement.children[1], {
            x: rectX + UISVGController.#PopupPadding,
            y: rectY + UISVGController.#PopupFontSize + UISVGController.#PopupPadding,
        });

        for (let i = 0; i < infoList.length; i++) {
            const infoText = popupElement.children[2 + i];

            SVGFactory.applyProperties(infoText, {
                x: rectX + UISVGController.#PopupPadding,
                y: rectY + (i+2) * UISVGController.#PopupFontSize + UISVGController.#PopupPadding,
            });

            infoText.textContent = infoList[i];
        }
    }

    #createPlanetInfo(planet) {
        return [
            `pos: (${planet.pos.x}, ${planet.pos.y})`,
            `player: ${planet.owner}`,
            `destroyed: ${planet.destroyed}`,
            `space mission: ${planet.spaceMissionPossible}`
        ];
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

    #renderWormholes(wormholes) {
        for (const wormhole of wormholes) {

            const line = SVGFactory.createElement('line', {
                x1: wormhole.x1,
                y1: wormhole.y1,
                x2: wormhole.x2,
                y2: wormhole.y2,
                stroke: color ?? 'black'
            });

            // Add the line to the DOM
            this.#svgContainer.appendChild(line);
        }
    }

    #drawLine(x, y, len, angleRad, color) {
        // angleRad contains the direction of the gravity wave so by defaut it points away from it's source
        // however for us it is better if the line drawn for it points towards the source, so we reverse the angle to point in the opposite direction
        angleRad = angleRad + Math.PI;

        if (!angleRad && angleRad !== 0) {
            return;
        }

        // Calculate the end point of the line
        const endX = x + len * Math.sin(angleRad);
        const endY = y + len * -Math.cos(angleRad);

        const line = SVGFactory.createElement('line', {
            x1: x,
            y1: y,
            x2: endX,
            y2: endY,
            stroke: color ?? 'black'
        });

        this.#waveElements.push({
            element: line,
            timestamp: new Date()
        });

        // Add the line to the DOM
        this.#svgContainer.appendChild(line);
    }

    #animate() {
        if (!this.#allowAnimation) {
            return;
        }

        const currentTime = new Date();

        this.#waveElements.forEach(item => {
            const elapsed = currentTime - item.timestamp;

            if (elapsed > UISVGController.#RemoveWavesAfterMs) {
                item.element.remove();
            }
        });
    }
}