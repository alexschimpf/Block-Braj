package com.schimpf.block.braj.pro;

import com.schimpf.block.braj.pro.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainMenuLayoutModifier implements OnClickListener, OnSeekBarChangeListener
{
	// This class is used to clear and switch views of the main menu.
	// This class also implements click and progressbar listeners.
	
	public static final int ABOUT = 0;
    public static final int SETTINGS = 1;
	public static final int LEVELS = 2;
	public static final int MAIN = 3;	
	public static final int NUM_LEVELS_PER_PAGE = 10;
	
	public static final int CREATE_LEVEL_ID = -1;
	public static final int SETTINGS_ID = -2;
	public static final int PLAY_NOW_ID = -3;
	public static final int BACK_ID = -4;
	public static final int ABOUT_ID = -5;
	public static final int TUTORIAL_ID = -6;
	public static final int NEXT_ID = -7;
	
	public final static int MUSIC_VOLUME_ID = 0;
	public final static int SOUNDEFFECT_VOLUME_ID = 1;
	
	private SurfaceView surfaceView;
	private MainMenuActivity activity;
	
	public FrameLayout mainView;
	public int currStartingLevel;
	public int currLayout;
	
	public MainMenuLayoutModifier(MainMenuActivity activity)
	{
		this.activity = activity;
		
		mainView = createMainContentView();
	}
	
	public void setLayout(int type, int optStartingLevel)
	{
		switch(type)
		{
			case MAIN:
				currLayout = MAIN;
				clearAndAddMainStuff();
				break;
			case ABOUT:
				currLayout = ABOUT;
				clearAndAddAboutStuff();
				break;
			case SETTINGS:
				currLayout = SETTINGS;
				clearAndAddSettingsStuff();
				break;
			case LEVELS:
				currLayout = LEVELS;
				clearAndAddLevelStuff(optStartingLevel);
				break;
		}
	}
	
	private FrameLayout createMainContentView()
    {
    	Bundle bundle = activity.getIntent().getExtras();		
		SharedPreferences prefs = activity.getSharedPreferences("com.schimpf.block.braj.pro", Context.MODE_PRIVATE);
		if(bundle != null && bundle.containsKey("isLevelComplete"))
		{
			// We have returned from the main activity.
			boolean isLevelComplete = bundle.getBoolean("isLevelComplete");
			if(isLevelComplete)
			{
				int levelNum = bundle.getInt("levelNumber");
				SharedPreferences.Editor editor = prefs.edit();
				editor.putBoolean("" + levelNum, true);
				editor.commit();
			}
		}
		
		FrameLayout retView = new FrameLayout(activity);
		
        surfaceView = new MainMenuSurfaceView(activity, activity);
		retView.addView(surfaceView);
		
		final LinearLayout ll = new LinearLayout(activity);
		ll.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams llp = 
				new LinearLayout.LayoutParams (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		ll.setLayoutParams(llp);
				
		final RelativeLayout rl = new RelativeLayout(activity);
		RelativeLayout.LayoutParams rlp = 
				new RelativeLayout.LayoutParams (RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		rl.setLayoutParams(rlp);
		rl.setGravity(Gravity.CENTER_HORIZONTAL);
		TextView titleView = new TextView(activity);
		titleView.setTextColor(Color.rgb(253, 255, 188));
		titleView.setTextSize(50);
		titleView.setText("Block Braj");
		titleView.setTextAppearance(activity, 0);
		rl.addView(titleView);
		ll.addView(rl);
		
		RelativeLayout.LayoutParams rlp2 = 
				new RelativeLayout.LayoutParams (RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		Button playButton = new Button(activity);
		playButton.setTextColor(Color.WHITE);
		playButton.setText("play now");
		playButton.setLayoutParams(rlp2);
		playButton.setId(PLAY_NOW_ID);
		playButton.setOnClickListener(this);
		playButton.setBackgroundResource(R.drawable.menu_level_button);
		ll.addView(playButton);
		
		/*
		Button levelCreatorButton = new Button(activity);
		levelCreatorButton.setTextColor(Color.WHITE);
		levelCreatorButton.setText("create new level");
		levelCreatorButton.setLayoutParams(rlp2);
		levelCreatorButton.setId(CREATE_LEVEL_ID);
		levelCreatorButton.setOnClickListener(this);
		levelCreatorButton.setBackgroundResource(R.drawable.menu_level_button);
		ll.addView(levelCreatorButton);
		*/
		
		Button settingsButton = new Button(activity);
		settingsButton.setTextColor(Color.WHITE);
		settingsButton.setText("settings");
		settingsButton.setLayoutParams(rlp2);
		settingsButton.setId(SETTINGS_ID);
		settingsButton.setOnClickListener(this);
		settingsButton.setBackgroundResource(R.drawable.menu_level_button);
		ll.addView(settingsButton);
		
		Button aboutButton = new Button(activity);
		aboutButton.setTextColor(Color.WHITE);
		aboutButton.setText("about");
		aboutButton.setLayoutParams(rlp2);
		aboutButton.setId(ABOUT_ID);
		aboutButton.setOnClickListener(this);
		aboutButton.setBackgroundResource(R.drawable.menu_level_button);
		ll.addView(aboutButton);

		final ScrollView sv = new ScrollView(activity); 
		sv.setScrollbarFadingEnabled(false);
		sv.addView(ll);
			
		retView.addView(sv);
	
		return retView;
    }
	
	private void clearAndAddMainStuff()
	{
    	FrameLayout newContentView = createMainContentView();
    	ScrollView newScrollView = (ScrollView)(newContentView.getChildAt(1));
    	LinearLayout newLinearLayout = (LinearLayout)(newScrollView).getChildAt(0);
    	newContentView.removeAllViews();
    	newScrollView.removeAllViews();
    	
    	ScrollView mainScrollView = (ScrollView)(mainView.getChildAt(1));
    	mainScrollView.removeAllViews();
    	mainScrollView.addView(newLinearLayout);
	}
	
	private void clearAndAddAboutStuff()
	{
		ScrollView mainScrollView = (ScrollView)(mainView.getChildAt(1));
		mainScrollView.setScrollbarFadingEnabled(false);
    	LinearLayout ll = (LinearLayout)(mainScrollView.getChildAt(0));
		ll.removeViews(1, ll.getChildCount() - 1);
		RelativeLayout.LayoutParams rlp = 
				new RelativeLayout.LayoutParams (RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		
		Button backButton = new Button(activity);
		backButton.setBackgroundResource(R.drawable.menu_level_button);
		backButton.setTextColor(Color.rgb(120, 120, 120));
		backButton.setText("back");
		backButton.setLayoutParams(rlp);
		backButton.setId(BACK_ID);
		backButton.setOnClickListener(this);
		ll.addView(backButton);	

		RelativeLayout rl = new RelativeLayout(activity);
		rl.setLayoutParams(rlp);
		rl.setGravity(Gravity.CENTER_HORIZONTAL);
		TextView view1 = new TextView(activity);
		view1.setTextColor(Color.WHITE);
		view1.setTextSize(20);
		view1.setText("programming = alex schimpf");	
		rl.addView(view1);
		ll.addView(rl);
		
		rl = new RelativeLayout(activity);
		rl.setLayoutParams(rlp);
		rl.setGravity(Gravity.CENTER_HORIZONTAL);
		TextView view2 = new TextView(activity);
		view2.setTextColor(Color.WHITE);
		view2.setTextSize(20);
		view2.setText("graphics = zach schimpf");
		rl.addView(view2);
		ll.addView(rl);
		
		rl = new RelativeLayout(activity);
		rl.setLayoutParams(rlp);
		rl.setGravity(Gravity.CENTER_HORIZONTAL);
		TextView view3 = new TextView(activity);
		view3.setTextColor(Color.WHITE);
		view3.setTextSize(20);
		view3.setText("music = julie cocq (julsy)");	
		rl.addView(view3);
		ll.addView(rl);
		
		rl = new RelativeLayout(activity);
		rl.setLayoutParams(rlp);
		rl.setGravity(Gravity.CENTER_HORIZONTAL);
		TextView view4 = new TextView(activity);
		view4.setTextColor(Color.WHITE);
		view4.setTextSize(20);
		view4.setText("2 + 2 = 37");	
		rl.addView(view4);
		ll.addView(rl);
	}
	
	private void clearAndAddSettingsStuff()
	{
    	ScrollView mainScrollView = (ScrollView)(mainView.getChildAt(1));
    	LinearLayout ll = (LinearLayout)(mainScrollView.getChildAt(0));
		ll.removeViews(1, ll.getChildCount() - 1);
		
		Button backButton = new Button(activity);
		backButton.setBackgroundResource(R.drawable.menu_level_button);
		backButton.setTextColor(Color.rgb(120, 120, 120));
		backButton.setText("back");
		backButton.setId(BACK_ID);
		backButton.setOnClickListener(this);
   	
		TextView musicVolumeView = new TextView(activity);
		musicVolumeView.setTextColor(Color.WHITE);
		musicVolumeView.setTextSize(20);
		musicVolumeView.setText("music volume");	
		musicVolumeView.setPadding(0, 75, 0, 0);
		musicVolumeView.setGravity(Gravity.CENTER_HORIZONTAL);
		ll.addView(musicVolumeView);

    	SeekBar musicVolumeSeekBar = new SeekBar(activity);
    	musicVolumeSeekBar.setMax(100);
    	musicVolumeSeekBar.setProgress(MusicManager.musicVolumeProgress);
    	musicVolumeSeekBar.setOnSeekBarChangeListener(this);
    	musicVolumeSeekBar.setId(MUSIC_VOLUME_ID);
    	ll.addView(musicVolumeSeekBar);
    	
		TextView soundEffectVolumeView = new TextView(activity);
		soundEffectVolumeView.setTextColor(Color.WHITE);
		soundEffectVolumeView.setTextSize(20);
		soundEffectVolumeView.setText("sound effect volume");	
		soundEffectVolumeView.setGravity(Gravity.CENTER_HORIZONTAL);
		ll.addView(soundEffectVolumeView);

    	SeekBar soundEffectVolumeSeekBar = new SeekBar(activity);
    	soundEffectVolumeSeekBar.setMax(100);
    	soundEffectVolumeSeekBar.setProgress(MusicManager.soundEffectVolumeProgress);
    	soundEffectVolumeSeekBar.setOnSeekBarChangeListener(this);
    	soundEffectVolumeSeekBar.setId(SOUNDEFFECT_VOLUME_ID);
    	ll.addView(soundEffectVolumeSeekBar);
    	
    	ll.addView(backButton);
	}
	
	private void clearAndAddLevelStuff(int startingLevel)
	{
    	currStartingLevel = startingLevel;
    	
    	ScrollView mainScrollView = (ScrollView)(mainView.getChildAt(1));
    	LinearLayout ll = (LinearLayout)(mainScrollView.getChildAt(0));
		ll.removeViews(1, ll.getChildCount() - 1);
		RelativeLayout.LayoutParams rlp = 
				new RelativeLayout.LayoutParams (RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

		Button backButton = new Button(activity);
		backButton.setBackgroundResource(R.drawable.menu_level_button);
		backButton.setTextColor(Color.rgb(120, 120, 120));
		backButton.setText("back");
		backButton.setLayoutParams(rlp);
		backButton.setId(BACK_ID);
		backButton.setOnClickListener(this);
		ll.addView(backButton);	
		
		if(startingLevel < NUM_LEVELS_PER_PAGE)
		{
			Button tutorialButton = new Button(activity);
			tutorialButton.setBackgroundResource(R.drawable.menu_level_button);
			tutorialButton.setTextColor(Color.WHITE);
			tutorialButton.setText("tutorial");
			tutorialButton.setLayoutParams(rlp);
			tutorialButton.setId(TUTORIAL_ID);
			tutorialButton.setOnClickListener(this);
			ll.addView(tutorialButton);	
		}
    	
    	try
		{
			int lastCompletedLevelNum = getLastCompletedLevelNum();
			
			// Create a button for every level file in the directory that is >= startingLevel.
			int i = 0;
			String[] levelFileNames = activity.getAssets().list("levels");
			for(String fileName : levelFileNames)
			{	
				String levelNumStr = fileName.substring(5, 7);
				if(levelNumStr.charAt(0) == '0')
					levelNumStr = levelNumStr.substring(1);
				
				int levelNum = Integer.parseInt(levelNumStr);
				
				if(i >= startingLevel - 1)				
				{
					String levelName = "level " + levelNum;			
					Button levelButton = new Button(activity);
					levelButton.setBackgroundResource(R.drawable.menu_level_button);
					levelButton.setTextColor(Color.RED);
					levelButton.setText(levelName);
					levelButton.setLayoutParams(rlp);
									
					// "Unlock" all levels up to last completed level, plus one more.
					if(levelNum <= lastCompletedLevelNum + 1)
					{
						levelButton.setTextColor(Color.WHITE);
						levelButton.setId(levelNum);
						levelButton.setOnClickListener(this);
					}
					
					ll.addView(levelButton);
				}
				
				i++;
				
				if(i - startingLevel + 1 == NUM_LEVELS_PER_PAGE && i <= levelFileNames.length - 1)
				{
					Button nextButton = new Button(activity);
					nextButton.setBackgroundResource(R.drawable.menu_level_button);
					nextButton.setTextColor(Color.rgb(120, 120, 120));
					nextButton.setText("next " + (levelNum + 1) + "-" + (levelNum + NUM_LEVELS_PER_PAGE));
					nextButton.setLayoutParams(rlp);
					nextButton.setId(NEXT_ID);
					nextButton.setOnClickListener(this);
					ll.addView(nextButton);	
					break;
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
    private int getLastCompletedLevelNum()
    {
    	int num = 0;
    		
		SharedPreferences prefs = activity.getSharedPreferences("com.schimpf.block.braj.pro", Context.MODE_PRIVATE);
		if(prefs != null)
			num = prefs.getInt("lastLevelCompleted", 0);
	
    	return num;
    }
    
	@Override
    public void onClick(View view)
	{
	    int id = view.getId(); 

	    switch(id)
	    {	
			case SETTINGS_ID:
				setLayout(SETTINGS, -1);
				break;
			case CREATE_LEVEL_ID:
				activity.goToLevelCreator();
				break;
			case PLAY_NOW_ID:
				setLayout(LEVELS, 1);	
				break;
			case BACK_ID:
				if(currStartingLevel > NUM_LEVELS_PER_PAGE)
					setLayout(LEVELS, currStartingLevel - NUM_LEVELS_PER_PAGE);
				else
					setLayout(MAIN, -1);
				break;
			case ABOUT_ID:
				setLayout(ABOUT, -1);
				break;
			case TUTORIAL_ID:
				activity.goToTutorial();
				break;
			case NEXT_ID:
			setLayout(LEVELS, currStartingLevel + NUM_LEVELS_PER_PAGE);
				break;
			default:
				activity.goToLevel(id);
				break;
		}
	}

	@Override
	public void onProgressChanged(SeekBar sb, int value, boolean isFromTouch) 
	{
		int id = sb.getId();
		float max = (float)sb.getMax();
			
		switch(id)
		{
			case MUSIC_VOLUME_ID:
				MusicManager.musicVolumeProgress = value;
				MusicManager.mp.setVolume(value/max, value/max);
				break;
			case SOUNDEFFECT_VOLUME_ID:
				MusicManager.soundEffectVolumeProgress = value;
				break;
			default:
				break;
		}
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) 
	{
	
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) 
	{

	}
}
