class Game {
    constructor(player, ai, ball) {
        this.POINTS_TO_WIN = 5;

        this.player = player;
        this.ai = ai;
        this.ball = ball; 

        this.running = false;
        this.playerWins = false;
        this.aiWins = false;

        this.playerScore = 0;
        this.aiScore = 0;
    }

    update(){

        if (this.ball.x < this.ball.r) { // ai scored
            this.addPointToAI();
            console.log("ai scored!");
        } else if (this.ball.x > width + this.ball.r) { // player scored
            this.addPointToPlayer();
            console.log("player scored!");
        }

        if (this.aiScore == this.POINTS_TO_WIN) {
            console.log("ai wins!");
            this.aiWins = true;
        } else if (this.playerScore == this.POINTS_TO_WIN) {
            console.log("player wins");
            this.playerWins = true;
        }

        if (this.playerWins || this.aiWins) {
            let startButton = document.getElementById("play-button");
            startButton.click();
        }
    }

    changeState() {
        this.running ? this.stopGame() : this.startGame();
    }

    startGame() {
        console.log("starting game");
        this.playerScore = 0;
        this.aiScore = 0;
        this.playerWins = false;
        this.aiWins = false;
        ball.reset();
        this.running = true;

        this.preventDefaultKeyBindings(true);
    }

    stopGame() {
        console.log("stopping game");
        this.running = false;
        this.preventDefaultKeyBindings(false);
    }

    // Prevents the default key actions for arrow keys to avoid page scrolling
    // while user is playing
    preventDefaultKeyBindings(bool) {
        var arrow_keys_handler = function(e) {
            switch(e.keyCode) {
                case 37: case 39: case 38:  case 40: // Arrow keys
                case 32: e.preventDefault(); break; // Space
                default: break; // do not block other keys
            }
        };
        bool ? window.addEventListener("keydown", arrow_keys_handler, false) :
        window.removeEventListener("keydown", arrow_keys_handler, false);
    }

    addPointToPlayer() {
        this.playerScore += 1;
        console.log(this.playerScore);
    }

    addPointToAI() {
        this.aiScore += 1;
        console.log(this.aiScore);
    }
}