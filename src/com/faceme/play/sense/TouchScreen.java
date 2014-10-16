package com.faceme.play.sense;

import com.faceme.play.PlayActivity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class TouchScreen implements OnTouchListener {
	protected final static boolean D = true;
	protected final static String TAG = "Log";

	protected Handler stateHandler;
	protected long lastSentMsg;

	public TouchScreen(Activity mainActivity, Handler handler, View v) {
		stateHandler = handler;
		v.setOnTouchListener(this);
		lastSentMsg = 0;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		if (System.currentTimeMillis() - lastSentMsg > PlayActivity.FRAME_PERIOD) {
			lastSentMsg = System.currentTimeMillis();
			Message msg = new Message();

			int x = -1;
			int y = -1;
			x = (int) event.getX();
			y = (int) event.getY();
			Bundle humbleBundle = new Bundle(3);
			humbleBundle.putFloat("x", x);
			humbleBundle.putFloat("y", y);

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				humbleBundle.putFloat("type", PlayActivity.ONTOUCHDOWN);
				break;

			case MotionEvent.ACTION_MOVE:
				humbleBundle.putFloat("type", PlayActivity.ONTOUCMOVE);
				break;

			case MotionEvent.ACTION_UP:
				humbleBundle.putFloat("type", PlayActivity.ONTOUCHUP);
				break;

			default:
				humbleBundle.putFloat("type", PlayActivity.ONTOUCHCANCEL);
				break;
			}

			msg.setData(humbleBundle);
			msg.what = PlayActivity.ONTOUCH;
			stateHandler.sendMessageDelayed(msg, 0);
		}

		return true;
	}

}
