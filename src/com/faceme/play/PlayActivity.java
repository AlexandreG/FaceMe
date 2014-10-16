package com.faceme.play;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import com.faceme.MainActivity;
import com.faceme.SoundManager;
import com.faceme.play.faceshape.FaceShapeHandler;
import com.faceme.play.sense.Accelerometer;
import com.faceme.play.sense.TouchScreen;

/**
 * The activity where you can play
 * @author AlexandreG
 *
 */
public class PlayActivity extends Activity {
	protected final static boolean D = true;
	protected final static String TAG = "Log";

	public final static int ONSHAKE = 1;
	public final static int ONTOUCH = 2;
	public final static int ONTOUCHDOWN = 21;
	public final static int ONTOUCMOVE = 22;
	public final static int ONTOUCHUP = 23;
	public final static int ONTOUCHCANCEL = 24;
	public final static int ONCAM = 3;
	public final static int ONACCELEROMETER = 4;

	public final static int FRAME_PERIOD = 50;
	public final static int DOUBLE_CLICK_PERIOD = 300;

	protected int SW;	//screen width
	protected int SH;	//screen height
	protected int fingerX;
	protected int fingerY;
	protected int touchType;
	protected float aX;
	protected float aY;
	protected float aZ;

	protected PlayFaceView mPlayFaceView;	//The view
	protected FaceShapeHandler mObHandler;	//The objects
	protected StateHandler mStateHandler;

	protected TouchScreen mTouchScreen;	//The touch
	protected Accelerometer mAcc;	//The accelerometer

	protected SoundManager mSoundManager;	//The sound

	//The engine
	private Handler handler;
	final Runnable runnable  = new Runnable(){
		public void run(){
			handler.postDelayed(this, FRAME_PERIOD);

			//Move all objects
			if(mObHandler != null)
				mObHandler.moveAll();

			//Update the view
			if(mPlayFaceView != null)
				mPlayFaceView.postInvalidate();
			
			//Update the state
			if(mStateHandler != null){
				mStateHandler.updateState();
			}
		}
	};

	//The sensors communication
	private Handler msgHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ONSHAKE:
				Log.d(TAG, "--Shaked");
				Bundle myBundle = msg.getData();
				//Log.d(TAG, "size :" +myBundle.size());
				float x = myBundle.getFloat("x");
				float y = myBundle.getFloat("y");
				float z = myBundle.getFloat("z");
				float G = myBundle.getFloat("G");
				mStateHandler.shakyShakeYourBody();
				break;

			case ONACCELEROMETER:
				//Log.d(TAG, "--ONACCELEROMETER");
				Bundle myBundle2 = msg.getData();
				aX = myBundle2.getFloat("x");
				aY = myBundle2.getFloat("y");
				mObHandler.accEvent(aX, aY);
				break;

			case ONTOUCH:
				//Log.d(TAG, "--Touched");
				Bundle myBundle3 = msg.getData();
				touchType =  (int) myBundle3.getFloat("type");
				//Log.d(TAG, "x :"+msg.arg1 + " y :"+msg.arg2);
				if(touchType == ONTOUCHUP || touchType == ONTOUCHCANCEL){
					fingerX = -1;
					fingerY = -1;
				}else{
					fingerX = (int) myBundle3.getFloat("x");
					fingerY = (int) myBundle3.getFloat("y");
				}
				if(touchType == ONTOUCHDOWN){
					mObHandler.touchDown(fingerX, fingerY);
				}
				mStateHandler.youTouchMyTralala();
				mObHandler.touchEvent(fingerX, fingerY);
				break;

			case ONCAM:
				Log.d(TAG, "--CamMovement");

				break;

			default:
				break;

			}
			super.handleMessage(msg);
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if(D) Log.v(TAG, "+++ ON CREATE +++");
		super.onCreate(savedInstanceState);

		mSoundManager = new SoundManager(this);
		
		mPlayFaceView = new PlayFaceView(this);
		mPlayFaceView.setBackgroundColor(Color.WHITE);
		setContentView(mPlayFaceView);

		SW = this.getResources().getDisplayMetrics().widthPixels;
		SH = this.getResources().getDisplayMetrics().heightPixels;
		mStateHandler = new StateHandler(this, mSoundManager);
		mObHandler = new FaceShapeHandler(this, SW, SH, mStateHandler, mSoundManager);
		mStateHandler.setShapeHandler(mObHandler);
		//mObHandler.add(SW/2, SH/2, BitmapFactory.decodeResource(this.getResources(), R.drawable.img));
		mPlayFaceView.setObHandler(mObHandler);

		loadFace();

		mTouchScreen = new TouchScreen(this, msgHandler, mPlayFaceView);
		mAcc = new Accelerometer(getApplicationContext(), msgHandler);
	}


	/**
	 * Load from memory the current face
	 */
	public void loadFace(){
		//we get the current face nb
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		int currentFaceNb = preferences.getInt("CURRENT_FACE", 0);
		//Log.d(TAG, "Current Number :"+currentFaceNb);
		if(currentFaceNb == 0){
			Intent intent = new Intent();
			setResult(MainActivity.RESULT_NOCURRENTFACE, intent);
			finish();
		}

		ContextWrapper cw = new ContextWrapper(getApplicationContext());
		// path to /data/data/yourapp/app_data/imageDir
		File globalDirectory = cw.getDir(Integer.toString(currentFaceNb), Context.MODE_PRIVATE);
		//Log.d(TAG, "gd : "+globalDirectory.toString());

		try {
			for(int i=0 ; i<7 ; ++i){
				File f=new File(globalDirectory, "0"+Integer.toString(i)+".png");
				if(!f.exists()){
					Intent intent = new Intent();
					setResult(MainActivity.RESULT_NOCURRENTFACE, intent);
					finish();
				}
				//Log.d(TAG, "file : "+f.toString());
				FileInputStream fis = new FileInputStream(f);
				BufferedInputStream buf = new BufferedInputStream(fis, 8192);
				Bitmap b = BitmapFactory.decodeStream(buf);            
				//				Log.d(TAG, b+"");

				mObHandler.add(getXPos(i), getYPos(i), b);
			}

		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
	}

	public int getXPos(int i){
		int result = 0;
		switch (i) {
		//Left eye
		case 0:
			result = SW/4;
			break;
		case 1:
			result = SW/4;
			break;
		case 2:
			result = SW/4;
			break;
			//Right eye
		case 3:
			result = 3*SW/4;
			break;
		case 4:
			result = 3*SW/4;
			break;
		case 5:
			result = 3*SW/4;
			break;
			//Mouth
		case 6:
			result = SW/2;
			break;

		default:
			break;
		}

		return result;
	}

	public int getYPos(int i){
		int result = 0;
		switch (i) {
		//Left eye
		case 0:
			result = SH/4;
			break;
		case 1:
			result = SH/4;
			break;
		case 2:
			result = SH/4;
			break;
			//Right eye
		case 3:
			result = SH/4;
			break;
		case 4:
			result = SH/4;
			break;
		case 5:
			result = SH/4;
			break;
			//Mouth
		case 6:
			result = 3*SH/4;
			break;

		default:
			break;
		}

		return result;
	}




	@Override
	protected void onResume() {
		if(D) Log.v(TAG, "+ ON RESUME +");
		super.onResume();

		//Start the engine
		handler = new Handler();
		runnable.run();
		
		//start the accelerometer
		if(mAcc != null){
			mAcc.start();
		}
	}

	@Override
	protected void onPause() {
		if(D) Log.v(TAG, "- ON PAUSE -");
		super.onPause();

		//Stop the engine
		handler.removeCallbacks(runnable);
		
		//Stop the accelerometer
		if(mAcc !=null){
			mAcc.stop();
		}
	}

	@Override
	protected void onDestroy() {
		if(D) Log.v(TAG, "--- ON DESTROY ---");
		super.onDestroy();

		if(msgHandler != null){
			msgHandler.removeCallbacksAndMessages(null);
		}
	}
}
