package edu.wmich.cs.samuel.kison;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * This class is a handler that controls all of the images used in the application. Any time any object needs an image, it can make a call to one of this
 * class's static methods. Everything is initialized by Main.java.
 * 
 * @author Samuel Kison
 *
 */
public class ImageHolder
{
	private static BufferedImage[] allImgs;

	/**
	 * Initialize all images to be used
	 */
	public static void loadImages()
	{
		/*26 images in total: 2 Destroyer segments, 3 Submarine segments, 3 Cruiser segments, 
		 * 4 Battleship segments, 5 Carrier segments, 1 water tile, 1 broken ship image, 
		 * 1 success hit marker, 1 fail hit marker, 5 full ships, and the game's icon*/
		allImgs = new BufferedImage[27];

		try
		{
			allImgs[0] = ImageIO.read(ImageHolder.class.getResource("/Destroyer1.png"));
			allImgs[1] = ImageIO.read(ImageHolder.class.getResource("/Destroyer2.png"));
			allImgs[2] = ImageIO.read(ImageHolder.class.getResource("/Submarine1.png"));
			allImgs[3] = ImageIO.read(ImageHolder.class.getResource("/Submarine2.png"));
			allImgs[4] = ImageIO.read(ImageHolder.class.getResource("/Submarine3.png"));
			allImgs[5] = ImageIO.read(ImageHolder.class.getResource("/Cruiser1.png"));
			allImgs[6] = ImageIO.read(ImageHolder.class.getResource("/Cruiser2.png"));
			allImgs[7] = ImageIO.read(ImageHolder.class.getResource("/Cruiser3.png"));
			allImgs[8] = ImageIO.read(ImageHolder.class.getResource("/Battleship1.png"));
			allImgs[9] = ImageIO.read(ImageHolder.class.getResource("/Battleship2.png"));
			allImgs[10] = ImageIO.read(ImageHolder.class.getResource("/Battleship3.png"));
			allImgs[11] = ImageIO.read(ImageHolder.class.getResource("/Battleship4.png"));
			allImgs[12] = ImageIO.read(ImageHolder.class.getResource("/Carrier1.png"));
			allImgs[13] = ImageIO.read(ImageHolder.class.getResource("/Carrier2.png"));
			allImgs[14] = ImageIO.read(ImageHolder.class.getResource("/Carrier3.png"));
			allImgs[15] = ImageIO.read(ImageHolder.class.getResource("/Carrier4.png"));
			allImgs[16] = ImageIO.read(ImageHolder.class.getResource("/Carrier5.png"));
			allImgs[17] = ImageIO.read(ImageHolder.class.getResource("/WaterTile.png"));
			allImgs[18] = ImageIO.read(ImageHolder.class.getResource("/BrokenShip.png"));
			allImgs[19] = ImageIO.read(ImageHolder.class.getResource("/HitMarkerSuccess.png"));
			allImgs[20] = ImageIO.read(ImageHolder.class.getResource("/HitMarkerFail.png"));
			allImgs[21] = ImageIO.read(ImageHolder.class.getResource("/Destroyer.png"));
			allImgs[22] = ImageIO.read(ImageHolder.class.getResource("/Submarine.png"));
			allImgs[23] = ImageIO.read(ImageHolder.class.getResource("/Cruiser.png"));
			allImgs[24] = ImageIO.read(ImageHolder.class.getResource("/Battleship.png"));
			allImgs[25] = ImageIO.read(ImageHolder.class.getResource("/Carrier.png"));
			allImgs[26] = ImageIO.read(ImageHolder.class.getResource("/Battleship Icon.png"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Get any image held by this class
	 * 
	 * @param index
	 *            index of the image to get within the allImgs array
	 * @return BufferedImage the image pointed to by index
	 */
	public static BufferedImage getImage(int index)
	{
		return allImgs[index];
	}

	/**
	 * Get a segment of the Destroyer ship
	 * 
	 * @param in
	 *            index of the Destroyer (0 = head)
	 * @return BufferedImage image for the specified index of the ship
	 */
	public static BufferedImage getDestroyer(int in)
	{
		int index = 21; //index of the full Destroyer
		switch (in)
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

	/**
	 * Get a segment of the Submarine ship
	 * 
	 * @param in
	 *            index of the Submarine (0 = head)
	 * @return BufferedImage image for the specified index of the ship
	 */
	public static BufferedImage getSubmarine(int in)
	{
		int index = 22; //index of the full Submarine
		switch (in)
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

	/**
	 * Get a segment of the Cruiser ship
	 * 
	 * @param in
	 *            index of the Cruiser (0 = head)
	 * @return BufferedImage image for the specified index of the ship
	 */
	public static BufferedImage getCruiser(int in)
	{
		int index = 23; //index of the full Cruiser
		switch (in)
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

	/**
	 * Get a segment of the Battleship ship
	 * 
	 * @param in
	 *            index of the Battleship (0 = head)
	 * @return BufferedImage image for the specified index of the ship
	 */
	public static BufferedImage getBattleship(int in)
	{
		int index = 24; //index of the full Battleship
		switch (in)
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

	/**
	 * Get a segment of the Carrier ship
	 * 
	 * @param in
	 *            index of the Carrier (0 = head)
	 * @return BufferedImage image for the specified index of the ship
	 */
	public static BufferedImage getCarrier(int in)
	{
		int index = 25; //index of the full Carrier
		switch (in)
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

	/**
	 * Get the water tile
	 * 
	 * @return BufferedImage water tile image
	 */
	public static BufferedImage getWaterTile()
	{
		return allImgs[17];
	}

	/**
	 * Get the broken ship image
	 * 
	 * @return BufferedImage broken ship image
	 */
	public static BufferedImage getBrokenShip()
	{
		return allImgs[18];
	}

	/**
	 * Get the game icon
	 * 
	 * @return BufferedImage game icon
	 */
	public static BufferedImage getGameIcon()
	{
		return allImgs[26];
	}

	/**
	 * Get a hitmarker image
	 * 
	 * @param hit
	 *            true = successful hit, false = failed hit
	 * @return BufferedImage the hitmarker specified by 'hit'
	 */
	public static BufferedImage getHitMarker(boolean hit)
	{
		if (hit)//successful hit
		{
			return allImgs[19];
		}
		else//no hit
		{
			return allImgs[20];
		}
	}

	/**
	 * Take in a normal square image and return one that is scaled to the static size "Main.squareSize"
	 * 
	 * @param img
	 *            Original square image
	 * @return ImageIcon the resized image
	 */
	public static ImageIcon defaultScaleIcon(Image img)
	{
		Image newImg = img.getScaledInstance(Main.squareSize, Main.squareSize, java.awt.Image.SCALE_FAST);

		return new ImageIcon(newImg);
	}

	/**
	 * Rotate any given square image to any cardinal direction <br>
	 * Code reference: https://stackoverflow.com/questions/9749121/java-image-rotation-with-affinetransform-outputs-black-image-but-works-well-whe
	 * 
	 * @param inputImage
	 *            the original, upright image
	 * @param rotation
	 *            'Left', 'Right', 'Up', or 'Down'- specifies the new rotation of the image
	 * @return BufferedImage the newly rotated image
	 */
	public static BufferedImage rotateImage(BufferedImage inputImage, String rotation)
	{
		if (rotation.equals("Right") || rotation.equals("Left") || rotation.equals("Down"))
		{
			AffineTransform tx = new AffineTransform();
			tx.translate(inputImage.getHeight() / 2, inputImage.getWidth() / 2);

			if (rotation.equals("Right"))
			{
				tx.rotate(Math.PI / 2);
			}
			else if (rotation.equals("Down"))
			{
				tx.rotate(Math.PI);
			}
			else//Left
			{
				tx.rotate((Math.PI / 2) * 3);
			}

			// first - center image at the origin so rotate works OK
			tx.translate(-inputImage.getWidth() / 2, -inputImage.getHeight() / 2);
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
			BufferedImage newImage = new BufferedImage(inputImage.getHeight(), inputImage.getWidth(),
					inputImage.getType());
			op.filter(inputImage, newImage);

			return newImage;
		}
		else
		{
			return inputImage;
		}
	}
}
