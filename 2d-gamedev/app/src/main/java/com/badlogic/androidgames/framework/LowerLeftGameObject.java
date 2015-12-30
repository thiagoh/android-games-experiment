package com.badlogic.androidgames.framework;

import com.badlogic.androidgames.framework.math.Rectangle;
import com.badlogic.androidgames.framework.math.Vector2;

public class LowerLeftGameObject {
	public final Vector2 position;
	public final Rectangle bounds;

	public LowerLeftGameObject(float x, float y, float width, float height) {
		this.position = new Vector2(x, y);
		this.bounds = new Rectangle(x, y, width, height);
	}

	@Override
	public String toString() {
		return "LowerLeftGameObject{" +
				"position=" + position +
				", bounds=" + bounds +
				'}';
	}
}
