package com.tcg.libgdxmultiplayerdemo.managers.input;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

public final class MyInput {

    private static final boolean[] keys;
    private static final boolean[] pKeys;

    static final Vector2 mouse;

    private static final int NUM_KEYS = 7;
    public static final int CLICK = 0;
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int THRUST = 3;
    public static final int SHOOT = 4;
    public static final int ENTER = 5;
    public static final int ESCAPE = 6;

    static {
        mouse = new Vector2();
        keys = new boolean[NUM_KEYS];
        pKeys = new boolean[NUM_KEYS];
    }

    public static void update() {
        for (int i = 0; i < keys.length; i++) {
            pKeys[i] = keys[i];
        }
    }

    public static void setKey(int key, boolean b) {
        keys[key] = b;
    }

    public static boolean keyCheck(int key) {
        return keys[key];
    }

    public static boolean keyCheckPressed(int key) {
        return keys[key] && !pKeys[key];
    }

    public static Vector2 screenMouse() {
        return new Vector2(mouse);
    }

    public static Vector2 worldMouse(Viewport viewport) {
        return new Vector2(viewport.unproject(mouse));
    }

}
