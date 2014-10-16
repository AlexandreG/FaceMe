package com.faceme.play;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.faceme.play.faceshape.FaceShapeHandler;

public class PlayFaceView extends View {
	protected final static String TAG = "Log";
	
	protected FaceShapeHandler obHandler;
	private Canvas mCanvas;
	protected Paint mPaint;

	
	public PlayFaceView(Context context) {
		super(context);
		init();
	}

	public PlayFaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PlayFaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void init(){
		mCanvas = new Canvas();
		mPaint = new Paint();
			mPaint.setAntiAlias(true);
			mPaint.setDither(true);
	        mPaint.setFilterBitmap(true);
			mPaint.setColor(Color.BLACK);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeJoin(Paint.Join.ROUND);
			mPaint.setStrokeCap(Paint.Cap.ROUND);
			mPaint.setStrokeWidth(6);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if(obHandler != null){
			obHandler.drawAll(canvas, mPaint);
		}
	}

	public void setObHandler(FaceShapeHandler obHandler) {
		this.obHandler = obHandler;
	}

}
