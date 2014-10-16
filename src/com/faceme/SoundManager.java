package com.faceme;

import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.preference.PreferenceManager;

/**
 * SoundManager initialize and plays sounds.
 * 
 * @author AlexandreG
 *
 */
public class SoundManager {
	public final static boolean D = false;
	public final static String TAG = "Log";

	protected SoundPool sp;
	protected Context appContext;
	protected Random rd;

	//the total number of sounds
	public final static int SOUND_NB = 19;	

	protected boolean mute;

	//the id of all sounds
	public final int launchapp;
	public final int delete;
	public final int error;
	public final int globalbutton;

	public final int applause;
	public final int globaltouchdown;
	public final int collisionPong;

	public final int shakeroll0;
	public final int shakeroll1;
	public final int happy0;
	public final int happy1;
	public final int shakescale;

	public final int touchshape0;
	public final int touchshape1;
	public final int touchshape2;
	public final int touchshape3;
	public final int touchshape4;
	public final int touchshape5;
	public final int touchshape6;

	public final int success;

	public SoundManager(Context ct) {
		appContext = ct;
		rd = new Random();
		sp = new SoundPool(SOUND_NB, AudioManager.STREAM_MUSIC, 0);

		//load sound config
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(ct);
		mute = preferences.getBoolean("MUTE_SOUND", false);

		//init all files
		launchapp = sp.load(appContext, R.raw.launchapp, 1);
		delete = sp.load(appContext, R.raw.delete, 1);
		error = sp.load(appContext, R.raw.error, 1);
		globalbutton = sp.load(appContext, R.raw.globalbutton, 1);

		applause = sp.load(appContext, R.raw.applause, 1);
		globaltouchdown = sp.load(appContext, R.raw.globaltouchdown, 1);
		collisionPong = sp.load(appContext, R.raw.collisionpong, 1);

		shakeroll0 = sp.load(appContext, R.raw.shakeroll0, 1);
		shakeroll1 = sp.load(appContext, R.raw.shakeroll1, 1);
		happy0 = sp.load(appContext, R.raw.happy0, 1);
		happy1 = sp.load(appContext, R.raw.happy1, 1);
		shakescale = sp.load(appContext, R.raw.shakescale, 1);

		touchshape0 = sp.load(appContext, R.raw.touchshape0, 1);
		touchshape1 = sp.load(appContext, R.raw.touchshape1, 1);
		touchshape2 = sp.load(appContext, R.raw.touchshape2, 1);
		touchshape3 = sp.load(appContext, R.raw.touchshape3, 1);
		touchshape4 = sp.load(appContext, R.raw.touchshape4, 1);
		touchshape5 = sp.load(appContext, R.raw.touchshape5, 1);
		touchshape6 = sp.load(appContext, R.raw.touchshape6, 1);

		success = sp.load(appContext, R.raw.success, 1);
	}

	/**
	 * Play the given sound
	 * @param nb the id of the sound
	 */
	public void playSound(final int nb) {
		if (mute == false) {
			sp.play(nb, 1, 1, 0, 0, 1);
		}
	}

	/**
	 * Choose a sound at random and play it
	 */
	public void playSoundTouchShape() {
		if (mute == false) {
			int random = rd.nextInt(6);
			switch (random) {
			case 0:
				sp.play(touchshape0, 1, 1, 0, 0, 1);
				break;
			case 1:
				sp.play(touchshape1, 1, 1, 0, 0, 1);
				break;
			case 2:
				sp.play(touchshape2, 1, 1, 0, 0, 1);
				break;
			case 3:
				sp.play(touchshape3, 1, 1, 0, 0, 1);
				break;
			case 4:
				sp.play(touchshape4, 1, 1, 0, 0, 1);
				break;
			case 5:
				sp.play(touchshape5, 1, 1, 0, 0, 1);
				break;
			case 6:
				sp.play(touchshape6, 1, 1, 0, 0, 1);
				break;

			default:
				sp.play(touchshape6, 1, 1, 0, 0, 1);
				break;
			}
		}

	}

	public boolean isMute() {
		return mute;
	}

	public void setMute(boolean mute) {
		this.mute = mute;
	}

}
