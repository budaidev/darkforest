export class SVGFactory {
    static #XMLNS = 'http://www.w3.org/2000/svg';

    /**
     * @param {number} width
     * @param {number} height
     * @returns {SVGElement}
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
     * @returns {SVGElement}
     */
    static createElement(name, properties) {
        const svgElement = document.createElementNS(SVGFactory.#XMLNS, name);
        SVGFactory.applyProperties(svgElement, properties);

        return svgElement;
    }

    /**
     * @param {SVGElement} element 
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