package com.schimpf.block.braj.pro;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;

public class Level 
{
	IAreaShape[][] grid;
	Model model;
	int rows, columns;
	int exitRow, exitColumn;
	Sprite exitSprite;
	Sprite background;
	int playerStartRow, playerStartColumn;
    float width, height;    
    LevelEnvironment environment;
	
	private static Random rand = new Random();

	public Level(String fileName, Model model)
	{
		this.model = model;
		
		try
		{			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(model.activity.getAssets().open(fileName));  			
			loadLevel(doc);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	};
	
	public Level(File file, Model model)
	{
		this.model = model;
		
		try
		{			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(file);  			
			loadLevel(doc);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	};
	
	public Level(int numRows, int numColumns, int exitRow, int exitColumn, int playerRow, int playerColumn, Model model)
	{		
		this.model = model;
		
		rows = numRows;
		columns = numColumns;
		this.exitRow = exitRow;
		this.exitColumn = exitColumn;
		playerStartRow = playerRow;
		playerStartColumn = playerColumn;
		grid = new IAreaShape[numRows][numColumns];
		
		width = columns * Model.BLOCK_WIDTH;
		height = rows * Model.BLOCK_HEIGHT;
		
		int environmentChoice = rand.nextInt(LevelEnvironment.NUM_ENVIRONMENTS);
		environment = new LevelEnvironment(model, environmentChoice, width);
	}
	
	private void loadLevel(Document doc)
	{
		doc.getDocumentElement().normalize();
		
		Element root = (Element)doc.getElementsByTagName("level").item(0);
		
		rows = Integer.valueOf(root.getElementsByTagName("numRows").item(0).getTextContent().trim());
		columns = Integer.valueOf(root.getElementsByTagName("numColumns").item(0).getTextContent().trim());
		
		exitRow = Integer.valueOf(root.getElementsByTagName("exitRow").item(0).getTextContent().trim());
		exitColumn = Integer.valueOf(root.getElementsByTagName("exitColumn").item(0).getTextContent().trim());
		
		playerStartRow = Integer.valueOf(root.getElementsByTagName("playerRow").item(0).getTextContent().trim());
		playerStartColumn = Integer.valueOf(root.getElementsByTagName("playerColumn").item(0).getTextContent().trim());
		
		grid = new IAreaShape[rows][columns];
		
		width = columns * Model.BLOCK_WIDTH;
		height = rows * Model.BLOCK_HEIGHT;

		int environmentChoice = rand.nextInt(LevelEnvironment.NUM_ENVIRONMENTS);
		environment = new LevelEnvironment(model, environmentChoice, width);
		
		NodeList blockList = root.getElementsByTagName("block");
		for(int i = 0; i < blockList.getLength(); i++)
		{
			Element blockElement = (Element)blockList.item(i);
			int row = Integer.valueOf(blockElement.getElementsByTagName("row").item(0).getTextContent().trim());
			int column = Integer.valueOf(blockElement.getElementsByTagName("column").item(0).getTextContent().trim());
			boolean isWall = Boolean.valueOf(blockElement.getElementsByTagName("isWall").item(0).getTextContent().trim());
			
			Block block;
			if(isWall)
				block = RandomBlockFactory.buildWallBlock(model, this, row, column);
			else
				block = RandomBlockFactory.buildCarryBlock(model, this, row, column);
					
			grid[row][column] = block;
		}
	}
	
	public boolean isBlockAtGridIndex(int row, int column)
	{
		return row > -1 && column > -1 && row < rows && column < columns && grid[row][column] != null;
	}

	public int getHighestRowWithBlock(int startRow, int column, boolean includeExit)
	{
		int highestBlockRow = model.level.rows; // returns one below the lowest row
		
		for(int i = startRow; i < model.level.rows; i++)
		{
			if(includeExit && i == exitRow && column == exitColumn)
			{
				// We set it to one greater than we should, since it will be decreased by one later.
				highestBlockRow = i + 1;
				break;
			}
			else if(model.level.isBlockAtGridIndex(i, column))
			{
				highestBlockRow = i;
				break;
			}
		}
		
		return highestBlockRow;
	}
	
	public boolean isAtPlayerStartIndex(int row, int column)
	{
		return row == playerStartRow && column == playerStartColumn;
	}
	
	public boolean isAtExitIndex(int row, int column)
	{
		return row == exitRow && column == exitColumn;
	}

	public static Vector2 getPositionFromGridIndex(float row, float column)
	{
		Vector2 position = Vector2Pool.obtain(column * Model.BLOCK_WIDTH, row * Model.BLOCK_HEIGHT);
		return position;
	}
	
	public static Vector2 getGridIndexFromPosition(float x, float y)
	{
		x -= (Math.round(x) % Model.BLOCK_WIDTH); 
		y -= (Math.round(y) % Model.BLOCK_HEIGHT);
		x /= Model.BLOCK_WIDTH;
		y /= Model.BLOCK_HEIGHT;
		
		Vector2 index = Vector2Pool.obtain(Math.round(y), Math.round(x));
		return index;
	}
	
	public ArrayList<IAreaShape> getAllEntities()
	{
		ArrayList<IAreaShape> result = new ArrayList<IAreaShape>();
		for(int i = 0; i < columns; i++)
		{
			for(int j = 0; j < rows; j++)
			{
				if(grid[j][i] != null)
					result.add(grid[j][i]); 
			}
		}
		
		return result;
	}

	public void initPlayer()
	{
		model.player = new Player(playerStartRow, playerStartColumn, environment.playerTTR, model);
	}

	public Sprite createExit()
	{
		Vector2 exitPosition = Level.getPositionFromGridIndex(exitRow, exitColumn);
		exitSprite =
				new Sprite(exitPosition.x, exitPosition.y, Model.BLOCK_WIDTH, Model.BLOCK_HEIGHT, 
						model.exitTR, ((SimpleBaseGameActivity)(model.activity)).getVertexBufferObjectManager());
		
		return exitSprite;
	}
}
