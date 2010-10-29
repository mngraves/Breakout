package com.mngraves.breakout;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Color;
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
	public static final int SLIDER_DEFAULT_X = 140;
	public static final int SLIDER_Y_OFFSET = 100;
	public static final int SLIDER_COLOR = Color.GREEN;
	
	public static final int BLOCK_WIDTH = 100;
	public static final int BLOCK_HEIGHT = 50;
	public static final int BLOCKS_PER_ROW = 15;
	public static final int BLOCK_HITPOINTS = 1;
	
	
	public static final String KEY_WIDTH = "width";
	public static final String KEY_HEIGHT = "height";
	public static final String KEY_X = "locX";
	public static final String KEY_Y = "locY";
	
	public static final String KEY_BOX_HITPOINTS = "boxHitPoints";
	public static final String KEY_BOX_INDEX = "boxIndex";
	public static final String KEY_BOX_COLOR = "boxColor";
	public static final String KEY_BOX_POINTS = "boxPoints";
	public static final String KEY_BOX_EFFECT = "boxEffect";
	
	
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
	
	/**
	 * Load the current map
	 */
	private void loadLevelMap(){
		Bundle[] bundles = new Bundle[BLOCKS_PER_ROW];
		Rect screenBounds = mGamePlayView.getScreenBounds();
		int blockWidth = Math.round(screenBounds.width()/BLOCKS_PER_ROW);
		for(int i = 0; i < BLOCKS_PER_ROW; i++){
			Bundle data = new Bundle();
			data.putInt(KEY_WIDTH, blockWidth);
			data.putInt(KEY_HEIGHT, BLOCK_HEIGHT);
			data.putInt(KEY_X, i*blockWidth);
			data.putInt(KEY_Y, 100);
			data.putInt(KEY_BOX_COLOR, Color.CYAN);
			data.putInt(KEY_BOX_INDEX, i);
			data.putInt(KEY_BOX_HITPOINTS, BLOCK_HITPOINTS);
			bundles[i] = data;
		}
		
		mGameWorld.addMapBlocks(bundles);
	}
	
	private void initializeGameWorld(){
		Rect bounds = mGamePlayView.getScreenBounds();
		mGameWorld = GameWorld.getInstance();
		mGameWorld.create(bounds);
		loadLevelMap();
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
