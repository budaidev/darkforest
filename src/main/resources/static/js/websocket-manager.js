class WebSocketManager {
    static #PlanetsUrl = 'ws://localhost:8080/planets';
    static #ReconnectIntervalMs = 1000;

    #connection;
    #statusTextboxElement;
    #reconnectTimeoutHandle;

    #messageSubscribers = [];

    constructor(statusElementId) {
        this.#statusTextboxElement = document.getElementById(statusElementId);

        this.#startConnecting();
    }

    /**
     * 
     * @param {messageHandlerFunc} handler 
     * @returns {void}
     * 
     * @callback messageHandlerFunc
     * @param {Object} event
     */
    subscribeToMessage(handler) {
        this.#messageSubscribers.push(handler);
    }

    /**
     * @returns {void}
     */
    #reconnect() {
        if (this.#connection === undefined || (this.#connection.readyState !== WebSocket.OPEN && this.#connection.readyState !== WebSocket.CONNECTING)) {
            this.#connection = new WebSocket(WebSocketManager.#PlanetsUrl);

            this.#attachEventHandlers();
        }
    }

    /**
     * @returns {void}
     */
    #attachEventHandlers() {
        this.#connection.onopen = this.#openHandler.bind(this);
        this.#connection.onclose = this.#closeHandler.bind(this);
        this.#connection.onmessage = this.#messageHandler.bind(this);
        this.#connection.onerror = this.#errorHandler.bind(this);
    }

    /**
     * @param {Event} event
     * @returns {void}
     */
    #openHandler(event) {
        this.#statusTextboxElement.value = 'Online';
        this.#reconnectTimeoutHandle = undefined;

        if (this.#reconnectTimeoutHandle !== undefined) {
            clearTimeout(this.#reconnectTimeoutHandle);
        }
    }

    /**
     * @param {CloseEvent} event 
     * @return {void}
     */
    #closeHandler(event) {
        this.#statusTextboxElement.value = 'Offline';

        this.#startConnecting();
    }

    /**
     * @param {MessageEvent} event 
     * @returns {void}
     */
    #messageHandler(event) {
        this.#messageSubscribers.forEach(messageConsumer => {
            const data = JSON.parse(event.data);
            console.log(data);
            messageConsumer(data);
        });
    }

    /**
     * @param {Event} event 
     * @return {void}
     */
    #errorHandler(event) {
        console.error(event);

        if (this.#connection.readyState === WebSocket.CLOSED || this.#connection.readyState === WebSocket.CLOSING) {
            this.#startConnecting();
        }
    }

    /**
     * @returns {void}
     */
    #startConnecting() {
        if (this.#reconnectTimeoutHandle === undefined) {
            this.#reconnectTimeoutHandle = setInterval(this.#reconnect.bind(this), WebSocketManager.#ReconnectIntervalMs);

            this.#reconnect();
        }
    }
}