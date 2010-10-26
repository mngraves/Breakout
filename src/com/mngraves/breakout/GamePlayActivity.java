package com.mngraves.breakout;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class GamePlayActivity extends Activity{
	private GamePlayView mGamePlayView;
	private GameWorld mGameWorld;
	private Timer mGameUpdateTimer;
	
	public static final int GAME_UPDATE_INTERVAL = 50;
	public static final int NORMAL_BALL_RADIUS = 10;
	public static final int BALL_START_X = 240;
	public static final int BALL_START_Y = 400;
	public static final int SLIDER_WIDTH = 200;
	public static final int SLIDER_DEPTH = 30;
	
	public static final String KEY_WIDTH = "width";
	public static final String KEY_HEIGHT = "height";
	public static final String KEY_X = "locX";
	public static final String KEY_Y = "locY";
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
        // Create game play view and set full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                         WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mGamePlayView = new GamePlayView(this);
        setContentView(mGamePlayView);
        mGamePlayView.requestFocus();
        initializeGameWorld();
        startGameUpdateTimer();
	}
	
	private void initializeGameWorld(){
		Rect bounds = mGamePlayView.getScreenBounds();
		mGameWorld = GameWorld.getInstance();
		mGameWorld.create(bounds);
		/*
		 * Add the slider
		 */
		Bundle data = new Bundle();
		data.putInt(KEY_WIDTH, SLIDER_WIDTH);
		data.putInt(KEY_HEIGHT, SLIDER_DEPTH);
		data.putInt(KEY_X, 220);
		data.putInt(KEY_Y, bounds.height() - 100);
		mGameWorld.addBox(data);
		//mGameWorld.addBall();
	}
	
	private void startGameUpdateTimer(){
    	mGameUpdateTimer = new Timer();
    	mGameUpdateTimer.schedule(new GameUpdateTimerTask(), 0, GAME_UPDATE_INTERVAL);
	}
	
	private void stopGameUpdateTimer(){
		if(mGameUpdateTimer != null){
			mGameUpdateTimer.cancel();
		}
	}
	
	private class GameUpdateTimerTask extends TimerTask{
    	@Override
    	public void run(){
    		if(mGameWorld != null){
    			mGameWorld.update();
    			mGamePlayView.postInvalidate();
    		}
    	}		
	}

}
