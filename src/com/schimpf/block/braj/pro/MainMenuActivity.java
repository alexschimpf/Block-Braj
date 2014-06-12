package com.schimpf.block.braj.pro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

public class MainMenuActivity extends Activity
{
	MainMenuLayoutModifier layoutModifier;

	@Override
	public void onCreate(Bundle savedInstanceState) 
    {
	    super.onCreate(savedInstanceState);
	        
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	        	        
	    layoutModifier = new MainMenuLayoutModifier(this);
        setContentView(layoutModifier.mainView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
	    return false;
	}
 
	@Override
	public void onPause() 
	{
        super.onPause();
        if(!MusicManager.continueMusic)
			MusicManager.pause();
		else
			MusicManager.continueMusic = false;
	}
	    
	@Override
	public void onResume() 
	{
	    super.onResume();
        MusicManager.start(this);
	    MusicManager.continueMusic = false;
	}
	
	@Override
	public void onStop()
	{
		super.onStop();
	}  
	    
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)  
	{
	    if(keyCode == KeyEvent.KEYCODE_BACK) 
	    {
		    if(layoutModifier.currLayout == MainMenuLayoutModifier.MAIN)
		    	finish();
		    else
		    	layoutModifier.setLayout(MainMenuLayoutModifier.MAIN, -1);
	    }
	    
	    return super.onKeyDown(keyCode, event);
	}
	
	public void goToLevelCreator()
    {
	    Intent intent = new Intent(this, LevelCreatorActivity.class);
		startActivity(intent);
		finish();
	}
	    
	public void goToLevel(int id)
	{
		Intent intent = new Intent(this, MainActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("levelNumber", id);
		intent.putExtras(bundle);
		startActivity(intent);
		finish(); 
	}

	public void goToTutorial()
	{
	    Intent intent = new Intent(MainMenuActivity.this, TutorialActivity.class);
	    startActivity(intent);
	    finish();
	}
}
