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
            
            let startButton = document.getElementById("button");
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
    }

    stopGame() {
        console.log("stopping game");
        this.running = false;
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