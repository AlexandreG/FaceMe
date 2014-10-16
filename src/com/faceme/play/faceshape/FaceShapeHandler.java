package com.faceme.play.faceshape;

import java.util.LinkedList;
import java.util.Random;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import com.faceme.SoundManager;
import com.faceme.play.PlayActivity;
import com.faceme.play.StateHandler;

public class FaceShapeHandler {
	protected final static String TAG = "Log";

	protected LinkedList<FaceShape> mShapeList;

	protected Matrix matrix;
	protected int shapeRadius; // the radius of the circle around each shape
	protected int sW;
	protected int sH;

	protected Context ct;
	protected boolean isTablet;

	protected StateHandler stateH;
	protected Random rd;

	protected long lastTouchEvent;
	protected long lastTouchDown;
	protected int xFinger;
	protected int yFinger;

	protected long lastAccEvent;
	protected float xAcc;
	protected float yAcc;

	// in fact, these var are finger position ...
	protected int mouthScaleX;
	protected int mouthScaleY;
	protected int lEyeScaleX;
	protected int lEyeScaleY;
	protected int rEyeScaleX;
	protected int rEyeScaleY;

	protected SoundManager soundManager; // The sound

	public FaceShapeHandler(Context context, int w, int h, StateHandler sh, SoundManager sm) {
		super();
		// this.drawView = dv;
		// sW = h;
		// sH = w;
		sW = w;
		sH = h;
		shapeRadius = sH / 4;
		ct = context;
		boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
		boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
		isTablet = (xlarge || large);

		matrix = new Matrix();
		mShapeList = new LinkedList<FaceShape>();
		stateH = sh;
		rd = new Random();

		lastTouchEvent = 0;
		xFinger = -1;
		yFinger = -1;

		lastAccEvent = 0;
		xAcc = 0;
		yAcc = 0;

		mouthScaleX = -1;
		mouthScaleY = -1;
		lEyeScaleX = -1;
		lEyeScaleY = -1;
		rEyeScaleX = -1;
		rEyeScaleY = -1;

		soundManager = sm;
	}

	public void drawAll(Canvas canvas, Paint paint) {
		int s = stateH.getState();

		// if(s == StateHandler.WAIT_BLINK){
		// for(FaceShape drawObj : mShapeList){
		// matrix.reset();
		// drawObj.drawScaled(canvas, paint, matrix);
		// }
		// }
		if (s == StateHandler.WAIT_LOOKDOWN) {
			for (FaceShape drawObj : mShapeList) {
				drawObj.draw(canvas, paint);
			}
		}
		if (s == StateHandler.EMOTE_SHAKE) {
			for (FaceShape drawObj : mShapeList) {
				matrix.reset();
				drawObj.drawTurned(canvas, paint, matrix);
			}
		}
		if (s == StateHandler.EMOTE_SHOCK) {
			// for(FaceShape drawObj : mShapeList){
			// matrix.reset();
			// drawObj.drawScaled(canvas, paint, matrix);
			// }
			matrix.reset();
			mShapeList.get(0).drawScaled(canvas, paint, matrix);
			matrix.reset();
			mShapeList.get(1).drawScaled(canvas, paint, matrix);
			matrix.reset();
			mShapeList.get(3).drawScaled(canvas, paint, matrix);
			matrix.reset();
			mShapeList.get(4).drawScaled(canvas, paint, matrix);
			matrix.reset();
			mShapeList.get(6).drawScaled(canvas, paint, matrix);

		}
		if (s == StateHandler.EMOTE_HAPPY) {
			if (mShapeList != null) {
				// we draw the mouth
				mShapeList.get(mShapeList.size() - 1).draw(canvas, paint);
				// we draw the happy eyes

				paint.setAntiAlias(true);
				paint.setColor(Color.BLACK);
				paint.setStyle(Paint.Style.STROKE);
				paint.setStrokeWidth(10);
				canvas.drawArc(stateH.getRectFLeft(), 225, 90, false, paint);
				// canvas.drawRect(stateH.getRectFLeft(), paint);
				canvas.drawArc(stateH.getRectFRight(), 225, 90, false, paint);
				// canvas.drawRect(stateH.getRectFRight(), paint);

			}

		}
		if (s == StateHandler.ACTION_TRACKFINGER) {
			// for(FaceShape drawObj : mShapeList){
			// drawObj.draw(canvas, paint);
			// }
			// for(FaceShape drawObj : mShapeList){
			// matrix.reset();
			// drawObj.drawScaled(canvas, paint, matrix);
			// }

			paint.setAntiAlias(true);
			paint.setColor(Color.BLACK);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(10);
			int padding = (int) (shapeRadius / 1.5);

			// if left eye is hitted we draw a draw a cross
			if (mShapeList.get(1).isHitted()) {
				mShapeList.get(1).drawCrossScaled(canvas, paint, matrix);
				matrix.reset();
				mShapeList.get(2).drawTurned(canvas, paint, matrix);
			} else {
				matrix.reset();
				mShapeList.get(0).drawScaled(canvas, paint, matrix);
				matrix.reset();
				mShapeList.get(1).drawScaled(canvas, paint, matrix);
				matrix.reset();
				mShapeList.get(2).drawTurned(canvas, paint, matrix);
			}

			// if right eye is hitted we draw a draw a cross
			if (mShapeList.get(4).isHitted()) {
				mShapeList.get(4).drawCrossScaled(canvas, paint, matrix);
				matrix.reset();
				mShapeList.get(5).drawTurned(canvas, paint, matrix);
			} else {
				matrix.reset();
				mShapeList.get(3).drawScaled(canvas, paint, matrix);
				matrix.reset();
				mShapeList.get(4).drawScaled(canvas, paint, matrix);
				matrix.reset();
				mShapeList.get(5).drawTurned(canvas, paint, matrix);
			}
			// if the mouth is hitted we draw a D
			if (mShapeList.get(6).isHitted()) {
				canvas.drawArc(stateH.getRectFMouth(), 0, 180, true, paint);
			} else {
				matrix.reset();
				mShapeList.get(6).drawScaled(canvas, paint, matrix);
			}

		}

		if (s == StateHandler.ACTION_SCALE) {
			for (FaceShape drawObj : mShapeList) {
				matrix.reset();
				drawObj.drawScaled(canvas, paint, matrix);
			}

		}

		if (s == StateHandler.ACTION_TELEPORT) {
			for (FaceShape drawObj : mShapeList) {
				matrix.reset();
				drawObj.drawScaled(canvas, paint, matrix);
			}

		}

		if (s == StateHandler.ACTION_MOVING) {
			for (FaceShape drawObj : mShapeList) {
				matrix.reset();
				drawObj.drawScaled(canvas, paint, matrix);
			}

		}

		// drawObj.draw(canvas, paint);
		// matrix.reset();
		// drawObj.drawTurned(canvas, paint, matrix);

		// if(faceState == SHAKED){
		// //Log.d("Log", "shaked");
		// Log.d("Log", "shaked!!!");
		//
		// for(FaceShape drawObj : mShapeList){
		// matrix.reset();
		// drawObj.drawTurned(canvas, paint, matrix);
		// }
		// }
		// Log.d("Log", mShapeList.size()+"");
	}

	public void moveAll() {
		int s = stateH.getState();
		long NOW = System.currentTimeMillis();

		if (s == StateHandler.WAIT_LOOKDOWN) {
			if (NOW - stateH.getLastLookDown() > 3000) {
				int x = rd.nextInt(shapeRadius / 2) - shapeRadius / 4;
				int y = rd.nextInt(shapeRadius / 2) - shapeRadius / 4;
				mShapeList.get(1).moveTo(mShapeList.get(1).xCenter + x, mShapeList.get(1).yCenter + y);
				mShapeList.get(4).moveTo(mShapeList.get(4).xCenter + x, mShapeList.get(4).yCenter + y);
				stateH.setLastLookDown(NOW);
			}

		}

		if (s == StateHandler.EMOTE_SHAKE) {
			float angle = mShapeList.get(0).getAngle();
			angle += 30;
			for (FaceShape obj : mShapeList) {
				obj.turnTo(angle);
			}
		}

		if (s == StateHandler.EMOTE_SHOCK) {

			float scaleA = mShapeList.get(0).getScaleX();
			float scaleB = mShapeList.get(3).getScaleX();
			// too big
			if (scaleA > 1.6) {
				stateH.setGoingBig(false);
			}
			// too small
			if (scaleA < 0.4) {
				stateH.setGoingBig(true);
			}
			if (stateH.isGoingBig() == true) {
				scaleA += 0.2;
				scaleB -= 0.2;
			} else {
				scaleA -= 0.2;
				scaleB += 0.2;
			}
			mShapeList.get(0).setScaleX(scaleA);
			mShapeList.get(0).setScaleY(scaleA);
			mShapeList.get(1).setScaleX(scaleB);
			mShapeList.get(1).setScaleY(scaleB);
			// mShapeList.get(2).setScaleX(scaleA);
			// mShapeList.get(2).setScaleY(scaleA);

			mShapeList.get(3).setScaleX(scaleB);
			mShapeList.get(3).setScaleY(scaleB);
			mShapeList.get(4).setScaleX(scaleA);
			mShapeList.get(4).setScaleY(scaleA);
			// mShapeList.get(5).setScaleX(scaleB);
			// mShapeList.get(5).setScaleY(scaleB);

			mShapeList.get(6).setScaleX(scaleA);
			mShapeList.get(6).setScaleY(scaleB);

		}

		if (s == StateHandler.EMOTE_HAPPY) {
			float posY = stateH.getPosY();
			// if we went to high
			if (posY < 0) {
				stateH.setGoingDown(true);
			}
			// if we went to down
			if (posY > sH / 16) {
				stateH.setGoingDown(false);
			}
			if (stateH.isGoingDown() == true) {
				posY += 20;
				mShapeList.get(6).y -= 5;
			} else {
				posY -= 20;
				mShapeList.get(6).y += 5;
			}
			stateH.setPosY(posY);
			// I arbitrary chose to save the eye position in the first object
			stateH.getRectFLeft().set(sW / 4 - sH / 6, sH / 4 + posY, sW / 4 + sH / 4, sH / 2 + sH / 8 + posY);
			stateH.getRectFRight().set(3 * sW / 4 - sH / 6, sH / 4 + posY, 3 * sW / 4 + sH / 4, sH / 2 + sH / 8 + posY);
			mShapeList.get(0).y = (int) posY;
		}

		if (s == StateHandler.ACTION_TRACKFINGER) {

			// if screen is being touch
			if (NOW - lastTouchEvent < PlayActivity.FRAME_PERIOD * 20) {
				// deal with the user finger
				actionTrackFinger(NOW);
			} else {
				// if the accelerometer is working
				if (NOW - lastAccEvent < PlayActivity.FRAME_PERIOD * 2) {
					actionAcceleratorMode(NOW);
				}
			}
			// BLINKING
			blink(NOW);

		}

		if (s == StateHandler.ACTION_SCALE) {

			// if screen is being touch
			if (NOW - lastTouchEvent < PlayActivity.FRAME_PERIOD * 20) {
				// deal with the user finger
				actionTrackFinger(NOW);
			} else {
				// if the accelerometer is working
				if (NOW - lastAccEvent < PlayActivity.FRAME_PERIOD * 2) {
					actionAcceleratorMode(NOW);
				}
			}
		}

		if (s == StateHandler.ACTION_TELEPORT) {
			// move the shapes according to the accelerometer
			moveWithAccelerometer(NOW);

			// BLINKING
			// blink(NOW);

		}
		if (s == StateHandler.ACTION_MOVING) {
			// move the shapes according to the accelerometer
			moveWithBounds(NOW);

			// BLINKING
			// blink(NOW);

		}
		// ACTION_MOVING
		// if(s == StateHandler.ACTION_TELEPORT){
		// actionTeleportTouchedShape(newMouseX, newMouseY);
		// }
		//
		// if(s == StateHandler.ACTION_MOVING){
		// actionAcceleratorMode(newAx, newAy, newAz);
		// }
	}

	public void add(int x, int y, Bitmap bp) {
		// Bitmap cropped = Bitmap.createBitmap(bp, sWidth/2-radius, sWidth/4,
		// radius, radius);
		// OLD!// Bitmap cropped = Bitmap.createBitmap(bp, sW/2-radius,
		// sH/2-radius, radius*2, radius*2);
		mShapeList.add(new FaceShape(x, y, bp));
		// Log.d("Log", "add");
	}

	public void actionTrackFinger(long NOW) {

		if (xFinger > 0 && yFinger > 0) {
			// -------------------------the eyes
			FaceShape tempObj;
			int xc = xFinger;
			int yc = yFinger;
			int xa;
			int ya;
			float AC;
			float AD;
			float BD;
			int finalX;
			int finalY;

			// Left eye
			tempObj = mShapeList.get(1);
			xa = tempObj.xCenter;
			ya = tempObj.yCenter;

			AC = (float) Math.sqrt((xc - xa) * (xc - xa) + (yc - ya) * (yc - ya));

			// AD = (0.3f*shapeRadius*(xc - xa))/AC;
			// BD = (0.3f*shapeRadius*(yc - ya))/AC;
			// New formula !
			AD = ((AC / sW) * 0.5f * shapeRadius * (xc - xa)) / AC;
			BD = ((AC / sW) * 0.5f * shapeRadius * (yc - ya)) / AC;

			finalX = (int) ((int) tempObj.xCenter + AD);
			finalY = (int) ((int) tempObj.yCenter + BD);
			// Log.d(TAG, "before cast :"+tempObj.xCenter+AD );
			// Log.d(TAG, "after cast :"+finalX);
			tempObj.moveTo(finalX, finalY);

			// Right eye
			tempObj = mShapeList.get(4);
			xa = tempObj.xCenter;
			ya = tempObj.yCenter;

			AC = (float) Math.sqrt((xc - xa) * (xc - xa) + (yc - ya) * (yc - ya));

			AD = ((AC / sW) * 0.5f * shapeRadius * (xc - xa)) / AC;
			BD = ((AC / sW) * 0.5f * shapeRadius * (yc - ya)) / AC;

			finalX = (int) ((int) tempObj.xCenter + AD);
			finalY = (int) ((int) tempObj.yCenter + BD);
			tempObj.moveTo(finalX, finalY);

			// -------------------------the eyebrows
			mShapeList.get(2).moveTo((int) (mShapeList.get(2).xCenter + AD / 4),
					(int) (mShapeList.get(2).yCenter + BD / 4));
			mShapeList.get(5).moveTo((int) (mShapeList.get(5).xCenter + AD / 4),
					(int) (mShapeList.get(5).yCenter + BD / 4));

			// -------------------------the mouth
			mShapeList.get(6).moveTo((int) (mShapeList.get(6).xCenter - AD / 2),
					(int) (mShapeList.get(6).yCenter - BD / 2));
			stateH.getRectFMouth().set(sW / 2 - sH / 6 - AD / 2, sH / 2 - BD / 2, sW / 2 + sH / 6 - AD / 2,
					sH / 2 + sH / 3 - BD / 2);

		} else {
			mShapeList.get(1).move((mShapeList.get(1).xCenter - mShapeList.get(1).x) / 3,
					(mShapeList.get(1).yCenter - mShapeList.get(1).y) / 3);
			mShapeList.get(4).move((mShapeList.get(4).xCenter - mShapeList.get(4).x) / 3,
					(mShapeList.get(4).yCenter - mShapeList.get(4).y) / 3);

			mShapeList.get(2).move((mShapeList.get(2).xCenter - mShapeList.get(2).x) / 3,
					(mShapeList.get(2).yCenter - mShapeList.get(2).y) / 3);
			mShapeList.get(5).move((mShapeList.get(5).xCenter - mShapeList.get(5).x) / 3,
					(mShapeList.get(5).yCenter - mShapeList.get(5).y) / 3);

			mShapeList.get(6).move((mShapeList.get(6).xCenter - mShapeList.get(6).x) / 3,
					(mShapeList.get(6).yCenter - mShapeList.get(6).y) / 3);

			// // mShapeList.get(1).center();
			// // mShapeList.get(4).center();
			// // //-------------------------the eyebrows
			// mShapeList.get(2).center();
			// mShapeList.get(5).center();
			// // //-------------------------the mouth
			// mShapeList.get(6).center();
		}

		// dealing with the mouth scale
		if (mouthScaleX > 0 && mouthScaleY > 0) {
			if (xFinger > 0 && yFinger > 0) {
				double newScaleX = (xFinger - mShapeList.get(6).xCenter);
				newScaleX = newScaleX / (mouthScaleX - mShapeList.get(6).xCenter);
				// Log.d(TAG, newScaleX+"");
				// Log.d(TAG, xFinger-mShapeList.get(6).xCenter+"");
				// Log.d(TAG, mouthScaleX-mShapeList.get(6).xCenter+"");
				// we cap the scale : solve negative scale and too little mouth
				if (newScaleX < 0.2)
					newScaleX = 0.2f;
				if (newScaleX > 5)
					newScaleX = 5;
				// we calm down this shitty scale
				newScaleX = Math.sqrt(newScaleX);
				mShapeList.get(6).scaleX = (float) newScaleX;

				double newScaleY = (yFinger - mShapeList.get(6).yCenter);
				newScaleY = newScaleY / (mouthScaleY - mShapeList.get(6).yCenter);
				if (newScaleY < 0.2)
					newScaleY = 0.2f;
				if (newScaleY > 5)
					newScaleY = 5;
				// we calm down this shitty scale
				newScaleY = Math.sqrt(newScaleY);
				mShapeList.get(6).scaleY = (float) newScaleY;
			}
		}
		// dealing with the left eye scale
		if (lEyeScaleX > 0 && lEyeScaleY > 0) {
			if (xFinger > 0 && yFinger > 0) {
				double newScaleX = (xFinger - mShapeList.get(0).xCenter);
				newScaleX = newScaleX / (lEyeScaleX - mShapeList.get(0).xCenter);
				// Log.d(TAG, newScaleX+"");
				// Log.d(TAG, xFinger-mShapeList.get(6).xCenter+"");
				// Log.d(TAG, mouthScaleX-mShapeList.get(6).xCenter+"");
				// we cap the scale : solve negative scale and too little mouth
				if (newScaleX < 0.2)
					newScaleX = 0.2f;
				if (newScaleX > 5)
					newScaleX = 5;
				// we calm down this shitty scale
				newScaleX = Math.sqrt(newScaleX);
				mShapeList.get(0).scaleX = (float) newScaleX;
				mShapeList.get(1).scaleX = (float) newScaleX;

				double newScaleY = (yFinger - mShapeList.get(0).yCenter);
				newScaleY = newScaleY / (lEyeScaleY - mShapeList.get(0).yCenter);
				if (newScaleY < 0.2)
					newScaleY = 0.2f;
				if (newScaleY > 5)
					newScaleY = 5;
				// we calm down this shitty scale
				newScaleY = Math.sqrt(newScaleY);
				mShapeList.get(0).scaleY = (float) newScaleY;
				mShapeList.get(1).scaleY = (float) newScaleY;
			}
		}
		// dealing with the right eye scale
		if (rEyeScaleX > 0 && rEyeScaleY > 0) {
			if (xFinger > 0 && yFinger > 0) {
				double newScaleX = (xFinger - mShapeList.get(3).xCenter);
				newScaleX = newScaleX / (rEyeScaleX - mShapeList.get(3).xCenter);
				// Log.d(TAG, newScaleX+"");
				// Log.d(TAG, xFinger-mShapeList.get(6).xCenter+"");
				// Log.d(TAG, mouthScaleX-mShapeList.get(6).xCenter+"");
				// we cap the scale : solve negative scale and too little mouth
				if (newScaleX < 0.2)
					newScaleX = 0.2f;
				if (newScaleX > 5)
					newScaleX = 5;
				// we calm down this shitty scale
				newScaleX = Math.sqrt(newScaleX);
				mShapeList.get(3).scaleX = (float) newScaleX;
				mShapeList.get(4).scaleX = (float) newScaleX;

				double newScaleY = (yFinger - mShapeList.get(3).yCenter);
				newScaleY = newScaleY / (rEyeScaleY - mShapeList.get(3).yCenter);
				if (newScaleY < 0.2)
					newScaleY = 0.2f;
				if (newScaleY > 5)
					newScaleY = 5;
				// we calm down this shitty scale
				newScaleY = Math.sqrt(newScaleY);
				mShapeList.get(3).scaleY = (float) newScaleY;
				mShapeList.get(4).scaleY = (float) newScaleY;
			}
		}
		if (NOW - lastTouchEvent > 200) {
			mouthScaleX = -1;
			mouthScaleY = -1;
			lEyeScaleX = -1;
			lEyeScaleY = -1;
			rEyeScaleX = -1;
			rEyeScaleY = -1;
		}
	}

	public void actionAcceleratorMode(long NOW) {

		int speedCoef = 2;
		// the accelerometer axis are inverted for phone and tablets
		if (isTablet) {
			// iris
			mShapeList.get(1).vX = -speedCoef * xAcc;
			mShapeList.get(1).vY = speedCoef * yAcc;
			mShapeList.get(4).vX = -speedCoef * xAcc;
			mShapeList.get(4).vY = speedCoef * yAcc;
			// eyebrown
			mShapeList.get(2).vX = -speedCoef * xAcc / 6;
			mShapeList.get(2).vY = speedCoef * yAcc / 6;
			mShapeList.get(5).vX = -speedCoef * xAcc / 6;
			mShapeList.get(5).vY = speedCoef * yAcc / 6;
			// mouth
			mShapeList.get(6).vX = -speedCoef * xAcc / 4;
			mShapeList.get(6).vY = speedCoef * yAcc / 4;
		} else {
			// iris
			mShapeList.get(1).vX = speedCoef * yAcc;
			mShapeList.get(1).vY = speedCoef * xAcc;
			mShapeList.get(4).vX = speedCoef * yAcc;
			mShapeList.get(4).vY = speedCoef * xAcc;
			// eyebrown
			mShapeList.get(2).vX = speedCoef * yAcc / 6;
			mShapeList.get(2).vY = speedCoef * xAcc / 6;
			mShapeList.get(5).vX = speedCoef * yAcc / 6;
			mShapeList.get(5).vY = speedCoef * xAcc / 6;
			// the mouth
			mShapeList.get(6).vX = speedCoef * yAcc / 4;
			mShapeList.get(6).vY = speedCoef * xAcc / 3;
		}

		// if we went to far, we stop the speed
		for (FaceShape obj : mShapeList) {

			// if((obj.x - obj.xCenter)*(obj.x - obj.xCenter)+ (obj.y -
			// obj.yCenter)*(obj.y - obj.yCenter)>
			// (shapeRadius/2)*(shapeRadius/2)){
			// on the right
			if (obj.x - obj.xCenter > shapeRadius / 6 && obj.vX > 0) {
				obj.vX = 0;
			}
			// on the left
			if (obj.xCenter - obj.x > shapeRadius / 6 && obj.vX < 0) {
				obj.vX = 0;
			}
			// on the bottom
			if (obj.y - obj.yCenter > shapeRadius / 6 && obj.vY > 0) {
				obj.vY = 0;
			}
			// on the top
			if (obj.yCenter - obj.y > shapeRadius / 6 && obj.vY < 0) {
				obj.vY = 0;
			}
			// }
		}

		// moving everything
		for (FaceShape obj : mShapeList) {
			obj.move((int) obj.vX, (int) obj.vY);
		}

		stateH.getRectFMouth().set(mShapeList.get(6).x - sH / 6, mShapeList.get(6).y - sH / 4,
				mShapeList.get(6).x + sH / 6, sH / 2 + sH / 3);

		/*
		 * float xAccToUse = -xAcc; xAccToUse = (xAccToUse+10)*sW/20; float
		 * yAccToUse = yAcc; yAccToUse = (yAccToUse+10)*sH/20;
		 */

	}

	public void moveWithAccelerometer(long NOW) {
		int speedCoef = 4;
		// the accelerometer axis are inverted for phone and tablets
		// if(isTablet){
		mShapeList.get(0).vX += -speedCoef * xAcc;
		mShapeList.get(0).vY += speedCoef * yAcc;
		mShapeList.get(1).vX += -speedCoef * xAcc;
		mShapeList.get(1).vY += speedCoef * yAcc;
		mShapeList.get(2).vX += -speedCoef * xAcc;
		mShapeList.get(2).vY += speedCoef * yAcc;

		mShapeList.get(3).vX += speedCoef * yAcc / 2;
		mShapeList.get(3).vY += speedCoef * xAcc / 2;
		mShapeList.get(4).vX += speedCoef * yAcc / 2;
		mShapeList.get(4).vY += speedCoef * xAcc / 2;
		mShapeList.get(5).vX += speedCoef * yAcc / 2;
		mShapeList.get(5).vY += speedCoef * xAcc / 2;

		mShapeList.get(6).vX += speedCoef * xAcc;
		mShapeList.get(6).vY += -speedCoef * yAcc;

		// some bug fixing : not compulsory
		// mShapeList.get(0).x = mShapeList.get(1).x;
		// mShapeList.get(0).y = mShapeList.get(1).y;
		// mShapeList.get(2).x = mShapeList.get(1).x;
		// mShapeList.get(2).y = mShapeList.get(1).y;
		//
		// mShapeList.get(3).x = mShapeList.get(4).x;
		// mShapeList.get(3).y = mShapeList.get(4).y;
		// mShapeList.get(5).x = mShapeList.get(4).x;
		// mShapeList.get(5).y = mShapeList.get(4).y;

		// we cape the speed
		int maxSpeed = 30;
		for (FaceShape obj : mShapeList) {
			if (obj.vX > maxSpeed) {
				obj.vX = maxSpeed;
			}
			if (obj.vX < -maxSpeed) {
				obj.vX = -maxSpeed;
			}
			if (obj.vY > maxSpeed) {
				obj.vY = maxSpeed;
			}
			if (obj.vY < -maxSpeed) {
				obj.vY = -maxSpeed;
			}
		}

		/*
		 * }else{ for(FaceShape obj : mShapeList){ obj.vX = speedCoef* yAcc;
		 * obj.vY = speedCoef* xAcc; } }
		 */

		// if we go outside the screen, we invert the speed
		int padding = -shapeRadius;
		for (FaceShape obj : mShapeList) {
			// going to the left
			if (obj.x < -padding && obj.vX < 0) {
				obj.vX = -obj.vX;
				soundManager.playSound(soundManager.collisionPong);
			}
			// going to the right
			if (obj.x > sW + padding && obj.vX > 0) {
				obj.vX = -obj.vX;
				soundManager.playSound(soundManager.collisionPong);
			}
			// going to the up
			if (obj.y < -padding && obj.vY < 0) {
				obj.vY = -obj.vY;
				soundManager.playSound(soundManager.collisionPong);
			}
			// going to the down
			if (obj.y > sH + padding && obj.vY > 0) {
				obj.vY = -obj.vY;
				soundManager.playSound(soundManager.collisionPong);
			}
		}

		// //if we go outside the screen, teleport
		// int padding = shapeRadius;
		// for(FaceShape obj : mShapeList){
		// //going to the left
		// if(obj.x <-padding && obj.vX < 0){
		// obj.moveTo(sW+padding, obj.y);
		// }
		// //going to the right
		// if(obj.x >sW+padding && obj.vX > 0){
		// obj.moveTo(-padding, obj.y);
		// }
		// //going to the up
		// if(obj.y <-padding && obj.vY < 0){
		// obj.moveTo(obj.x, sH+padding);
		// }
		// //going to the down
		// if(obj.y >sH+padding && obj.vY > 0){
		// obj.moveTo(obj.x, -padding);
		// }
		// }

		// moving everything
		for (FaceShape obj : mShapeList) {
			obj.move((int) obj.vX, (int) obj.vY);
		}

	}

	public void moveWithBounds(long NOW) {
		// if we go outside the screen, we invert the speed
		int padding = -shapeRadius;
		for (FaceShape obj : mShapeList) {
			// going to the left
			if (obj.x < -padding && obj.vX < 0) {
				obj.vX = -obj.vX;
				soundManager.playSound(soundManager.collisionPong);
			}
			// going to the right
			if (obj.x > sW + padding && obj.vX > 0) {
				obj.vX = -obj.vX;
				soundManager.playSound(soundManager.collisionPong);
			}
			// going to the up
			if (obj.y < -padding && obj.vY < 0) {
				obj.vY = -obj.vY;
				soundManager.playSound(soundManager.collisionPong);
			}
			// going to the down
			if (obj.y > sH + padding && obj.vY > 0) {
				obj.vY = -obj.vY;
				soundManager.playSound(soundManager.collisionPong);
			}
		}

		// moving everything
		for (FaceShape obj : mShapeList) {
			obj.move((int) obj.vX, (int) obj.vY);
		}

	}

	public void cleanList() {
		for (FaceShape obj : mShapeList) {
			obj.cleanValues();
		}
	}

	public int getsW() {
		return sW;
	}

	public int getsH() {
		return sH;
	}

	public long getLastTouchEvent() {
		return lastTouchEvent;
	}

	public void touchEvent(int x, int y) {
		lastTouchEvent = System.currentTimeMillis();
		xFinger = x;
		yFinger = y;
	}

	public void accEvent(float x, float y) {
		lastAccEvent = System.currentTimeMillis();
		xAcc = x;
		yAcc = y;
	}

	public void blink(long NOW) {
		float scale = mShapeList.get(0).scaleY;
		// after some time, blink agan
		if (stateH.getLastBlink() == 0) {
			// scale = 0.99f;
			stateH.setLastBlink(NOW + 2000);
		}
		if (NOW - stateH.getLastBlink() > 5000) {
			scale = 0.99f;
			stateH.setLastBlink(NOW);
		}
		// closing the eye
		if (scale < 1 && scale > 0 && stateH.isOpening() == false) {
			scale -= 0.18;

			mShapeList.get(0).scaleY = scale;
			mShapeList.get(1).scaleY = scale;
			// Log.d(TAG, (int)(scale*shapeRadius/30) +"");
			// mShapeList.get(2).x = mShapeList.get(2).xCenter - (int)
			// (scale*shapeRadius/30);
			// mShapeList.get(2).y = mShapeList.get(2).yCenter - (int)
			// (scale*shapeRadius/30);
			mShapeList.get(3).scaleY = scale;
			mShapeList.get(4).scaleY = scale;
			// mShapeList.get(5).x = mShapeList.get(5).xCenter + (int)
			// (scale*shapeRadius/30);
			// mShapeList.get(5).y = mShapeList.get(5).yCenter - (int)
			// (scale*shapeRadius/30);
		}
		// when closed we open
		if (scale <= 0) {
			stateH.setOpening(true);
			scale = 0.01f;
		}
		// opening the eye
		if (scale < 1 && scale > 0 && stateH.isOpening() == true) {
			scale += 0.18;

			mShapeList.get(0).scaleY = scale;
			mShapeList.get(1).scaleY = scale;
			// Log.d(TAG, (int)(scale*shapeRadius/30) +"");
			// mShapeList.get(2).x = mShapeList.get(2).xCenter - (int)
			// (scale*shapeRadius/30);
			// mShapeList.get(2).y = mShapeList.get(2).yCenter - (int)
			// (scale*shapeRadius/30);
			mShapeList.get(3).scaleY = scale;
			mShapeList.get(4).scaleY = scale;
			// mShapeList.get(5).x = mShapeList.get(5).xCenter + (int)
			// (scale*shapeRadius/30);
			// mShapeList.get(5).y = mShapeList.get(5).yCenter - (int)
			// (scale*shapeRadius/30);
		}
		// when eye opened
		if (scale > 1) {
			stateH.setOpening(false);
			scale = 1;
		}

	}

	public void touchDown(int x, int y) {
		soundManager.playSound(soundManager.globaltouchdown);
		int touchedShape = getTouchedShape(x, y);

		if (stateH.getState() == StateHandler.ACTION_TRACKFINGER) {

			// -----------------------------------------------------------------simple
			// click mode
			if (System.currentTimeMillis() - lastTouchDown > PlayActivity.DOUBLE_CLICK_PERIOD) {

				// the mouth : a D
				if (touchedShape == 6) {
					if (mShapeList.get(6).isHitted() == false) {
						soundManager.playSoundTouchShape();
					}
					mShapeList.get(6).setHitted(!mShapeList.get(6).isHitted());
				}

				// left eye : a cross and eyebrowns
				if (touchedShape == 0) {
					if (mShapeList.get(1).isHitted() == false) {
						soundManager.playSoundTouchShape();
					}

					mShapeList.get(1).setHitted(!mShapeList.get(1).isHitted());
					mShapeList.get(2).turnTo(-45);
					mShapeList.get(5).turnTo(45);
				}

				// right eye : a cross and eyebrowns
				if (touchedShape == 3) {
					if (mShapeList.get(4).isHitted() == false) {
						soundManager.playSoundTouchShape();
					}
					mShapeList.get(4).setHitted(!mShapeList.get(4).isHitted());
					mShapeList.get(5).turnTo(45);
					mShapeList.get(2).turnTo(-45);
				}

				// a little update for the 2 eyebrowns
				if (touchedShape == 3 || touchedShape == 0) {
					if (!mShapeList.get(1).isHitted() && !mShapeList.get(4).isHitted()) {
						if (!mShapeList.get(1).isHitted()) {
							mShapeList.get(2).turnTo(0);
						}
						if (!mShapeList.get(4).isHitted()) {
							mShapeList.get(5).turnTo(0);
						}

					}
				}

			}// double click end

		}// trackfinger end

		if (stateH.getState() == StateHandler.ACTION_SCALE) {
			// -----------------------------------------------------------------simple
			// click mode
			if (System.currentTimeMillis() - lastTouchDown > PlayActivity.DOUBLE_CLICK_PERIOD) {
				// if we touchDown a shape, we scale it

				// the left eye
				if (touchedShape == 0) {
					lEyeScaleX = x;
					lEyeScaleY = y;
				}

				// the right eye
				if (touchedShape == 3) {
					rEyeScaleX = x;
					rEyeScaleY = y;
				}

				// the mouth
				if (touchedShape == 6) {
					mouthScaleX = x;
					mouthScaleY = y;
				}

			}

		}

		if (stateH.getState() == StateHandler.ACTION_TELEPORT) {
			int padding = shapeRadius;

			int newX = rd.nextInt(sW - 2 * padding) + padding;
			int newY = rd.nextInt(sH - 2 * padding) + padding;

			// the mouth : a D
			if (touchedShape == 6) {
				mShapeList.get(6).moveTo(newX, newY);
			}

			// left eye : a cross and eyebrowns
			if (touchedShape == 0) {
				mShapeList.get(0).moveTo(newX, newY);
				mShapeList.get(1).moveTo(newX, newY);
				mShapeList.get(2).moveTo(newX, newY);
			}

			// right eye : a cross and eyebrowns
			if (touchedShape == 3) {
				mShapeList.get(3).moveTo(newX, newY);
				mShapeList.get(4).moveTo(newX, newY);
				mShapeList.get(5).moveTo(newX, newY);
			}

		}

		if (stateH.getState() == StateHandler.ACTION_MOVING) {

			// the mouth
			if (touchedShape == 6) {
				mShapeList.get(6).vX = mShapeList.get(6).vX * 2;
				mShapeList.get(6).vY = mShapeList.get(6).vY * 2;
			}

			// left eye : a cross and eyebrowns
			if (touchedShape == 0) {
				mShapeList.get(0).vX = mShapeList.get(0).vX * 2;
				mShapeList.get(0).vY = mShapeList.get(0).vY * 2;
				mShapeList.get(1).vX = mShapeList.get(1).vX * 2;
				mShapeList.get(1).vY = mShapeList.get(1).vY * 2;
				mShapeList.get(2).vX = mShapeList.get(2).vX * 2;
				mShapeList.get(2).vY = mShapeList.get(2).vY * 2;
			}

			// right eye : a cross and eyebrowns
			if (touchedShape == 3) {
				mShapeList.get(3).vX = mShapeList.get(3).vX * 2;
				mShapeList.get(3).vY = mShapeList.get(3).vY * 2;
				mShapeList.get(4).vX = mShapeList.get(4).vX * 2;
				mShapeList.get(4).vY = mShapeList.get(4).vY * 2;
				mShapeList.get(5).vX = mShapeList.get(5).vX * 2;
				mShapeList.get(5).vY = mShapeList.get(5).vY * 2;
			}

		}

		lastTouchDown = System.currentTimeMillis();
	}

	/*
	 * return the number of the shape touched by the specified coordinates if no
	 * collision, return -1
	 */
	public int getTouchedShape(int x, int y) {
		int result = -1;
		int padding = shapeRadius / 3; // a little padding for the collision

		// for the moment, we don't consider scaled shapes
		int w = shapeRadius * 2;
		int h = shapeRadius * 2;
		FaceShape tmp = null;

		// the mouth
		tmp = mShapeList.get(6);
		if (x < tmp.x + w / 2 - padding && x > tmp.x - w / 2 + padding) {
			if (y < tmp.y + h / 2 - padding && y > tmp.y - h / 2 + padding) {
				result = 6;
			}
		}

		// the left eye
		tmp = mShapeList.get(0);
		if (x < tmp.x + w / 2 - padding && x > tmp.x - w / 2 + padding) {
			if (y < tmp.y + h / 2 - padding && y > tmp.y - h / 2 + padding) {
				result = 0;
			}
		}

		// the right eye
		tmp = mShapeList.get(3);
		if (x < tmp.x + w / 2 - padding && x > tmp.x - w / 2 + padding) {
			if (y < tmp.y + h / 2 - padding && y > tmp.y - h / 2 + padding) {
				result = 3;
			}
		}

		return result;
	}

	public LinkedList<FaceShape> getmShapeList() {
		return mShapeList;
	}

}
