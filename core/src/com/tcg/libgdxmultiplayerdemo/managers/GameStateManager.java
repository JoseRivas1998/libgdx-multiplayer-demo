package com.tcg.libgdxmultiplayerdemo.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.tcg.libgdxmultiplayerdemo.gamestates.AbstractGameState;
import com.tcg.libgdxmultiplayerdemo.gamestates.GameStateType;

public class GameStateManager implements Disposable {

    public static final String TAG = GameStateManager.class.getSimpleName();

    private AbstractGameState currentState;
    private SpriteBatch sb;
    private ShapeRenderer sr;

    public GameStateManager(GameStateType initialState) {
        sb = new SpriteBatch();
        sr = new ShapeRenderer();
        setState(initialState);
    }

    public void setState(GameStateType gameStateType) {
        if (currentState != null) currentState.dispose();
        Gdx.app.debug(TAG, "Switching to game state: " + gameStateType);
        try {
            currentState = (AbstractGameState) ClassReflection.getConstructor(gameStateType.stateClass, GameStateManager.class).newInstance(this);
        } catch (ReflectionException e) {
            throw new GdxRuntimeException("Game state " + gameStateType + " could not be created.", e);
        }
        currentState.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void step(float dt) {
        currentState.handleInput(dt);
        currentState.update(dt);
        currentState.draw(dt, sb, sr);
    }

    public void resize(int width, int height) {
        currentState.resize(width, height);
    }

    @Override
    public void dispose() {
        currentState.dispose();
        sb.dispose();
        sr.dispose();
    }
}
