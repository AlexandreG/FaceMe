package com.faceme.manage;

import java.io.Serializable;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;

public class DataItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected BitmapDrawable bpDrawable; // a bitmap of the mouth
	protected LayerDrawable leftEyeDrawable;
	protected LayerDrawable rightEyeDrawable;
	protected boolean selectable;

	public DataItem(BitmapDrawable bpDrawable, LayerDrawable leftEye, LayerDrawable rightEye, boolean selectable) {
		super();
		setBp(bpDrawable);
		setLdLeft(leftEye);
		setLdRigth(rightEye);
		setSelectable(selectable);
	}

	public BitmapDrawable getBpDrawable() {
		return bpDrawable;
	}

	public void setBp(BitmapDrawable bpD) {
		this.bpDrawable = bpD;
	}

	public LayerDrawable getLdLeft() {
		return leftEyeDrawable;
	}

	public void setLdLeft(LayerDrawable led) {
		this.leftEyeDrawable = led;
	}

	public LayerDrawable getLdRigth() {
		return rightEyeDrawable;
	}

	public void setLdRigth(LayerDrawable red) {
		this.rightEyeDrawable = red;
	}

	public boolean isSelectable() {
		return selectable;
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
		// Log.d("Log", ""+selectable);
	}

}
