let playerPaddle;
let aiPaddle;
let ball;

// Initializes the canvass. Imported from p5.js library
function setup() {
    var canvas = createCanvas(624, 351);
    canvas.parent("ping-pong-canvas");
    playerPaddle = new Paddle(26);
    aiPaddle = new Paddle(width - 48);
    ball = new Ball();
}

// Refreshes canvas at 60hz. Imported from p5.js library
function draw() {
    background('#101824');

    playerPaddle.display();
    aiPaddle.display();

    playerPaddle.update();
    aiPaddle.update();
    processAI();

    ball.update();
    ball.display();

    ball.hasHitPlayer(playerPaddle);
    ball.hasHitAI(aiPaddle);

    stroke(255); 
    line(width / 2, 0, width / 2, height); // draw white line across center
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