class Ball {
    constructor() {
        this.r = 5;
        this.reset();
    }

    update() {
        // Change direction when top/bottom of canvas is hit
        if (this.y < this.r || this.y > height - this.r) {
            this.ySpeed = -this.ySpeed;
        }
        // If it gets to the end of screen (player scored)
        if (this.x < this.r || this.x > width + this.r) {
            this.reset();
        }

        this.x += this.xSpeed;
        this.y += this.ySpeed;
    }

    reset() {
        // Position ball at center of screen
        this.x = width/2;
        this.y = height/2;

        this.xSpeed = random(3, 4); 

        // 50% change of either direction at start
        let goingLeft = random(1) > 0.5;
        if (goingLeft) {
            this.xSpeed = -this.xSpeed;
        }

        this.ySpeed = random(1, 5);
        let goingDown = random(1) > 0.5;
        if (goingDown) {
            this.ySpeed = -this.ySpeed;
        } 
        
    }

    display() {
        ellipse(this.x, this.y, this.r * 2, this.r * 2);
    }

    hasHitPlayer(player) {
        if (this.x - this.r <= player.x + player.width && this.x > player.x) {
            if (this.isSameHeight(player)) {
                this.xSpeed = -this.xSpeed;
                if (this.xSpeed < 10.5) {
                    this.xSpeed += 0.5;
                }
                console.log(this.xSpeed);
            }
        }
    }

    hasHitAI(ai) {
        if (this.x + this.r >= ai.x && this.x <= ai.x + ai.width) {
            if (this.isSameHeight(ai)) {
              this.xSpeed = -this.xSpeed;
              if (this.xSpeed > -10.5) {
                    this.xSpeed -= 0.5;
                }
              console.log(this.xSpeed);
            }
          }
    }

    isSameHeight(player) {
        return this.y >= player.y && this.y <= player.y + player.height;
    }
}