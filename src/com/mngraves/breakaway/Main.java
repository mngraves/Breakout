package com.mngraves.breakaway;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Main extends Activity {
    public static final int ACTIVITY_START_GAME = 1;
    public static final int ACTIVITY_END_GAME = 2;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button startButton = (Button)findViewById(R.id.start_button);
        startButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startGame();
			}
		});
    }
    
    private void startGame(){
    	Intent i = new Intent(this, GamePlayActivity.class);
    	startActivityForResult(i, ACTIVITY_START_GAME);
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
	}
    
    
}