// Constructed from p5.js library
class Paddle {
    constructor(x) { // x is input var because it is what differs when creating player/ai paddles
        console.log("making paddle!");
        this.x = x;
        this.y = height / 2; // height is height of display window
        // height & width of the paddle
        this.height = 80;
        this.width = 20;

        this.movingUp = false;
        this.movingDown = false;
    }

    display() {
        fill(255);
        rect(this.x, this.y, this.width, this.height);
    }

    moveUp() {
        this.y -= 2;
    }

    moveDown() {
        this.y += 2;
    }
}