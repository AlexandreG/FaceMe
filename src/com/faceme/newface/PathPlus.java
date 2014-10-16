package com.faceme.newface;

import com.faceme.newface.NewFaceTool.Size;

import android.graphics.Path;

/**
 * A path plus its position and size
 * @author AlexandreG
 *
 */
public class PathPlus {
	protected Size s;
	protected Path p;
	protected int x;
	protected int y;

	public PathPlus(Path p, Size s, int x, int y) {
		this.p = p;
		this.s = s;
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Size getS() {
		return s;
	}

	public void setS(Size s) {
		this.s = s;
	}

	public Path getP() {
		return p;
	}

	public void setP(Path p) {
		this.p = p;
	}

}
