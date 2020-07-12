let playerPaddle;
let aiPaddle;
let ball;
let game;
let playerScoreText;
let aiScoreText;

// Initializes the canvass. Imported from p5.js library
function setup() {   
    var canvas = createCanvas(624, 351);
    canvas.parent("ping-pong-canvas");
    aiScoreText = text('Howdy', 10, 10);
   
    playerPaddle = new Paddle(26);   
    aiPaddle = new Paddle(width - 48); 
    ball = new Ball();
    game = new Game(playerPaddle, aiPaddle, ball);
    
    noLoop();
    let startButton = document.getElementById("button");
    startButton.onclick = function() {
        startButton.blur();
        game.changeState();
        game.running ? loop() : noLoop();
        changeButton(startButton);
    }  
}

function changeButton(button) {
    if (game.running) {
        button.innerText = "Stop Game";
        button.style.backgroundColor = "rgb(150,30,30)";
    } else {
        button.innerText = "Start Game";
        button.style.backgroundColor = "rgb(20,90,20)";
    }
}

// Refreshes canvas at 60hz. Imported from p5.js library
function draw() {
    game.update();
    noStroke();  
    background('#101824');
    
    if (game.running) {
        playerPaddle.display();
        aiPaddle.display();
        ball.display();

        playerPaddle.update();
        aiPaddle.update();
        ball.update();
        processAI();

        ball.hasHitPlayer(playerPaddle);
        ball.hasHitAI(aiPaddle);

        stroke(255); 
        line(width / 2, 0, width / 2, height); // draw white line across center
        
        //fill(255);
        textSize(30);
        textAlign(CENTER);
        text(game.playerScore, width / 2 - 299, 40);
        text(game.aiScore, width / 2 + 299, 40)  
    }
    
    if (!game.running) {
        textSize(40);
        if (game.aiWins) {
            fill('rgb(150,30,30)');
            text('You Lose!', width / 2, 190);
        } else if (game.playerWins) {
            fill('rgb(20,90,20)');
            text('You Win!', width / 2, 190);
        }   
    }
}

// Built-in functions for keypresses
function keyPressed() {
    if (keyCode == UP_ARROW) {
        playerPaddle.movingUp = true;
    } else if (keyCode == DOWN_ARROW) {
        playerPaddle.movingDown = true;
    } 
    if (key == ' ') {
        playerPaddle.movingFaster = true;
    } 
}

function keyReleased() {
    if (keyCode == UP_ARROW) {
        playerPaddle.movingUp = false;
    } else if (keyCode == DOWN_ARROW) {
        playerPaddle.movingDown = false;
    }
    if (key == ' ') {
        playerPaddle.movingFaster = false;
    }
   
}

// if ball is above middle of paddle, go up; down if below
function processAI() {
    let middleOfPaddle = (aiPaddle.height / 2) + aiPaddle.y;

    if (middleOfPaddle > ball.y) {
        aiPaddle.movingUp = true;
        aiPaddle.movingDown = false;
      } else {
        aiPaddle.movingDown = true;
        aiPaddle.movingUp = false;
     
      }
}