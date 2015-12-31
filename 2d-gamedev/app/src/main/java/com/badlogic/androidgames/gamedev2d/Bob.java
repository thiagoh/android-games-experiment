package com.badlogic.androidgames.gamedev2d;

import com.badlogic.androidgames.framework.DynamicGameObject;

/**
 * Created by thiago on 30/12/15.
 */
public class Bob extends DynamicGameObject {

	public float walkingTime;
	public int direction;
	public static final int RIGHT = 0;
	public static final int LEFT = 1;

	public Bob(float x, float y, float width, float height) {
		super(x, y, width, height);
		walkingTime = 0.0f;
		direction = Math.random() > 0.5d ? RIGHT : LEFT;
	}
}
