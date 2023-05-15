export class GameConfig {
    /**
     * @param {Array<number>} bots 
     * @param {string} gameType 
     */
    constructor(bots, gameType) {
        this.bots = bots;
        this.gameType = gameType;
    }
}

export default class GameManager {
    static UrlRoot = 'http://localhost:8080';

    #currentGameKey = '';
    #currentGameId = '';

    /**
     * @returns {string}
     */
    get currentGameKey() {
        return this.#currentGameKey;
    }

    /**
     * @param {string} value
     */
    set currentGameKey(value) {
        console.log(`Setting current game key to: ${value}`);

        this.#currentGameKey = value;
    }

    /**
     * @returns {string}
     */
    get currentGameId() {
        return this.#currentGameId;
    }

    /**
     * @param {string} value
     */
    set currentGameId(value) {
        console.log(`Setting current game id to: ${value}`);

        this.#currentGameId = value;
    }

    /**
     * @returns {Promise<string>}
     */
    async getGameKey() {
    	const response = await fetch(`${GameManager.UrlRoot}/getGameKey`);

    	this.currentGameKey = (await response.json()).key;

        return this.currentGameKey;
 	}
  
    /**
     * @param {GameConfig} gameConfig 
     * @return {Promise<string>}
     */
 	async createGame(gameConfig) {
        console.log('create game with config ' + gameConfig)
 	    
 	    const response = await fetch(`${GameManager.UrlRoot}/createGame/${this.currentGameKey}`, {
 	    	method: 'POST',
 	    	headers: {
 	    	   'Content-Type': 'application/json',
    	    },
 	    	body: JSON.stringify(gameConfig)
 	    });
 	    
 	    const gameCreatedResult = await response.json();
 	    console.log(gameCreatedResult);

        this.currentGameId = gameCreatedResult.gameId;

        return this.currentGameId;
    };
  
    /**
     * @returns {Promise<void>}
     */
    async connectWebSocket() {
    	await fetch(`${GameManager.UrlRoot}/connect/${this.currentGameId}/${this.currentGameKey}`);
    };
  
    /**
     * @returns {Promise<void>}
     */
    async startGame() {
    	const response = await fetch(`${GameManager.UrlRoot}/startGame/${this.currentGameId}/${this.currentGameKey}`);
    	
    	console.log(await response.json());
    };
  
    /**
     * @returns {Promise<void>}
     */
    async stopGame() {
    	const response = await fetch(`${GameManager.UrlRoot}/stopGame/${this.currentGameId}/${this.currentGameKey}`);
    	
    	console.log(await response.json());
    };

    /**
     * @returns {Promise<void>}
     */
    async getBots() {
        const response = await fetch(`${GameManager.UrlRoot}/bots`);

        console.log(await response.json());
    };
}