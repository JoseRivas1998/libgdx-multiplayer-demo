class Bullet {
    constructor(bulletId, playerId, x, y, direction) {
        this.bulletId = bulletId;
        this.playerId = playerId;
        this.x = x;
        this.y = y;
        this.direction = direction;
    }
}

module.exports = Bullet;
