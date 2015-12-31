package com.badlogic.androidgames.gamedev2d;

import android.util.FloatMath;

import com.badlogic.androidgames.framework.DynamicGameObject;
import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.GameObject;
import com.badlogic.androidgames.framework.Input.TouchEvent;
import com.badlogic.androidgames.framework.LowerLeftGameObject;
import com.badlogic.androidgames.framework.Screen;
import com.badlogic.androidgames.framework.Sound;
import com.badlogic.androidgames.framework.SpatialHashGrid;
import com.badlogic.androidgames.framework.gl.Camera2D;
import com.badlogic.androidgames.framework.gl.RectangleDrawer;
import com.badlogic.androidgames.framework.gl.SpriteBatcher;
import com.badlogic.androidgames.framework.gl.Texture;
import com.badlogic.androidgames.framework.gl.TextureRegion;
import com.badlogic.androidgames.framework.impl.GLGame;
import com.badlogic.androidgames.framework.impl.GLGraphics;
import com.badlogic.androidgames.framework.math.OverlapTester;
import com.badlogic.androidgames.framework.math.Vector2;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class SpriteBatcherTest extends GLGame {

	public Screen getStartScreen() {
		return new SpriteBatcherScreen(this);
	}

	class SpriteBatcherScreen extends Screen {

		boolean debugCollisions = false;

		final int NUM_TARGETS = 40;
		final int NUM_BARS = 10;
		final float WORLD_WIDTH = 9.6f;
		final float WORLD_HEIGHT = 4.8f;
		final float SECS_TO_FILL_BARS = 4.0f;
		final float MAX_LEN = FloatMath.sqrt(WORLD_WIDTH * WORLD_WIDTH + WORLD_HEIGHT * WORLD_HEIGHT);
		final WorldListener listener;
		GLGraphics glGraphics;
		Cannon cannon;
		DynamicGameObject ball;
		LowerLeftGameObject emptyBar;
		LowerLeftGameObject fullBar;
		List<Bob> targets;
		SpatialHashGrid grid;
		TextureRegion cannonRegion;

		TextureRegion ballRegion;
		TextureRegion backgroundRegion;
		TextureRegion bobRegion;
		TextureRegion emptyBarRegion;
		TextureRegion fullBarRegion;
		SpriteBatcher spriteBatcher;
		RectangleDrawer rectangleBatcher;
		Vector2 touchPos = new Vector2();

		Vector2 gravity = new Vector2(0, -10);
		Camera2D camera;
		Texture mainAtlas;
		Texture background;

		float deltaTouch = 0;
		boolean fullPowerPlayed = false;
		boolean gameOverPlayed = false;

		public SpriteBatcherScreen(final Game game) {
			super(game);
			glGraphics = ((GLGame) game).getGLGraphics();

			cannon = new Cannon(0.4f, 0.4f, 1, 0.5f);

			float barContainerY = 1.2f;
			float barContainerX = 0.2f;
			emptyBar = new LowerLeftGameObject(barContainerX, barContainerY, 0.5f, 3.0f);
			fullBar = new LowerLeftGameObject(barContainerX, barContainerY, 0.5f, 3.0f);

			ball = new DynamicGameObject(0, 0, 0.2f, 0.2f);
			targets = new ArrayList<>(NUM_TARGETS);
			grid = new SpatialHashGrid(WORLD_WIDTH, WORLD_HEIGHT, 2.5f);

			for (int i = 0; i < NUM_TARGETS; i++) {

				float x = 0.5f + ((float) Math.random() * (WORLD_WIDTH - 1.0f));
				float y = 0.5f + ((float) Math.random() * (WORLD_HEIGHT - 1.0f));

				Bob target = new Bob(x, y, 0.2f, 0.3f);
				target.velocity.x = (float) Math.random();

				grid.insertStaticObject(target);
				targets.add(target);
			}

			spriteBatcher = new SpriteBatcher(glGraphics, 200);
			if (debugCollisions) {
				rectangleBatcher = new RectangleDrawer(glGraphics, 200, true);
			}
			camera = new Camera2D(glGraphics, WORLD_WIDTH, WORLD_HEIGHT);
			listener = new WorldListener() {

				public Sound jumpSound;
				public Sound highJumpSound;
				public Sound hitSound;
				public Sound bombSound;
				public Sound superBombSound;
				public Sound coinSound;
				public Sound clickSound;
				public Sound fullPowerSound;
				public Sound gameOverSound;

				public void load() {
					jumpSound = game.getAudio().newSound("jump.ogg");
					gameOverSound = game.getAudio().newSound("game-over.ogg");
					fullPowerSound = game.getAudio().newSound("full-power.ogg");
					bombSound = game.getAudio().newSound("bomb.ogg");
					superBombSound = game.getAudio().newSound("granade.ogg");
					highJumpSound = game.getAudio().newSound("highjump.ogg");
					hitSound = game.getAudio().newSound("hit.ogg");
					coinSound = game.getAudio().newSound("coin.ogg");
					clickSound = game.getAudio().newSound("click.ogg");
				}

				public void gameOver() {
					gameOverSound.play(1);
				}

				public void jump() {
					jumpSound.play(1);
				}

				public void highJump() {
					highJumpSound.play(1);
				}

				public void bomb() {
					bombSound.play(1);
				}

				public void superBomb() {
					superBombSound.play(1);
				}

				public void hit() {
					hitSound.play(1);
				}

				public void coin() {
					coinSound.play(1);
				}

				public void fullPower() {
					fullPowerSound.play(1);
				}
			};

			listener.load();
		}

		@Override
		public void update(float deltaTime) {
			List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
			game.getInput().getKeyEvents();

			deltaTouch += deltaTime;

			int deltaTouchNormalized = (int) Math.floor(Math.min(deltaTouch, SECS_TO_FILL_BARS) * NUM_BARS / SECS_TO_FILL_BARS);
			fullBar.bounds.height = 3.0f * deltaTouchNormalized / 10.0f;

			if (!fullPowerPlayed && deltaTouchNormalized > 9.0f) {
				listener.fullPower();
				fullPowerPlayed = true;
			}

//			Log.i("SpriteBatcherTest", "deltaTouchNormalized: " + deltaTouchNormalized);
//			Log.i("SpriteBatcherTest", "fullBar.bounds.height: " + fullBar.bounds.height);

			if (!gameOverPlayed && targets.size() == 0) {
				listener.gameOver();
				gameOverPlayed = true;
			}

			for (int i = 0, len = targets.size(); i < len; i++) {

				Bob target = targets.get(i);

				target.walkingTime += deltaTime;

				if (target.walkingTime > 1.0f) {
					target.walkingTime = 0;
					target.direction = target.direction == Bob.RIGHT ? Bob.LEFT : Bob.RIGHT;
				}

				target.position.add(deltaTime * target.velocity.x * (target.direction == Bob.RIGHT ? 1 : -1), deltaTime * target.velocity.y);
				target.bounds.lowerLeft.add(deltaTime * target.velocity.x * (target.direction == Bob.RIGHT ? 1 : -1), deltaTime * target.velocity.y);
			}

			for (int i = 0, len = touchEvents.size(); i < len; i++) {
				TouchEvent event = touchEvents.get(i);

				camera.touchToWorld(touchPos.set(event.x, event.y));

				cannon.angle = touchPos.sub(cannon.position).angle();

				if (event.type == TouchEvent.TOUCH_UP) {

					deltaTouch = Math.max(1.0f, Math.min(deltaTouch * 1.5f, SECS_TO_FILL_BARS));
					float ballSpeed = Math.min(MAX_LEN, touchPos.len()) * deltaTouch;

//					Log.i("SpriteBatcherTest", "ballSpeed: " + ballSpeed);
//					Log.i("SpriteBatcherTest", "TOUCH_UP deltaTouch: " + deltaTouch);

					if (deltaTouchNormalized > 9.0f) {

						listener.superBomb();

					} else {

						listener.bomb();
					}

					deltaTouch = 0.0f;
					fullPowerPlayed = false;

					float radians = cannon.angle * Vector2.TO_RADIANS;
					ball.position.set(cannon.position);
					ball.velocity.x = FloatMath.cos(radians) * ballSpeed;
					ball.velocity.y = FloatMath.sin(radians) * ballSpeed;
					ball.bounds.lowerLeft.set(ball.position.x - 0.1f, ball.position.y - 0.1f);
				}
			}


			ball.velocity.add(gravity.x * deltaTime, gravity.y * deltaTime);
			ball.position.add(ball.velocity.x * deltaTime, ball.velocity.y * deltaTime);
			ball.bounds.lowerLeft.add(ball.velocity.x * deltaTime, ball.velocity.y * deltaTime);

			checkCollisions();

//			if (ball.position.y > 0) {
//				//camera.position.set(ball.position);
//				camera.zoom = 1 + ball.position.y / WORLD_HEIGHT;
//			} else {
//				camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2);
//				camera.zoom = 1;
//			}
			//camera.zoom = 2.0f;
		}

		private void checkCollisions() {

			List<GameObject> colliders = grid.getPotentialColliders(ball);
			int len = colliders.size();
			for (int i = 0; i < len; i++) {
				GameObject collider = colliders.get(i);
				if (OverlapTester.overlapRectangles(ball.bounds, collider.bounds)) {
					grid.removeObject(collider);
					targets.remove(collider);
					listener.hit();
				}
			}
		}

		@Override
		public void present(float deltaTime) {
			GL10 gl = glGraphics.getGL();
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			camera.setViewportAndMatrices();

			gl.glEnable(GL10.GL_BLEND);
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			gl.glEnable(GL10.GL_TEXTURE_2D);

			spriteBatcher.beginBatch(background);
			spriteBatcher.drawSprite(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, WORLD_WIDTH, WORLD_HEIGHT, backgroundRegion);
			spriteBatcher.endBatch();

			spriteBatcher.beginBatch(mainAtlas);

			for (int i = 0, len = targets.size(); i < len; i++) {
				Bob target = targets.get(i);
				spriteBatcher.drawSprite(target.position.x, target.position.y, 0.5f * (target.direction == Bob.RIGHT ? 1 : -1), 0.5f, bobRegion);
			}

			spriteBatcher.drawSprite(ball.position.x, ball.position.y, 0.2f, 0.2f, ballRegion);
			spriteBatcher.drawLowerLeftSprite(emptyBar.position.x, emptyBar.position.y, 0.5f, 3.0f, emptyBarRegion);
			spriteBatcher.drawLowerLeftSprite(fullBar.position.x, fullBar.position.y, 0.5f, fullBar.bounds.height, fullBarRegion);
			spriteBatcher.drawSprite(cannon.position.x, cannon.position.y, 1, 0.5f, cannon.angle, cannonRegion);
			spriteBatcher.endBatch();

			gl.glDisable(GL10.GL_TEXTURE_2D);

			if (debugCollisions) {
				rectangleBatcher.beginBatch();
				for (int i = 0, len = targets.size(); i < len; i++) {
					Bob target = targets.get(i);
					rectangleBatcher.drawRectangle(target.position.x, target.position.y,
							target.bounds.width * (target.direction == Bob.RIGHT ? 1 : -1), target.bounds.height,
							1.0f, 0.0f, 0.0f, 1.0f);
				}
				rectangleBatcher.endBatch();
			}
		}

		@Override
		public void pause() {
		}

		@Override
		public void resume() {
			mainAtlas = new Texture(((GLGame) game), "atlas-full.png");
			background = new Texture(((GLGame) game), "background.png");
			backgroundRegion = new TextureRegion(background, 0, 0, 320, 480);
			cannonRegion = new TextureRegion(mainAtlas, 0, 0, 64, 32);
			ballRegion = new TextureRegion(mainAtlas, 0, 32, 16, 16);
			bobRegion = new TextureRegion(mainAtlas, 32, 32, 32, 32);
			emptyBarRegion = new TextureRegion(mainAtlas, 3, 68, 28, 251);
			fullBarRegion = new TextureRegion(mainAtlas, 35, 68, 28, 251);
		}

		@Override
		public void dispose() {
		}
	}
}
