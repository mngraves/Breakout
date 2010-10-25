package com.mngraves.breakaway;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class GamePlayView extends View implements OnTouchListener{
	private Context mContext;
	private Paint mPaint;
	private Rect mScreenBounds;
	private GameWorld mGameWorld;
	
	private static final int BACKGROUND_COLOR = Color.RED;
	private static final int BALL_COLOR = Color.GREEN;
	
	public GamePlayView(Context context){
		super(context);
		mContext = context;
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setOnTouchListener(this);
        mPaint = new Paint();
        setScreenBounds();
        mGameWorld = GameWorld.getInstance();
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		float x = event.getRawX();
		float y = event.getRawY();
		
		mGameWorld.setBall((int)x, (int)y);
		
		return false;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		GameWorld world = GameWorld.getInstance();
		Point ballPoint = world.getBallPosition();
		
		canvas.drawColor(BACKGROUND_COLOR);
		
		
		mPaint.setColor(BALL_COLOR);
		canvas.drawCircle(ballPoint.x, ballPoint.y, GamePlayActivity.NORMAL_BALL_RADIUS, mPaint);
		
	}
	
	private void setScreenBounds(){
		mScreenBounds = new Rect(0, 0, getRight(), getBottom());
	}

}
