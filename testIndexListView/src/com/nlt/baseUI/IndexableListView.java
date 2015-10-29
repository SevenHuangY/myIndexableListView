package com.nlt.baseUI;

import com.nlt.baseObject.IndexableAdapter;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.ListAdapter;
import android.widget.ListView;

public class IndexableListView extends ListView 
{
	private boolean isFastScroll = false;
	private IndexableScroller mScroller;
	private GestureDetector mGestureDetector = null;

	private int mWidth, mHeight;
	
	private Context context;
	private final String TAG = "test";

	

	public IndexableListView(Context context)
	{
		this(context, null);
	}

	public IndexableListView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public IndexableListView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		this.context = context;
		
	}

	/**
	 * 当点击坐标在有效区域时，拦截该事件，阻止该事件分发给item
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		// TODO Auto-generated method stub
		if(mScroller.isInRightRect(ev.getX(), ev.getY()))
			return true;
		
		return super.onInterceptTouchEvent(ev);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		// TODO Auto-generated method stub
		if (mScroller != null && mScroller.onTouchEvent(ev))
			return true;
		
		if(mGestureDetector != null)
		{
			mGestureDetector.onTouchEvent(ev);
		}
		
		return super.onTouchEvent(ev);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mWidth = getMeasuredWidth();
		mHeight = getMeasuredHeight();
		// init
		if (isFastScroll)
		{
			if (mScroller == null)
			{
				mScroller = new IndexableScroller(context, mWidth, mHeight, this);
				if(mGestureDetector == null)
				{
					mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener()
					{
						@Override
						public boolean onFling(MotionEvent e1, MotionEvent e2,
								float velocityX, float velocityY)
						{
							// TODO Auto-generated method stub
							if (mScroller != null)
								mScroller.show();
							return super.onFling(e1, e2, velocityX, velocityY);
						}					
					});
				}
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if (isFastScroll)
		{
			mScroller.onDraw(canvas);
		}
	}

	@Override
	public void setFastScrollEnabled(boolean enabled)
	{
		// TODO Auto-generated method stub
		isFastScroll = enabled;
	}

	
}
