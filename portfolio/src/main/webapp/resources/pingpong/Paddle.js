// Constructed from p5.js library
class Paddle {
    constructor(x) { // x is input var because it is what differs when creating player/ai paddles
        this.x = x;
        this.y = height / 2; // height is height of display window
        // height & width of the paddle
        this.height = 80;
        this.width = 3;

        this.movingUp = false;
        this.movingDown = false;
        this.movingFaster = false;
    }

    display() {
        fill(255);
        rect(this.x, this.y, this.width, this.height);
    }

    moveUp() {
        if (this.y > 0) {
            this.y -= 2;
        }
    }

    moveFaster() {
        if (this.movingFaster) {
            if (this.movingUp && this.y > 0) {
                this.y -= 2;
            } else if (this.movingDown && this.y < height - this.height) {
                this.y += 2;
            }
        }
    }

    moveDown() {
        if (this.y < height - this.height) {
            this.y += 2;
        }
    }

    update() {
        if (this.movingUp) {
            this.moveUp();
        } else if (this.movingDown) {
            this.moveDown();
        }
        if (this.movingFaster) {
            this.moveFaster();  
        }
    }

}