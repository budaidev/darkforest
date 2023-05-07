class DropdownList {

    #container
    #itemsList = [];

    /**
     * @param {string} containerId 
     * @param {onchangeHandler} onchangeHandler 
     * 
     * @callback onchangeHandler
     * @param {Event} event
     */
    constructor(containerId, onchangeHandler) {
        this.#container = document.getElementById(containerId);
        this.#container.onchange = onchangeHandler;
    }

    /**
     * @returns {string}
     */
    get selected() {
        return this.#container.value;
    }

    /**
     * @param {Array<{ display: string; value: string }>} itemsList
     * @returns {void}
     */
    addItems(itemsList) {
        for (const item of itemsList) {
            this.addAndSelectItem(item);
        }
    }

    /**
     * @param {{ display: string; value: string }} item 
     * @returns {void}
     */
    addAndSelectItem(item) {
        const option = this._createOption(item, true);

        for (const item of this.#itemsList) {
            item.selected = false;
        }

        this.#itemsList.push(option);
        this.#container.appendChild(option);

        container.value = option.value;
        const changeEvent = new Event("change");
        this.#container.dispatchEvent(changeEvent);
    }

    /**
     * @param {{ display: string; value: string }} item 
     * @param {boolean} selected 
     * @returns {HTMLOptionElement}
     */
    #createOption(item, selected = false) {
        const optionElement = document.createElement("option");
        optionElement.label = item.display;
        optionElement.value = item.value;
        optionElement.selected = selected;

        return optionElement;
    }
}