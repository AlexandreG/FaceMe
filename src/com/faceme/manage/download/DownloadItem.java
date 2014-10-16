package com.faceme.manage.download;

import java.io.Serializable;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;

public class DownloadItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected BitmapDrawable bpDrawable; // a bitmap of the mouth
	protected LayerDrawable leftEyeDrawable;
	protected LayerDrawable rightEyeDrawable;
	protected String faceInfo;

	public DownloadItem(BitmapDrawable bpDrawable, LayerDrawable leftEye, LayerDrawable rightEye, String faceInfo) {
		super();
		setBp(bpDrawable);
		setLdLeft(leftEye);
		setLdRigth(rightEye);
		setFaceInfo(faceInfo);
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

	public String getFaceInfo() {
		return faceInfo;
	}

	public void setFaceInfo(String faceInfo) {
		this.faceInfo = faceInfo;
	}

}
