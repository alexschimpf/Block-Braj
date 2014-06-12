package com.schimpf.block.braj.pro;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainMenuSurfaceView extends SurfaceView implements SurfaceHolder.Callback
{	
	MainThread thread;
	MainMenuActivity activity;	
	MainMenuScrollingBackground background;	
	Paint paint;

	public MainMenuSurfaceView(Context context, MainMenuActivity activity) 
	{
		super(context);
		
		this.activity = activity;
		
		paint = new Paint();
		paint.setStrokeWidth(10);
		paint.setStyle(Style.FILL_AND_STROKE);
		
		thread = new MainThread(this);
		
		getHolder().addCallback(this);
	}
	
	private void init()
	{
		background = new MainMenuScrollingBackground(this);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) 
	{

	}

	public void surfaceCreated(SurfaceHolder holder) 
	{
		if(thread == null)
			thread = new MainThread(this);
		
		thread.running = true;
		thread.start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) 
	{
		// Try to join thread, until the join is successful.
		
		boolean retry = true;
		if(thread != null)
		{
			thread.running = false;
			while(retry)
			{
				try
				{
					thread.join();
					retry = false; // the join was successful
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
		thread = null;
	}
	
	public void update()
	{
		background.update();
	}
	
	@Override
	public void onDraw(Canvas canvas)
	{
		if(canvas != null)
			background.draw(canvas, paint);
	}
	
	/******************************************************************************/
    /**                         Draw/Update Thread Class                         **/
    /******************************************************************************/

	public class MainThread extends Thread
	{
		MainMenuSurfaceView view;
		SurfaceHolder surfaceHolder;
	    boolean       running;
	    
	    public MainThread(MainMenuSurfaceView view)
	    {
	    	this.view = view;
	        surfaceHolder = view.getHolder();
	    	running = false;
	    }
	    
	    @Override
	    public void run()
	    {
	    	init();
	    	
	    	Canvas canvas;
	    	while(running)
	    	{
	            canvas = null;
	            try 
	            {
	                canvas = surfaceHolder.lockCanvas(null);
	                synchronized (surfaceHolder) 
	                {
	                	try
	                	{               		
	                	    view.update();
	                		view.onDraw(canvas);
	                	}
	                	catch (Exception e)
	                	{
	                		e.printStackTrace();
	                	}
	                }
	            } 
	            catch (Exception e)
	            {
	            	e.printStackTrace();
	            }
	            finally 
	            {
	                if (canvas != null) 
	                    surfaceHolder.unlockCanvasAndPost(canvas);
	            }
	    	}
	    }	    
	}
}
