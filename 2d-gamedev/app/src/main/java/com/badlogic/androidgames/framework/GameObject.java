package com.badlogic.androidgames.framework;

import com.badlogic.androidgames.framework.math.Rectangle;
import com.badlogic.androidgames.framework.math.Vector2;

public class GameObject {
	public Vector2 position;
	public Rectangle bounds;
	public float opacity;
	public float life;

	public GameObject(float x, float y, float width, float height) {
		place(x, y, width, height);
		opacity = 1.0f;
		life = 1.0f;
	}

	public void place(float x, float y, float width, float height) {
		this.position = new Vector2(x, y);
		this.bounds = new Rectangle(x - width / 2, y - height / 2, width, height);
	}

	@Override
	public String toString() {
		return "GameObject{" +
				"position=" + position +
				", bounds=" + bounds +
				'}';
	}
}
