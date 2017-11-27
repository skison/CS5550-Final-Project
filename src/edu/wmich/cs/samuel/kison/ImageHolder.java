package edu.wmich.cs.samuel.kison;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageHolder
{
	private static BufferedImage[] allImgs;
	
	public static void loadImages()
	{
		/*26 images in total: 2 Destroyer segments, 3 Submarine segments, 3 Cruiser segments, 
		 * 4 Battleship segments, 5 Carrier segments, 1 water tile, 1 broken ship image, 
		 * 1 success hit marker, 1 fail hit marker, 5 full ships*/
		allImgs = new BufferedImage[26];
		
		try
		{
			allImgs[0] = ImageIO.read(new File("images/Destroyer1.png"));
			allImgs[1] = ImageIO.read(new File("images/Destroyer2.png"));
			allImgs[2] = ImageIO.read(new File("images/Submarine1.png"));
			allImgs[3] = ImageIO.read(new File("images/Submarine2.png"));
			allImgs[4] = ImageIO.read(new File("images/Submarine3.png"));
			allImgs[5] = ImageIO.read(new File("images/Cruiser1.png"));
			allImgs[6] = ImageIO.read(new File("images/Cruiser2.png"));
			allImgs[7] = ImageIO.read(new File("images/Cruiser3.png"));
			allImgs[8] = ImageIO.read(new File("images/Battleship1.png"));
			allImgs[9] = ImageIO.read(new File("images/Battleship2.png"));
			allImgs[10] = ImageIO.read(new File("images/Battleship3.png"));
			allImgs[11] = ImageIO.read(new File("images/Battleship4.png"));
			allImgs[12] = ImageIO.read(new File("images/Carrier1.png"));
			allImgs[13] = ImageIO.read(new File("images/Carrier2.png"));
			allImgs[14] = ImageIO.read(new File("images/Carrier3.png"));
			allImgs[15] = ImageIO.read(new File("images/Carrier4.png"));
			allImgs[16] = ImageIO.read(new File("images/Carrier5.png"));
			allImgs[17] = ImageIO.read(new File("images/WaterTile.png"));
			allImgs[18] = ImageIO.read(new File("images/BrokenShip.png"));
			allImgs[19] = ImageIO.read(new File("images/HitMarkerSuccess.png"));
			allImgs[20] = ImageIO.read(new File("images/HitMarkerFail.png"));
			allImgs[21] = ImageIO.read(new File("images/Destroyer.png"));
			allImgs[22] = ImageIO.read(new File("images/Submarine.png"));
			allImgs[23] = ImageIO.read(new File("images/Cruiser.png"));
			allImgs[24] = ImageIO.read(new File("images/Battleship.png"));
			allImgs[25] = ImageIO.read(new File("images/Carrier.png"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static BufferedImage getImage(int index)
	{
		return allImgs[index];
	}
	
	public static BufferedImage getDestroyer(int in)
	{
		int index = 21; //index of the full Destroyer
		switch(in)
		{
			case 1:
				index = 0;
				break;
			case 2:
				index = 1;
				break;
			default: //no need to do anything
				break; 
		}
		return allImgs[index];
	}
	
	public static BufferedImage getSubmarine(int in)
	{
		int index = 22; //index of the full Submarine
		switch(in)
		{
			case 1:
				index = 2;
				break;
			case 2:
				index = 3;
				break;
			case 3:
				index = 4;
				break;
			default: //no need to do anything
				break; 
		}
		return allImgs[index];
	}
	
	public static BufferedImage getCruiser(int in)
	{
		int index = 23; //index of the full Cruiser
		switch(in)
		{
			case 1:
				index = 5;
				break;
			case 2:
				index = 6;
				break;
			case 3:
				index = 7;
				break;
			default: //no need to do anything
				break; 
		}
		return allImgs[index];
	}
	
	public static BufferedImage getBattleship(int in)
	{
		int index = 24; //index of the full Battleship
		switch(in)
		{
			case 1:
				index = 8;
				break;
			case 2:
				index = 9;
				break;
			case 3:
				index = 10;
				break;
			case 4:
				index = 11;
				break;
			default: //no need to do anything
				break; 
		}
		return allImgs[index];
	}
	
	public static BufferedImage getCarrier(int in)
	{
		int index = 25; //index of the full Carrier
		switch(in)
		{
			case 1:
				index = 12;
				break;
			case 2:
				index = 13;
				break;
			case 3:
				index = 14;
				break;
			case 4:
				index = 15;
				break;
			case 5:
				index = 16;
				break;
			default: //no need to do anything
				break; 
		}
		return allImgs[index];
	}
	
	public static BufferedImage getWaterTile()
	{
		return allImgs[17];
	}
	
	public static BufferedImage getBrokenShip()
	{
		return allImgs[18];
	}
	
	public static BufferedImage getHitMarker(boolean hit)
	{
		if (hit) {return allImgs[19];} //successful hit
		else {return allImgs[20];} //no hit
	}
}
