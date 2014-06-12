package com.schimpf.block.braj.pro;
import org.andengine.audio.sound.Sound;
import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.input.touch.detector.PinchZoomDetector;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;

import android.app.Activity;

public class Model 
{
	static final int CAMERA_WIDTH = 800;
	static final int CAMERA_HEIGHT = 400;
	static final int BLOCK_WIDTH = 37;
	static final int BLOCK_HEIGHT = 37; 
	
	public static final int CARRYBLOCK_ID = 0;
	public static final int CLOUD_ID = 1;
	public static final int DAY_GROUNDBLOCK1_ID = 2;
	public static final int DAY_GROUNDBLOCK2_ID = 3;
	public static final int DAY_WALLBLOCK1_ID = 4;
	public static final int DAY_WALLBLOCK2_ID = 5;
	public static final int DAY_WALLBLOCK3_ID = 6;
	public static final int DAY_WALLBLOCK4_ID = 7;
	public static final int DESERT_GROUNDBLOCK1_ID = 8;
	public static final int DESERT_GROUNDBLOCK2_ID = 9;
	public static final int DESERT_WALLBLOCK1_ID = 10;
	public static final int DESERT_WALLBLOCK2_ID = 11;
	public static final int DESERT_WALLBLOCK3_ID = 12;
	public static final int DESERT_WALLBLOCK4_ID = 13;
	public static final int EXIT_ID = 14;
	public static final int NIGHT_GROUNDBLOCK1_ID = 15;
	public static final int NIGHT_GROUNDBLOCK2_ID = 16;
	public static final int NIGHT_WALLBLOCK1_ID = 17;
	public static final int NIGHT_WALLBLOCK2_ID = 18;
	public static final int NIGHT_WALLBLOCK3_ID = 19;
	public static final int NIGHT_WALLBLOCK4_ID = 20;
	public static final int SNOW_GROUNDBLOCK1_ID = 21;
	public static final int SNOW_GROUNDBLOCK2_ID = 22;
	public static final int SNOW_WALLBLOCK1_ID = 23;
	public static final int SNOW_WALLBLOCK2_ID = 24;
	public static final int SNOW_WALLBLOCK3_ID = 25;
	public static final int SNOW_WALLBLOCK4_ID = 26;
	
	public static final int NUM_LEVELS = 30;
	
	// Main.
	Activity activity;	
	ZoomCamera camera;		
	Scene scene;
	HUD hud;
	Level level;	
    SurfaceScrollDetector scrollDetector;
    PinchZoomDetector pinchZoomDetector;
    float startPinchZoomFactor;
	
	// Sprites.
    Player player;
    ButtonSprite restartButton;
	ButtonSprite handleBlockButton;
	ButtonSprite moveLeftButton;
	ButtonSprite moveRightButton;
	
	// Texture regions.
	ITextureRegion exitTR;
	ITextureRegion carryBlockTR;
	ITextureRegion cloudTR;
	
	ITiledTextureRegion day_playerTTR;
	ITextureRegion day_wallBlock1TR;
	ITextureRegion day_wallBlock2TR;
	ITextureRegion day_wallBlock3TR;
	ITextureRegion day_wallBlock4TR;
	ITextureRegion day_groundBlock1TR;
	ITextureRegion day_groundBlock2TR;
	
	ITiledTextureRegion night_playerTTR;
	ITextureRegion night_wallBlock1TR;
	ITextureRegion night_wallBlock2TR;
	ITextureRegion night_wallBlock3TR;
	ITextureRegion night_wallBlock4TR;
	ITextureRegion night_groundBlock1TR;
	ITextureRegion night_groundBlock2TR;
	
	ITiledTextureRegion snow_playerTTR;
	ITextureRegion snow_wallBlock1TR;
	ITextureRegion snow_wallBlock2TR;
	ITextureRegion snow_wallBlock3TR;
	ITextureRegion snow_wallBlock4TR;
	ITextureRegion snow_groundBlock1TR;
	ITextureRegion snow_groundBlock2TR;
	
	ITiledTextureRegion desert_playerTTR;
	ITextureRegion desert_wallBlock1TR;
	ITextureRegion desert_wallBlock2TR;
	ITextureRegion desert_wallBlock3TR;
	ITextureRegion desert_wallBlock4TR;
	ITextureRegion desert_groundBlock1TR;
	ITextureRegion desert_groundBlock2TR;
	
	ITiledTextureRegion day_restartButtonTTR;
	ITiledTextureRegion day_handleBlockButtonTTR;
	ITiledTextureRegion day_moveLeftButtonTTR;
	ITiledTextureRegion day_moveRightButtonTTR;	
	ITiledTextureRegion desert_restartButtonTTR;
	ITiledTextureRegion desert_handleBlockButtonTTR;
	ITiledTextureRegion desert_moveLeftButtonTTR;
	ITiledTextureRegion desert_moveRightButtonTTR;	
	ITiledTextureRegion snow_restartButtonTTR;
	ITiledTextureRegion snow_handleBlockButtonTTR;
	ITiledTextureRegion snow_moveLeftButtonTTR;
	ITiledTextureRegion snow_moveRightButtonTTR;
	ITiledTextureRegion night_restartButtonTTR;
	ITiledTextureRegion night_handleBlockButtonTTR;
	ITiledTextureRegion night_moveLeftButtonTTR;
	ITiledTextureRegion night_moveRightButtonTTR;
	
	// Sounds.
	Sound blockSound;

	Model(Activity mainActivity)
	{
		this.activity = mainActivity;	
	}
}
