package com.tcg.libgdxmultiplayerdemo.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.tcg.libgdxmultiplayerdemo.MultiplayerDemo;
import com.tcg.libgdxmultiplayerdemo.entities.Bullet;
import com.tcg.libgdxmultiplayerdemo.entities.Ship;
import com.tcg.libgdxmultiplayerdemo.managers.ContentManager;
import com.tcg.libgdxmultiplayerdemo.managers.GameStateManager;
import com.tcg.libgdxmultiplayerdemo.managers.input.MyInput;
import com.tcg.libgdxmultiplayerdemo.net.BulletDTO;
import com.tcg.libgdxmultiplayerdemo.net.PlayerDTO;
import com.tcg.libgdxmultiplayerdemo.net.SocketConnections;
import com.tcg.libgdxmultiplayerdemo.net.SocketUtil;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.Semaphore;

public class PlayState extends AbstractGameState {

    private static final String TAG = PlayState.class.getSimpleName();
    private Viewport viewport;
    private Ship player;
    private Map<String, Ship> enemies;
    private Map<String, Bullet> friendlyBullets;
    private Semaphore friendlyBulletMutex;
    private Map<String, Bullet> enemyBullets;
    private Semaphore enemyBulletsMutex;

    private Socket socket;
    private static final float SOCKET_UPDATE_TIME = 1 / 60f;
    private float socketUpdateTimer;

    public PlayState(GameStateManager gsm) {
        super(gsm);
    }

    @Override
    protected void init() {
        viewport = new FitViewport(MultiplayerDemo.WORLD_WIDTH, MultiplayerDemo.WORLD_HEIGHT);
        player = new Ship(Ship.PLAYER_COLOR);
        enemies = new HashMap<>();
        socketUpdateTimer = 0;
        friendlyBullets = new HashMap<>();
        friendlyBulletMutex = new Semaphore(1);
        enemyBullets = new HashMap<>();
        enemyBulletsMutex = new Semaphore(1);
        initSocketEvents();
    }

    private void initSocketEvents() {
        try {
            socket = IO.socket("http://localhost:5000");
            SocketConnections.addConnection(socket);
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        Gdx.app.log(TAG, "Socket Connected with ID: " + socket.id());
                        PlayerDTO playerDTO = new PlayerDTO(socket.id(), player);
                        SocketUtil.emit(socket, SocketEvents.PLAYER_READY, playerDTO);
                    } catch (JSONException e) {
                        Gdx.app.error(TAG, e.getMessage(), e);
                    }
                }
            });
            socket.on(SocketEvents.PLAYER_LIST, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONArray data = (JSONArray) args[0];
                        for (int i = 0; i < data.length(); i++) {
                            addEnemy(data.getJSONObject(i));
                        }
                    } catch (JSONException e) {
                        Gdx.app.error(TAG, e.getMessage(), e);
                    }
                }
            });
            socket.on(SocketEvents.NEW_PLAYER, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject data = (JSONObject) args[0];
                        addEnemy(data);
                    } catch (JSONException e) {
                        Gdx.app.error(TAG, e.getMessage(), e);
                    }
                }
            });
            socket.on(SocketEvents.PLAYER_MOVE, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject data = (JSONObject) args[0];
                        PlayerDTO player = new PlayerDTO(data);
                        Ship ship = enemies.get(player.id);
                        if (ship == null) {
                            addEnemy(data);
                        } else {
                            ship.setPosition(player.x, player.y);
                            ship.setAngle(player.angle);
                            ship.setVelocity(player.velX, player.velY);
                        }
                    } catch (JSONException e) {
                        Gdx.app.error(TAG, e.getMessage(), e);
                    }
                }
            });
            socket.on(SocketEvents.REMOVE_PLAYER, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject data = (JSONObject) args[0];
                        final String id = data.getString("id");
                        enemies.remove(id);
                    } catch (JSONException e) {
                        Gdx.app.error(TAG, e.getMessage(), e);
                    }
                }
            });
            socket.on(SocketEvents.DUPLICATE_BULLET, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject data = (JSONObject) args[0];
                        final String errorId = data.getString("errorId");
                        final String bulletId = data.getString("bulletId");
                        friendlyBulletMutex.acquire();
                        final Bullet bullet = friendlyBullets.get(errorId);
                        friendlyBullets.remove(errorId);
                        friendlyBullets.put(bulletId, bullet);
                        friendlyBulletMutex.release();
                    } catch (JSONException | InterruptedException e) {
                        Gdx.app.error(TAG, e.getMessage(), e);
                    }
                }
            });
            socket.on(SocketEvents.NEW_BULLET, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject data = (JSONObject) args[0];
                        BulletDTO bulletDTO = new BulletDTO(data);
                        enemyBulletsMutex.acquire();
                        enemyBullets.put(bulletDTO.bulletId, new Bullet(false, bulletDTO.x, bulletDTO.y, bulletDTO.direction));
                        enemyBulletsMutex.release();
                    } catch (JSONException | InterruptedException e) {
                        Gdx.app.error(TAG, e.getMessage(), e);
                    }
                }
            });
            socket.on(SocketEvents.REMOVE_BULLET, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject data = (JSONObject) args[0];
                        enemyBulletsMutex.acquire();
                        enemyBullets.remove(data.getString("bulletId"));
                        enemyBulletsMutex.release();
                    } catch (InterruptedException | JSONException e) {
                        Gdx.app.error(TAG, e.getMessage(), e);
                    }
                }
            });
            socket.connect();
        } catch (URISyntaxException e) {
            Gdx.app.error(TAG, e.getMessage(), e);
        }
    }

    private void addEnemy(JSONObject data) throws JSONException {
        PlayerDTO player = new PlayerDTO(data);
        Ship ship = new Ship(Ship.ENEMY_COLOR);
        ship.setAngle(player.angle);
        ship.setPosition(player.x, player.y);
        ship.setVelocity(player.velX, player.velY);
        enemies.put(player.id, ship);
    }

    @Override
    public void handleInput(float dt) {
        player.handleInput(dt);
        if(MyInput.keyCheckPressed(MyInput.SHOOT)) {
            fireBullet();
        }
    }

    private void fireBullet() {
        final float direction = player.getAngle();
        try {
            friendlyBulletMutex.acquire();
            StringBuilder idBuilder;
            do {
                idBuilder = new StringBuilder();
                idBuilder.append('-');
                for (int i = 0; i < 9; i++) {
                    idBuilder.append((char) MathUtils.random('a', 'z'));
                }
            } while (friendlyBullets.get(idBuilder.toString()) != null);
            final String bulletId = idBuilder.toString();
            friendlyBullets.put(bulletId, new Bullet(true, player.getPosition(), direction));
            friendlyBulletMutex.release();
            MultiplayerDemo.content.playSound(ContentManager.SoundEffect.SHOOT);
            BulletDTO bulletDTO = new BulletDTO(bulletId, socket.id(), player.getX(), player.getY(), direction);
            try {
                SocketUtil.emit(socket, SocketEvents.NEW_BULLET, bulletDTO);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (InterruptedException e) {
            Gdx.app.log(TAG, e.getMessage(), e);
        }
    }

    @Override
    public void update(float dt) {
        updateSocketTimer(dt);
        player.update(dt);
        for (Ship ship : enemies.values()) {
            ship.update(dt);
        }
        updateFriendlyBullets(dt);
        updateEnemyBullets(dt);
        viewport.apply(true);
    }

    private void updateEnemyBullets(float dt) {
        try {
            enemyBulletsMutex.acquire();
            Iterator<Map.Entry<String, Bullet>> iter = enemyBullets.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, Bullet> entry = iter.next();
                Bullet bullet = entry.getValue();
                bullet.update(dt);
            }
            enemyBulletsMutex.release();
        } catch (InterruptedException e) {
            Gdx.app.error(TAG, e.getMessage(), e);
        }
    }

    private void updateFriendlyBullets(float dt) {
        try {
            friendlyBulletMutex.acquire();
            Iterator<Map.Entry<String, Bullet>> iter = friendlyBullets.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, Bullet> entry = iter.next();
                Bullet bullet = entry.getValue();
                bullet.update(dt);
                if(bullet.getX() < 0 || bullet.getX() > MultiplayerDemo.WORLD_WIDTH || bullet.getY() < 0 || bullet.getY() > MultiplayerDemo.WORLD_HEIGHT) {
                    iter.remove();
                    try {
                        JSONObject data = new JSONObject();
                        data.put("bulletId", entry.getKey());
                        socket.emit(SocketEvents.REMOVE_BULLET, data);
                    } catch (JSONException e) {
                        Gdx.app.error(TAG, e.getMessage(), e);
                    }
                }
            }
            friendlyBulletMutex.release();
        } catch (InterruptedException e) {
            Gdx.app.log(TAG, e.getMessage(), e);
        }
    }

    private void updateSocketTimer(float dt) {
        socketUpdateTimer += dt;
        if (Float.compare(socketUpdateTimer, SOCKET_UPDATE_TIME) >= 0) {
            socketUpdateTimer = 0;
            if (player.hasMoved()) {
                final PlayerDTO data = new PlayerDTO(socket.id(), player);
                try {
                    SocketUtil.emit(socket, SocketEvents.PLAYER_MOVE, data);
                } catch (JSONException e) {
                    Gdx.app.error(TAG, e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public void draw(float dt, SpriteBatch sb, ShapeRenderer sr) {
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setProjectionMatrix(viewport.getCamera().combined);
        for (Ship ship : enemies.values()) {
            ship.draw(dt, sr);
        }
        player.draw(dt, sr);
        try {
            friendlyBulletMutex.acquire();
            for (Bullet bullet : friendlyBullets.values()) {
                bullet.draw(dt, sr);
            }
            friendlyBulletMutex.release();
            enemyBulletsMutex.acquire();
            for (Bullet bullet : enemyBullets.values()) {
                bullet.draw(dt, sr);
            }
            enemyBulletsMutex.release();
        } catch (InterruptedException e) {
            Gdx.app.log(TAG, e.getMessage(), e);
        }
        sr.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {

    }

    static class SocketEvents {
        final static String PLAYER_READY = "player_ready";
        final static String NEW_PLAYER = "new_player";
        final static String PLAYER_LIST = "player_list";
        final static String PLAYER_MOVE = "player_move";
        final static String REMOVE_PLAYER = "remove_player";
        final static String NEW_BULLET = "new_bullet";
        final static String REMOVE_BULLET = "remove_bullet";
        final static String DUPLICATE_BULLET = "duplicate_bullet";
    }

}
