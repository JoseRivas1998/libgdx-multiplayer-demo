const express = require('express');

const app = express();

const Player = require('./Player');
const Bullet = require('./Bullet');

app.get('/', (req, res) => {
    res.send('<h1>Server up and running</h1>');
});

const server = app.listen(5000, () => {
    console.log("Listening on port 5000");
});

const io = require('socket.io')(server);

const players = [];
const bullets = {};

io.on('connection', (socket) => {
    const id = socket.id;
    console.log(`New user connected ${id}`);
    socket.on('player_ready', (data) => {
        console.log(`PLAYER READY ${socket.id}`, data);
        const new_player = new Player(socket.id, data.x, data.y, data.angle, data.velX, data.velY);
        const original_players = [...players];
        players.push(new_player);
        socket.broadcast.emit('new_player', {...new_player});
        socket.emit('player_list', original_players);
    });
    socket.on('player_move', data => {
        let found = false;
        let player = null;
        for (let i = 0; i < players.length && !found; i++) {
            if (players[i].id === socket.id) {
                player = players[i];
                found = true;
            }
        }
        if (found && player) {
            player.x = data.x;
            player.y = data.y;
            player.angle = data.angle;
            player.velX = data.velX;
            player.velY = data.velY;
            socket.broadcast.emit('player_move', {...data, id: socket.id});
        }
    });
    socket.on('new_bullet', (data) => {
        console.log(`User with id ${id} FIRED `, data);
        let bid = data.bulletId;
        if (bullets[bid]) {
            console.log('ERROR THAT BULLET ALREADY EXISTS!');
            do {
                bid = '-' + Math.random().toString(26).substr(2, 9);
            } while (bullets[bid]);
            socket.emit('duplicate_bullet', {errorId: data.bulletId, bulletId: bid});
        }
        bullets[bid] = new Bullet(bid, id, data.x, data.y, data.direction);
        socket.broadcast.emit('new_bullet', {...bullets[bid]});
    });
    const deleteBullet = (bId) => {
        delete bullets[bId];
        socket.broadcast.emit('remove_bullet', {bulletId: bId});
    };
    socket.on('remove_bullet', data => {
        deleteBullet(data.bulletId);
    });
    socket.on('disconnect', () => {
        console.log(`User with id ${id} has disconnected.`);
        let found = false;
        for (let i = 0; i < players.length && !found; i++) {
            const player = players[i];
            if (player.id === id) {
                players.splice(i, 1);
                found = true;
            }
            const playersBullets = Object.keys(bullets).filter(bid => player.id === bullets[bid].playerId);
            for (let i = 0; i < playersBullets.length; i++) {
                deleteBullet(playersBullets[i]);
            }
        }
        socket.broadcast.emit('remove_player', {id: socket.id});
    });
});
