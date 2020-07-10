let playerPaddle;
let aiPaddle;

// Initializes the canvass. Imported from p5.js library
function setup() {
    var canvas = createCanvas(624, 351);
    canvas.parent("ping-pong-canvas");
    playerPaddle = new Paddle(26);
    aiPaddle = new Paddle(width - 48);
}

// Refreshes canvas at 60hz. Imported from p5.js library
function draw() {
    background('#101824');
    playerPaddle.display();
    aiPaddle.display();

    // make the player move according to the flag 
    if (playerPaddle.movingUp) {
        playerPaddle.moveUp();
    } else if (playerPaddle.movingDown) {
        playerPaddle.moveDown();
    }
}

// Built-in functions for keypresses
function keyPressed() {
    if (keyCode == UP_ARROW) {
        playerPaddle.movingUp = true;
    } else if (keyCode == DOWN_ARROW) {
        playerPaddle.movingDown = true;
    }
}

function keyReleased() {
    if (keyCode == UP_ARROW) {
        playerPaddle.movingUp = false;
    } else if (keyCode == DOWN_ARROW) {
        playerPaddle.movingDown = false;
    }
}
