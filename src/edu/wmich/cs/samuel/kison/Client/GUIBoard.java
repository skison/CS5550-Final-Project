package edu.wmich.cs.samuel.kison.Client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import edu.wmich.cs.samuel.kison.ImageHolder;
import edu.wmich.cs.samuel.kison.Main;
import edu.wmich.cs.samuel.kison.MessageQueue;

public class GUIBoard implements ActionListener
{
	private JButton[][] gameBoardSquares = new JButton[10][10];
	private JPanel gameBoard;
	private static final String COLS = "ABCDEFGHIJ";
	boolean player; //true = you, false = enemy
	Color yourColor = new Color(56, 201, 255); //background colors
	Color enemyColor = new Color(255, 90, 68);
	private GUIPanel panel; //parent Panel, used to send messages up a level

	public GUIBoard(boolean _player, GUIPanel newPanel)
	{
		panel = newPanel;
		player = _player;
		
		gameBoard = new JPanel(new GridLayout(0, 11)) {};
		gameBoard.setBorder(new CompoundBorder(new EmptyBorder(8, 8, 8, 8), new LineBorder(Color.BLACK)));
		// Set the BG color
		if(player) {gameBoard.setBackground(yourColor);}
		else {gameBoard.setBackground(enemyColor);}
		
		// create the board squares
		Insets buttonMargin = new Insets(0, 0, 0, 0);
		Border buttonBorder = BorderFactory.createDashedBorder(Color.BLACK);
		for (int ii = 0; ii < gameBoardSquares.length; ii++)
		{
			for (int jj = 0; jj < gameBoardSquares[ii].length; jj++)
			{
				JButton b = new JButton();
				b.setMargin(buttonMargin);
				b.setBorder(buttonBorder);
				b.setBackground(Color.BLUE);
				b.setPreferredSize(new Dimension(Main.squareSize, Main.squareSize));
				
				b.setIcon(ImageHolder.defaultScaleIcon(ImageHolder.getWaterTile()));
				b.setRolloverIcon(ImageHolder.defaultScaleIcon(ImageHolder.getHitMarker(false)));
				
				//keep track of x & y position
				b.putClientProperty("x", jj);
				b.putClientProperty("y", ii);
				
				//add action listener for this button
				b.addActionListener(this);
				
				gameBoardSquares[jj][ii] = b;
			}
		}
		
		/*
		 * fill the board
		 */
		gameBoard.add(new JLabel(""));
		// fill the top row
		for (int ii = 0; ii < gameBoardSquares.length; ii++)
		{
			gameBoard.add(new JLabel(COLS.substring(ii, ii + 1), SwingConstants.CENTER));
		}
		// fill the black non-pawn piece row
		for (int ii = 0; ii < gameBoardSquares.length; ii++)
		{
			for (int jj = 0; jj < gameBoardSquares.length; jj++)
			{
				switch (jj)
				{
					case 0: //add left-hand numbers
						gameBoard.add(new JLabel("" + ((ii + 1)), SwingConstants.CENTER));
					default:
						gameBoard.add(gameBoardSquares[jj][ii]);
				}
			}
		}
	}
	
	//Reset the icons on this board
	public void reset()
	{
		for (int ii = 0; ii < gameBoardSquares.length; ii++)
		{
			for (int jj = 0; jj < gameBoardSquares[ii].length; jj++)
			{
				gameBoardSquares[ii][jj].setIcon(ImageHolder.defaultScaleIcon(ImageHolder.getWaterTile()));
			}
		}
	}
	
	public JPanel getBoard()
	{
		return gameBoard;
	}
	
	//Methods for placing new ships & markers
	
	/*
	 * ship = name of ship
	 * x = x coordinate
	 * y = y coordinate
	 * NOTE: x & y coordinates determine where the FRONT of the ship is placed
	 * rotation = direction the ship should face (up, down, left, right)
	 * 
	 * NOTE: no error checking is performed here, that is the job of the server!
	 */
	public void placeShip(String ship, int x, int y, String rotation)
	{
		BufferedImage[] shipParts = null; //start null
		
		switch(ship)
		{
			case("Destroyer"):
				shipParts = new BufferedImage[2];
				shipParts[0] = ImageHolder.rotateImage(ImageHolder.getDestroyer(1), rotation);
				shipParts[1] = ImageHolder.rotateImage(ImageHolder.getDestroyer(2), rotation);
				break;
			case("Submarine"):
				shipParts = new BufferedImage[3];
				shipParts[0] = ImageHolder.rotateImage(ImageHolder.getSubmarine(1), rotation);
				shipParts[1] = ImageHolder.rotateImage(ImageHolder.getSubmarine(2), rotation);
				shipParts[2] = ImageHolder.rotateImage(ImageHolder.getSubmarine(3), rotation);
				break;
			case("Cruiser"):
				shipParts = new BufferedImage[3];
				shipParts[0] = ImageHolder.rotateImage(ImageHolder.getCruiser(1), rotation);
				shipParts[1] = ImageHolder.rotateImage(ImageHolder.getCruiser(2), rotation);
				shipParts[2] = ImageHolder.rotateImage(ImageHolder.getCruiser(3), rotation);
				break;
			case("Battleship"):
				shipParts = new BufferedImage[4];
				shipParts[0] = ImageHolder.rotateImage(ImageHolder.getBattleship(1), rotation);
				shipParts[1] = ImageHolder.rotateImage(ImageHolder.getBattleship(2), rotation);
				shipParts[2] = ImageHolder.rotateImage(ImageHolder.getBattleship(3), rotation);
				shipParts[3] = ImageHolder.rotateImage(ImageHolder.getBattleship(4), rotation);
				break;
			case("Carrier"):
				shipParts = new BufferedImage[5];
				shipParts[0] = ImageHolder.rotateImage(ImageHolder.getCarrier(1), rotation);
				shipParts[1] = ImageHolder.rotateImage(ImageHolder.getCarrier(2), rotation);
				shipParts[2] = ImageHolder.rotateImage(ImageHolder.getCarrier(3), rotation);
				shipParts[3] = ImageHolder.rotateImage(ImageHolder.getCarrier(4), rotation);
				shipParts[4] = ImageHolder.rotateImage(ImageHolder.getCarrier(5), rotation);
				break;
			default:
				break;
		}
		
		//now change the required JButton icons
		for(int i = 0; i < shipParts.length; i++)
		{
			gameBoardSquares[x][y].setIcon(ImageHolder.defaultScaleIcon(shipParts[i]));
			
			//increment or decrement x or y depending on the rotation
			switch(rotation)
			{
				case("Up"):
					y++;
					break;
				case("Down"):
					y--;
					break;
				case("Left"):
					x++;
					break;
				case("Right"):
					x--;
					break;
				default:
					break;
			}
		}
	}
	
	/**
	 * Used to set the image of a button to either the 'HitMarkerFail' or 'HitMarkerSuccess' at a specific location
	 * @param x X coordinate of attack
	 * @param y Y coordinate of attack
	 * @param hit true if attack was successful, false if it failed
	 */
	public void attackLocation(int x, int y, boolean hit)
	{
		gameBoardSquares[x][y].setIcon(ImageHolder.defaultScaleIcon(ImageHolder.getHitMarker(hit)));
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		JButton btn = (JButton) e.getSource();
		int x = (int) btn.getClientProperty("x");
		int y = (int) btn.getClientProperty("y");
		panel.receiveMessage(new String[]{"button", String.valueOf(player), Integer.toString(x), Integer.toString(y)});
	}

}
