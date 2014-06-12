package com.schimpf.block.braj.pro;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePack;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackLoader;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackTextureRegionLibrary;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.exception.TexturePackParseException;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.PinchZoomDetector;
import org.andengine.input.touch.detector.PinchZoomDetector.IPinchZoomDetectorListener;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.modifier.ease.EaseSineOut;

import com.badlogic.gdx.math.Vector2;
import com.schimpf.block.braj.pro.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends SimpleBaseGameActivity implements 
		IOnSceneTouchListener, OnClickListener, IPinchZoomDetectorListener, IScrollDetectorListener
{
	private static final int NEXT_LEVEL_ID = 0;
	private static final int MAIN_MENU_ID = 1;
	private static final int REPLAY_ID = 2;
	
	Model model;
	
	TextureRegion drawable;

	@Override
	public EngineOptions onCreateEngineOptions() 
	{
		model = new Model(this);
		model.camera = new ZoomCamera(0, 0, Model.CAMERA_WIDTH, Model.CAMERA_HEIGHT);
		model.camera.setZoomFactor(1.2f);
		
		
		final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, 
			new FillResolutionPolicy(), model.camera);
		engineOptions.getAudioOptions().setNeedsSound(true);
		engineOptions.getAudioOptions().setNeedsMusic(true); 
		return engineOptions; 
	}

	@Override
	protected void onCreateResources() 
	{
		BuildableBitmapTextureAtlas playerAtlas = new BuildableBitmapTextureAtlas(getTextureManager(), 256, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		model.day_playerTTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(playerAtlas, this, "player.png", 4, 1);
		model.night_playerTTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(playerAtlas, this, "player.png", 4, 1);
		model.snow_playerTTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(playerAtlas, this, "player.png", 4, 1);
		model.desert_playerTTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(playerAtlas, this, "player.png", 4, 1);
		try{ playerAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 1)); }
		catch(Exception e){ e.printStackTrace(); }
		playerAtlas.load();
		
		BuildableBitmapTextureAtlas buttonAtlas = new BuildableBitmapTextureAtlas(getTextureManager(), 512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);	
		model.day_moveLeftButtonTTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(buttonAtlas, this, "day_moveleft_button.png", 2, 1);
		model.day_moveRightButtonTTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(buttonAtlas, this, "day_moveright_button.png", 2, 1);
		model.day_handleBlockButtonTTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(buttonAtlas, this, "day_handleblock_button.png", 2, 1);
		model.day_restartButtonTTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(buttonAtlas, this, "day_restart_button.png", 2, 1);		
		model.desert_moveLeftButtonTTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(buttonAtlas, this, "desert_moveleft_button.png", 2, 1);
		model.desert_moveRightButtonTTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(buttonAtlas, this, "desert_moveright_button.png", 2, 1);
		model.desert_handleBlockButtonTTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(buttonAtlas, this, "desert_handleblock_button.png", 2, 1);
		model.desert_restartButtonTTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(buttonAtlas, this, "desert_restart_button.png", 2, 1);		
		model.snow_moveLeftButtonTTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(buttonAtlas, this, "snow_moveleft_button.png", 2, 1);
		model.snow_moveRightButtonTTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(buttonAtlas, this, "snow_moveright_button.png", 2, 1);
		model.snow_handleBlockButtonTTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(buttonAtlas, this, "snow_handleblock_button.png", 2, 1);
		model.snow_restartButtonTTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(buttonAtlas, this, "snow_restart_button.png", 2, 1);	
		model.night_moveLeftButtonTTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(buttonAtlas, this, "night_moveleft_button.png", 2, 1);
		model.night_moveRightButtonTTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(buttonAtlas, this, "night_moveright_button.png", 2, 1);
		model.night_handleBlockButtonTTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(buttonAtlas, this, "night_handleblock_button.png", 2, 1);
		model.night_restartButtonTTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(buttonAtlas, this, "night_restart_button.png", 2, 1);
		
		try{ buttonAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 1)); }
		catch(Exception e){ e.printStackTrace(); }
		buttonAtlas.load();
	
		try 
		{
			final TexturePack spritesheetTexturePack = 
					new TexturePackLoader(getTextureManager(), "spritesheets/").loadFromAsset(getAssets(), "everything.xml");
			spritesheetTexturePack.loadTexture();
			TexturePackTextureRegionLibrary library = spritesheetTexturePack.getTexturePackTextureRegionLibrary();
			
			model.carryBlockTR = library.get(Model.CARRYBLOCK_ID);
			model.cloudTR = library.get(Model.CLOUD_ID);
			model.day_wallBlock1TR = library.get(Model.DAY_WALLBLOCK1_ID);
			model.day_wallBlock2TR = library.get(Model.DAY_WALLBLOCK2_ID);
			model.day_wallBlock3TR = library.get(Model.DAY_WALLBLOCK3_ID);
			model.day_wallBlock4TR = library.get(Model.DAY_WALLBLOCK4_ID);
			model.day_groundBlock1TR = library.get(Model.DAY_GROUNDBLOCK1_ID);
			model.day_groundBlock2TR = library.get(Model.DAY_GROUNDBLOCK2_ID);
			model.night_wallBlock1TR = library.get(Model.NIGHT_WALLBLOCK1_ID);
			model.night_wallBlock2TR = library.get(Model.NIGHT_WALLBLOCK2_ID);
			model.night_wallBlock3TR = library.get(Model.NIGHT_WALLBLOCK3_ID);
			model.night_wallBlock4TR = library.get(Model.NIGHT_WALLBLOCK4_ID);
			model.night_groundBlock1TR = library.get(Model.NIGHT_GROUNDBLOCK1_ID);
			model.night_groundBlock2TR = library.get(Model.NIGHT_GROUNDBLOCK2_ID);			
			model.snow_wallBlock1TR = library.get(Model.SNOW_WALLBLOCK1_ID);
			model.snow_wallBlock2TR = library.get(Model.SNOW_WALLBLOCK2_ID);
			model.snow_wallBlock3TR = library.get(Model.SNOW_WALLBLOCK3_ID);
			model.snow_wallBlock4TR = library.get(Model.SNOW_WALLBLOCK4_ID);
			model.snow_groundBlock1TR = library.get(Model.SNOW_GROUNDBLOCK1_ID);
			model.snow_groundBlock2TR = library.get(Model.SNOW_GROUNDBLOCK2_ID);			
			model.desert_wallBlock1TR = library.get(Model.DESERT_WALLBLOCK1_ID);
			model.desert_wallBlock2TR = library.get(Model.DESERT_WALLBLOCK2_ID);
			model.desert_wallBlock3TR = library.get(Model.DESERT_WALLBLOCK3_ID);
			model.desert_wallBlock4TR = library.get(Model.DESERT_WALLBLOCK4_ID);
			model.desert_groundBlock1TR = library.get(Model.DESERT_GROUNDBLOCK1_ID);
			model.desert_groundBlock2TR = library.get(Model.DESERT_GROUNDBLOCK2_ID);			
			model.exitTR = library.get(Model.EXIT_ID);			
		} 
		catch (final TexturePackParseException e) 
		{
			e.printStackTrace();
		}
		
		try
		{
			model.blockSound = SoundFactory.createSoundFromAsset(getSoundManager(), this, "block_synth.wav");
			model.blockSound.setVolume((MusicManager.soundEffectVolumeProgress/100.0f) * .05f);
		} 
		catch(IOException e) 
		{
			e.printStackTrace();
		}
	}

	@Override
	protected Scene onCreateScene() 
	{		
		if(cameFromMainMenu())
		{
			// Build level from the level number passed from the main menu.
			int levelNumber = getIntent().getExtras().getInt("levelNumber");
			String singleNumberCorrector = levelNumber < 10 ? "0" : "";
			String fileName = "levels/level" + singleNumberCorrector + levelNumber + ".xml";
			model.level = new Level(fileName, model);
		}
		else if(cameFromLevelCreator())
		{
			// Build level from the file passed from the level creator.
			File file = ((SBundle)getIntent().getExtras().getSerializable("xmlFile")).file;
			model.level = new Level(file, model);		
		}

		model.pinchZoomDetector = new PinchZoomDetector(this);
		model.scrollDetector = new SurfaceScrollDetector(this);
		
		model.level.initPlayer();
		
		setupHUD();
		ArrayList<IAreaShape> children = model.level.getAllEntities();
		setupScene(children);
				
		return model.scene;
	}
	
	@Override
	public boolean onSceneTouchEvent(Scene scene, TouchEvent event)
	{
		float x = event.getX();
		float y = event.getY();
		
		model.pinchZoomDetector.onTouchEvent(event);
		if(model.pinchZoomDetector.isZooming()) 
			model.scrollDetector.setEnabled(false);
		else 
		{
			if(event.isActionDown()) 	
		        model.scrollDetector.setEnabled(true);

			model.scrollDetector.onTouchEvent(event);
		}

		
		/* Handles the case when touch slides off buttons. */
		/* Otherwise, action_up is not fired. */
		boolean touchedMoveLeftButton = model.moveLeftButton.contains(x, y);
		boolean touchedMoveRightButton = model.moveRightButton.contains(x, y);
		boolean touchedHandleBlockButton = model.handleBlockButton.contains(x, y);
		boolean touchedHUD = y >= model.camera.getCenterY() + 50;
		
		if(!touchedMoveLeftButton && touchedHUD) 
			model.moveLeftButton.setCurrentTileIndex(0);
		
		if(!touchedMoveRightButton && touchedHUD) 
			model.moveRightButton.setCurrentTileIndex(0);
		
		if(!touchedHandleBlockButton && touchedHUD)
			model.handleBlockButton.setCurrentTileIndex(0);
		
		return true;
	}
	
	@Override
	protected void onPause() 
	{
		super.onPause();
		if(!MusicManager.continueMusic)
			MusicManager.pause();
		else
			MusicManager.continueMusic = false;
	}
	
	@Override
	protected void onResume() 
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
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
	    if (keyCode == KeyEvent.KEYCODE_BACK) 
	    	returnToPreviousActivity(false);
	    
	    return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onClick(View v) 
	{
		// This event only fires after completing a level.
		// This event is fired from the level-complete dialog box.
		
		int id = v.getId();
		
		int currLevelNum = getIntent().getExtras().getInt("levelNumber");
		updateLastCompletedLevelNum(currLevelNum);
		
		switch (id)
		{
			case NEXT_LEVEL_ID:			
				// Restart activity with the next level num.
				Intent intent = new Intent(MainActivity.this, MainActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("levelNumber", currLevelNum + 1);
				intent.putExtras(bundle);
				restartActivity(intent);
				break;
			case MAIN_MENU_ID:
				returnToMainMenu(true);
				break;
			case REPLAY_ID:
				restartActivity(getIntent());
				break;
			default:
				break;
		}
	}
	
	@Override
	protected Dialog onCreateDialog(final int id) 
	{
		// This event only fires after completing a level.
		
		final LinearLayout view = createDialogView();
		return new AlertDialog.Builder(this)
		.setCancelable(false)
		.setView(view)
		.create();
	}

	private void setupScene(ArrayList<IAreaShape> children) {
		model.scene = new Scene();	
		model.scene.setOnSceneTouchListener(this);
		model.scene.setOnAreaTouchTraversalFrontToBack();
		model.scene.setTouchAreaBindingOnActionDownEnabled(true);
		
		if(children == null)
			return;
		
		model.scene.setBackground(model.level.environment.background);

		drawClouds();		
		drawGround();
		
		model.scene.attachChild(model.player);
		
		model.level.createExit();
		model.scene.attachChild(model.level.exitSprite);

		for(IAreaShape entity : children) 
			model.scene.attachChild(entity);
	}

	private void setupHUD()
	{
		createHandleBlockButton();
		createMoveLeftButton();
		createMoveRightButton();
		createRestartButton();
		
		model.hud = new HUD();
		model.hud.attachChild(model.restartButton);
		model.hud.attachChild(model.handleBlockButton);
		model.hud.attachChild(model.moveLeftButton); 
		model.hud.attachChild(model.moveRightButton);
		model.hud.registerTouchArea(model.restartButton);
		model.hud.registerTouchArea(model.handleBlockButton);
		model.hud.registerTouchArea(model.moveLeftButton);
		model.hud.registerTouchArea(model.moveRightButton);
		model.camera.setHUD(model.hud);
	}

	private void drawClouds()
	{
		Random rand = new Random();
		int numClouds = rand.nextInt(6);
		
		for(int i = 0; i < numClouds; i++)
		{
			float x = -(model.level.width / 2) + rand.nextInt((int)(model.level.width * 2));
			float height = 40 + rand.nextInt(30);
			float y = (model.level.height - height - 5) - rand.nextInt((int)model.level.height);
			float restartY = (model.level.height - height - 5) - rand.nextInt((int)model.level.height);
			float width = height * 2.5f;
			Sprite cloud = new Sprite(x, y, width, height, model.cloudTR, getVertexBufferObjectManager());
			
			int direction = rand.nextInt(10) <= 5 ? 1 : -1;
			
			Path path = new Path(6)
			.to(x, y)
			.to(model.level.width + (direction * Model.CAMERA_WIDTH), y)
			.to(model.level.width + (direction * Model.CAMERA_WIDTH), restartY)
			.to(model.level.width - (direction * Model.CAMERA_WIDTH), restartY)
			.to(model.level.width - (direction * Model.CAMERA_WIDTH), y)
			.to(x, y);
			
			
			float duration = 500 + (rand.nextFloat() * 1500);
			cloud.registerEntityModifier(new LoopEntityModifier(new PathModifier(duration, path, null, null, EaseSineOut.getInstance())));
			cloud.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			
			model.scene.attachChild(cloud);
		}
	}
	
	private void drawGround()
	{
		LevelEnvironment environment = model.level.environment;
		
		int row = model.level.rows;
		for(int i = 0; i < model.level.columns; i++)
		{
			ITextureRegion tr;
			if(i % 2 == 0)
				tr = environment.groundBlock1TR;
			else 
				tr = environment.groundBlock2TR;
			
			Vector2 pos = Level.getPositionFromGridIndex(row, i);
			Sprite sprite = new Sprite(pos.x, pos.y, tr, getVertexBufferObjectManager());
			model.scene.attachChild(sprite);
		}
	}

	private void createHandleBlockButton()
	{
		model.handleBlockButton = new ButtonSprite(Model.CAMERA_WIDTH - 175, Model.CAMERA_HEIGHT - 95, 
				model.level.environment.handleBlockButtonTTR, getVertexBufferObjectManager()) 
		{
			@Override
		    public boolean onAreaTouched(TouchEvent event, float x, float y) {
				if (event.isActionDown()) 
				{	
					if(model.player.isCarryingBlock())
					{
						if(model.player.dropBlock())
						{
							model.blockSound.setRate(.9f);
							model.blockSound.play(); 
						}
					}
					else 
					{
						if(model.player.pickUpBlock())
						{
							model.blockSound.setRate(1);
							model.blockSound.play(); 
						}
					}
										
					this.setCurrentTileIndex(1);
				}
				else if (event.isActionUp() || event.isActionCancel() || event.isActionOutside()) 
					this.setCurrentTileIndex(0);

				return super.onAreaTouched(event, x, y);
		    };
		};
	}
	
	private void createMoveRightButton() 
	{
		model.moveRightButton = new ButtonSprite(205, Model.CAMERA_HEIGHT - 95, model.level.environment.moveRightButtonTTR, 
				getVertexBufferObjectManager()) 
		{
			@Override
		    public boolean onAreaTouched(TouchEvent event, float x, float y) 
			{
				if (event.isActionDown()) 
				{
					model.player.moveRight();
					this.setCurrentTileIndex(1);
					if(isPlayerAtExit())
						handleLevelComplete();
				}
				else if (event.isActionUp() || event.isActionCancel() || event.isActionOutside()) 
					this.setCurrentTileIndex(0);

				return super.onAreaTouched(event, x, y);
		    };
		};
	}

	private void createMoveLeftButton() 
	{
		model.moveLeftButton = new ButtonSprite(75, Model.CAMERA_HEIGHT - 95, model.level.environment.moveLeftButtonTTR, 
				getVertexBufferObjectManager()) 
		{
			@Override
		    public boolean onAreaTouched(TouchEvent event, float x, float y) 
			{
				if (event.isActionDown()) 
				{
					model.player.moveLeft();
					this.setCurrentTileIndex(1);
					if(isPlayerAtExit())
						handleLevelComplete();
				}
				else if (event.isActionUp() || event.isActionCancel() || event.isActionOutside()) 
					this.setCurrentTileIndex(0);

				return super.onAreaTouched(event, x, y);
		    };
		};
	}

	private void createRestartButton() 
	{
		model.restartButton = new ButtonSprite(75, 95 - 75, model.level.environment.restartButtonTTR, getVertexBufferObjectManager()) 
		{
			@Override
		    public boolean onAreaTouched(TouchEvent event, float x, float y) 
			{
				if (event.isActionDown()) 
				{
					MusicManager.continueMusic = true;
					
					setCurrentTileIndex(1);
					restartActivity(getIntent());
				}

				return super.onAreaTouched(event, x, y);
		    };
		};
	}
	
	protected void showLevelCompleteDialog()
	{
	    runOnUiThread(new Runnable() 
		{
			public void run() 
		    {
		        showDialog(0);
		    }
		});
	}
	
	private LinearLayout createDialogView()
	{
		final LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.setBackgroundColor(Color.rgb(0, 6, 45));
	
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		
		final RelativeLayout rl = new RelativeLayout(this);
		RelativeLayout.LayoutParams rlp = 
				new RelativeLayout.LayoutParams (RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		rl.setLayoutParams(rlp);
		rl.setGravity(Gravity.CENTER_HORIZONTAL);
		TextView titleView = new TextView(this);
		titleView.setTextColor(Color.rgb(253, 255, 188));
		titleView.setTextSize(30);
		
		int currLevelNum = getIntent().getExtras().getInt("levelNumber");
		if(currLevelNum < Model.NUM_LEVELS)
		{
			titleView.setText("Good job, braj!");
			rl.addView(titleView);
			ll.addView(rl);
			
			Button nextLevelButton = new Button(this);
			nextLevelButton.setTextColor(Color.WHITE);
			nextLevelButton.setText("next level");
			nextLevelButton.setLayoutParams(lp);
			nextLevelButton.setBackgroundResource(R.drawable.menu_level_button);
			nextLevelButton.setId(NEXT_LEVEL_ID);
			nextLevelButton.setOnClickListener(this);
			ll.addView(nextLevelButton);
		}
		else
		{
			titleView.setText("You completed every level!");
			rl.addView(titleView);
			ll.addView(rl);
		}

		
		Button mainMenuButton = new Button(this);
		mainMenuButton.setTextColor(Color.WHITE);
		mainMenuButton.setText("main menu");
		mainMenuButton.setLayoutParams(lp);
		mainMenuButton.setBackgroundResource(R.drawable.menu_level_button);
		mainMenuButton.setId(MAIN_MENU_ID);
		mainMenuButton.setOnClickListener(this);
		ll.addView(mainMenuButton);
		
		Button replayButton = new Button(this);
		replayButton.setTextColor(Color.WHITE);
		replayButton.setText("replay");
		replayButton.setLayoutParams(lp);
		replayButton.setBackgroundResource(R.drawable.menu_level_button);
		replayButton.setId(REPLAY_ID);
		replayButton.setOnClickListener(this);
		ll.addView(replayButton);	
		
		return ll;
	}

	private boolean isPlayerAtExit()
	{
		return model.player.currRow == model.level.exitRow && 
			model.player.currColumn == model.level.exitColumn;
	}
	
	private void handleLevelComplete()
	{
		if(cameFromLevelCreator())
			returnToLevelCreator();
		else
		{
			model.pinchZoomDetector.setEnabled(false);
			model.scrollDetector.setEnabled(false);
		    if(model.player != null)
		    	model.scene.detachChild(model.player);
			zoomOnDoor();
			showLevelCompleteDialog();
		}
	}
	
	private void restartActivity(Intent intent)
	{	
	    overridePendingTransition(0, 0);
	    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
	    finish();

	    overridePendingTransition(0, 0);
	    startActivity(intent);
	}
	
	private void returnToPreviousActivity(boolean isLevelComplete)
	{
		if(cameFromMainMenu())
			returnToMainMenu(isLevelComplete);
		else if(cameFromLevelCreator())
			returnToLevelCreator();
	}
	
	private void returnToMainMenu(boolean isLevelComplete)
	{
		MusicManager.continueMusic = true;
		
		if(cameFromMainMenu())
		{
			// This is an actual level.
			Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
			Bundle extras = new Bundle();
			extras.putBoolean("isLevelComplete", isLevelComplete);
			int levelNumber = getIntent().getExtras().getInt("levelNumber");
			extras.putInt("levelNumber", levelNumber);
			intent.putExtras(extras);
			
			startActivity(intent);
			finish();
		}
	}
	
	private void returnToLevelCreator()
	{
		MusicManager.continueMusic = true;
		
		if(cameFromLevelCreator())
		{
			// This is a level we are testing.
			Intent intent = new Intent(MainActivity.this, LevelCreatorActivity.class);
			Bundle extras = new Bundle();
			SBundle file = ((SBundle)getIntent().getExtras().getSerializable("xmlFile"));
			extras.putSerializable("xmlFile", file);
			intent.putExtras(extras);
			
			startActivity(intent);
			finish();	
		}
	}
	
	private void updateLastCompletedLevelNum(int currLevelNum)
	{
		int lastCompletedLevelNum = getLastCompletedLevelNum();
		if(currLevelNum > lastCompletedLevelNum)
		{
			SharedPreferences prefs = getSharedPreferences("com.schimpf.block.braj.pro", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putInt("lastLevelCompleted", currLevelNum);
			editor.commit();
		}
	}
	
    private int getLastCompletedLevelNum()
    {
    	int num = 0;
    		
		SharedPreferences prefs = getSharedPreferences("com.schimpf.block.braj.pro", Context.MODE_PRIVATE);
		if(prefs != null)
			num = prefs.getInt("lastLevelCompleted", 0);
	
    	return num;
    }
    
    private void zoomOnDoor()
    {
    	Thread thread = new Thread(new Runnable()
    	{
			@Override
			public void run() 
			{
		    	double curr = Calendar.getInstance().getTimeInMillis();
		    	double end = curr + 3000;
		    	
		    	while(curr < end)
		    	{
		    		float currZoomFactor = model.camera.getZoomFactor();
		    		model.camera.setZoomFactor(currZoomFactor + .000015f);
		    		curr = Calendar.getInstance().getTimeInMillis();
		    	}
			}
    	});
    	
    	thread.start();
    }
    
    private boolean cameFromMainMenu()
    {
    	return getIntent().getExtras() != null && getIntent().getExtras().containsKey("levelNumber");
    }
    
    private boolean cameFromLevelCreator()
    {
    	return getIntent().getExtras() != null && getIntent().getExtras().containsKey("xmlFile");
    }

	@Override
	public void onScrollStarted(ScrollDetector detector, int pId, float deltaX, float deltaY) 
	{		
		model.camera.setChaseEntity(null);
		
		float zoomFactor = model.camera.getZoomFactor();
		model.camera.offsetCenter(-deltaX / zoomFactor, -deltaY / zoomFactor);
	}

	@Override
	public void onScroll(ScrollDetector detector, int pId, float deltaX, float deltaY) 
	{
		float zoomFactor = model.camera.getZoomFactor();
		model.camera.offsetCenter(-deltaX / zoomFactor, -deltaY / zoomFactor);
	}

	@Override
	public void onScrollFinished(ScrollDetector detector, int pId, float deltaX, float deltaY) 
	{
		float zoomFactor = model.camera.getZoomFactor();
		model.camera.offsetCenter(-deltaX / zoomFactor, -deltaY / zoomFactor);
		model.camera.setChaseEntity(model.player);
		model.pinchZoomDetector.setEnabled(true);
		
		model.camera.setChaseEntity(model.player);
	}

	@Override
	public void onPinchZoomStarted(PinchZoomDetector detector, TouchEvent event) 
	{
		model.startPinchZoomFactor = model.camera.getZoomFactor();
	}

	@Override
	public void onPinchZoom(PinchZoomDetector detector, TouchEvent event, float zoomFactor) 
	{
		float result = model.startPinchZoomFactor * zoomFactor;
		
		if(result >= .65 && result <= 1.4)
			model.camera.setZoomFactor(model.startPinchZoomFactor * zoomFactor);
	}

	@Override
	public void onPinchZoomFinished(PinchZoomDetector pPinchZoomDetector, TouchEvent pTouchEvent, float zoomFactor) 
	{
		float result = model.startPinchZoomFactor * zoomFactor;
		
		if(result >= .8 && result <= 1.4)
			model.camera.setZoomFactor(model.startPinchZoomFactor * zoomFactor);
		
		model.camera.setChaseEntity(model.player);
	}
}
