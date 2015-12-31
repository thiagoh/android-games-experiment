package com.badlogic.androidgames.framework;

import com.badlogic.androidgames.framework.math.Rectangle;
import com.badlogic.androidgames.framework.math.Vector2;

public class ColoredRectangle extends Rectangle {
	public float red, blue, green, alpha;

	public ColoredRectangle(float x, float y, float width, float height, float red, float blue, float green, float alpha) {
		super(x, y, width, height);

		this.red = red;
		this.blue = blue;
		this.green = green;
		this.alpha = alpha;
	}

	@Override
	public String toString() {
		return "ColoredRectangle{" +
				"red=" + red +
				", blue=" + blue +
				", green=" + green +
				", alpha=" + alpha +
				"} " + super.toString();
	}
}
