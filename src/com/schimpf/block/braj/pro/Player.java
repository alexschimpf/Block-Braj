package com.schimpf.block.braj.pro;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;


public class Player extends AnimatedSprite
{
	private static final int RIGHT_TILE_INDEX= 0;
	private static final int LEFT_TILE_INDEX = 1;
	
	Model model;
	Block carryBlock;
	int currRow;
	int currColumn;
	
	public Player(int row, int column, ITiledTextureRegion textureRegion, Model model) 
	{
		super(0, 0, Model.BLOCK_WIDTH, Model.BLOCK_HEIGHT, textureRegion, 
				((SimpleBaseGameActivity)(model.activity)).getVertexBufferObjectManager());
		
		Vector2 position = Level.getPositionFromGridIndex(row, column);
		float x = (int)position.x;
		float y = (int)position.y;
		setPosition(x, y);
		
		this.model = model;
		carryBlock = null;
		
		currRow = row;
		currColumn = column;

		model.camera.setCenter(getX(), getY());
		model.camera.setChaseEntity(this);	
	}
	
	public void moveLeft()
	{
		if (isFacingRight()) 
		{
			int currIndex = getCurrentTileIndex();
			setCurrentTileIndex(currIndex + (LEFT_TILE_INDEX - RIGHT_TILE_INDEX));  
			return;
		}

		int targetRow = getTargetMoveRow();
		if(targetRow < 0)
			return;

		move(targetRow, currColumn - 1);
	}
	
	public void moveRight()
	{
		if (!isFacingRight()) 
		{
			int currIndex = getCurrentTileIndex();
			setCurrentTileIndex(currIndex - (LEFT_TILE_INDEX - RIGHT_TILE_INDEX));  
			return;
		}
		
		int targetRow = getTargetMoveRow();
		if(targetRow < 0)
			return;
		
		move(targetRow, currColumn + 1);
	}
	
	public boolean pickUpBlock()
	{	
		if(carryBlock != null)
			return false;

		int targetRow = getTargetPickupRow();
		if(targetRow < 0)
			return false;

		int offset = isFacingRight() ? 1 : -1;
		boolean isPickupSuccessful =
				((Block)(model.level.grid[targetRow][currColumn + offset])).move(currRow - 1, currColumn, false);
		
		if(isPickupSuccessful)
		{
			if(isFacingRight())
				setCurrentTileIndex(RIGHT_TILE_INDEX + 2);
			else
				setCurrentTileIndex(LEFT_TILE_INDEX + 2);			
			
			// If we actually picked up a block, set the player's carryBlock and set its old grid spot to null.
			carryBlock = (Block)(model.level.grid[currRow - 1][currColumn]);
			model.level.grid[targetRow][currColumn + offset] = null;
		}

		return true;
	}
	
	public boolean dropBlock()
	{	
		// Player has no block to drop.
		if(carryBlock == null)
			return false;
		
		int offset = isFacingRight() ? 1 : -1;
		
		int targetRow = getTargetDropRow();
		if(targetRow < 0)
			return false;

		if(carryBlock.move(targetRow, currColumn + offset, true)) 
		{
			if(isFacingRight())
				setCurrentTileIndex(RIGHT_TILE_INDEX);
			else
				setCurrentTileIndex(LEFT_TILE_INDEX);
			
			// If the block was actually moved, clear the player's carryBlock.
			carryBlock = null;		
			return true;
		}
		
		return false;
	}
	
	// Resets the player's position and updates the grid.
	private boolean move(int targetRow, int targetColumn)
	{	
		// Player can't actually move.
		if(!isTargetValid(targetRow, targetColumn)) 
			return false;

		model.level.grid[targetRow][targetColumn] = this; // move player to new spot in grid
		model.level.grid[currRow][currColumn] = null;     // sets its old spot to null
		currRow = targetRow;
		currColumn = targetColumn;
		
		Vector2 position = Level.getPositionFromGridIndex(targetRow, targetColumn);
		setPosition(position.x, position.y);
		
		// If carrying a block, move that block, too.
		if(isCarryingBlock())
			carryBlock.move(targetRow - 1, targetColumn, false);
		
		return true;
	}
	
	// Gets the target row of the player, as a result of movement.
	private int getTargetMoveRow()
	{
		int offset = isFacingRight() ? 1 : -1;
		
		int targetRow = -1;		
		int targetColumn = currColumn + offset;
		
		boolean isBlockInFrontOfPlayer = model.level.isBlockAtGridIndex(currRow, targetColumn); 
		boolean isBlockInFrontOfAndAbovePlayer = model.level.isBlockAtGridIndex(currRow - 1, targetColumn);
		
		if(!isCarryingBlock())
		{
			if (!isBlockInFrontOfPlayer) 
			{
				// There is no block directly in front of player.
				targetRow = model.level.getHighestRowWithBlock(currRow, targetColumn, true) - 1; // Find the block the player will land on.
			}
			else if(!isBlockInFrontOfAndAbovePlayer && !model.level.isBlockAtGridIndex(currRow - 1, currColumn)) 
			{
				// There is a block in front of player, but not above that block.
				// Also, there is no block directly over player.
				targetRow = currRow - 1;
			}
		}
		else 
		{	
			if (!isBlockInFrontOfPlayer && !isBlockInFrontOfAndAbovePlayer) 
			{
				// There is no block directly in front of or in front of and above player.
				targetRow = model.level.getHighestRowWithBlock(currRow, targetColumn, true) - 1; // Find the block the player will land on.
			}
			else if(isBlockInFrontOfPlayer && model.level.isBlockAtGridIndex(currRow - 2, currColumn))
			{
				// There is a block in front of the player and a block just above the carryBlock.
				return targetRow;
			}		
			else if(isBlockInFrontOfPlayer && !isBlockInFrontOfAndAbovePlayer && !model.level.isBlockAtGridIndex(currRow - 2, targetColumn))
			{
				// There is a block in front of player but not 1 and 2 above that one.
				targetRow = currRow - 1;
			}
		}

		return targetRow;
	}
	
	// Gets the target row of the block we are picking up.
	private int getTargetPickupRow()
	{
		int offset = isFacingRight() ? 1 : -1;

		int targetRow = -1;
		if(!model.level.isBlockAtGridIndex(currRow - 1, currColumn + offset) && 
				model.level.isBlockAtGridIndex(currRow, currColumn + offset) &&
				!((Block)model.level.grid[currRow][currColumn + offset]).isWall) 
		{
			// There is a non-wall block in front of player, and no block above that one.
			targetRow = currRow;
		}

		return targetRow;
	}
		
	// Gets the target row of the block we are dropping.
	private int getTargetDropRow()
	{
		int offset = isFacingRight() ? 1 : -1;

		int targetRow = -1;
		if(!model.level.isBlockAtGridIndex(currRow, currColumn + offset) && 
				!model.level.isBlockAtGridIndex(currRow - 1, currColumn + offset))
		{
			// There is no block in front of or in front of and above the player.
			targetRow = model.level.getHighestRowWithBlock(currRow, currColumn + offset, false) - 1; // Find the row the block will land on.
		}
		else if (model.level.isBlockAtGridIndex(currRow, currColumn + offset) && 
				!model.level.isBlockAtGridIndex(currRow - 1, currColumn + offset))
		{
			// There is a block in front of player, but no block above that one.
			targetRow = currRow - 1;
		}		
		
		if(model.level.isAtExitIndex(targetRow, currColumn + offset))
			return -1;
		
		return targetRow;
	}
	
	// Determines if player's and carry block's (if applicable) targets are in range and not already filled up.
	private boolean isTargetValid(int targetRow, int targetColumn)
	{
		boolean isPlayerTargetValid = !(targetRow < 0 || targetColumn < 0 || targetRow > model.level.rows - 1 || targetColumn > model.level.columns - 1 ||
				model.level.grid[targetRow][targetColumn] != null);

		if(!isCarryingBlock())
		{
			// We just need to check the validity of player's position.
			return isPlayerTargetValid;
		}
		else
		{
			// We need to check the validity of the player's carry block as well.
			int targetCarryBlockRow = targetRow - 1;
			return isPlayerTargetValid && carryBlock.isTargetValid2(targetCarryBlockRow, targetColumn);
		}
	}
	
	private boolean isFacingRight()
	{
		return (getCurrentTileIndex() % 2) == RIGHT_TILE_INDEX;
	}
	
	public boolean isCarryingBlock()
	{
		return carryBlock != null;
	}
}
