package com.schimpf.block.braj.pro;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import com.badlogic.gdx.math.Vector2;

public class Block extends Sprite 
{
	Model model;
	int currRow;
	int currColumn;
	boolean isWall;
	
	public Block(int row, int column, boolean isWall, ITextureRegion texture, Model model) 
	{
		super(0, 0, Model.BLOCK_WIDTH, Model.BLOCK_HEIGHT, texture, ((SimpleBaseGameActivity)(model.activity)).getVertexBufferObjectManager());
		
		Vector2 position = Level.getPositionFromGridIndex(row, column);
		float x = (int)position.x;
		float y = (int)position.y;
		setPosition(x, y);
		
		this.model = model;
		this.isWall = isWall;
	}
	
	// Tries to reset position (if valid) and updates the grid.
	public boolean move(int targetRow, int targetColumn, boolean isForDrop)
	{	
		// Block can't actually move.
		if(isForDrop && !isTargetValid(targetRow, targetColumn)) // need to check for collision with exit
			return false;
		else if(!isForDrop && !isTargetValid2(targetRow, targetColumn)) // don't need exit collision check
			return false;
		
		model.level.grid[targetRow][targetColumn] = this; // move block to new spot in grid
		model.level.grid[currRow][currColumn] = null;     // set its old spot to null
		currRow = targetRow;
		currColumn = targetColumn;
				
		Vector2 position = Level.getPositionFromGridIndex(targetRow, targetColumn);
		setPosition(position.x, position.y);
		
		return true;
	}
	
	// Checks for collision with exit, too (for dropping).
	public boolean isTargetValid(int targetRow, int targetColumn)
	{
		return !(targetRow < 0 || targetColumn < 0 || targetRow > model.level.rows - 1 || targetColumn > model.level.columns - 1 ||
				model.level.grid[targetRow][targetColumn] != null || isWall || model.level.isAtExitIndex(targetRow, targetColumn));
	}
	
	// Doesn't check for collision with exit (for any non-dropping movement).
	public boolean isTargetValid2(int targetRow, int targetColumn)
	{
		return !(targetRow < 0 || targetColumn < 0 || targetRow > model.level.rows - 1 || targetColumn > model.level.columns - 1 ||
				model.level.grid[targetRow][targetColumn] != null || isWall);
	}
}
