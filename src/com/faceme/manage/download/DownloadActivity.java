package com.faceme.manage.download;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.faceme.R;
import com.faceme.SoundManager;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class DownloadActivity extends Activity {
	public final static boolean D = false;
	public final static String TAG = "Log";

	private ProgressDialog progress;

	private LinkedList<DownloadItem> mData;
	private DlListAdapter mAdapter;
	private ListView mMiniFaceDlList;

	public int TOTAL_LIST_ITEMS;
	public int NUM_ITEMS_PAGE = 5;

	private int noOfBtns;
	private Button[] btns;
	private TextView currentPage;
	private int currentPageNb;

	List<ParseObject> rawObjects;

	protected int sW;
	protected int sH;
	protected SoundManager mSoundManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download_layout);

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

		// we get the screen size for epicface
		DisplayMetrics dimension = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dimension);
		sW = dimension.widthPixels;
		sH = dimension.heightPixels;

		mData = new LinkedList<DownloadItem>();
		currentPage = (TextView) findViewById(R.id.currentPage);

		mMiniFaceDlList = (ListView) findViewById(R.id.faceListView);
		mAdapter = new DlListAdapter(this, R.layout.download_miniface, mData);
		mMiniFaceDlList.setAdapter(mAdapter);

		openProgressCircle();
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Face");
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> objects, ParseException e) {
				progress.hide();
				if (e == null) {
					Log.d(TAG, "faces found :" + objects.size());
					mSoundManager.playSound(mSoundManager.success);
					TOTAL_LIST_ITEMS = objects.size();
					rawObjects = objects;
					currentPageNb = 1;
					loadFaces(0);
					Btnfooter();
					CheckBtnBackGroud(0);
				} else {
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), "Failed to connect the database", Toast.LENGTH_LONG).show();
				}
			}
		});

		mSoundManager = new SoundManager(this);
	}

	private void Btnfooter() {
		int val = TOTAL_LIST_ITEMS % NUM_ITEMS_PAGE;
		if (val != 0)
			val = 1;
		noOfBtns = TOTAL_LIST_ITEMS / NUM_ITEMS_PAGE + val;

		LinearLayout ll = (LinearLayout) findViewById(R.id.btnLay);

		btns = new Button[noOfBtns];

		for (int i = 0; i < noOfBtns; i++) {
			btns[i] = new Button(this);
			btns[i].setBackgroundColor(getResources().getColor(android.R.color.transparent));
			btns[i].setText("" + (i + 1));

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			ll.addView(btns[i], lp);

			final int j = i;
			btns[j].setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					mSoundManager.playSound(mSoundManager.globalbutton);
					currentPageNb = j + 1;
					loadFaces(j);
					CheckBtnBackGroud(j);
				}
			});
		}
	}

	/**
	 * Method for Checking Button Backgrounds
	 */
	private void CheckBtnBackGroud(int index) {
		currentPage.setText("Web content : Page " + (index + 1) + " of " + noOfBtns);
		for (int i = 0; i < noOfBtns; i++) {
			if (i == index) {
				btns[index].setBackgroundDrawable(getResources().getDrawable(R.drawable.box_green));
				btns[i].setTextColor(getResources().getColor(android.R.color.white));
			} else {
				btns[i].setBackgroundColor(getResources().getColor(android.R.color.transparent));
				btns[i].setTextColor(getResources().getColor(android.R.color.black));
			}
		}

	}

	public void openProgressCircle() {
		progress.setMessage("Connection to the database");
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

	public void loadFaces(int requestPage) {
		mAdapter.clear();

		int start = requestPage * NUM_ITEMS_PAGE;
		for (int i = start; i < (start) + NUM_ITEMS_PAGE; i++) {
			if (i < rawObjects.size()) {
				mData.add(getItemFromRaw(rawObjects.get(i)));
			} else {
				break;
			}
		}

		// for(int i=0 ; i<rawObjects.size() ; ++i){
		// mData.add(getItemFromRaw(rawObjects.get(i)));
		// }
		mAdapter.notifyDataSetChanged();
	}

	public DownloadItem getItemFromRaw(ParseObject po) {
		byte[] byteArray;
		Bitmap b;
		Drawable[] layers = new Drawable[3];

		String userName = po.getString("user_name");
		String country = po.getString("country");

		// The mouth
		byteArray = po.getBytes("mouth");
		b = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		BitmapDrawable bdMouth = new BitmapDrawable(Bitmap.createScaledBitmap(b, sH / 6, sH / 6, true));

		// The left eye
		byteArray = po.getBytes("left_eyelid");
		b = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		layers[0] = new BitmapDrawable(Bitmap.createScaledBitmap(b, sH / 6, sH / 6, true));

		byteArray = po.getBytes("left_iris");
		b = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		layers[1] = new BitmapDrawable(Bitmap.createScaledBitmap(b, sH / 6, sH / 6, true));

		byteArray = po.getBytes("left_eyebrown");
		b = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		layers[2] = new BitmapDrawable(Bitmap.createScaledBitmap(b, sH / 6, sH / 6, true));

		LayerDrawable ldLeft = new LayerDrawable(layers);

		// The right eye
		byteArray = po.getBytes("right_eyelid");
		b = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		layers[0] = new BitmapDrawable(Bitmap.createScaledBitmap(b, sH / 6, sH / 6, true));

		byteArray = po.getBytes("right_iris");
		b = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		layers[1] = new BitmapDrawable(Bitmap.createScaledBitmap(b, sH / 6, sH / 6, true));

		byteArray = po.getBytes("right_eyebrown");
		b = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		layers[2] = new BitmapDrawable(Bitmap.createScaledBitmap(b, sH / 6, sH / 6, true));

		LayerDrawable ldRight = new LayerDrawable(layers);

		return new DownloadItem(bdMouth, ldLeft, ldRight, userName + " (" + country + ")");
	}

	public ArrayList<Bitmap> getBitmapListFromRaw(ParseObject po) {
		ArrayList<Bitmap> result = new ArrayList<Bitmap>();
		byte[] byteArray;
		Bitmap b;

		// The left eye
		byteArray = po.getBytes("left_eyelid");
		b = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		b = Bitmap.createScaledBitmap(b, sH / 2, sH / 2, true);
		result.add(b);

		byteArray = po.getBytes("left_iris");
		b = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		b = Bitmap.createScaledBitmap(b, sH / 2, sH / 2, true);
		result.add(b);

		byteArray = po.getBytes("left_eyebrown");
		b = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		b = Bitmap.createScaledBitmap(b, sH / 2, sH / 2, true);
		result.add(b);

		// The right eye
		byteArray = po.getBytes("right_eyelid");
		b = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		b = Bitmap.createScaledBitmap(b, sH / 2, sH / 2, true);
		result.add(b);

		byteArray = po.getBytes("right_iris");
		b = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		b = Bitmap.createScaledBitmap(b, sH / 2, sH / 2, true);
		result.add(b);

		byteArray = po.getBytes("right_eyebrown");
		b = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		b = Bitmap.createScaledBitmap(b, sH / 2, sH / 2, true);
		result.add(b);

		// The mouth
		byteArray = po.getBytes("mouth");
		b = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		b = Bitmap.createScaledBitmap(b, sH / 2, sH / 2, true);
		result.add(b);

		return result;
	}

	public void downloadItemOnClickHandler(View v) {
		mSoundManager.playSound(mSoundManager.globalbutton);
		DownloadItem itemToDownload = (DownloadItem) v.getTag();
		createDialogDownloadConfirm(itemToDownload);
	}

	private void createDialogDownloadConfirm(final DownloadItem itemToDownload) {

		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Confirm download ?");
		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int whichButton) {
				// Toast.makeText(getApplicationContext(),
				// "Hm, I can't upload it. It is too ugly.",
				// Toast.LENGTH_LONG).show();
				startDownload(itemToDownload);
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

	public void startDownload(DownloadItem itemToDownload) {
		int clickPos;
		clickPos = mAdapter.getPosition(itemToDownload);
		int rawObjectIndex = clickPos + (currentPageNb - 1) * NUM_ITEMS_PAGE;

		// we get the nb of faces
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		int nbFace = preferences.getInt("NB_FACE", 0);

		ArrayList<Bitmap> bps = getBitmapListFromRaw(rawObjects.get(rawObjectIndex));

		// we save the bitmap
		for (int i = 0; i < bps.size(); ++i) {
			saveToInternalSorage(nbFace, bps.get(i), "0" + Integer.toString(i) + ".png");
			// Log.d(TAG, newFaceView.getmBpList().get(i).getWidth()+"/"+
			// newFaceView.getmBpList().get(i).getWidth()+"=>"+
			// croppedBpList.get(i).getWidth()+"/"+croppedBpList.get(i).getHeight());
		}

		// we update the number of faces
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt("NB_FACE", nbFace + 1);
		editor.putInt("CURRENT_FACE", nbFace + 1);
		editor.commit();

		Toast.makeText(getApplicationContext(), "Download successful !", Toast.LENGTH_LONG).show();
		mSoundManager.playSound(mSoundManager.success);
	}

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
}
