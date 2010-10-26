package com.mngraves.breakout;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class GamePlayActivity extends Activity{
	private GamePlayView mGamePlayView;
	private GameWorld mGameWorld;
	private Timer mGameUpdateTimer;
	
	public static final int GAME_UPDATE_INTERVAL = 50;
	public static final int NORMAL_BALL_RADIUS = 10;
	
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
		mGameWorld = GameWorld.getInstance();
		mGameWorld.create(mGamePlayView.getScreenBounds());
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
