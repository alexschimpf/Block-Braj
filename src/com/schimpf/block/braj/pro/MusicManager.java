package com.schimpf.block.braj.pro;

import com.schimpf.block.braj.pro.R;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class MusicManager 
{
	public static MediaPlayer mp;
	public static boolean continueMusic = true;
	public static Model model;
	
	public static int tracks[];
	public static int currTrack;
	
	public static int musicVolumeProgress;
	public static int soundEffectVolumeProgress;
	
	public static void start(Context context) 
	{		
		if(mp == null)
		{
			tracks = new int[4];
			tracks[0] = R.raw.music1;
			tracks[1] = R.raw.music2;
			tracks[2] = R.raw.music3;
			tracks[3] = R.raw.music4;
			
			currTrack = (int)(Math.random() * tracks.length);
			
			musicVolumeProgress = 100;
			soundEffectVolumeProgress = 100;
			
			mp = MediaPlayer.create(context, tracks[currTrack]);
			mp.setOnCompletionListener(createOnCompletionListener(context));
			
			mp.setVolume(1f, 1f);
		}
		
		if(!continueMusic)
			mp.pause();
		
		if (!mp.isPlaying()) 
		{
			mp.setLooping(true);
			mp.start();
		}
	}

	public static void pause() 
	{
		if (mp != null && mp.isPlaying()) 
			mp.pause();
	}
	
	private static OnCompletionListener createOnCompletionListener(final Context context)
	{
		return new OnCompletionListener() 
		{
			@Override
			public void onCompletion(MediaPlayer mp) 
			{
				mp.release();
				currTrack = (currTrack + 1) % tracks.length;
				mp = MediaPlayer.create(context, tracks[currTrack]);
				start(context);
			}
			
		};
	}
}
