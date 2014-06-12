package com.schimpf.block.braj.pro;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.PinchZoomDetector;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.input.touch.detector.PinchZoomDetector.IPinchZoomDetectorListener;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import com.schimpf.block.braj.pro.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TutorialActivity extends SimpleBaseGameActivity 
		implements IOnSceneTouchListener, IPinchZoomDetectorListener, IScrollDetectorListener, OnClickListener
{
	private static final int MAIN_MENU_ID = 0;
	private static final int PLAY_LEVEL1_ID = 1;
	private static final int RESTART_ID = 2;
	
	Model model;
	
	ButtonSprite nextButtonSprite;
	ButtonSprite backButtonSprite;
	
	AnimatedSprite tiledTutorialSprite;
	ITiledTextureRegion tutorialTTR;
	ITiledTextureRegion backButtonTTR;
	ITiledTextureRegion nextButtonTTR;
	
	int currStage;
	
	@Override
	public EngineOptions onCreateEngineOptions() 
	{
		model = new Model(this);
		model.camera = new ZoomCamera(0, 0, Model.CAMERA_WIDTH, Model.CAMERA_HEIGHT);
		model.camera.setZoomFactor(1f);
		
		final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, 
			new FillResolutionPolicy(), model.camera);
		engineOptions.getTouchOptions().setNeedsMultiTouch(true);
		engineOptions.getAudioOptions().setNeedsSound(true);
		engineOptions.getAudioOptions().setNeedsMusic(true); 
		return engineOptions; 
	}

	@Override
	protected void onCreateResources()
	{
		BuildableBitmapTextureAtlas atlas = new BuildableBitmapTextureAtlas(getTextureManager(), 4096, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		tutorialTTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(atlas, this, "tutorial.png", 4, 1);
		try{ atlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 1)); }
		catch(Exception e){ e.printStackTrace(); }
		atlas.load();
		
		BuildableBitmapTextureAtlas buttonAtlas = new BuildableBitmapTextureAtlas(getTextureManager(), 512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);	
		backButtonTTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(buttonAtlas, this, "tutorial_back_button.png", 2, 1);
		nextButtonTTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(buttonAtlas, this, "tutorial_next_button.png", 2, 1);
		try{ buttonAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 1)); }
		catch(Exception e){ e.printStackTrace(); }
		buttonAtlas.load();
	}

	@Override
	protected Scene onCreateScene() 
	{
		currStage = 0;

		setupScene();
		setupHUD();

		model.pinchZoomDetector = new PinchZoomDetector(this);
		model.scrollDetector = new SurfaceScrollDetector(this);
		
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
	    	returnToMainMenu();
	    
	    return super.onKeyDown(keyCode, event);
	}
	
	private void setupScene() 
	{
		model.scene = new Scene();
		model.scene.setOnSceneTouchListener(this);
		model.scene.setOnAreaTouchTraversalFrontToBack();
		model.scene.setTouchAreaBindingOnActionDownEnabled(true);
		
		tiledTutorialSprite = new AnimatedSprite(0, 0, 800, 400, tutorialTTR, getVertexBufferObjectManager());
		float x = 0; //(Model.CAMERA_WIDTH - tiledTutorialSprite.getWidth()) / 2;
		tiledTutorialSprite.setX(x);
		model.scene.attachChild(tiledTutorialSprite);
	}

	private void setupHUD()
	{	
		createNextButton();
		createBackButton();
		
		model.hud = new HUD();
		model.hud.attachChild(nextButtonSprite);
		model.hud.registerTouchArea(nextButtonSprite);
		model.camera.setHUD(model.hud);
	}
	
	private void createBackButton() 
	{
		backButtonSprite = new ButtonSprite(Model.CAMERA_WIDTH - 175, 10, backButtonTTR, 
				getVertexBufferObjectManager()) 
		{
			@Override
		    public boolean onAreaTouched(TouchEvent event, float x, float y) 
			{
				if (event.isActionDown()) 
				{
					setCurrentTileIndex(1);
					
					int currTileIndex = tiledTutorialSprite.getCurrentTileIndex();
					tiledTutorialSprite.setCurrentTileIndex(currTileIndex - 1);
					
					currStage--;
					
					if(currStage == 0)
					{
						model.hud.detachChild(backButtonSprite);
						model.hud.unregisterTouchArea(backButtonSprite);
					}
				}
				else if (event.isActionUp() || event.isActionCancel() || event.isActionOutside()) 
					setCurrentTileIndex(0);

				return super.onAreaTouched(event, x, y);
		    };
		};
	}

	private void createNextButton() 
	{
	    nextButtonSprite = new ButtonSprite(Model.CAMERA_WIDTH - 85, 10, nextButtonTTR, 
				getVertexBufferObjectManager()) 
		{
			@Override
		    public boolean onAreaTouched(TouchEvent event, float x, float y) 
			{
				if (event.isActionDown()) 
				{				
					setCurrentTileIndex(1);
					
					int currTileIndex = tiledTutorialSprite.getCurrentTileIndex();
					if(currTileIndex == tiledTutorialSprite.getTileCount() - 1)
					{
						showTutorialCompleteDialog();
						return true;
					}
					
					tiledTutorialSprite.setCurrentTileIndex(currTileIndex + 1);
					currStage++;
					
					if(currStage == 1)
					{
						backButtonSprite.setCurrentTileIndex(0);
						model.hud.attachChild(backButtonSprite);
						model.hud.registerTouchArea(backButtonSprite);
					}
				}
				else if (event.isActionUp() || event.isActionCancel() || event.isActionOutside()) 
					setCurrentTileIndex(0);

				return super.onAreaTouched(event, x, y);
		    };
		};
	}
	
	private void restartActivity()
	{	
		Intent intent = new Intent(TutorialActivity.this, TutorialActivity.class);
	    overridePendingTransition(0, 0);
	    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
	    finish();

	    overridePendingTransition(0, 0);
	    startActivity(intent);
	}

	private void returnToMainMenu()
	{
		MusicManager.continueMusic = true;
		
		Intent intent = new Intent(TutorialActivity.this, MainMenuActivity.class);
		startActivity(intent);
		finish();
	}
	
	private void goToLevel1()
	{
		Intent intent = new Intent(TutorialActivity.this, MainActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("levelNumber", 1);
		intent.putExtras(bundle);
		startActivity(intent);
		finish();
	}
	
	protected void showTutorialCompleteDialog()
	{
	    runOnUiThread(new Runnable() 
		{
		    public void run() 
		    {
		        showDialog(0);
		    }
		});
	}

	@Override
	public void onClick(View v) 
	{
		int id = v.getId();
		
		switch(id)
		{
			case MAIN_MENU_ID:
				returnToMainMenu();
				break;
			case RESTART_ID:
				restartActivity();
				break;
			case PLAY_LEVEL1_ID:
				goToLevel1();
				break;
			default:
				break;
		}
	}
	
	@Override
	protected Dialog onCreateDialog(final int id) 
	{
		// This event only fires after completing tutorial.
		
		final LinearLayout view = createDialogView();
		return new AlertDialog.Builder(this)
		.setCancelable(false)
		.setView(view)
		.create();
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
		titleView.setText("You completed the tutorial!");
		rl.addView(titleView);
		
		Button nextLevelButton = new Button(this);
		nextLevelButton.setTextColor(Color.WHITE);
		nextLevelButton.setText("play level 1");
		nextLevelButton.setLayoutParams(lp);
		nextLevelButton.setBackgroundResource(R.drawable.menu_level_button);
		nextLevelButton.setId(PLAY_LEVEL1_ID);
		nextLevelButton.setOnClickListener(this);

		Button mainMenuButton = new Button(this);
		mainMenuButton.setTextColor(Color.WHITE);
		mainMenuButton.setText("main menu");
		mainMenuButton.setLayoutParams(lp);
		mainMenuButton.setBackgroundResource(R.drawable.menu_level_button);
		mainMenuButton.setId(MAIN_MENU_ID);
		mainMenuButton.setOnClickListener(this);
		
		Button replayButton = new Button(this);
		replayButton.setTextColor(Color.WHITE);
		replayButton.setText("restart tutorial");
		replayButton.setLayoutParams(lp);
		replayButton.setBackgroundResource(R.drawable.menu_level_button);
		replayButton.setId(RESTART_ID);
		replayButton.setOnClickListener(this);
		
		ll.addView(rl);
		ll.addView(nextLevelButton);
		ll.addView(mainMenuButton);
		ll.addView(replayButton);
		
		return ll;
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
