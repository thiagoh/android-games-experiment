package com.badlogic.androidgames.gamedev2d;

/**
 * Created by thiago on 30/12/15.
 */
public interface WorldListener {
	public void load();

	public void jump();

	public void gameOver();

	public void highJump();

	public void hit();

	public void bomb();

	public void superBomb();

	public void fullPower();

	public void coin();
}
