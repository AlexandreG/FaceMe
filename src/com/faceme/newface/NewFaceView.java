package com.faceme.newface;

import java.util.LinkedList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.faceme.newface.NewFaceTool.Size;

public class NewFaceView extends View implements OnTouchListener {
	protected final static boolean D = true;
	protected final static String TAG = "Log";

	NewFaceTool newFaceTool;
	NewFaceActivity newFaceActivity;

	private Canvas mCanvas;
	private Paint mPaint;
	private Paint mCirclePaint;
	private Paint mDotPaint;
	private Path mPath;
	protected LinkedList<PathPlus> mPathList;
	protected LinkedList<Bitmap> mBpList;

	public int sWidth;
	public int sHeight;

	public NewFaceView(Context context) {
		super(context);
		init(context);
	}

	public NewFaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public NewFaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	/**
	 * Initialize the objects
	 * 
	 * @param context
	 */
	public void init(Context context) {
		setFocusable(true);
		setFocusableInTouchMode(true);

		this.setOnTouchListener(this);

		sHeight = context.getResources().getDisplayMetrics().heightPixels;
		sWidth = context.getResources().getDisplayMetrics().widthPixels;

		mBpList = new LinkedList<Bitmap>();
		mBpList.add(Bitmap.createBitmap(sWidth, sHeight, Bitmap.Config.ARGB_8888));
		mPath = new Path();
		mPathList = new LinkedList<PathPlus>();
		// mPathList.add(mPath);
		mCanvas = new Canvas(mBpList.getFirst());
		mPaint = new Paint();

		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(Color.BLACK);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(16);

		mCirclePaint = new Paint(mPaint);
		// mCirclePaint.setStrokeWidth(2);
		mCirclePaint.setColor(Color.WHITE);
		mCirclePaint.setStyle(Paint.Style.FILL);

		mDotPaint = new Paint(mPaint);
		mDotPaint.setStyle(Paint.Style.FILL);
	}

	public void initComplement(NewFaceTool nft, NewFaceActivity nfa) {
		newFaceTool = nft;
		newFaceActivity = nfa;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	// ------------------------------------------------------------------OnTouchListener
	private float mX, mY;
	private static final float TOUCH_TOLERANCE = 4;

	private void touch_start(float x, float y) {
		mPath.reset();
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
		mPathList.addLast(new PathPlus(mPath, newFaceTool.size, (int) x, (int) y));
		// newFaceTool.drawDot((int)x, (int)y, mCanvas, mDotPaint);
	}

	private void touch_move(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}

	}

	private void touch_up() {
		mPath.lineTo(mX, mY);
		// commit the path to our offscreen
		// mCanvas.drawPath(mPath, mPaint);
		// kill this so we don't double draw
		mPath = new Path();
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();

		mPaint.setStyle(Paint.Style.STROKE);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// new event => we enable the undo button
			if (mPathList.size() == 0) {
				newFaceActivity.setEnableUndoButtun(true);
			}
			touch_start(x, y);
			break;
		case MotionEvent.ACTION_MOVE:
			touch_move(x, y);
			break;
		case MotionEvent.ACTION_UP:
			touch_up();
			break;
		}

		// mPaint.setStyle(Paint.Style.FILL);

		return true;
	}

	// ------------------------------------------------------------------onDraw
	@Override
	protected void onDraw(Canvas canvas) {
		// The circle
		newFaceTool.drawTarget(canvas, mCirclePaint, sWidth, sHeight, newFaceActivity.isScaleShapes());

		// the path and the dots
		newFaceTool.drawPathList(canvas, mPaint, mDotPaint, mPathList);

		// The bitmaps
		int listSize = mBpList.size();
		switch (newFaceTool.faceState) {
		case 1:
			if (listSize >= 1)
				canvas.drawBitmap(mBpList.get(0), 0, 0, mPaint);
			break;
		case 2:
			if (listSize >= 2)
				canvas.drawBitmap(mBpList.get(0), 0, 0, mPaint);
			canvas.drawBitmap(mBpList.get(1), 0, 0, mPaint);
			break;

		case 3:
			if (listSize >= 3)
				canvas.drawBitmap(mBpList.get(0), 0, 0, mPaint);
			canvas.drawBitmap(mBpList.get(1), 0, 0, mPaint);
			canvas.drawBitmap(mBpList.get(2), 0, 0, mPaint);
			break;

		case 4:
			if (listSize >= 4)
				canvas.drawBitmap(mBpList.get(3), 0, 0, mPaint);
			break;

		case 5:
			if (listSize >= 5)
				canvas.drawBitmap(mBpList.get(3), 0, 0, mPaint);
			canvas.drawBitmap(mBpList.get(4), 0, 0, mPaint);
			break;

		case 6:
			if (listSize >= 6)
				canvas.drawBitmap(mBpList.get(3), 0, 0, mPaint);
			canvas.drawBitmap(mBpList.get(4), 0, 0, mPaint);
			canvas.drawBitmap(mBpList.get(5), 0, 0, mPaint);
			break;

		case 7:
			if (listSize >= 7)
				canvas.drawBitmap(mBpList.get(6), 0, 0, mPaint);
			break;

		default:
			break;
		}
	}

	public void updateSize() {
		if (newFaceTool.size == Size.SMALL) {
			mPaint.setStrokeWidth(8);
		}
		if (newFaceTool.size == Size.MEDIUM) {
			mPaint.setStrokeWidth(16);
		}
		if (newFaceTool.size == Size.LARGE) {
			mPaint.setStrokeWidth(40);
		}
	}

	public void nextStep() {
		if (newFaceTool.faceState <= 7) {
			// we draw for real the paths and the dots
			drawAllThePath();

			// we clear the paths
			mPathList.clear();
			// we disable the button
			newFaceActivity.setEnableUndoButtun(false);
			mBpList.add(Bitmap.createBitmap(sWidth, sHeight, Bitmap.Config.ARGB_8888));
			if (newFaceTool.faceState - 1 <= mBpList.size()) {
				mCanvas = new Canvas(mBpList.get(newFaceTool.faceState - 1));
			}

		}
	}

	public void drawAllThePath() {
		// we draw for real the paths and the dots
		newFaceTool.drawPathList(mCanvas, mPaint, mDotPaint, mPathList);
	}

	public LinkedList<Bitmap> getmBpList() {
		return mBpList;
	}

	public void removeLast() {
		mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

		// mBpList.removeLast();
		// mBpList.add(Bitmap.createBitmap(sWidth,sHeight,Bitmap.Config.ARGB_8888));
		// mCanvas = new Canvas(mBpList.get(newFaceTool.faceState-1));

		if (mPathList.size() > 0) {
			mPathList.removeLast();
		}
		if (mPathList.size() == 0) {
			newFaceActivity.setEnableUndoButtun(false);
		}
	}

}