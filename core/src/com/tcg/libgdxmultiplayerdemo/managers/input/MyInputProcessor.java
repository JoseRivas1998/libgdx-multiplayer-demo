package com.tcg.libgdxmultiplayerdemo.managers.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class MyInputProcessor extends InputAdapter {
    @Override
    public boolean keyDown(int keycode) {
        return checkKeys(keycode, true);
    }

    @Override
    public boolean keyUp(int keycode) {
        return checkKeys(keycode, false);
    }

    private boolean checkKeys(int keycode, boolean value) {
        boolean keyFound = false;
        if(keycode == Input.Keys.LEFT || keycode == Input.Keys.A) {
            MyInput.setKey(MyInput.LEFT, value);
            keyFound = true;
        }
        if(keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) {
            MyInput.setKey(MyInput.RIGHT, value);
            keyFound = true;
        }
        if(keycode == Input.Keys.UP || keycode == Input.Keys.W) {
            MyInput.setKey(MyInput.THRUST, value);
            keyFound = true;
        }
        if(keycode == Input.Keys.SPACE) {
            MyInput.setKey(MyInput.SHOOT, value);
            keyFound = true;
        }
        if(keycode == Input.Keys.ENTER) {
            MyInput.setKey(MyInput.ENTER, value);
            keyFound = true;
        }
        if(keycode == Input.Keys.ESCAPE) {
            MyInput.setKey(MyInput.ESCAPE, value);
            keyFound = true;
        }
        return keyFound;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        MyInput.mouse.set(screenX, screenY);
        if(button == Input.Buttons.LEFT) {
            MyInput.setKey(MyInput.CLICK, true);
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        MyInput.mouse.set(screenX, screenY);
        if(button == Input.Buttons.LEFT) {
            MyInput.setKey(MyInput.CLICK, false);
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        MyInput.mouse.set(screenX, screenY);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        MyInput.mouse.set(screenX, screenY);
        return true;
    }

}
