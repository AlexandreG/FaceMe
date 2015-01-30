package com.faceme;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.faceme.manage.ManageActivity;
import com.faceme.newface.NewFaceActivity;
import com.faceme.play.PlayActivity;

/**
 * This activity deals with the home screen
 * 
 * @author AlexandreG
 * 
 */
public class MainActivity extends Activity {
	public final static boolean D = false;
	public final static String TAG = "Log";
	public final static int REQUEST_NEW_FACE = 1;
	public final static int REQUEST_PLAY = 2;
	public final static int RESULT_NOCURRENTFACE = 1;

	public final static int MAX_NB_UPLOAD = 5;
	public final static int MAX_USERNAME_SIZE = 10;

	protected ImageView epicFace; // the rotating image in the backgroung
	private static final float ROTATE_FROM = 0.0f;
	private static final float ROTATE_TO = -10.0f * 360.0f;// 3.141592654f *
															// 32.0f;

	protected int sW; // screen width
	protected int sH; // screen height

	protected SoundManager mSoundManager;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (D)
			Log.v(TAG, "+++ ON CREATE +++");

		setContentView(R.layout.main_layout);

		// we put this in the front (we want epic face in the backgroud)
		LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayoutMain);
		ll.bringToFront();

		// we get the screen size for epicface
		DisplayMetrics dimension = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dimension);
		sW = dimension.widthPixels;
		sH = dimension.heightPixels;

		// epicFace
		epicFace = (ImageView) findViewById(R.id.epicFace);

		// start animation
		RotateAnimation r = new RotateAnimation(ROTATE_FROM, ROTATE_TO, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		r.setInterpolator(new LinearInterpolator());
		r.setDuration(120000);

		r.setRepeatCount(10000);
		epicFace.startAnimation(r);

		mSoundManager = new SoundManager(this);
		// mSoundManager.playSound(mSoundManager.launchapp);
		
		//if first launch, we add a default face in the memory
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean isFirstLaunch = preferences.getBoolean("IS_FIRST_LAUNCH", true);
		if(isFirstLaunch == true){
			saveDemoFaceInMemory();
			
			// we update the number of faces and first launch
			SharedPreferences.Editor editor = preferences.edit();
			editor.putInt("NB_FACE", 1);
			editor.putInt("CURRENT_FACE", 1);
			editor.putBoolean("IS_FIRST_LAUNCH", false);
			editor.commit();
			
			//Launch game mode
			onClickPlay(null);
		}
		

		// if no faces, we disable the play button
		int currentFaceNb = preferences.getInt("CURRENT_FACE", 0);
		Button bt = (Button) findViewById(R.id.buttonPlay);
		Button bt2 = (Button) findViewById(R.id.buttonManage);
		if (currentFaceNb == 0) {
			bt.setEnabled(false);
			bt2.setEnabled(false);
		} else {
			bt.setEnabled(true);
			bt2.setEnabled(true);
		}
	}

	/**
	 * Action when NewFace button is clicked
	 */
	public void onClickNewFace(View v) {
		mSoundManager.playSound(mSoundManager.globalbutton);
		Intent intent = new Intent(this, NewFaceActivity.class);
		startActivityForResult(intent, REQUEST_NEW_FACE);
	}

	/**
	 * Action when Play button is clicked
	 */
	public void onClickPlay(View v) {
		mSoundManager.playSound(mSoundManager.globalbutton);
		Intent intent = new Intent(this, PlayActivity.class);
		startActivityForResult(intent, REQUEST_PLAY);
	}

	/**
	 * Action when Manage button is clicked
	 */
	public void onClickManage(View v) {
		mSoundManager.playSound(mSoundManager.globalbutton);
		Intent intent = new Intent(this, ManageActivity.class);
		startActivity(intent);
	}

	/**
	 * Action when Quit button is clicked
	 */
	public void onClickQuit(View v) {
		mSoundManager.playSound(mSoundManager.globalbutton);

		// Show confirm popup
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Quit game ?");
		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				finish();
				mSoundManager.playSound(mSoundManager.globalbutton);
			}
		});

		alert.setNegativeButton("Nope", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
				mSoundManager.playSound(mSoundManager.globalbutton);
			}
		});

		alert.show();
	}

	/**
	 * Action when Menu button is clicked
	 */
	public void onClickMenu(View v) {
		mSoundManager.playSound(mSoundManager.globalbutton);
		openOptionsMenu();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (D)
			Log.v(TAG, "onActivityResult " + resultCode);
		switch (requestCode) {
		case REQUEST_NEW_FACE:
			if (resultCode == Activity.RESULT_OK) {
				// if we just finished a face, we launch the play mode
				Toast.makeText(getBaseContext(), "Wonderful !", Toast.LENGTH_LONG).show();
				mSoundManager.playSound(mSoundManager.applause);
				Intent intent = new Intent(this, PlayActivity.class);
				startActivityForResult(intent, REQUEST_PLAY);
			}
			break;

		case REQUEST_PLAY:
			if (resultCode == RESULT_NOCURRENTFACE) {
				Toast.makeText(getBaseContext(), "Newston we got a problem!", Toast.LENGTH_LONG).show();
			}
			break;
		}
	}

	@Override
	public void openOptionsMenu() {

		Configuration config = getResources().getConfiguration();
		if ((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) > Configuration.SCREENLAYOUT_SIZE_LARGE) {
			int originalScreenLayout = config.screenLayout;
			config.screenLayout = Configuration.SCREENLAYOUT_SIZE_LARGE;
			super.openOptionsMenu();
			config.screenLayout = originalScreenLayout;
		} else {
			super.openOptionsMenu();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		// we load the user preference
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean showHints = preferences.getBoolean("SHOW_HINT", true);
		MenuItem item = menu.findItem(R.id.hintItem);
		item.setChecked(showHints);

		boolean scaleShape = preferences.getBoolean("SCALE_SHAPE", false);
		MenuItem item2 = menu.findItem(R.id.shapeSize);
		item2.setChecked(scaleShape);

		boolean muteSound = preferences.getBoolean("MUTE_SOUND", false);
		MenuItem itemSound = menu.findItem(R.id.sound);
		itemSound.setChecked(muteSound);

		String userName = preferences.getString("USER_NAME", "Ananamous");
		MenuItem itemName = menu.findItem(R.id.username);
		itemName.setTitle("Username : " + userName);
		itemName.setTitleCondensed("Username : " + userName);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();

		switch (item.getItemId()) {
		case R.id.hintItem:
			// we update the menu
			item.setChecked(!item.isChecked());

			// we save the value
			editor.putBoolean("SHOW_HINT", item.isChecked());
			editor.commit();
			return false;
		case R.id.shapeSize:
			// we update the menu
			item.setChecked(!item.isChecked());

			// we save the value
			editor.putBoolean("SCALE_SHAPE", item.isChecked());
			editor.commit();
			return false;
		case R.id.username:
			showUsernameDialog(item);
			return false;
		case R.id.muteSound:
			// we update the menu
			item.setChecked(!item.isChecked());
			// we save the value
			editor.putBoolean("MUTE_SOUND", item.isChecked());
			editor.commit();
			if (mSoundManager != null)
				mSoundManager.setMute(item.isChecked());
			return false;
		}

		mSoundManager.playSound(mSoundManager.globalbutton);
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Display a popup to change the username
	 * 
	 * @param item
	 */
	public void showUsernameDialog(final MenuItem item) {
		// we get the current name
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String userName = preferences.getString("USER_NAME", "Ananamous");

		// we build the popup
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		final EditText input = new EditText(this);
		input.setText(userName);
		input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(MAX_USERNAME_SIZE) });

		alert.setView(input);
		alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				mSoundManager.playSound(mSoundManager.globalbutton);
				SharedPreferences.Editor editor = preferences.edit();

				String value = input.getText().toString().trim();
				// little back door
				if (value.equals("ggJydR6D")) {
					// we update the current value
					int uploadRemaining = preferences.getInt("UPLOAD_REMAINING", MainActivity.MAX_NB_UPLOAD);
					uploadRemaining += 5;
					// we save it
					editor.putInt("UPLOAD_REMAINING", uploadRemaining);
					editor.commit();

					Toast.makeText(getApplicationContext(), "5 extra upload for you <3", Toast.LENGTH_SHORT).show();
				} else {
					if (value.isEmpty() == true) {
						// we save the new name
						editor.putString("USER_NAME", "Ananamous");
						editor.commit();

						item.setTitle("Username : Ananamous");
						item.setTitleCondensed("Username : Ananamous");
					} else {
						// we save the new name
						editor.putString("USER_NAME", value);
						editor.commit();

						item.setTitle("Username : " + value);
						item.setTitleCondensed("Username : " + value);

					}

					Toast.makeText(getApplicationContext(), "Username updated", Toast.LENGTH_SHORT).show();
				}
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				mSoundManager.playSound(mSoundManager.globalbutton);
				dialog.cancel();
			}
		});
		alert.show();
	}

	/**
	 * Save in the internal storage the face in the drawables
	 */
	private void saveDemoFaceInMemory(){
		//load bitmap from res
		LinkedList<Bitmap> bpList = new LinkedList<Bitmap>();
		bpList.add(BitmapFactory.decodeResource(getResources(), R.drawable.facedemo00));
		bpList.add(BitmapFactory.decodeResource(getResources(), R.drawable.facedemo01));
		bpList.add(BitmapFactory.decodeResource(getResources(), R.drawable.facedemo02));
		bpList.add(BitmapFactory.decodeResource(getResources(), R.drawable.facedemo03));
		bpList.add(BitmapFactory.decodeResource(getResources(), R.drawable.facedemo04));
		bpList.add(BitmapFactory.decodeResource(getResources(), R.drawable.facedemo05));
		bpList.add(BitmapFactory.decodeResource(getResources(), R.drawable.facedemo06));
		
		// we save the bitmaps
		for (int i = 0; i < bpList.size(); ++i) {
			saveToInternalSorage(0, bpList.get(i), "0" + Integer.toString(i) + ".png");
			// Log.d(TAG, newFaceView.getmBpList().get(i).getWidth()+"/"+
			// newFaceView.getmBpList().get(i).getWidth()+"=>"+
			// croppedBpList.get(i).getWidth()+"/"+croppedBpList.get(i).getHeight());
		}

		// we recycle the bitmap
		for (Bitmap bp : bpList) {
			bp.recycle();
			bp = null;
		}
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
	}

	@Override
	protected void onPause() {
		if (D)
			Log.v(TAG, "- ON PAUSE -");
		super.onPause();
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
