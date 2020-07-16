class Game {
    constructor(player, ai, ball) {
        this.POINTS_TO_WIN = 2;

        this.player = player;
        this.ai = ai;
        this.ball = ball; 

        this.running = false;
        this.playerWins = false;
        this.aiWins = false;

        this.playerScore = 0;
        this.aiScore = 0;

        this.displayText = false;
    }

    update() {
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
            fetch("/user-data?query=post&postType=loggame&game=pong&result=lose");
        } else if (this.playerScore == this.POINTS_TO_WIN) {
            console.log("player wins");
            this.playerWins = true;
            fetch("/user-data?query=post&postType=loggame&game=pong&result=win");
        }
        if (this.playerWins || this.aiWins) { 
            this.displayText = true;
            let startButton = document.getElementById("play-button");
            startButton.click();
        }
    }

    changeState() {
        this.running ? this.stopGame() : this.startGame();
    }

    startGame() {
        getLeaderboard();
        this.playerScore = 0;
        this.aiScore = 0;
        this.playerWins = false;
        this.aiWins = false;
        this.displayText = false;
        ball.reset();
        this.running = true;

        this.preventDefaultKeyBindings(true);
    }

    stopGame() {
        if (!(this.playerWins || this.aiWins)) {
            fetch("/user-data?query=post&postType=loggame&game=pong&result=lose");
        }
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