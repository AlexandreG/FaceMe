package com.faceme.play;

import java.util.Random;

import android.content.Context;
import android.graphics.RectF;
import android.os.Handler;
import android.util.Log;

import com.faceme.SoundManager;
import com.faceme.play.faceshape.FaceShape;
import com.faceme.play.faceshape.FaceShapeHandler;

public class StateHandler {
	protected final static String TAG = "Log";

	// public final static int WAIT_BLINK = 10;
	protected boolean opening;
	protected long lastBlink;
	public final static int WAIT_LOOKDOWN = 11;
	protected long lastLookDown;
	public final static int WAIT_WOLFENSTEIN = 12;
	public final static int WAIT_BREAKOUT = 13;
	public final static int WAIT_LEAVING = 14;

	public final static int EMOTE_SHAKE = 20;
	public final static int EMOTE_HAPPY = 21;
	protected boolean goingDown;
	protected RectF rectFLeft;
	protected RectF rectFRight;
	protected RectF rectFMouth;
	protected float posY;
	public final static int EMOTE_ANGRY = 22;
	public final static int EMOTE_SHOCK = 23;
	protected boolean goingBig;
	public final static int EMOTE_DRUNK = 24;

	public final static int ACTION_TRACKFINGER = 30;
	public final static int ACTION_TELEPORT = 31;
	public final static int ACTION_MOVING = 32;
	public final static int ACTION_SCALE = 33;

	public final static int SHAKE_DURATION = 2000;
	public final static int EMOTE_DURATION = 2000;
	public final static int WAITING_DURATION_1 = 20000;
	public final static int WAITING_DURATION_2 = 10000;

	protected int state; // the state of the face

	protected long stateBeginning; // the moment the state begun
	protected Random rd;
	protected Context ct;

	protected FaceShapeHandler fsh;

	protected SoundManager soundManager; // The sound

	public StateHandler(Context context, SoundManager sm) {
		rd = new Random();
		ct = context;

		int sW = ct.getResources().getDisplayMetrics().widthPixels;
		int sH = ct.getResources().getDisplayMetrics().heightPixels;
		rectFLeft = new RectF(sW / 4 - sH / 6, sH / 4, sW / 4 + sH / 4, sH / 2 + sH / 8);
		rectFRight = new RectF(3 * sW / 4 - sH / 6, sH / 4, 3 * sW / 4 + sH / 4, sH / 2 + sH / 8);
		rectFMouth = new RectF(sW / 2 - sH / 6, 3 * sH / 4 - sH / 4, sW / 2 + sH / 6, sH / 2 + sH / 3);

		setState(ACTION_TRACKFINGER);
		soundManager = sm;
	}

	public void setShapeHandler(FaceShapeHandler faceShapeHandler) {
		fsh = faceShapeHandler;
	}

	// shake means new state !
	public void shakyShakeYourBody() {
		// update state
		switch (state) {

		case EMOTE_SHAKE: // we do nothing obviously!
			break;
		case EMOTE_HAPPY: // we do nothing obviously!
			break;
		case EMOTE_SHOCK: // we do nothing obviously!
			break;
		case EMOTE_ANGRY: // we do nothing obviously!
			break;
		case EMOTE_DRUNK: // we do nothing obviously!
			break;

		// we new state !
		default:
			newShakeState();

			break;
		}
	}

	public void youTouchMyTralala() {
		// we leave the waiting mode
		if (state >= 10 && state <= 19) { // waiting mode
			setState(ACTION_TRACKFINGER);
		}
	}

	public int getState() {
		return state;
	}

	public void setState(int s) {
		Log.d(TAG, "New state :" + s);
		stateBeginning = System.currentTimeMillis();
		state = s;
		if (fsh != null)
			fsh.cleanList();

		switch (s) {
		case ACTION_TRACKFINGER:
			opening = false;
			lastBlink = 0;
			break;
		// case WAIT_BLINK:
		// opening = false;
		// lastBlink = 0;
		// break;
		case WAIT_LOOKDOWN:
			lastLookDown = 0;
			break;
		case EMOTE_SHOCK:
			goingBig = true;
			// fsh.getmShapeList().get(0).moveTo(fsh.getsW()/2 - fsh.getsH()/2,
			// fsh.getsW()/2);
			// fsh.getmShapeList().get(1).moveTo(fsh.getsW()/2 - fsh.getsH()/2,
			// fsh.getsW()/2);
			// fsh.getmShapeList().get(2).moveTo(fsh.getsW()/2 - fsh.getsH()/2,
			// fsh.getsW()/2);
			//
			// fsh.getmShapeList().get(3).moveTo(fsh.getsW()/2 + fsh.getsH()/2,
			// fsh.getsW()/2);
			// fsh.getmShapeList().get(4).moveTo(fsh.getsW()/2 + fsh.getsH()/2,
			// fsh.getsW()/2);
			// fsh.getmShapeList().get(5).moveTo(fsh.getsW()/2 + fsh.getsH()/2,
			// fsh.getsW()/2);

			break;
		case EMOTE_HAPPY:
			goingDown = true;
			posY = 0;
			break;
		case ACTION_SCALE:
			float scaleX;
			float scaleY;

			scaleX = (rd.nextInt(9) + 2);
			scaleX = scaleX / 5.0f;
			scaleY = (rd.nextInt(9) + 2);
			scaleY = scaleY / 5.0f;
			fsh.getmShapeList().get(0).setScale(scaleX, scaleY);
			fsh.getmShapeList().get(1).setScale(scaleX, scaleY);

			scaleX = (rd.nextInt(9) + 2);
			scaleX = scaleX / 5.0f;
			scaleY = (rd.nextInt(9) + 2);
			scaleY = scaleY / 5.0f;
			fsh.getmShapeList().get(3).setScale(scaleX, scaleY);
			fsh.getmShapeList().get(4).setScale(scaleX, scaleY);

			scaleX = (rd.nextInt(9) + 2);
			scaleX = scaleX / 5.0f;
			scaleY = (rd.nextInt(9) + 2);
			scaleY = scaleY / 5.0f;
			fsh.getmShapeList().get(6).setScale(scaleX, scaleY);

			break;

		case ACTION_TELEPORT:
			int posX = rd.nextInt(fsh.getsW());
			int posY = rd.nextInt(fsh.getsH());
			// int angle = rd.nextInt(360);
			fsh.getmShapeList().get(0).moveTo(posX, posY);
			// fsh.getmShapeList().get(0).turnTo(angle);
			fsh.getmShapeList().get(1).moveTo(posX, posY);
			// fsh.getmShapeList().get(1).turnTo(angle);
			fsh.getmShapeList().get(2).moveTo(posX, posY);
			// fsh.getmShapeList().get(2).turnTo(angle);

			posX = rd.nextInt(fsh.getsW());
			posY = rd.nextInt(fsh.getsH());
			// angle = rd.nextInt(360);
			fsh.getmShapeList().get(3).moveTo(posX, posY);
			// fsh.getmShapeList().get(3).turnTo(angle);
			fsh.getmShapeList().get(4).moveTo(posX, posY);
			// fsh.getmShapeList().get(4).turnTo(angle);
			fsh.getmShapeList().get(5).moveTo(posX, posY);
			// fsh.getmShapeList().get(5).turnTo(angle);

			posX = rd.nextInt(fsh.getsW());
			posY = rd.nextInt(fsh.getsH());
			// angle = rd.nextInt(360);
			fsh.getmShapeList().get(6).moveTo(posX, posY);
			// fsh.getmShapeList().get(6).turnTo(angle);

			break;

		case ACTION_MOVING:
			int speedX = rd.nextInt(20) - 10;
			int speedY = rd.nextInt(20) - 10;
			fsh.getmShapeList().get(0).setSpeed(speedX, speedY);
			fsh.getmShapeList().get(1).setSpeed(speedX, speedY);
			fsh.getmShapeList().get(2).setSpeed(speedX, speedY);

			speedX = rd.nextInt(20) - 10;
			speedY = rd.nextInt(20) - 10;
			fsh.getmShapeList().get(3).setSpeed(speedX, speedY);
			fsh.getmShapeList().get(4).setSpeed(speedX, speedY);
			fsh.getmShapeList().get(5).setSpeed(speedX, speedY);

			speedX = rd.nextInt(20) - 10;
			speedY = rd.nextInt(20) - 10;
			fsh.getmShapeList().get(6).setSpeed(speedX, speedY);
			break;
		default:
			break;
		}
	}

	protected void newShakeState() {
		final int newState;
		if (state == ACTION_TRACKFINGER) {
			newState = ACTION_SCALE;

			// we show the animation
			setState(EMOTE_SHOCK);
			soundManager.playSound(soundManager.shakescale);
		} else if (state == ACTION_SCALE) {
			newState = ACTION_MOVING;

			// we show the animation
			setState(EMOTE_SHAKE);
			soundManager.playSound(soundManager.shakeroll0);
		} else {
			newState = ACTION_TRACKFINGER;

			// we show the animation
			setState(EMOTE_HAPPY);
			soundManager.playSound(soundManager.happy1);
		}

		// we program the end of the emote
		final Handler handler2 = new Handler();
		handler2.postDelayed(new Runnable() {
			@Override
			public void run() {
				setState(newState);
			}
		}, EMOTE_DURATION + 1);

	}

	public void updateState() {

		if (state == WAIT_LOOKDOWN) {
			if (System.currentTimeMillis() - fsh.getLastTouchEvent() > WAITING_DURATION_2
					&& System.currentTimeMillis() - stateBeginning > WAITING_DURATION_2) {
				setState(ACTION_TELEPORT);
			}
		} else {
			if (System.currentTimeMillis() - fsh.getLastTouchEvent() > WAITING_DURATION_1
					&& System.currentTimeMillis() - stateBeginning > WAITING_DURATION_1) {

				setState(EMOTE_SHAKE);
				// we program the action
				final Handler handler1 = new Handler();
				handler1.postDelayed(new Runnable() {
					@Override
					public void run() {
						setState(WAIT_LOOKDOWN);
					}
				}, SHAKE_DURATION / 2);

			}
		}
	}

	/*--------------------------------------------------------The deep depth of getters and setters--*/

	public boolean isOpening() {
		return opening;
	}

	public void setOpening(boolean opening) {
		this.opening = opening;
	}

	public long getLastBlink() {
		return lastBlink;
	}

	public void setLastBlink(long lastBlink) {
		this.lastBlink = lastBlink;
	}

	public long getLastLookDown() {
		return lastLookDown;
	}

	public void setLastLookDown(long lastLookDown) {
		this.lastLookDown = lastLookDown;
	}

	public boolean isGoingDown() {
		return goingDown;
	}

	public void setGoingDown(boolean goingDown) {
		this.goingDown = goingDown;
	}

	public RectF getRectFLeft() {
		return rectFLeft;
	}

	public RectF getRectFRight() {
		return rectFRight;
	}

	public float getPosY() {
		return posY;
	}

	public void setPosY(float posY) {
		this.posY = posY;
	}

	public RectF getRectFMouth() {
		return rectFMouth;
	}

	public boolean isGoingBig() {
		return goingBig;
	}

	public void setGoingBig(boolean goingBig) {
		this.goingBig = goingBig;
	}

}
