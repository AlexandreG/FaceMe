package com.faceme.play.sense;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;

public class MyCamera implements SurfaceHolder.Callback {
	protected final static int MOVEMENT = 0002;

	Camera camera;
	PictureCallback rawCallback;
	ShutterCallback shutterCallback;
	PictureCallback jpegCallback;
	SurfaceView surfaceView;
	SurfaceHolder surfaceHolder;

	private final int BITMAP_WIDTH = 10;
	private final int BITMAP_HEIGHT = 10;
	private final int COLOR_SENSITIVITY = 40;
	private final int PHOTO_PERIOD = 300;

	protected Bitmap refbm;
	protected Bitmap currentbm;

	protected long reftime;
	protected long currenttime;

	Button stop, start;
	boolean threadLaunched;

	Thread myTread;
	Handler stateHandler;

	public MyCamera(final Activity mainActivity, final Context context, Handler handler) {
		stateHandler = handler;

		// ---------------------------------------CallBacks

		/** Handles data for jpeg picture */
		shutterCallback = new ShutterCallback() {
			public void onShutter() {
				// Log.i("Log", "onShutter'd");
			}
		};

		rawCallback = new PictureCallback() {
			public void onPictureTaken(byte[] data, Camera camera) {
				// Log.d("Log", "onPictureTaken - raw");
			}
		};

		jpegCallback = new PictureCallback() {
			public void onPictureTaken(byte[] data, Camera camera) {
				// Log.d("Log", "onPictureTaken - jpeg");

				if (data != null) {
					BitmapFactory.Options options = new BitmapFactory.Options();
					if (refbm != null) {
						refbm.recycle();
						refbm = null;
					}
					refbm = currentbm;
					Bitmap temp = BitmapFactory.decodeByteArray(data, 0, data.length, options);
					currentbm = Bitmap.createScaledBitmap(temp, BITMAP_WIDTH, BITMAP_HEIGHT, false);
					if (temp != null) {
						temp.recycle();
						temp = null;
					}
					// Log.d("Log", "Picture saved !");
				} else {
					Log.d("Log", "CAM :data null !");
				}
			}
		};

		Log.d("Log", "CAM :callbacks done");

		// ---------------------------------------Inits

		// surfaceView = (SurfaceView)
		// mainActivity.findViewById(R.id.SurfaceView);
		// surfaceView = new SurfaceView(context);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		camera = null;
		reftime = System.currentTimeMillis() - 2000;
		threadLaunched = true;

		// ---------------------------------------Thread
		myTread = new Thread() {
			@Override
			public void run() {
				while (threadLaunched) {
					currenttime = System.currentTimeMillis();
					if (currenttime - reftime > PHOTO_PERIOD) {
						try {
							// Silent Mode Programatically
							// NewFaceActivity.ringMode(false);
							camera.takePicture(shutterCallback, rawCallback, jpegCallback);
						} catch (Exception e) {
							e.printStackTrace();
						}
						final boolean tmp = compareBitmap();

						if (tmp == false) {
							// Log.d("Log", "CAM :Mouvement!");
							Message msg = new Message();
							msg.what = MOVEMENT;
							stateHandler.sendMessageDelayed(msg, MOVEMENT);
						} else {
							// Log.d("Log", "CAM : /");

						}

						reftime = System.currentTimeMillis();
					}
					// Log.d("Log", "fin du if");

				}
			}
		};
	}

	public void start() {
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// handler
				// ----------------------------launch Camera
				if (camera == null) {

					try {
						// camera = Camera.open();
						if (Camera.getNumberOfCameras() >= 2) {

							// if you want to open front facing camera use this
							// line
							camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);

							// if you want to use the back facing camera
							// camera =
							// Camera.open(CameraInfo.CAMERA_FACING_BACK);

						}
						Camera.Parameters param;
						param = camera.getParameters();
						// modify parameter
						param.setPreviewFrameRate(20);
						List<Camera.Size> previewSizes = param.getSupportedPreviewSizes();
						List<Camera.Size> pictureSizes = param.getSupportedPictureSizes();

						/*
						 * for(int i = 0 ; i< previewSizes.size() ; ++i){
						 * Log.d("Log", previewSizes.get(i).height + " "+
						 * previewSizes.get(i).width); }
						 * 
						 * for(int i = 0 ; i< pictureSizes.size() ; ++i){
						 * Log.d("Log", pictureSizes.get(i).height + " "+
						 * pictureSizes.get(i).width); }
						 */
						Camera.Size previewSize = previewSizes.get(previewSizes.size() - 1);
						Camera.Size pictureSize = pictureSizes.get(pictureSizes.size() - 1);
						param.setPreviewSize(previewSize.width, previewSize.height);
						param.setPictureSize(pictureSize.width, pictureSize.height);
						camera.setParameters(param);
						camera.setPreviewDisplay(surfaceHolder);
						camera.startPreview();

					} catch (IOException e) {
						Log.e("Log", "CAM :init_camera: " + e);
						e.printStackTrace();
					} catch (RuntimeException e) {
						Log.e("Log", "CAM :init_camera: " + e);
						e.printStackTrace();
						return;
					}

					Log.d("Log", "CAM : launch Cam done");
				}

				// ----------------------------launch Thread
				threadLaunched = true;
				try {
					myTread.start();
					Log.d("Log", "CAM :launch Thread done");
				} catch (IllegalThreadStateException e) {
					Log.d("Log", "exception at myThread.start : " + e);
				}
				// end of Handler
			}
		}, 1000);
	}

	public void stop() {
		threadLaunched = false;
		if (camera != null) {
			camera.stopPreview();
			camera.release();
			camera = null;
			Log.d("Log", "CAM :Camera stopped");
		}
	}

	protected boolean compareBitmap() {
		if (refbm == null || currentbm == null) {
			return false;
		}
		if (refbm.getHeight() != currentbm.getHeight() || refbm.getWidth() != currentbm.getWidth()) {
			return false;
		}
		boolean similar = true;
		int px1 = 0;
		int px2 = 0;
		for (int i = 0; i < currentbm.getHeight(); ++i) {
			for (int j = 0; j < currentbm.getWidth(); ++j) {
				px1 = refbm.getPixel(i, j);
				px2 = currentbm.getPixel(i, j);
				// Log.d("Log", Color.red(px1) + " ; " + Color.red(px2));
				if (Color.red(px1) - Color.red(px2) < COLOR_SENSITIVITY
						&& Color.red(px1) - Color.red(px2) < COLOR_SENSITIVITY

						&& Color.blue(px1) - Color.blue(px2) < COLOR_SENSITIVITY
						&& Color.blue(px1) - Color.blue(px2) < COLOR_SENSITIVITY

						&& Color.green(px1) - Color.green(px2) < COLOR_SENSITIVITY
						&& Color.green(px1) - Color.green(px2) < COLOR_SENSITIVITY) {
				} else {
					similar = false;
				}
			}
		}
		return similar;
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub

	}

}
