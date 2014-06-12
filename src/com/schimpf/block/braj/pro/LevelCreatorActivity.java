package com.schimpf.block.braj.pro;

import java.io.File;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePack;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackLoader;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackTextureRegionLibrary;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.exception.TexturePackParseException;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.PinchZoomDetector;
import org.andengine.input.touch.detector.PinchZoomDetector.IPinchZoomDetectorListener;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.color.Color;

import com.badlogic.gdx.math.Vector2;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class LevelCreatorActivity extends SimpleBaseGameActivity implements IOnSceneTouchListener, IOnAreaTouchListener, 
		IPinchZoomDetectorListener
{
	private static final int DIALOG_ENTER_LEVEL_INFO = 0;
	private static final int DIALOG_ERROR_MESSAGE = 1;
	
	LevelCreator levelCreator;
	Level level;
	Model model;
	ZoomCamera camera;
	Scene scene;
	HUD hud;
	
	ITiledTextureRegion levelCreatorDoneButtonTTR;
	ButtonSprite levelCreatorDoneButton;	
	ITextureRegion controlBaseTR;
	ITextureRegion controlKnobTR;
	
	Sprite exitSprite = null, playerSprite = null;
	boolean isMovingExit = false, isMovingPlayer = false;
	
	
	@Override
	public EngineOptions onCreateEngineOptions() 
	{	
		model = new Model(this);
		camera = new ZoomCamera(0, 0, Model.CAMERA_WIDTH, Model.CAMERA_HEIGHT);
		camera.setZoomFactor(1.2f);
		
		// If we came from main menu, open up the level info dialog.
		if(cameFromMainMenu())
			showDialog(DIALOG_ENTER_LEVEL_INFO);

		final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, 
			new FillResolutionPolicy(), camera);
		engineOptions.getTouchOptions().setNeedsMultiTouch(true);
		engineOptions.getAudioOptions().setNeedsSound(true);
		engineOptions.getAudioOptions().setNeedsMusic(true);
		
		return engineOptions; 
	}

	@Override
	protected void onCreateResources() 
	{
		BuildableBitmapTextureAtlas buttonAtlas = new BuildableBitmapTextureAtlas(getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		levelCreatorDoneButtonTTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(buttonAtlas, this, "levelcreatordone_button.png", 2, 1);
		try{ buttonAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 1)); }
		catch(Exception e){ e.printStackTrace(); }
		buttonAtlas.load();
		
		BuildableBitmapTextureAtlas controlAtlas = new BuildableBitmapTextureAtlas(getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		controlBaseTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(controlAtlas, this, "controlbase.png");
		controlKnobTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(controlAtlas, this, "controlknob.png");
		try{ controlAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 1)); }
		catch(Exception e){ e.printStackTrace(); }
		controlAtlas.load();
		
		BuildableBitmapTextureAtlas playerAtlas = new BuildableBitmapTextureAtlas(getTextureManager(), 256, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		model.day_playerTTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(playerAtlas, this, "player.png", 4, 1);
		model.night_playerTTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(playerAtlas, this, "player.png", 4, 1);
		model.snow_playerTTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(playerAtlas, this, "player.png", 4, 1);
		model.desert_playerTTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(playerAtlas, this, "player.png", 4, 1);
		try{ playerAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 1)); }
		catch(Exception e){ e.printStackTrace(); }
		playerAtlas.load();
		
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
	}

	@Override
	protected Scene onCreateScene() 
	{		
		scene = new Scene();	
		
		if(!cameFromMainMenu())
		{
			// We have returned from the testing of a level.
			// Load the level file we were previously creating.
			Bundle bundle = getIntent().getExtras();
			File file = ((SBundle)bundle.getSerializable("xmlFile")).file;
			initLevel(file);
		}
		
		model.pinchZoomDetector = new PinchZoomDetector(this);
		
		return scene;
	}
	
	@Override
	public boolean onAreaTouched(final TouchEvent event, final ITouchArea area, final float localX, final float localY)
	{
		float x = event.getX();
		float y = event.getY();
		
		model.pinchZoomDetector.onTouchEvent(event);
		
		if(event.isActionDown())
		{
			Vector2 position = Level.getGridIndexFromPosition(x, y);
			int row = (int)position.x;
			int column = (int)position.y;
			
			if(level.isAtExitIndex(row, column))
			{
				if(isMovingExit)
					isMovingExit = false;
				else
					isMovingExit = true;
				
				isMovingPlayer = false;
			}
			else if(level.isAtPlayerStartIndex(row, column))
			{
				if(isMovingPlayer)
					isMovingPlayer = false;
				else
					isMovingPlayer = true;
				
				isMovingExit = false;
			}
			else
			{		
				isMovingExit = false;
				isMovingPlayer = false;
				
				try
				{
					Block block = (Block)area;			
					if(block.isWall)
					{
						// If it is a wall, remove it.
						removeWallBlock(block, row, column);
					}
					else
					{
						// If it is not a wall, remove it and replace it with a wall.
						replaceCarryBlock(block, row, column);
					}
				}
				catch(Exception e)
				{
					// There was a casting error (tried to cast a player or exit to a block).
					return true;
				}
			}
		}
		
		return true;
	}
	
	@Override
	public boolean onSceneTouchEvent(Scene scene, TouchEvent event)
	{
		float x = event.getX();
		float y = event.getY();
		
		model.pinchZoomDetector.onTouchEvent(event);
		
		if(event.isActionDown())
		{		
			Vector2 nearestGridIndex = Level.getGridIndexFromPosition(x, y);
			int row = (int)nearestGridIndex.x;
			int column = (int)nearestGridIndex.y;

			if(row < 0 || column < 0 || row > level.rows - 1 || column > level.columns - 1)
			{
				// Touch event occured outside bounds of level.
				return true;
			}
			else if (isMovingExit)
			{
				// Move the exit sprite.
				moveExit(row, column);
			}
			else if (isMovingPlayer)
			{
				// Move the player sprite.
				movePlayer(row, column);
			}
			else if(!level.isAtExitIndex(row, column) && !level.isAtPlayerStartIndex(row, column))
			{
				// Add a new block where event occured.
				addNewBlock(row, column);
			}
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) 
	    {
	    	returnToMainMenu();
	        return true;
	    }
	    
	    return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected Dialog onCreateDialog(final int id) 
	{

		switch(id)
		{
			case DIALOG_ENTER_LEVEL_INFO:
				final ScrollView sv = createDialogView();
				return new AlertDialog.Builder(this)
				.setCancelable(false)
				.setView(sv)
				.setPositiveButton("Enter", new OnClickListener() 
				{
					@Override
					public void onClick(final DialogInterface dialog, final int which) 
					{
						LinearLayout ll = (LinearLayout)sv.getChildAt(0);
						String numRowsStr = ((EditText)(ll.getChildAt(2))).getText().toString();
						String numColumnsStr = ((EditText)(ll.getChildAt(4))).getText().toString();
						
						initLevel(numRowsStr, numColumnsStr);
					}
				})
				.setNegativeButton(android.R.string.cancel, new OnClickListener() 
				{
					@Override
					public void onClick(final DialogInterface dialog, final int which) 
					{
						returnToMainMenu();
					}
				})
				.create();
					
			case DIALOG_ERROR_MESSAGE: 
				return new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Level creator error")
				.setCancelable(false)
				.setMessage("You entered something incorrectly or did not fill out every field.")
				.setPositiveButton(android.R.string.ok, new OnClickListener() 
				{
					@Override
					public void onClick(final DialogInterface pDialog, final int pWhich) 
					{
						returnToMainMenu();
					}
				})
				.create();
				
			default:
				return null;
		}
	}
	
	private void setupScene() 
	{
		scene.setOnAreaTouchListener(this);
		scene.setOnSceneTouchListener(this);
		
		drawExit();
		drawPlayer();
		drawGrid();
		drawBoundingLines();
		
		AnalogOnScreenControl analogControl = createAnalogControl();
		scene.setChildScene(analogControl);
	}

	private void setupHUD()
	{
		createLevelCreatorDoneButton();
		
		hud = new HUD();
		hud.attachChild(levelCreatorDoneButton);
		hud.registerTouchArea(levelCreatorDoneButton);
		camera.setHUD(hud);
	}
	
	private void createLevelCreatorDoneButton()
	{
		levelCreatorDoneButton = new ButtonSprite(95, Model.CAMERA_HEIGHT - 95, levelCreatorDoneButtonTTR, getVertexBufferObjectManager()) 
		{
			@Override
		    public boolean onAreaTouched(TouchEvent event, float x, float y) 
			{
				if (event.isActionDown()) 
				{
					generateLevel();
					this.setCurrentTileIndex(1);
				}
				else if (event.isActionUp() || event.isActionCancel() || event.isActionOutside()) 
					this.setCurrentTileIndex(0);

				return super.onAreaTouched(event, x, y);
		    };
		};
	}
	
	private AnalogOnScreenControl createAnalogControl()
	{
		return new AnalogOnScreenControl(Model.CAMERA_WIDTH - 180, Model.CAMERA_HEIGHT - 95, camera, 
				controlBaseTR, controlKnobTR, .1f, 200, getVertexBufferObjectManager(), new AnalogOnScreenControl.IAnalogOnScreenControlListener()
		{
			@Override
			public void onControlChange(BaseOnScreenControl control, float x, float y) 
			{
				 // x between -1 (left) to 1 (right).
				 // y between -1 (up) to 1 (down).
				
				float currCenterX = camera.getCenterX();
				float currCenterY = camera.getCenterY();
				
				boolean isLeft = x < -.8;
				boolean isRight = x > .8;
				boolean isUp = y < -.8;
				boolean isDown = y > .8;
				if(isLeft)
					camera.setCenter(currCenterX - Model.BLOCK_WIDTH, currCenterY);
				else if(isRight)
					camera.setCenter(currCenterX + Model.BLOCK_WIDTH, currCenterY);
				else if(isUp)
					camera.setCenter(currCenterX, currCenterY - Model.BLOCK_HEIGHT);
				else if(isDown)
					camera.setCenter(currCenterX, currCenterY + Model.BLOCK_HEIGHT);
			}

			@Override
			public void onControlClick(AnalogOnScreenControl pAnalogOnScreenControl) 
			{						
			}
		});
	}
	
	private ScrollView createDialogView()
	{
		final LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.setBackgroundColor(android.graphics.Color.rgb(0, 6, 45));
		LinearLayout.LayoutParams llp = 
				new LinearLayout.LayoutParams (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		ll.setLayoutParams(llp);
				
		final RelativeLayout rl = new RelativeLayout(this);
		RelativeLayout.LayoutParams rlp = 
				new RelativeLayout.LayoutParams (RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		rl.setLayoutParams(rlp);
		rl.setGravity(Gravity.CENTER_HORIZONTAL);
		TextView titleView = new TextView(this);
		titleView.setTextColor(android.graphics.Color.rgb(253, 255, 188));
		titleView.setTextSize(30);
		titleView.setText("Enter level info.");
		rl.addView(titleView);

		final TextView numRowsLabel = new TextView(this);
		numRowsLabel.setText("Number of rows");
		numRowsLabel.setTextColor(android.graphics.Color.WHITE);
		final EditText numRowsEdit = new EditText(this);
		numRowsEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);
		numRowsEdit.setRawInputType(InputType.TYPE_CLASS_NUMBER);
		numRowsEdit.setBackgroundColor(android.graphics.Color.rgb(0, 6, 45));
		numRowsEdit.setTextColor(android.graphics.Color.WHITE);
		
		final TextView numColumnsLabel = new TextView(this);
		numColumnsLabel.setText("Number of columns");
		numColumnsLabel.setTextColor(android.graphics.Color.WHITE);
		final EditText numColumnsEdit = new EditText(this);
		numColumnsEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);
		numColumnsEdit.setRawInputType(InputType.TYPE_CLASS_NUMBER);
		numColumnsEdit.setBackgroundColor(android.graphics.Color.rgb(0, 6, 45));
		numColumnsEdit.setTextColor(android.graphics.Color.WHITE);

		ll.addView(rl);
		ll.addView(numRowsLabel);
		ll.addView(numRowsEdit);		
		ll.addView(numColumnsLabel);
		ll.addView(numColumnsEdit);
		
		final ScrollView sv = new ScrollView(this);
		sv.addView(ll);
		
		return sv;
	}
	
	private void drawBoundingLines()
	{		
		Line line = new Line(0, 0, 0, level.height, 2, getVertexBufferObjectManager());
		line.setColor(Color.WHITE);
		scene.attachChild(line);
		line = new Line(0, 0, level.width, 0, 2, getVertexBufferObjectManager());
		line.setColor(Color.WHITE);
		scene.attachChild(line);
		line = new Line(0, level.height, level.width, level.height, 2, getVertexBufferObjectManager());
		line.setColor(Color.WHITE);
		scene.attachChild(line);
		line = new Line(level.width, 0, level.width, level.height, 2, getVertexBufferObjectManager());
		line.setColor(Color.WHITE);
		scene.attachChild(line);
	}
	
	private void drawExit()
	{
		if(exitSprite != null)
		{
			scene.unregisterTouchArea(exitSprite);
			scene.detachChild(exitSprite);
		}
		
		float x = Level.getPositionFromGridIndex(level.exitRow, level.exitColumn).x;
		float y = Level.getPositionFromGridIndex(level.exitRow, level.exitColumn).y;
		exitSprite = new Sprite(x, y, Model.BLOCK_WIDTH, Model.BLOCK_HEIGHT, model.exitTR, getVertexBufferObjectManager());
		scene.registerTouchArea(exitSprite);
		scene.attachChild(exitSprite);
	}
	
	private void drawPlayer()
	{
		if(playerSprite != null)
		{
			scene.unregisterTouchArea(playerSprite);
			scene.detachChild(playerSprite);
		}
		
		float x = Level.getPositionFromGridIndex(level.playerStartRow, level.playerStartColumn).x;
		float y = Level.getPositionFromGridIndex(level.playerStartRow, level.playerStartColumn).y;
		playerSprite = new Sprite(x, y, Model.BLOCK_WIDTH, Model.BLOCK_HEIGHT, level.environment.playerTTR, getVertexBufferObjectManager());
		scene.registerTouchArea(playerSprite);
		scene.attachChild(playerSprite);	
	}
	
	private void drawGrid()
	{
		for(int i = 0; i < level.rows; i++)
		{
			for(int j = 0; j < level.columns; j++)
			{
				if(level.isBlockAtGridIndex(i, j))
				{
					Block block = (Block)level.grid[i][j];
					scene.registerTouchArea(block);
					scene.attachChild(block);
				}
			}
		}
	}
	
	// For coming from dialog box (from main menu).
	private void initLevel(String numRowsStr, String numColumnsStr)
	{
		try
		{
			int numRows = Integer.valueOf(numRowsStr);
			int numColumns = Integer.valueOf(numColumnsStr);
			int exitRow = 0;
			int exitColumn = 0;
			int playerRow = 1;
			int playerColumn = 1;
				
			// Initialize the level and level creator.
			level = new Level(numRows, numColumns, exitRow, exitColumn, playerRow, playerColumn, model);
			levelCreator = new LevelCreator(level, model);
 
			camera.setCenter(level.width/2, level.height/2);
				 
			setupHUD();
			setupScene();
		}
		catch(Exception e)
		{
			e.printStackTrace();			
			showDialog(DIALOG_ERROR_MESSAGE);
		}
	}
	
	// For coming from main activity.
	private void initLevel(File file)
	{
		// Initialize the level and level creator.
		level = new Level(file, model);
		levelCreator = new LevelCreator(level, model);
						 
		camera.setCenter(level.width / 2, level.height / 2);
						 
		setupHUD();
		setupScene();
	}
	
	private void removeWallBlock(Block block, int row, int column)
	{
		scene.unregisterTouchArea(block);
		scene.detachChild(block);			
		level.grid[row][column] = null;
	}
	
	private void replaceCarryBlock(Block block, int row, int column)
	{
		Block newBlock;
		if(Math.random() < .5)
			newBlock = new Block(row, column, true, model.day_wallBlock1TR, model);
		else 
			newBlock = new Block(row, column, true, model.day_wallBlock2TR, model);
		scene.unregisterTouchArea(block);
		scene.detachChild(block);		
		scene.registerTouchArea(newBlock);
		scene.attachChild(newBlock);				
		level.grid[row][column] = newBlock;
	}
	
	private void addNewBlock(int row, int column)
	{
		Block block = new Block(row, column, false, model.carryBlockTR, model);
		scene.registerTouchArea(block);
		scene.attachChild(block);
		level.grid[row][column] = block;
	}
	
	private void movePlayer(int row, int column)
	{
		isMovingPlayer = false;
		level.playerStartRow = row;
		level.playerStartColumn = column;
		drawPlayer();
	}
	
	private void moveExit(int row, int column)
	{
		isMovingExit = false;
		level.exitRow = row;
		level.exitColumn = column;
		drawExit();
	}
	
	private boolean cameFromMainMenu()
	{
		return getIntent().getExtras() == null || !getIntent().getExtras().containsKey("xmlFile");
	}
	
	@TargetApi(11)
	private void generateLevel()
	{		
		String xmlCode = levelCreator.generateXMLCode();
		levelCreator.generateXMLCode();

		//Log.v("blockbraj", xmlCode);  
		int maxLogSize = 3000;
	    for(int i = 0; i <= xmlCode.length() / maxLogSize; i++) 
	    {
	        int start = i * maxLogSize;
	        int end = (i+1) * maxLogSize;
	        end = end > xmlCode.length() ? xmlCode.length() : end;
	        Log.v("blockbraj", xmlCode.substring(start, end));
	    }
		
		File file = levelCreator.generateXMLFile();
		Intent intent = new Intent(LevelCreatorActivity.this, MainActivity.class);
		Bundle extras = new Bundle();
		SBundle bundleFile = new SBundle(file);
		extras.putSerializable("xmlFile", bundleFile);
		intent.putExtras(extras);					
		startActivity(intent);
		finish();
	}	
	
	private void returnToMainMenu()
	{
		MusicManager.continueMusic = true;
		
		Intent intent = new Intent(LevelCreatorActivity.this, MainMenuActivity.class);
		startActivity(intent);
		finish();
	}
	
	@Override
	public void onPinchZoomStarted(PinchZoomDetector detector, TouchEvent event) 
	{
		model.startPinchZoomFactor = camera.getZoomFactor();
	}

	@Override
	public void onPinchZoom(PinchZoomDetector detector, TouchEvent event, float zoomFactor) 
	{
		float result = model.startPinchZoomFactor * zoomFactor;
		camera.setZoomFactor(model.startPinchZoomFactor * zoomFactor);
	}

	@Override
	public void onPinchZoomFinished(PinchZoomDetector pPinchZoomDetector, TouchEvent pTouchEvent, float zoomFactor) 
	{
		float result = model.startPinchZoomFactor * zoomFactor;
		camera.setZoomFactor(model.startPinchZoomFactor * zoomFactor);
	}
}
