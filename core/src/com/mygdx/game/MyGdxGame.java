package com.mygdx.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MyGdxGame extends ApplicationAdapter {
	boolean isServer;
	public MyGdxGame(boolean isServer){
		this.isServer = isServer;
	}

	@Override
	public void create () {
		System.setProperty("javax.net.debug","all");
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		if (isServer) {
			SSLServerThread sslst = new SSLServerThread(13769);
			sslst.start();
		} else {
			SSLClientThread sslct = new SSLClientThread("23.119.224.210", 13769);
			sslct.start();
		}
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
}
