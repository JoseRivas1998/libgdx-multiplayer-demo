package com.tcg.libgdxmultiplayerdemo.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.tcg.libgdxmultiplayerdemo.MultiplayerDemo;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = MultiplayerDemo.WORLD_WIDTH;
		config.height = MultiplayerDemo.WORLD_HEIGHT;
		config.foregroundFPS = 0;
		config.vSyncEnabled = true;
		new LwjglApplication(new MultiplayerDemo(), config);
	}
}
