package com.tcg.libgdxmultiplayerdemo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.tcg.libgdxmultiplayerdemo.gamestates.GameStateType;
import com.tcg.libgdxmultiplayerdemo.managers.ContentManager;
import com.tcg.libgdxmultiplayerdemo.managers.GameStateManager;
import com.tcg.libgdxmultiplayerdemo.managers.input.MyInput;
import com.tcg.libgdxmultiplayerdemo.managers.input.MyInputProcessor;
import com.tcg.libgdxmultiplayerdemo.net.SocketConnections;

public class MultiplayerDemo extends ApplicationAdapter {

	public static final int WORLD_WIDTH = 800;
	public static final int WORLD_HEIGHT = 600;

	private GameStateManager gsm;
	public static ContentManager content;

	@Override
	public void create () {
		Gdx.input.setInputProcessor(new MyInputProcessor());
		content = new ContentManager();
		gsm = new GameStateManager(GameStateType.PLAY);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		float dt = Gdx.graphics.getDeltaTime();

		gsm.step(dt);
		MyInput.update();
	}

	@Override
	public void resize(int width, int height) {
		gsm.resize(width, height);
	}

	@Override
	public void dispose () {
		gsm.dispose();
		content.dispose();
		SocketConnections.dispose();
	}
}
