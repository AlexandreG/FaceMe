package com.faceme.play.sense;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.faceme.play.PlayActivity;

public class Accelerometer implements SensorEventListener {
	protected final static boolean D = true;
	protected final static String TAG = "Log";

	protected final static float ACCURACY = 3f;
	protected SensorManager sensorManager;
	protected long lastUpdate;
	protected Handler stateHandler;

	protected Context appContext;

	public Accelerometer(Context context, Handler handler) {
		super();
		appContext = context;
		stateHandler = handler;

		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		lastUpdate = System.currentTimeMillis();
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			getAccelerometer(event);
		}

	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	private void getAccelerometer(SensorEvent event) {
		float[] values = event.values;
		// Movement
		float x = values[0];
		float y = values[1];
		float z = values[2];

		float accelationSquareRoot = (x * x + y * y + z * z)
				/ (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
		long actualTime = System.currentTimeMillis();

		if (actualTime - lastUpdate < PlayActivity.FRAME_PERIOD) {
			return;
		}
		lastUpdate = actualTime;

		if (accelationSquareRoot >= ACCURACY) {
			// Log.d("Log", "device was shuffled");
			Message msg = new Message();
			msg.what = PlayActivity.ONSHAKE;

			Bundle humbleBundle = new Bundle(4);
			humbleBundle.putFloat("x", x);
			humbleBundle.putFloat("y", y);
			humbleBundle.putFloat("z", z);
			humbleBundle.putFloat("G", accelationSquareRoot);

			msg.setData(humbleBundle);

			stateHandler.sendMessageDelayed(msg, 0);
		} else {
			// Log.d("Log", "toto");

			Message msg = new Message();
			msg.what = PlayActivity.ONACCELEROMETER;

			Bundle humbleBundle = new Bundle(3);
			humbleBundle.putFloat("x", x);
			humbleBundle.putFloat("y", y);
			humbleBundle.putFloat("z", z);

			msg.setData(humbleBundle);
			stateHandler.sendMessageDelayed(msg, 0);

		}
	}

	public void start() {
		// register this class as a listener for the orientation and
		// accelerometer sensors
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	public void stop() {
		sensorManager.unregisterListener(this);
	}

}
