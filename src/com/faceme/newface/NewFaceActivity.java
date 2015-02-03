package com.faceme.newface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.faceme.R;
import com.faceme.SoundManager;
import com.faceme.newface.NewFaceTool.Size;

/**
 * The activity when you create a new face
 * 
 * @author AlexandreG
 * 
 */
public class NewFaceActivity extends Activity {
	private final static boolean D = true;
	private final static String TAG = "Log";

	private final static int FRAME_PERIOD = 50;
	private final static int CROP_ITER = 5;

	private boolean showHints;
	private boolean scaleShapes;

	// Layout
	private NewFaceView newFaceView;
	private RadioGroup radioGroupSize;
	private TextView stepTextView;
	private TextView textViewStepTitle;
	private Button undo;
	private Button next;

	private NewFaceTool mNewFaceTool;

	private SoundManager mSoundManager;

	// Draw each FRAME_PERIOD
	private Handler handler;
	final Runnable runnable = new Runnable() {
		public void run() {
			handler.postDelayed(this, FRAME_PERIOD);
			newFaceView.postInvalidate();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (D)
			Log.v(TAG, "+++ ON CREATE +++");
		super.onCreate(savedInstanceState);

		setContentView(R.layout.new_face);

		mSoundManager = new SoundManager(this);

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		showHints = preferences.getBoolean("SHOW_HINT", true);
		scaleShapes = preferences.getBoolean("SCALE_SHAPE", false);

		mNewFaceTool = new NewFaceTool();

		newFaceView = (NewFaceView) findViewById(R.id.NewFaceView);
		newFaceView.initComplement(mNewFaceTool, this);

		stepTextView = (TextView) findViewById(R.id.textViewStep);
		stepTextView.setText("Step 1/7");
		textViewStepTitle = (TextView) findViewById(R.id.textViewStepTitle);
		textViewStepTitle.setText("Left eyelid");

		next = (Button) findViewById(R.id.next);
		next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSoundManager.playSound(mSoundManager.globalbutton);

				++mNewFaceTool.faceState;
				if (mNewFaceTool.faceState <= 7) {
					stepTextView.setText("Step " + mNewFaceTool.faceState + "/7");
					newFaceView.nextStep();
					showMessage();
				} else {
					// Toast.makeText(getBaseContext(),
					// "Saving in progress ...", Toast.LENGTH_LONG).show();
					newFaceView.drawAllThePath();
					saveNewFace();
				}
				
				textViewStepTitle.setText(getCurrentStateTitle(mNewFaceTool.faceState));
			}
		});

		undo = (Button) findViewById(R.id.undo);
		// at the begining : no undo possible : we disable undo
		undo.setEnabled(false);
		undo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSoundManager.playSound(mSoundManager.globalbutton);
				newFaceView.removeLast();
			}
		});

		radioGroupSize = (RadioGroup) findViewById(R.id.radioGroupSize);
		radioGroupSize.check(R.id.radioButtonSizeMedium);

		radioGroupSize.setOnCheckedChangeListener(new android.widget.RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// Log.d(TAG, checkedId+"");
				mSoundManager.playSound(mSoundManager.globalbutton);
				if (checkedId == R.id.radioButtonSizeSmall) {
					mNewFaceTool.size = Size.SMALL;
				}
				if (checkedId == R.id.radioButtonSizeMedium) {
					mNewFaceTool.size = Size.MEDIUM;
				}
				if (checkedId == R.id.radioButtonSizeLarge) {
					mNewFaceTool.size = Size.LARGE;
				}
				newFaceView.updateSize();
			}
		});

		// we send the pop up just after : we need to wait few ms to have the
		// width of he side bar
		// (before : side bar = 0, we draw the circle on the left and the popup
		// block the circle in this state
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				showMessage();
			}
		}, 50);
	}
	
	/**
	 * Return the title of the current state ex: "Left eyelid"
	 */
	private String getCurrentStateTitle(int state){
		switch (state) {
		case 1:
			return "Left eyelid";
		case 2:
			return "Left iris";
		case 3:
			return "Left eyebrow";
		case 4:
			return "Right eyelid";
		case 5:
			return "Right iris";
		case 6:
			return "Right eyebrow";
		case 7:
			return "Mouth";

		default:
			return "";
		}
	}

	public void setEnableUndoButtun(boolean bl) {
		undo.setEnabled(bl);
	}

	/**
	 * Show the popup for each state
	 */
	public void showMessage() {
		if (showHints == true) {

			ImageView image = new ImageView(this);

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// AlertDialog alertDialog = new AlertDialog.Builder(this).create();

			// Setting Dialog Title
			builder.setTitle("Let's create a new face ! (" + mNewFaceTool.faceState + ")");

			// Setting Dialog Message
			switch (mNewFaceTool.faceState) {
			case 1:
				builder.setMessage(this.getString(R.string.step1));
				image.setImageResource(R.drawable.popup1);
				break;
			case 2:
				builder.setMessage(this.getString(R.string.step2));
				image.setImageResource(R.drawable.popup2);
				break;
			case 3:
				builder.setMessage(this.getString(R.string.step3));
				image.setImageResource(R.drawable.popup3);
				break;
			case 4:
				builder.setMessage(this.getString(R.string.step4));
				image.setImageResource(R.drawable.popup4);
				break;
			case 5:
				builder.setMessage(this.getString(R.string.step5));
				image.setImageResource(R.drawable.popup5);
				break;
			case 6:
				builder.setMessage(this.getString(R.string.step6));
				image.setImageResource(R.drawable.popup6);
				break;
			case 7:
				builder.setMessage(this.getString(R.string.step7));
				image.setImageResource(R.drawable.popup7);
				break;

			default:
				break;
			}

			builder.setView(image);

			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					mSoundManager.playSound(mSoundManager.globalbutton);
				}
			});

			builder.show();
		}
	}

	/**
	 * Save in the momory the new face
	 */
	public void saveNewFace() {
		// we get the nb of faces
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		int nbFace = preferences.getInt("NB_FACE", 0);

		// we crop the bitmap
		LinkedList<Bitmap> croppedBpList = cropAndResize(newFaceView.getmBpList());
		// we save the bitmap
		for (int i = 0; i < croppedBpList.size(); ++i) {
			saveToInternalSorage(nbFace, croppedBpList.get(i), "0" + Integer.toString(i) + ".png");
			// Log.d(TAG, newFaceView.getmBpList().get(i).getWidth()+"/"+
			// newFaceView.getmBpList().get(i).getWidth()+"=>"+
			// croppedBpList.get(i).getWidth()+"/"+croppedBpList.get(i).getHeight());
		}

		// we recycle the bitmap
		mNewFaceTool.recycleBpList(newFaceView.getmBpList());
		mNewFaceTool.recycleBpList(croppedBpList);

		// we update the number of faces
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt("NB_FACE", nbFace + 1);
		editor.putInt("CURRENT_FACE", nbFace + 1);
		editor.commit();

		// Set result and finish this Activity
		Intent intent = new Intent();
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	/**
	 * Save the given bitmap to the memory
	 * 
	 * @param nbFace
	 *            the number of all faces
	 * @param bitmapImage
	 *            the bitmap to save
	 * @param name
	 *            the name of the file
	 */
	private void saveToInternalSorage(int nbFace, Bitmap bitmapImage, String name) {
		ContextWrapper cw = new ContextWrapper(getApplicationContext());
		// path to /data/data/yourapp/app_data/imageDir
		File directory = cw.getDir(Integer.toString(nbFace + 1), Context.MODE_PRIVATE);
		// Create imageDir
		File mypath = new File(directory, name);
		// Log.d(TAG, "mypath: " +mypath);

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(mypath);
			// Use the compress method on the BitMap object to write image to
			// the OutputStream
			bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.close();
		} catch (FileNotFoundException e) {
			Log.d(TAG, "File not found: " + e.getMessage());
		} catch (IOException e) {
			Log.d(TAG, "Error accessing file: " + e.getMessage());
		}

	}

	/**
	 * Resize the given bitmaps
	 */
	public LinkedList<Bitmap> cropAndResize(LinkedList<Bitmap> bpList) {
		LinkedList<Bitmap> result = new LinkedList<Bitmap>();
		Bitmap tmpBp;
		Bitmap tmpBp2;
		for (Bitmap currentBp : bpList) {
			// DICHOTOMIE METHODE
			// tmpBp = mNewFaceTool.cropTransparentBpm(currentBp, CROP_ITER);

			// RECTANGLE METHODE
			int sW = newFaceView.sWidth;
			int sH = newFaceView.sHeight;

			if (scaleShapes == true) {
				tmpBp = Bitmap.createBitmap(currentBp, sW / 2 - (int) (sH * 0.4) - mNewFaceTool.sideBarWidth / 2,
						(int) (sH * 0.1), (int) (sH * 0.8), (int) (sH * 0.8));
				tmpBp2 = Bitmap.createScaledBitmap(tmpBp, sH / 2, sH / 2, true);
				result.add(tmpBp2);

			} else {
				tmpBp = Bitmap.createBitmap(currentBp, sW / 2 - sH / 4 - mNewFaceTool.sideBarWidth / 2, sH / 4, sH / 2,
						sH / 2);
				result.add(tmpBp);
			}

			// Little check
			// canvas.drawRect(sW/2-sH/4-sideBarWidth/2, sH/4, sH/2 +
			// sW/2-sH/4-sideBarWidth/2, sH/2+sH/4, paint);
			// if(tmpBp.getWidth()> newFaceView.sWidth/2 || tmpBp.getHeight()>
			// newFaceView.sHeight/2 ){
			// Log.d(TAG, "Caution : Picture very big !");
			//
		}

		return result;
	}

	public boolean isScaleShapes() {
		return scaleShapes;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		mNewFaceTool.sideBarWidth = findViewById(R.id.linearLayout).getWidth();
		// Log.d(TAG, mNewFaceTool.sideBarWidth+"");
	}

	@Override
	protected void onStart() {
		if (D)
			Log.v(TAG, "++ ON START ++");
		super.onStart();
	}

	@Override
	protected void onResume() {
		if (D)
			Log.v(TAG, "+ ON RESUME +");
		super.onResume();

		// Draw each FRAME_PERIOD
		handler = new Handler();
		runnable.run();
	}

	@Override
	protected void onPause() {
		if (D)
			Log.v(TAG, "- ON PAUSE -");
		super.onPause();

		// Stop the draw fonction
		handler.removeCallbacks(runnable);
	}

	@Override
	protected void onStop() {
		if (D)
			Log.v(TAG, "-- ON STOP --");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		if (D)
			Log.v(TAG, "--- ON DESTROY ---");
		super.onDestroy();
	}

}