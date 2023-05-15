import { SVGFactory } from "./svg-factory.js";
import { GameEvent, Planet, Wormhole } from './api-types.js';

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

export default class UISVGController {
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
    #effectCounterElements;
    #effectPopupElements;
    #wormholeElements;

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

        if(gameEvent.wormHoles && gameEvent.wormHoles.length > 0) {
            this.#renderWormholes(gameEvent.wormHoles);
        }
    }

    /**
     * @returns {void}
     */
    enableAnimation() {
        this.#allowAnimation = true;
    }

    /**
     * @returns {void}
     */
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

        this.#wormholeElements = new Map();
        this.#planetElements = new Map();
        this.#planetPopupElements = new Map();
        this.#waveElements = [];
        this.#planets = new Map();
        this.#effectCounterElements = new Map();
        this.#effectPopupElements = new Map();

        this.#playerDict = {
            0: 0
        };
    }

    /**
     * @param {GameEvent} gameEvent 
     * @returns {void}
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
     * @return {void}
     */
    #renderPlanets(planets) {
        const planetPopups = [];
        const effectPopups = [];
        for (const planet of planets) {
            if (this.#planetElements.has(planet.id)) {
                this.#updatePlanetElement(planet, this.#planetElements.get(planet.id));
                this.#updatePlanetPopupElement(planet, this.#planetPopupElements.get(planet.id));
                this.#updatePlanetEffectCounter(planet, this.#effectCounterElements.get(planet.id));
                this.#updateEffectPopup(planet, this.#effectPopupElements.get(planet.id));
            } else {
                this.#planets.set(planet.id, planet);

                this.#createPlanetElement(planet);
                planetPopups.push(this.#createPlanetPopup(planet));
                this.#createEffectCounter(planet);
                effectPopups.push(this.#createEffectPopup(planet));
            }
        }

        planetPopups.forEach(planetPopup => this.#svgContainer.appendChild(planetPopup));
        effectPopups.forEach(effectPopup => this.#svgContainer.appendChild(effectPopup));
    }

    /**
     * @param {Planet} planet 
     * @returns {SVGCircleElement}
     */
    #createPlanetElement(planet) {
        const planetElement = SVGFactory.createElement('circle');
        planetElement.id = 'planet' + planet.id

        SVGFactory.applyProperties(planetElement, {
            r: UISVGController.#PlanetSize
        });
        
        this.#updatePlanetElement(planet, planetElement);

        this.#svgContainer.appendChild(planetElement);
        this.#planetElements.set(planet.id, planetElement);

        SVGFactory.applyProperties(planetElement, {
            onmouseenter: `show('planet-popup-${planet.id}')`,
            onmouseleave: `hide('planet-popup-${planet.id}')`,
        });

        return planetElement;
    }

    /**
     * @param {Planet} planet 
     * @param {SVGCircleElement} planetElement 
     * @returns {void}
     */
    #updatePlanetElement(planet, planetElement) {
        const backgroundColor = UISVGController.#Colors[this.#playerDict[planet.owner]] ?? 'red';
        const renderedPosition = this.#calculateRenderedPosition(planet.pos.x, planet.pos.y);

        planetElement.style.fill = backgroundColor;

        if (planet.destroyed === true) {
            planetElement.style.fill = 'black';
        }

        if (planet.owner === 0 && planet.spaceMissionPossible === false) {
            planetElement.style.fill = 'darkgrey';
        }

        SVGFactory.applyProperties(planetElement, {
            cx: renderedPosition.x,
            cy: renderedPosition.y,
        });
    }

    /**
     * @param {Planet} planet 
     * @returns {SVGGElement}
     */
    #createPlanetPopup(planet) {
        const renderedPosition = this.#calculateRenderedPosition(planet.pos.x, planet.pos.y);
        const displayedName = planet.id;

        const rectX = renderedPosition.x + UISVGController.#PlanetSize;
        const rectY = renderedPosition.y - UISVGController.#PlanetSize;

        const infoList = this.#createPlanetInfo(planet);
        const id = `planet-popup-${planet.id}`;

        const element = this.#createGenericPopup(rectX, rectY, [displayedName, ...infoList], id);
        this.#planetPopupElements.set(planet.id, element);

        return element;
    }

    /**
     * @param {number} x 
     * @param {number} y 
     * @param {Array<string>} textContents 
     * @param {string} id 
     * @returns {SVGGElement}
     */
    #createGenericPopup(x, y, textContents, id) {
        const textLength = textContents.reduce((acc, current) => Math.max(acc, current.length), 0);

        const rect = SVGFactory.createElement('rect', {
            x: x,
            y: y,
            width: textLength*8 + UISVGController.#PopupPadding*2 + 2,
            height: (textContents.length) * UISVGController.#PopupFontSize + 2 + UISVGController.#PopupPadding*2,
            fill: 'white'
        });

        const titleText = SVGFactory.createElement('text', {
            x: x + UISVGController.#PopupPadding,
            y: y + UISVGController.#PopupFontSize + UISVGController.#PopupPadding,
            'font-size': UISVGController.#PopupFontSize,
            'font-weight': 'bold',
            'font-family': 'Courier'
        });

        titleText.textContent = textContents[0];

        const group = SVGFactory.createElement('g', {
            visibility: 'hidden'
        });
        group.id = id;
        group.appendChild(rect);
        group.appendChild(titleText);

        for (let i = 1; i < textContents.length; i++) {
            group.appendChild(this.#createPopupContentLineElement(x, y, i, textContents[i]));
        }

        return group;
    }

    /**
     * @param {number} popupX 
     * @param {number} popupY 
     * @param {number} lineNumber 
     * @param {string} text 
     * @returns {SVGTextElement}
     */
    #createPopupContentLineElement(popupX, popupY, lineNumber, text) {
        const infoText = SVGFactory.createElement('text', {
            x: popupX + UISVGController.#PopupPadding,
            y: popupY + (lineNumber+1) * UISVGController.#PopupFontSize + UISVGController.#PopupPadding,
            'font-size': UISVGController.#PopupFontSize,
            'font-weight': 'normal',
            'font-family': 'Courier'
        });

        infoText.textContent = text;

        return infoText;
    }

    /**
     * @param {Planet} planet 
     * @param {SVGGElement} popupElement
     * @returns {void}
     */
    #updatePlanetPopupElement(planet, popupElement) {
        const renderedPosition = this.#calculateRenderedPosition(planet.pos.x, planet.pos.y);

        const rectX = renderedPosition.x + UISVGController.#PlanetSize;
        const rectY = renderedPosition.y - UISVGController.#PlanetSize;

        const infoList = this.#createPlanetInfo(planet);

        this.#updateGenericPopup(popupElement, rectX, rectY, [`${planet.id}`, ...infoList]);
    }

    /**
     * @param {SVGGElement} popupElement 
     * @param {number} x 
     * @param {number} y 
     * @param {Array<string>} textContent 
     * @param {Array<string>} colors 
     */
    #updateGenericPopup(popupElement, x, y, textContent, colors = undefined) {
        const textLength = textContent.reduce((acc, current) => Math.max(acc, current.length), 0);

        SVGFactory.applyProperties(popupElement.children[0], {
            x: x,
            y: y,
            width: textLength*8 + UISVGController.#PopupPadding*2 + 2,
            height: (textContent.length) * UISVGController.#PopupFontSize + 2 + UISVGController.#PopupPadding*2,
        });

        SVGFactory.applyProperties(popupElement.children[1], {
            x: x + UISVGController.#PopupPadding,
            y: y + UISVGController.#PopupFontSize + UISVGController.#PopupPadding,
        });

        const existingLineCount = popupElement.children.length - 2;

        for (let i = 1; i < existingLineCount; i++) {
            const infoText = popupElement.children[1 + i];

            SVGFactory.applyProperties(infoText, {
                x: x + UISVGController.#PopupPadding,
                y: y + (i+1) * UISVGController.#PopupFontSize + UISVGController.#PopupPadding,
            });

            infoText.textContent = textContent[i];

            const color = colors !== undefined 
                ? colors[i]
                : undefined;

            infoText.style.fill = color ?? 'black';
        }

        for (let i = existingLineCount; i < textContent.length; i++) {
            const lineElement = this.#createPopupContentLineElement(x, y, i, textContent[i]);

            const color = colors !== undefined 
                ? colors[i] 
                : undefined;

            popupElement.appendChild(lineElement);

            lineElement.style.fill = color ?? 'black';
        }
    }

    /**
     * @param {Planet} planet 
     * @returns {SVGTextElement}
     */
    #createEffectCounter(planet) {
        const effectElement = SVGFactory.createElement('text', {
            'font-size': 12,
            'font-weight': 'normal',
            'font-family': 'Courier',
            onmouseenter: `show('effect-popup-${planet.id}')`,
            onmouseleave: `hide('effect-popup-${planet.id}')`,
        });

        this.#updatePlanetEffectCounter(planet, effectElement);

        this.#svgContainer.appendChild(effectElement);
        this.#effectCounterElements.set(planet.id, effectElement);

        return effectElement;
    }

    /**
     * @param {Planet} planet 
     * @returns {SVGGElement}
     */
    #createEffectPopup(planet) {
        const pos = this.#calculatePlanetEffectCounterPosition(planet);
        const xOffset = `${planet.effectsEmitted.length}`.length * 8;
        const title = 'Effects emitted from here';

        const popupContent = this.#createEffectPopupItems(planet);
        const contentLines = popupContent.map(x => x.text)
        const id = `effect-popup-${planet.id}`;

        const element = this.#createGenericPopup(pos.x + xOffset, pos.y, [title, ...contentLines], id);
        this.#effectPopupElements.set(planet.id, element);

        return element;
    }

    /**
     * @param {Planet} planet 
     * @param {SVGGElement} popupElement 
     */
    #updateEffectPopup(planet, popupElement) {
        const pos = this.#calculatePlanetEffectCounterPosition(planet);
        const xOffset = `${planet.effectsEmitted.length}`.length * 8;
        const title = 'Effects emitted from here';
        const popupContent = this.#createEffectPopupItems(planet);
        const contentLines = popupContent.map(x => x.text)
        const colors = popupContent.map(x => x.color);

        this.#updateGenericPopup(popupElement, pos.x + xOffset, pos.y, [title, ...contentLines], ['black', ...colors]);
    }

    /**
     * @param {Planet} planet 
     * @returns {Array<{text: string, color: string}>}
     */
    #createEffectPopupItems(planet) {
        return planet.effectsEmitted.map(effect => {
            return {
                text: `${effect.p} - ${effect.type} - ${effect.c}`,
                color: this.#getPlayerColor(effect.p)
            };
        });
    }

    /**
     * @param {Planet} planet 
     * @param {SVGTextElement} effectCounterElement 
     * @returns {void}
     */
    #updatePlanetEffectCounter(planet, effectCounterElement) {
        const pos = this.#calculatePlanetEffectCounterPosition(planet);

        SVGFactory.applyProperties(effectCounterElement, {
            x: pos.x,
            y: pos.y,
        });

        effectCounterElement.textContent = planet.effectsEmitted.length;

        if (planet.effectsEmitted.length > 0) {
            const color = this.#getPlayerColor(planet.effectsEmitted[planet.effectsEmitted.length-1].p);
            effectCounterElement.style.fill = color;
        }
    }

    /**
     * @param {Planet} planet 
     * @returns {Position}
     */
    #calculatePlanetEffectCounterPosition(planet) {
        const renderedPlanetPosition = this.#calculateRenderedPosition(planet.pos.x, planet.pos.y);

        return new Position(
            renderedPlanetPosition.x - `${planet.effectsEmitted.length}`.length * 4 + 2,
            renderedPlanetPosition.y - UISVGController.#PlanetSize - 4);
    }

    /**
     * @param {Planet} planet 
     * @returns {Array<string>}
     */
    #createPlanetInfo(planet) {
        return [
            `pos: (${planet.pos.x}, ${planet.pos.y})`,
            `player: ${planet.owner}`,
            `destroyed: ${planet.destroyed}`,
            `space mission: ${planet.spaceMissionPossible}`,
            `shoot: ${planet.alreadyShot}`,
        ];
    }

    /**
     * @param {number} playerId
     * @returns {string}
     */
    #getPlayerColor(playerId) {
        return UISVGController.#Colors[this.#playerDict[playerId]];
    }

    /**
     * @param {number} x 
     * @param {number} y 
     * @param {number} sizeToOffset
     * @returns {Position}
     */
    #calculateRenderedPosition(x, y) {
        const widthRatio = this.#containerWidth / this.#mapWidth;
        const heightRatio = this.#containerHeight / this.#mapHeight;

        return new Position(Math.round(x * widthRatio), Math.round(y * heightRatio));
    }

    /**
     * @param {Array<Wormhole>} wormholes 
     * @returns {void}
     */
    #renderWormholes(wormholes) {
        for (const wormhole of wormholes) {
            if (!this.#wormholeElements.has(wormhole.id)) {

                const startPos = this.#calculateRenderedPosition(wormhole.x, wormhole.y);
                const endPos = this.#calculateRenderedPosition(wormhole.xb, wormhole.yb);

                const line = SVGFactory.createElement('line', {
                    id: wormhole.id,
                    class: 'wormhole',
                    x1: startPos.x,
                    y1: startPos.y,
                    x2: endPos.x,
                    y2: endPos.y,
                    stroke: 'purple'
                });

                // Add the line to the DOM
                this.#svgContainer.appendChild(line);

                this.#wormholeElements.set(wormhole.id, line);
            }
        }
    }

    /**
     * @param {number} x 
     * @param {number} y 
     * @param {number} len 
     * @param {number} angleRad 
     * @param {string} color 
     * @returns {void}
     */
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

    /**
     * @returns {void}
     */
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

/**
 * @param {string} elementId
 * @returns {void}
 */
export const show = (elementId) => {
    SVGFactory.applyProperties(document.getElementById(elementId), {
        visibility: undefined
    });
};

/**
 * @param {string} elementId 
 * @returns {void}
 */
export const hide = (elementId) => {
    SVGFactory.applyProperties(document.getElementById(elementId), {
        visibility: 'hidden'
    });
};