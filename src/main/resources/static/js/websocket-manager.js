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

    subscribeToMessage(handler) {
        this.#messageSubscribers.push(handler);
    }

    #reconnect() {
        if (this.#connection === undefined || (this.#connection.readyState !== WebSocket.OPEN && this.#connection.readyState !== WebSocket.CONNECTING)) {
            this.#connection = new WebSocket(WebSocketManager.#PlanetsUrl);

            this.#attachEventHandlers();
        }
    }

    #attachEventHandlers() {
        this.#connection.onopen = this.#openHandler.bind(this);
        this.#connection.onclose = this.#closeHandler.bind(this);
        this.#connection.onmessage = this.#messageHandler.bind(this);
        this.#connection.onerror = this.#errorHandler.bind(this);
    }

    #openHandler(event) {
        this.#statusTextboxElement.value = 'Online';
        this.#reconnectTimeoutHandle = undefined;

        if (this.#reconnectTimeoutHandle !== undefined) {
            clearTimeout(this.#reconnectTimeoutHandle);
        }
    }

    #closeHandler(event) {
        this.#statusTextboxElement.value = 'Offline';

        this.#startConnecting();
    }

    #messageHandler(event) {
        this.#messageSubscribers.forEach(messageConsumer => {
            const data = JSON.parse(event.data);
            console.log(data);
            messageConsumer(data);
        });
    }

    #errorHandler(event) {
        console.error(event);

        if (this.#connection.readyState === WebSocket.CLOSED || this.#connection.readyState === WebSocket.CLOSING) {
            this.#startConnecting();
        }
    }

    #startConnecting() {
        if (this.#reconnectTimeoutHandle === undefined) {
            this.#reconnectTimeoutHandle = setInterval(this.#reconnect.bind(this), WebSocketManager.#ReconnectIntervalMs);

            this.#reconnect();
        }
    }
}