package com.faceme.play.faceshape;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

public class FaceShape {
	protected final static boolean D = true;
	protected final static String TAG = "Log";

	public final int xCenter;
	public final int yCenter;

	protected int x;
	protected int y;
	protected float vX;
	protected float vY;

	protected float angle;
	protected float scaleX;
	protected float scaleY;
	protected Bitmap bp;

	protected boolean hitted; // really useful only for the eyes

	public FaceShape(int x, int y, Bitmap bp) {
		super();
		this.x = x;
		this.y = y;
		this.vX = 0;
		this.vY = 0;
		this.bp = bp;
		this.xCenter = x;
		this.yCenter = y;

		this.angle = 0;
		this.scaleX = 1f;
		this.scaleY = 1f;

		this.hitted = false;
	}

	public void draw(Canvas canvas, Paint paint) {
		// canvas.drawBitmap(bp, x-bp.getWidth()/2, y-bp.getHeight()/2, paint);

		canvas.drawBitmap(bp, x - bp.getWidth() / 2, y - bp.getHeight() / 2, paint);
	}

	public void drawTurned(Canvas canvas, Paint paint, Matrix matrix) {
		// Log.d("Log", "drawturned");
		matrix.setTranslate(x - bp.getWidth() / 2, y - bp.getHeight() / 2);
		matrix.postRotate(angle, x, y);
		canvas.drawBitmap(bp, matrix, paint);
		// canvas.drawBitmap(bp, x-bp.getWidth()/2, y-bp.getHeight()/2, paint);
	}

	public void drawScaled(Canvas canvas, Paint paint, Matrix matrix) {
		// Log.d("Log", "drawturned");
		matrix.setScale(scaleX, scaleY);
		matrix.postTranslate(x - scaleX * bp.getWidth() / (2), y - scaleY * bp.getHeight() / (2));
		canvas.drawBitmap(bp, matrix, paint);
		// canvas.drawBitmap(bp, x-bp.getWidth()/2, y-bp.getHeight()/2, paint);
	}

	public void drawCrossScaled(Canvas canvas, Paint paint, Matrix matrix) {
		int padding = bp.getWidth() / 3;
		// funny result
		// canvas.drawLine(x-bp.getWidth()/2 +padding, y-scaleY*bp.getHeight()/2
		// +padding, x+bp.getWidth()/2 -padding, y+scaleY*bp.getHeight()/2
		// -padding,paint);
		// canvas.drawLine(x-bp.getWidth()/2 +padding, y+scaleY*bp.getHeight()/2
		// -padding, x+bp.getWidth()/2 -padding, y-scaleY*bp.getHeight()/2
		// +padding,paint);
		// classic result
		canvas.drawLine(x - bp.getWidth() / 2 + padding, y + scaleY * (-bp.getHeight() / 2 + padding),
				x + bp.getWidth() / 2 - padding, y + scaleY * (bp.getHeight() / 2 - padding), paint);
		canvas.drawLine(x - bp.getWidth() / 2 + padding, y + scaleY * (bp.getHeight() / 2 - padding), x + bp.getWidth()
				/ 2 - padding, y + scaleY * (-bp.getHeight() / 2 + padding), paint);
		// canvas.drawLine(, paint);
	}

	public void moveTo(int newX, int newY) {
		this.x = newX;
		this.y = newY;

		// Log.d(TAG, newX+"");
	}

	public void move(int dx, int dy) {
		this.x += dx;
		this.y += dy;
	}

	public void setSpeed(int vx, int vy) {
		this.vX += vx;
		this.vY += vy;
	}

	public void turnTo(float newAngle) {
		this.angle = newAngle;
	}

	public void turn(float angleStep) {
		this.angle += angleStep;
	}

	public void center() {
		this.x = xCenter;
		this.y = yCenter;
	}

	public float getAngle() {
		return angle;
	}

	public void cleanValues() {
		center();
		turnTo(0);
		this.vX = 0;
		this.vY = 0;
		this.scaleX = 1f;
		this.scaleY = 1f;
		this.hitted = false;
	}

	public boolean isHitted() {
		return hitted;
	}

	public void setHitted(boolean hitted) {
		this.hitted = hitted;
	}

	public void setScale(float x, float y) {
		this.scaleX = x;
		this.scaleY = y;
	}

	public float getScaleX() {
		return scaleX;
	}

	public float getScaleY() {
		return scaleY;
	}

	public void setScaleX(float scaleX) {
		this.scaleX = scaleX;
	}

	public void setScaleY(float scaleY) {
		this.scaleY = scaleY;
	}

}
