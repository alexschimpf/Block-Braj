package com.schimpf.block.braj.pro;

import java.util.ArrayList;

import com.schimpf.block.braj.pro.R;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

public class MainMenuScrollingBackground 
{
	private MainMenuSurfaceView surfaceView;
	private Bitmap modelBitmap;
	private float bitmapWidth;
	
	ArrayList<Background> backgroundList;
	
	public MainMenuScrollingBackground(MainMenuSurfaceView surfaceView)
	{
		this.surfaceView = surfaceView;		
	    initBitmapList();
	}
	
	public void initBitmapList()
	{
		backgroundList = new ArrayList<Background>();
		
		modelBitmap = BitmapFactory.decodeResource(surfaceView.getResources(), R.drawable.background_image);
		modelBitmap = Bitmap.createScaledBitmap(modelBitmap, modelBitmap.getWidth(), surfaceView.getHeight(), false);
		
		bitmapWidth = modelBitmap.getWidth();
		
		addInitialBackgrounds();
	}
	
	public void update()
	{
		if(backgroundList == null || backgroundList.size() < 1)
			return;
		
		if(isNewLinkNeeded())
		{
			Background newLink = new Background(modelBitmap.copy(Config.ARGB_8888, false), -bitmapWidth);
			backgroundList.add(0, newLink);
		}
		
		if(isLastLinkReadyForRemoval())
			backgroundList.remove(backgroundList.size() - 1);
		
		for(Background background : backgroundList)
			background.x += background.speed;
	}
	
	public void draw(Canvas canvas, Paint paint)
	{
		for(Background background : backgroundList)
			canvas.drawBitmap(background.bitmap, background.x, background.y, paint);
	}
	
	private void addInitialBackgrounds()
	{
		backgroundList.add(new Background(modelBitmap.copy(Config.ARGB_8888, false), -bitmapWidth));
		
		int currX = 0;
		while(currX < surfaceView.getWidth())
		{
			backgroundList.add(new Background(modelBitmap.copy(Config.ARGB_8888, false), currX));
			currX += bitmapWidth;
		}
	}
	
	private boolean isLastLinkReadyForRemoval()
	{
		Background lastInChain = backgroundList.get(backgroundList.size() - 1);
		return isOOB(lastInChain);
	}
	
	private boolean isNewLinkNeeded()
	{
		Background firstInChain = backgroundList.get(0);
		return isPastLeftEdge(firstInChain);
	}
	
	private boolean isOOB(Background background)
	{
		return background.x >= surfaceView.getWidth();
	}
	
	private boolean isPastLeftEdge(Background background)
	{
		return background.x >= 0;
	}
	
	/******************************************************************************/
    /**                         Single Background Class                          **/
    /******************************************************************************/
	
	public class Background
	{
		private static final float SPEED = 1;
		
		Bitmap bitmap;
		float x, y;
		float width, height;
		float speed;
		
		public Background(Bitmap bitmap, float x)
		{
			this.bitmap = bitmap;
			this.speed = SPEED;
			
			this.x = x;
			y = 0;
			
			width = bitmap.getWidth();
			height = bitmap.getHeight();
		}
	}
}
