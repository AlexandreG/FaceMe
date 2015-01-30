package com.faceme.manage;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Properties;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.faceme.MainActivity;
import com.faceme.R;
import com.faceme.SoundManager;
import com.faceme.manage.download.DownloadActivity;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

public class ManageActivity extends Activity {
	protected final static boolean D = true;
	protected final static String TAG = "Log";

	public final static int REQUEST_DOWNLOAD = 1;
	public final static int REQUEST_UPLOAD = 2;

	public final static int RESULT_NO_NETWORK = 1;
	public final static int RESULT_SUCCES = 2;

	protected final static int SAMPLE_SIZE = 3;

	private LinkedList<DataItem> mData;

	private ListView mMiniFaceList;
	private NotiFicationListAdapter mAdapter;
	protected int sW;
	protected int sH;

	protected SoundManager mSoundManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.manage_face);

		// we get the screen size for epicface
		DisplayMetrics dimension = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dimension);
		sW = dimension.widthPixels;
		sH = dimension.heightPixels;

		mData = new LinkedList<DataItem>();
		loadFaces();

		mMiniFaceList = (ListView) findViewById(R.id.listView);
		mAdapter = new NotiFicationListAdapter(this, R.layout.list_miniface, mData);
		mMiniFaceList.setAdapter(mAdapter);

		mSoundManager = new SoundManager(this);
	}

	public void loadFaces() {
		// we get the max number of faces
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		int nbFace = preferences.getInt("NB_FACE", 0);
		int currentFace = preferences.getInt("CURRENT_FACE", 0);
		// Log.d(TAG, "Max Number face:"+nbFace);

		if (nbFace == 0) {
			Toast.makeText(getBaseContext(), "No faces to show", Toast.LENGTH_LONG).show();
			mSoundManager.playSound(mSoundManager.error);
			return;
		}

		ContextWrapper cw = new ContextWrapper(getApplicationContext());
		// path to /data/data/yourapp/app_data/imageDir

		for (int i = 0; i < nbFace; ++i) {

			File globalDirectory = cw.getDir(Integer.toString(i + 1), Context.MODE_PRIVATE);
			// Log.d(TAG, globalDirectory.exists()+"");
			File f;
			boolean selectable;
			if (globalDirectory.exists()) {
				// Log.d(TAG, "global dir : "+globalDirectory.toString());
				f = new File(globalDirectory, "06.png");
				// Log.d(TAG, "f : "+f.toString());
				// if f exists, we can load the bitmap
				if (f.exists()) {
					// The mouth
					Bitmap b = getMouthBp(globalDirectory);
					BitmapDrawable ob = new BitmapDrawable(b);
					// The left eye
					LayerDrawable ldLeft = getLeftLd(globalDirectory);
					// The right eye
					LayerDrawable ldRight = getRightLd(globalDirectory);
					// Log.d(TAG, "currentFace :" +currentFace+" , i :"+ i);
					if (currentFace == i + 1) {
						selectable = false;
					} else {
						selectable = true;
					}
					mData.add(new DataItem(ob, ldLeft, ldRight, selectable));

				} else {
					// else it means the folder is empty, so we delete it
					/*
					 * if (f.isDirectory()) { String[] children = dir.list();
					 * for (int i = 0; i < children.length; i++) { new File(dir,
					 * children[i]).delete(); } }
					 */
					if (!globalDirectory.delete())
						Log.d(TAG, "Problem while deleting file :" + globalDirectory);
				}

			}
		}
	}

	public Bitmap getMouthBp(File globalPath) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = SAMPLE_SIZE;
		File f = new File(globalPath, "06.png");
		FileInputStream fis;
		BufferedInputStream buf;
		Bitmap b = null;
		try {
			fis = new FileInputStream(f);
			buf = new BufferedInputStream(fis, 8192);
			b = BitmapFactory.decodeStream(buf, null, options);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return b;
	}

	public LayerDrawable getLeftLd(File globalPath) {
		Resources r = getResources();
		Drawable[] layers = new Drawable[3];

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = SAMPLE_SIZE;
		File f;
		FileInputStream fis;
		BufferedInputStream buf;
		Bitmap b = null;
		try {
			f = new File(globalPath, "00.png");
			fis = new FileInputStream(f);
			buf = new BufferedInputStream(fis, 8192);
			b = BitmapFactory.decodeStream(buf, null, options);
			layers[0] = new BitmapDrawable(b);
			fis.close();
			buf.close();

			f = new File(globalPath, "01.png");
			fis = new FileInputStream(f);
			buf = new BufferedInputStream(fis, 8192);
			b = BitmapFactory.decodeStream(buf, null, options);
			layers[1] = new BitmapDrawable(b);
			fis.close();
			buf.close();

			f = new File(globalPath, "02.png");
			fis = new FileInputStream(f);
			buf = new BufferedInputStream(fis, 8192);
			b = BitmapFactory.decodeStream(buf, null, options);
			layers[2] = new BitmapDrawable(b);
			fis.close();
			buf.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		LayerDrawable layerDrawable = new LayerDrawable(layers);
		return layerDrawable;
	}

	public LayerDrawable getRightLd(File globalPath) {
		Resources r = getResources();
		Drawable[] layers = new Drawable[3];

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = SAMPLE_SIZE;
		File f;
		FileInputStream fis;
		BufferedInputStream buf;
		Bitmap b = null;
		try {
			f = new File(globalPath, "03.png");
			fis = new FileInputStream(f);
			buf = new BufferedInputStream(fis, 8192);
			b = BitmapFactory.decodeStream(buf, null, options);
			layers[0] = new BitmapDrawable(b);
			fis.close();
			buf.close();

			f = new File(globalPath, "04.png");
			fis = new FileInputStream(f);
			buf = new BufferedInputStream(fis, 8192);
			b = BitmapFactory.decodeStream(buf, null, options);
			layers[1] = new BitmapDrawable(b);
			fis.close();
			buf.close();

			f = new File(globalPath, "05.png");
			fis = new FileInputStream(f);
			buf = new BufferedInputStream(fis, 8192);
			b = BitmapFactory.decodeStream(buf, null, options);
			layers[2] = new BitmapDrawable(b);
			fis.close();
			buf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		LayerDrawable layerDrawable = new LayerDrawable(layers);
		return layerDrawable;
	}

	/*------------------------------------------------------------Download ---*/
	public void downLoadFaceOnClickHandler(View v) {
		if (isNetworkAvailable() == true) {
			mSoundManager.playSound(mSoundManager.globalbutton);

			Intent intent = new Intent(this, DownloadActivity.class);
			startActivityForResult(intent, REQUEST_DOWNLOAD);
		} else {
			mSoundManager.playSound(mSoundManager.error);

			Toast.makeText(getApplicationContext(), "Internet connection not found", Toast.LENGTH_LONG).show();
		}
	}

	/*------------------------------------------------------------Upload ---*/
	public void uploadItemOnClickHandler(View v) {
		mSoundManager.playSound(mSoundManager.globalbutton);

		DataItem itemToUpload = (DataItem) v.getTag();
		createDialogUpladConfirm(itemToUpload);
	}

	// the dialog windows : confirm upload
	private void createDialogUpladConfirm(final DataItem itemToUpload) {

		int uploadRemaining = getUploadRemaining();
		if (uploadRemaining > 0) {

			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle(R.string.face_upload_text);
			alert.setMessage("Due to the limitations of the server, you only have " + uploadRemaining
					+ " upload remaining");

			alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int whichButton) {
					mSoundManager.playSound(mSoundManager.globalbutton);

					// Toast.makeText(getApplicationContext(),
					// "Hm, I can't upload it. It is too ugly.",
					// Toast.LENGTH_LONG).show();
					startUpload(itemToUpload);
				}
			});

			alert.setNegativeButton("Nope", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					mSoundManager.playSound(mSoundManager.globalbutton);
				}
			});

			alert.show();
		} else {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Sorry but I can't let you upload it :/");

			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int whichButton) {
					mSoundManager.playSound(mSoundManager.globalbutton);

				}
			});

			alert.setNegativeButton("Fuuu", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
					mSoundManager.playSound(mSoundManager.globalbutton);

				}
			});

			alert.show();
		}

	}

	/*------------------------------------------------------------Delete ---*/

	// the onclick listener : delete button
	public void removeItemOnClickHandler(View v) {
		mSoundManager.playSound(mSoundManager.globalbutton);

		DataItem itemToRemove = (DataItem) v.getTag();
		createDialogRemoveConfirm(itemToRemove);
	}

	// the dialog windows : confirm delete
	private void createDialogRemoveConfirm(final DataItem itemToRemove) {

		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.face_remove_text);
		alert.setPositiveButton("Yes!", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int whichButton) {
				mSoundManager.playSound(mSoundManager.delete);
				deletFace(itemToRemove);
			}
		});

		alert.setNegativeButton("Nooo", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
				mSoundManager.playSound(mSoundManager.globalbutton);
			}
		});

		alert.show();
	}

	// And the one who REALLY delete the face
	protected void deletFace(DataItem itemToRemove) {
		int clickPos;
		clickPos = mAdapter.getPosition(itemToRemove);

		// lets load the important numbers
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		int currentFace = preferences.getInt("CURRENT_FACE", 0);
		int nbFace = preferences.getInt("NB_FACE", 0);
		// Log.d(TAG, currentFace+"");
		// Log.d(TAG, positionSelected+"");

		// we need to convert the clickPos from the adaptater to the real folder
		// number in the internal storage
		// they are different because of the empty folders
		int realIndex;
		realIndex = getRealIndex(clickPos);
		// Log.d(TAG, "realIndex : "+realIndex);
		// Log.d(TAG, "clickPos : "+clickPos);

		// we delete from the internal storage the folder and its content
		ContextWrapper cw = new ContextWrapper(getApplicationContext());
		File directoryToDelete = cw.getDir(Integer.toString(realIndex), Context.MODE_PRIVATE);
		deleteRecursive(directoryToDelete);

		// if we delete the current face, we need to select an other one
		// it is important to put this shit AFTER deleting it ===_===;
		if (realIndex == currentFace) {
			Log.d(TAG, "face selected to delete");
			currentFace = getFirstFaceRealIndex();

			// if all the faces are deleted, we update the nb of face
			if (currentFace == -1) {
				nbFace = 0;
				currentFace = 0;
				SharedPreferences.Editor editor = preferences.edit();
				editor.putInt("NB_FACE", nbFace);
				editor.putInt("CURRENT_FACE", currentFace);
				editor.commit();
				Log.d(TAG, "lets reset the numbers!");
			} else {
				// we update the currentface with an arbitrary one
				SharedPreferences.Editor editor = preferences.edit();
				editor.putInt("CURRENT_FACE", currentFace);
				editor.commit();
			}
			Log.d(TAG, "New face selected : " + currentFace);
			updateAdaptaterButtons(clickPos);
		}

		// we remove it from the adaptater
		mAdapter.remove(itemToRemove);

	}

	// we need to convert the clickPos from the adaptater to the real folder
	// number in the internal storage
	// they are different because of the empty folders
	public int getRealIndex(int clickPos) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		int nbFace = preferences.getInt("NB_FACE", 0);

		++clickPos; // between 1 and ?
		int realFolderCounter = 0; // between 0 and nbFace

		ContextWrapper cw = new ContextWrapper(getApplicationContext());
		for (int i = 1; i <= nbFace; ++i) {
			File globalDirectory = cw.getDir(Integer.toString(i), Context.MODE_PRIVATE);
			// Log.d(TAG, globalDirectory.exists()+"");
			File f;
			// Log.d(TAG, i+"i");
			if (globalDirectory.exists()) {
				// Log.d(TAG, "global dir : "+globalDirectory.toString());
				f = new File(globalDirectory, "06.png");
				// Log.d(TAG, "f : "+f.toString());
				// if f exists, we can load the bitmap
				if (f.exists()) {
					++realFolderCounter;
					// Log.d(TAG, "Folder n"+i+"is a real folder");
					// Log.d(TAG,
					// "Total number of real folder : "+realFolderCounter);
					if (realFolderCounter == clickPos) {
						return i;
					}
				}

			}
		}
		return -1;
	}

	// delete the desired file or folder
	public void deleteRecursive(File fileOrDirectory) {
		if (fileOrDirectory.isDirectory())
			for (File child : fileOrDirectory.listFiles())
				deleteRecursive(child);

		fileOrDirectory.delete();
	}

	public int getFirstFaceRealIndex() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		int nbFace = preferences.getInt("NB_FACE", 0);
		File f;

		ContextWrapper cw = new ContextWrapper(getApplicationContext());
		for (int i = 1; i <= nbFace; ++i) {
			File globalDirectory = cw.getDir(Integer.toString(i), Context.MODE_PRIVATE);
			if (globalDirectory.exists()) {
				f = new File(globalDirectory, "06.png");
				if (f.exists()) {
					Log.d(TAG, "smallest sellected file :" + f.toString());
					return i;
				}
			}
		}
		return -1;
	}

	/*------------------------------------------------------------Select ---*/

	public void selectFaceOnClickHandler(View v) {
		mSoundManager.playSound(mSoundManager.globalbutton);

		// we get the position of the selected item
		DataItem itemToRemove = (DataItem) v.getTag();
		int clickPos;
		clickPos = mAdapter.getPosition(itemToRemove);

		// we get the real index of it
		int realIndex = -1;
		realIndex = getRealIndex(clickPos);

		// we update the preferences
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt("CURRENT_FACE", realIndex);
		editor.commit();

		// we update the buttons and the Linkedlist
		updateAdaptaterButtons(clickPos);
	}

	public void updateAdaptaterButtons(int index) {
		boolean selectable;
		for (int i = 0; i < mData.size(); ++i) {
			if (i == index) {
				selectable = false;
				// Log.d(TAG, "index depuis l'activity" + index);
			} else {
				selectable = true;
			}
			mData.get(i).setSelectable(selectable);
		}
		mAdapter.notifyDataSetChanged();
		// Log.d(TAG, mData.get(index).isSelectable()+"");

	}

	/*------------------------------------------------------------Upload Next---*/
	private ProgressDialog progress;

	public void startUpload(DataItem itemToUpload) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		int clickPos;
		clickPos = mAdapter.getPosition(itemToUpload);
		int realIndex;
		realIndex = getRealIndex(clickPos);

		progress = new ProgressDialog(this);

		Properties properties = new Properties();
		try {
			InputStream fileStream = getAssets().open("parse.properties");
			properties.load(fileStream);
			fileStream.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Parse.initialize(this, properties.getProperty("appid"), properties.getProperty("clientkey"));
		progress = new ProgressDialog(this);

		// if no network = stop that shit
		if (isNetworkAvailable() == false) {
			Toast.makeText(getApplicationContext(), "Internet connection not found", Toast.LENGTH_LONG).show();
			mSoundManager.playSound(mSoundManager.error);
			return;
		}

		ArrayList<Bitmap> bpList;
		ArrayList<byte[]> dataS = null;
		bpList = getBitmapList(realIndex);
		dataS = getDataFromBitmapList(bpList);
		Log.d(TAG, realIndex + "");
		Log.d(TAG, dataS.size() + "");

		ParseObject face = new ParseObject("Face");
		face.put("left_eyelid", dataS.get(0));
		face.put("left_iris", dataS.get(1));
		face.put("left_eyebrown", dataS.get(2));
		face.put("right_eyelid", dataS.get(3));
		face.put("right_iris", dataS.get(4));
		face.put("right_eyebrown", dataS.get(5));
		face.put("mouth", dataS.get(6));
		face.put("country", this.getResources().getConfiguration().locale.getCountry());
		face.put("screenWidth", sW);
		face.put("screeHeight", sH);
		face.put("user_name", preferences.getString("USER_NAME", "unnamed"));
		face.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				progress.hide();
				Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_LONG).show();
				mSoundManager.playSound(mSoundManager.success);
				decreaseUploadRemaining();
			}
		});
		openProgressCircle();

	}

	public ArrayList<Bitmap> getBitmapList(int realIndex) {
		ArrayList<Bitmap> result = new ArrayList<Bitmap>();

		ContextWrapper cw = new ContextWrapper(getApplicationContext());
		// path to /data/data/yourapp/app_data/imageDir
		File globalDirectory = cw.getDir(Integer.toString(realIndex), Context.MODE_PRIVATE);
		// Log.d(TAG, "gd : "+globalDirectory.toString());

		try {
			for (int i = 0; i < 7; ++i) {
				File f = new File(globalDirectory, "0" + Integer.toString(i) + ".png");
				if (!f.exists()) {
					Intent intent = new Intent();
					setResult(MainActivity.RESULT_NOCURRENTFACE, intent);
					finish();
				}
				// Log.d(TAG, "file : "+f.toString());
				FileInputStream fis = new FileInputStream(f);
				BufferedInputStream buf = new BufferedInputStream(fis, 8192);
				Bitmap b = BitmapFactory.decodeStream(buf);
				// Log.d(TAG, b+"");
				result.add(b);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public ArrayList<byte[]> getDataFromBitmapList(ArrayList<Bitmap> bpList) {
		ArrayList<byte[]> result = new ArrayList<byte[]>();
		for (Bitmap bp : bpList) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bp.compress(Bitmap.CompressFormat.PNG, 100, bos);
			result.add(bos.toByteArray());
		}

		return result;
	}

	public boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public void openProgressCircle() {
		progress.setMessage("Upload in progress");
		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progress.setIndeterminate(true);
		progress.show();
		final int totalProgressTime = 100;

		final Thread t = new Thread() {
			@Override
			public void run() {

				int jumpTime = 0;
				while (jumpTime < totalProgressTime) {
					try {
						sleep(200);
						jumpTime += 5;
						progress.setProgress(jumpTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			}
		};
		t.start();
	}

	public int getUploadRemaining() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		int uploadRemaining = preferences.getInt("UPLOAD_REMAINING", MainActivity.MAX_NB_UPLOAD);

		return uploadRemaining;
	}

	public void decreaseUploadRemaining() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		// we update the current value
		int uploadRemaining = preferences.getInt("UPLOAD_REMAINING", MainActivity.MAX_NB_UPLOAD);
		uploadRemaining = uploadRemaining - 1;
		if (uploadRemaining < 0) {
			uploadRemaining = 0;
		}
		// we save it
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt("UPLOAD_REMAINING", uploadRemaining);
		editor.commit();
	}

	/*------------------------------------------------------------onResult ---*/

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_DOWNLOAD) {
			if (resultCode == RESULT_NO_NETWORK) {
				Toast.makeText(getApplicationContext(), "Internet connection not found", Toast.LENGTH_LONG).show();
				mSoundManager.playSound(mSoundManager.error);
			}

		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}
}
