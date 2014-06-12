package com.schimpf.block.braj.pro;

import java.util.Random;

import org.andengine.opengl.texture.region.ITextureRegion;

public class RandomBlockFactory 
{
	// This class statically builds blocks with random textures, based on a given environment.
	
	private static float WALL1_PROB_LIMIT = 25;
	private static float WALL2_PROB_LIMIT = 50;
	private static float WALL3_PROB_LIMIT = 75;
	
	private static Random rand = new Random();
	
	public static Block buildWallBlock(Model model, Level level, int row, int column)
	{
	    ITextureRegion textureRegion = getRandomWallTextureRegion(level.environment);	    
	    return new Block(row, column, true, textureRegion, model);		
	}
	
	public static Block buildCarryBlock(Model model, Level level, int row, int column)
	{	
		return new Block(row, column, false, model.carryBlockTR, model);
	}
	
	private static ITextureRegion getRandomWallTextureRegion(LevelEnvironment le)
	{
		int randNum = rand.nextInt(100);
		if(randNum < WALL1_PROB_LIMIT)
			return le.wallBlock1TR;
		else if(randNum < WALL2_PROB_LIMIT)
			return le.wallBlock2TR;
		else if(randNum < WALL3_PROB_LIMIT)
			return le.wallBlock3TR;
		else
			return le.wallBlock4TR;
	}
}
