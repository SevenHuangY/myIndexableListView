package com.nlt.baseUI;

import com.nlt.baseObject.IndexableAdapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;

public class IndexableScroller
{
	private int mState;
	private static final int STATE_HIDDEN = 0;
	private static final int STATE_SHOWING = 1;
	private static final int STATE_SHOWN = 2;
	private static final int STATE_HIDING = 3;

	private IndexableListView mListView;
	private IndexableAdapter mAdapter;
	private String[] mSections;
	private int viewWidth, viewHeight; // ListView width,height
	private int indexWidth, indexHeight; // index width,height
	private int itemHeight;
	private int previewHeight;
	private int mCurrentSection = -1;
	private Context context;

	private RectF mIndexbarRect;
	private RectF mPreviewRectF;
	private int margin;
	private int padding;
	private float density;
	private Paint indexPaint;
	private Paint previewPaint;
	private float textSize;
	private final String TAG = "test";
	private boolean mIsIndexing = false;
	private float mAlphaRate;

	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (mState)
			{
				case STATE_SHOWING:
					// Fade in effect
					mAlphaRate += (1 - mAlphaRate) * 0.2;
					if (mAlphaRate > 0.9)
					{
						mAlphaRate = 1;
						setState(STATE_SHOWN);
					}
					mListView.invalidate();
					fade(10);
					break;
				case STATE_SHOWN:
					// If no action, hide automatically
					setState(STATE_HIDING);
					break;
				case STATE_HIDING:
					// Fade out effect
					mAlphaRate -= mAlphaRate * 0.2;
					if (mAlphaRate < 0.1)
					{
						mAlphaRate = 0;
						setState(STATE_HIDDEN);
					}
					mListView.invalidate();
					fade(10);
					break;
			}
		}
	};

	public IndexableScroller(Context context, int width, int height,
			IndexableListView listView)
	{
		this.viewHeight = height;
		this.viewWidth = width;
		this.context = context;

		mListView = listView;
		mAdapter = (IndexableAdapter) mListView.getAdapter();
		mSections = (String[]) mAdapter.getSections();

		density = context.getResources().getDisplayMetrics().density;
		mAlphaRate = 1;

		margin = (int) (5 * density);
		indexWidth = (int) (20 * density);
		indexHeight = viewHeight - 2 * margin;

		indexPaint = new Paint();
		textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14,
				context.getResources().getDisplayMetrics());
		indexPaint.setTextSize(textSize);


		itemHeight = (int) (indexPaint.descent() - indexPaint.ascent());

		padding = (indexHeight - mSections.length * itemHeight)
				/ (mSections.length + 1);
		
		mIndexbarRect = new RectF((viewWidth - indexWidth - margin), margin,
				(viewWidth - margin), (viewHeight - margin));

		
		previewPaint = new Paint();
		textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 32,
				context.getResources().getDisplayMetrics());
		previewPaint.setTextSize(textSize);
		previewHeight = (int) (previewPaint.descent() - previewPaint.ascent());
		mPreviewRectF = new RectF((viewWidth - previewHeight) / 2 - margin, 
								  (viewHeight - previewHeight) / 2 - margin,
								  (viewWidth - previewHeight) / 2 + previewHeight + margin, 
								  (viewHeight - previewHeight) / 2 + previewHeight + margin);
		
	}

	/**
	 * 如果当前状态为隐藏，则显示，如果当前状态为正在隐藏，则重置
	 */
	public void show()
	{
		if (mState == STATE_HIDDEN)
			setState(STATE_SHOWING);

		else if (mState == STATE_HIDING)
			setState(STATE_HIDING);
	}

	public void hide()
	{
		if (mState == STATE_SHOWN)
			setState(STATE_HIDING);
	}

	private void fade(long delay)
	{
		mHandler.removeMessages(0);
		mHandler.sendEmptyMessageAtTime(0, SystemClock.uptimeMillis() + delay);
	}

	private void setState(int state)
	{
		if (state < STATE_HIDDEN || state > STATE_HIDING)
			return;

		mState = state;
		switch (mState)
		{
			case STATE_HIDDEN:
				// Cancel any fade effect
				mHandler.removeMessages(0);
				break;
			case STATE_SHOWING:
				// Start to fade in
				mAlphaRate = 0;
				fade(0);
				break;
			case STATE_SHOWN:
				// Cancel any fade effect
				mHandler.removeMessages(0);
				break;
			case STATE_HIDING:
				// Start to fade out after three seconds
				mAlphaRate = 1;
				fade(3000);
				break;
		}
	}

	public boolean onTouchEvent(MotionEvent ev)
	{
		switch (ev.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				// If down event occurs inside index bar region, start indexing
				if (mState != STATE_HIDDEN
						&& isInRightRect(ev.getX(), ev.getY()))
				{
					setState(STATE_SHOWN);

					// It demonstrates that the motion event started from index
					// bar
					mIsIndexing = true;
					// Determine which section the point is in, and move the
					// list to that section
					mCurrentSection = getSectionByPoint(ev.getY());
					mListView.setSelection(mAdapter
							.getPositionForSection(mCurrentSection));
					return true;
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (mIsIndexing)
				{
					// If this event moves inside index bar
					if (isInRightRect(ev.getX(), ev.getY()))
					{
						// Determine which section the point is in, and move the
						// list to that section
						mCurrentSection = getSectionByPoint(ev.getY());
						mListView.setSelection(mAdapter
								.getPositionForSection(mCurrentSection));
					}
					return true;
				}
				break;
			case MotionEvent.ACTION_UP:
				if (mIsIndexing)
				{
					mIsIndexing = false;
					mCurrentSection = -1;
				}
				if (mState == STATE_SHOWN)
					setState(STATE_HIDING);
				break;
		}
		return false;
	}

	private int getSectionByPoint(float y)
	{
		if (mSections == null || mSections.length == 0)
			return 0;

		float i = (y - margin) / (itemHeight + padding) - 1;
		int index = ((int) ((i + 0.5f) * 10)) / 10;

		return index;
	}

	public boolean isInRightRect(float x, float y)
	{
		// TODO Auto-generated method stub
		if (x > mIndexbarRect.left && x < mIndexbarRect.right)
		{
			return true;
		}
		return false;
	}

	public void onDraw(Canvas canvas)
	{
		if (mState == STATE_HIDDEN)
			return;

		indexPaint.setColor(Color.LTGRAY);
		indexPaint.setAntiAlias(true);
		indexPaint.setAlpha((int) (mAlphaRate * 255));
		canvas.drawRoundRect(mIndexbarRect, 10, 10, indexPaint);
		drawIndex(canvas);

		if (mCurrentSection >= 0)
		{	
			float previewWidth = previewPaint.measureText(mSections[mCurrentSection]);
				
			previewPaint.setColor(Color.BLACK);
			previewPaint.setAlpha(96);
			previewPaint.setAntiAlias(true);	
			canvas.drawRoundRect(mPreviewRectF, 10, 10, previewPaint);
			
			previewPaint.setColor(Color.WHITE);
			previewPaint.setAlpha(255);
			canvas.drawText(mSections[mCurrentSection], mPreviewRectF.left
					+ (mPreviewRectF.width() - previewWidth) / 2,
					mPreviewRectF.top + (mPreviewRectF.height() - previewHeight) / 2 - previewPaint.ascent(), previewPaint);
			
		}
	}

	private void drawIndex(Canvas canvas)
	{
		indexPaint.setColor(Color.WHITE);	
		for (int i = 0; i < mSections.length; i++)
		{
			float paddingLeft = (mIndexbarRect.width() - indexPaint
					.measureText(mSections[i])) / 2;
			
			canvas.drawText(mSections[i], mIndexbarRect.left + paddingLeft, 
					mIndexbarRect.top + itemHeight * i 
							+ padding * (i + 1) - indexPaint.ascent(), indexPaint);
		}
	}
}
