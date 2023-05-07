class DropdownList {
    constructor(containerId, onchangeHandler) {
        this.container = document.getElementById(containerId);
        this.container.onchange = onchangeHandler;
        this.itemsList = [];
    }

    get selected() {
        return this.container.value;
    }

    addItems(itemsList /* Array<{ display: string; value: string }> */) {
        for (const item of itemsList) {
            this.addAndSelectItem(item);
        }
    }

    addAndSelectItem(item /* { display: string; value: string } */) {
        const option = this._createOption(item, true);

        for (const item of this.itemsList) {
            item.selected = false;
        }

        this.itemsList.push(option);
        this.container.appendChild(option);

        container.value = option.value;
        const changeEvent = new Event("change");
        this.container.dispatchEvent(changeEvent);
    }

    _createOption(item, selected = false) {
        const optionElement = document.createElement("option");
        optionElement.label = item.display;
        optionElement.value = item.value;
        optionElement.selected = selected;

        return optionElement;
    }
}