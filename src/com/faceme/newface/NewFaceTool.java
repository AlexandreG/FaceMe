package com.faceme.newface;

import java.util.LinkedList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class NewFaceTool {

	// the size of the pencil
	public enum Size {
		SMALL, MEDIUM, LARGE
	}

	public Size size;
	public int faceState; // from 1 to 9

	public int sideBarWidth;

	public NewFaceTool() {
		size = Size.MEDIUM;
		faceState = 1;
	}

	/**
	 * Draw the grey background and the white circle
	 */
	public void drawTarget(Canvas canvas, Paint paint, int sW, int sH, boolean scaleShapes) {
		canvas.drawColor(Color.GRAY);
		if (scaleShapes == true) {
			canvas.drawCircle(sW / 2 - sideBarWidth / 2, sH / 2, sH * 0.40f, paint);
		} else {
			canvas.drawCircle(sW / 2 - sideBarWidth / 2, sH / 2, sH / 4, paint);
		}
	}

	/**
	 * Draw all the paths on the canvas
	 */
	public void drawPathList(Canvas canvas, Paint paint, Paint paintDot, LinkedList<PathPlus> ppList) {
		int radius = 0;
		// The current path
		for (PathPlus pp : ppList) {

			if (pp.getS() == Size.SMALL) {
				radius = 2;
				paint.setStrokeWidth(4);
			}
			if (pp.getS() == Size.MEDIUM) {
				radius = 6;
				paint.setStrokeWidth(12);
			}
			if (pp.getS() == Size.LARGE) {
				radius = 15;
				paint.setStrokeWidth(30);
			}

			canvas.drawCircle(pp.getX(), pp.getY(), radius, paintDot);
			canvas.drawPath(pp.getP(), paint);
		}
	}

	/**
	 * Clean from memory the given bitmap list
	 */
	public void recycleBpList(LinkedList<Bitmap> bpList) {
		for (Bitmap bp : bpList) {
			bp.recycle();
			bp = null;
		}
	}
}
