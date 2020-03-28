package com.tcg.libgdxmultiplayerdemo.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.tcg.libgdxmultiplayerdemo.MultiplayerDemo;
import com.tcg.libgdxmultiplayerdemo.entities.Ship;
import com.tcg.libgdxmultiplayerdemo.managers.GameStateManager;
import com.tcg.libgdxmultiplayerdemo.net.PlayerDTO;
import com.tcg.libgdxmultiplayerdemo.net.SocketConnections;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class PlayState extends AbstractGameState {

    private static final String TAG = PlayState.class.getSimpleName();
    private Viewport viewport;
    private Ship player;
    private Map<String, Ship> enemies;

    private Socket socket;
    private static final float SOCKET_UPDATE_TIME = 1 / 30f;
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
                        JSONObject data = new JSONObject();
                        data.put("x", player.getX());
                        data.put("y", player.getY());
                        data.put("angle", player.getAngle());
                        data.put("velX", player.getVelocityX());
                        data.put("velY", player.getVelocityY());
                        socket.emit(SocketEvents.PLAYER_READY, data);
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
    }

    @Override
    public void update(float dt) {
        updateSocketTimer(dt);
        player.update(dt);
        for (Ship ship : enemies.values()) {
            ship.update(dt);
        }
        viewport.apply(true);
    }

    private void updateSocketTimer(float dt) {
        socketUpdateTimer += dt;
        if (Float.compare(socketUpdateTimer, SOCKET_UPDATE_TIME) >= 0) {
            if (player.hasMoved()) {
                final PlayerDTO data = new PlayerDTO(socket.id(), player);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            socket.emit(SocketEvents.PLAYER_MOVE, data.toJSON());
                        } catch (JSONException e) {
                            Gdx.app.error(TAG, e.getMessage(), e);
                        }
                    }
                }).start();
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
    }

}
