package com.schimpf.block.braj.pro;

import org.andengine.entity.scene.background.RepeatingSpriteBackground;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.source.AssetBitmapTextureAtlasSource;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.util.Log;

public class LevelEnvironment 
{
	// This class holds the textures associated with a particular background environment.
	
	private final static int NIGHT = 0;
	private final static int DAY = 1;
	private final static int SNOW = 2;
	private final static int DESERT = 3;
	
	final static int NUM_ENVIRONMENTS = 4;
	
	Model model;
	
	ITextureRegion wallBlock1TR;
	ITextureRegion wallBlock2TR;
	ITextureRegion wallBlock3TR;
	ITextureRegion wallBlock4TR;
	ITextureRegion groundBlock1TR;
	ITextureRegion groundBlock2TR;
	ITiledTextureRegion playerTTR;
	
	ITiledTextureRegion restartButtonTTR;
	ITiledTextureRegion handleBlockButtonTTR;
	ITiledTextureRegion moveLeftButtonTTR;
	ITiledTextureRegion moveRightButtonTTR;
		
	SpriteBackground background;
	
	public LevelEnvironment(Model model, int choice, float levelWidth)
	{
		this.model = model;
		SimpleBaseGameActivity activity = (SimpleBaseGameActivity)model.activity;
		
		BitmapTextureAtlas atlas = new BitmapTextureAtlas(activity.getTextureManager(), 512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		ITextureRegion backgroundTR = null;
		
		switch(choice)
		{
			case NIGHT:
				wallBlock1TR = model.night_wallBlock1TR;
				wallBlock2TR = model.night_wallBlock2TR;
				wallBlock3TR = model.night_wallBlock3TR;
				wallBlock4TR = model.night_wallBlock4TR;
				groundBlock1TR = model.night_groundBlock1TR;
				groundBlock2TR = model.night_groundBlock2TR;
				playerTTR = model.night_playerTTR;
				restartButtonTTR = model.night_restartButtonTTR;
				handleBlockButtonTTR = model.night_handleBlockButtonTTR;
				moveLeftButtonTTR = model.night_moveLeftButtonTTR;
				moveRightButtonTTR = model.night_moveRightButtonTTR;
				background = new RepeatingSpriteBackground(Model.CAMERA_WIDTH * 2, Model.CAMERA_HEIGHT * 2, 
						activity.getTextureManager(), 
						AssetBitmapTextureAtlasSource.create(activity.getAssets(), "night_background.png"), 
						activity.getVertexBufferObjectManager());
				break;
			case DAY:
				wallBlock1TR = model.day_wallBlock1TR;
				wallBlock2TR = model.day_wallBlock2TR;
				wallBlock3TR = model.day_wallBlock3TR;
				wallBlock4TR = model.day_wallBlock4TR;
				groundBlock1TR = model.day_groundBlock1TR;
				groundBlock2TR = model.day_groundBlock2TR;
				playerTTR = model.day_playerTTR;
				restartButtonTTR = model.day_restartButtonTTR;
				handleBlockButtonTTR = model.day_handleBlockButtonTTR;
				moveLeftButtonTTR = model.day_moveLeftButtonTTR;
				moveRightButtonTTR = model.day_moveRightButtonTTR;
				backgroundTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "day_background.png", 0, 0);
				atlas.load();
				background = new SpriteBackground(0, 0, 0, new Sprite(0, 0, Model.CAMERA_WIDTH, Model.CAMERA_HEIGHT, backgroundTR, 
						activity.getVertexBufferObjectManager()));
				break;
			case SNOW:
				wallBlock1TR = model.snow_wallBlock1TR;
				wallBlock2TR = model.snow_wallBlock2TR;
				wallBlock3TR = model.snow_wallBlock3TR;
				wallBlock4TR = model.snow_wallBlock4TR;
				groundBlock1TR = model.snow_groundBlock1TR;
				groundBlock2TR = model.snow_groundBlock2TR;
				playerTTR = model.snow_playerTTR;
				restartButtonTTR = model.snow_restartButtonTTR;
				handleBlockButtonTTR = model.snow_handleBlockButtonTTR;
				moveLeftButtonTTR = model.snow_moveLeftButtonTTR;
				moveRightButtonTTR = model.snow_moveRightButtonTTR;
				backgroundTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "snow_background.png", 0, 0);
				atlas.load();
				background = new SpriteBackground(0, 0, 0, new Sprite(0, 0, Model.CAMERA_WIDTH, Model.CAMERA_HEIGHT, backgroundTR, 
						activity.getVertexBufferObjectManager()));
				break;
			case DESERT:
				wallBlock1TR = model.desert_wallBlock1TR;
				wallBlock2TR = model.desert_wallBlock2TR;
				wallBlock3TR = model.desert_wallBlock3TR;
				wallBlock4TR = model.desert_wallBlock4TR;
				groundBlock1TR = model.desert_groundBlock1TR;
				groundBlock2TR = model.desert_groundBlock2TR;
				playerTTR = model.desert_playerTTR;
				restartButtonTTR = model.desert_restartButtonTTR;
				handleBlockButtonTTR = model.desert_handleBlockButtonTTR;
				moveLeftButtonTTR = model.desert_moveLeftButtonTTR;
				moveRightButtonTTR = model.desert_moveRightButtonTTR;
				backgroundTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(atlas, activity, "desert_background.png", 0, 0);
				atlas.load();
				background = new SpriteBackground(0, 0, 0, new Sprite(0, 0, Model.CAMERA_WIDTH, Model.CAMERA_HEIGHT, backgroundTR, 
						activity.getVertexBufferObjectManager()));
				break;
		}
	}
}
