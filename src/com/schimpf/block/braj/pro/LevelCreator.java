package com.schimpf.block.braj.pro;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.util.Arrays;

import org.andengine.entity.shape.IAreaShape;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

public class LevelCreator 
{
	Level level;
	Model model;
	
	public LevelCreator(Level level, Model model)
	{
		this.level = level;
		this.model = model;
	}
	
	public File generateXMLFile()
	{
		int levelNum = getNextAvailableLevelNum();
		if(levelNum < 0)
			return null;
		
		 File file = new File("data/data/com.schimpf.block.braj.pro/" + levelNum + ".xml");
	     try
	     {
	         //file.createNewFile();	        
	    	 FileOutputStream fos = new FileOutputStream(file);
	    	 	    	 
	         XmlSerializer serializer = Xml.newSerializer();
	         serializer.setOutput(fos, "UTF-8");	        
	         serializeLevel(serializer);	    
	         fos.close();
	     }
	     catch(Exception e)
	     {
	    	 e.printStackTrace();
	    	 return null;
	     }
	     
	     return file;
	}
	
	public String generateXMLCode()
	{
	     try
	     {
	         StringWriter writer = new StringWriter();	    	 	    	 
	         XmlSerializer serializer = Xml.newSerializer();
	         serializer.setOutput(writer);	        
	         serializeLevel(serializer);	    
	         
	         return writer.toString();
	     }
	     catch(Exception e)
	     {
	    	 e.printStackTrace();
	    	 return null;
	     }
	}
	
	private void serializeLevel(XmlSerializer serializer)
	{
        try 
        {
			serializer.startDocument(null, Boolean.valueOf(true));

	        serializer.startTag(null, "level");
	        
	        // Number of rows/columns.
	        serializer.startTag(null, "numRows");
	        serializer.text("" + level.rows);
	        serializer.endTag(null, "numRows");
	        serializer.startTag(null, "numColumns");
	        serializer.text("" + level.columns);
	        serializer.endTag(null, "numColumns");
	        
	        // Exit row/column.
	        serializer.startTag(null, "exitRow");
	        serializer.text("" + level.exitRow);
	        serializer.endTag(null, "exitRow");
	        serializer.startTag(null, "exitColumn");
	        serializer.text("" + level.exitColumn);
	        serializer.endTag(null, "exitColumn");

			// Player row/column.
			serializer.startTag(null, "playerRow");
	        serializer.text("" + level.playerStartRow);
	        serializer.endTag(null, "playerRow");
			serializer.startTag(null, "playerColumn");
	        serializer.text("" + level.playerStartColumn);
	        serializer.endTag(null, "playerColumn");
	        			
			for(int i = 0; i < level.rows; i++)
			{
				for(int j = 0; j < level.columns; j++)
				{
					IAreaShape shape = level.grid[i][j];
					
					if(shape != null && (i != level.playerStartRow || j != level.playerStartColumn))
					{
						Block block = (Block)shape;
						
						serializer.startTag(null, "block");
						
						serializer.startTag(null, "row");
						serializer.text("" + i);
						serializer.endTag(null, "row");
						
						serializer.startTag(null, "column");
						serializer.text("" + j);
						serializer.endTag(null, "column");
						
						serializer.startTag(null, "isWall");
						serializer.text(String.valueOf(block.isWall));
						serializer.endTag(null, "isWall");

			        	serializer.endTag(null, "block");
					}
				}
			}
			
	        serializer.endTag(null,"level");
	        
	        serializer.endDocument();
	        serializer.flush();
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
	}
	
	private int getNextAvailableLevelNum() 
	{
		try
		{
			String[] levelFilenames = model.activity.getAssets().list("levels");
			Arrays.sort(levelFilenames);
			String lastLevelFilename = levelFilenames[levelFilenames.length - 1];
			int lastLevelNum = Integer.valueOf("" + lastLevelFilename.charAt(5)); 
			
			return lastLevelNum + 1;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return -1;
		}
	}
}
