package edu.wmich.cs.samuel.kison.Client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
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

/**
 * This is the lowest-level GUI structure for the Client. Two of these are created by a GUIPanel object, which can receive messages from this board, and also
 * make modifications to it if it received the appropriate message to do so from its parent GUIFrame.
 * 
 * <br>
 * <br>
 * Code reference (chess game): https://stackoverflow.com/questions/21142686/making-a-robust-resizable-swing-chess-gui
 * 
 * @author Samuel Kison
 *
 */
public class GUIBoard implements ActionListener
{
	private JButton[][] gameBoardSquares = new JButton[10][10]; //the grid of squares on the board
	private JPanel gameBoard; //the board itself
	private static final String COLS = "ABCDEFGHIJ"; //letters in the side column
	boolean player; //true = player, false = enemy
	Color yourColor = new Color(56, 201, 255); //background colors
	Color enemyColor = new Color(255, 90, 68); //^
	private GUIPanel panel; //parent Panel, used to send messages up a level

	/**
	 * Initialize this board
	 * 
	 * @param _player
	 *            true = player1(you), false = player2 (enemy)
	 * @param newPanel
	 *            the parent GUIPanel (so this board can send messages up to it)
	 */
	public GUIBoard(boolean _player, GUIPanel newPanel)
	{
		panel = newPanel;
		player = _player;

		gameBoard = new JPanel(new GridLayout(0, 11)) //create the JPanel that holds the board
		{

			/**
			 * default serial UID
			 */
			private static final long serialVersionUID = 1L;
		};
		gameBoard.setBorder(new CompoundBorder(new EmptyBorder(8, 8, 8, 8), new LineBorder(Color.BLACK)));

		// Set the BG color
		if (player)
		{
			gameBoard.setBackground(yourColor);
		}
		else
		{
			gameBoard.setBackground(enemyColor);
		}

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

				b.setIcon(ImageHolder.defaultScaleIcon(ImageHolder.getWaterTile())); //give a water tile to every button to start
				b.setRolloverIcon(ImageHolder.defaultScaleIcon(ImageHolder.getHitMarker(false))); //set a rollover icon of a hitmarker to tell which button you will click

				//keep track of x & y position
				b.putClientProperty("x", jj);
				b.putClientProperty("y", ii);

				//add action listener for this button
				b.addActionListener(this);

				gameBoardSquares[jj][ii] = b;
			}
		}

		//fill the board
		gameBoard.add(new JLabel("")); //empty square in the top-left corner
		for (int ii = 0; ii < gameBoardSquares.length; ii++)// fill the top row
		{
			gameBoard.add(new JLabel(COLS.substring(ii, ii + 1), SwingConstants.CENTER));
		}
		for (int ii = 0; ii < gameBoardSquares.length; ii++)// fill everything else (loop through rows)
		{
			for (int jj = 0; jj < gameBoardSquares.length; jj++) //loop through cols
			{
				switch (jj) //decide if this is a leftmost column, in which case, add a number
				{
					case 0: //add left-hand numbers
						gameBoard.add(new JLabel("" + ((ii + 1)), SwingConstants.CENTER));
					default: //add the tile (and YES, if case=0, both the number tile AND this should be added, which is why there is no break above!)
						gameBoard.add(gameBoardSquares[jj][ii]);
						break;
				}
			}
		}
	}

	/**
	 * Reset the icons on this board
	 */
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

	/**
	 * Used by GUIPanel ONLY to retrieve this board
	 * 
	 * @return
	 */
	protected JPanel getBoard()
	{
		return gameBoard;
	}

	/**
	 * This method places ship images on the board depending on the ship name, coordinates, and rotation specified
	 * 
	 * @param ship
	 *            Name of the current ship to place
	 * @param x
	 *            X coordinate of the ship's head on the board
	 * @param y
	 *            Y coordinate of the ship's head on the board
	 * @param rotation
	 *            Direction the ship is facing (Up, Down, Left, or Right)
	 */
	public void placeShip(String ship, int x, int y, String rotation)
	{
		BufferedImage[] shipParts = null; //start null

		switch (ship) //fill in the shipParts array with images depending on name and rotation
		{
			case ("Destroyer"):
				shipParts = new BufferedImage[2];
				shipParts[0] = ImageHolder.rotateImage(ImageHolder.getDestroyer(1), rotation);
				shipParts[1] = ImageHolder.rotateImage(ImageHolder.getDestroyer(2), rotation);
				break;
			case ("Submarine"):
				shipParts = new BufferedImage[3];
				shipParts[0] = ImageHolder.rotateImage(ImageHolder.getSubmarine(1), rotation);
				shipParts[1] = ImageHolder.rotateImage(ImageHolder.getSubmarine(2), rotation);
				shipParts[2] = ImageHolder.rotateImage(ImageHolder.getSubmarine(3), rotation);
				break;
			case ("Cruiser"):
				shipParts = new BufferedImage[3];
				shipParts[0] = ImageHolder.rotateImage(ImageHolder.getCruiser(1), rotation);
				shipParts[1] = ImageHolder.rotateImage(ImageHolder.getCruiser(2), rotation);
				shipParts[2] = ImageHolder.rotateImage(ImageHolder.getCruiser(3), rotation);
				break;
			case ("Battleship"):
				shipParts = new BufferedImage[4];
				shipParts[0] = ImageHolder.rotateImage(ImageHolder.getBattleship(1), rotation);
				shipParts[1] = ImageHolder.rotateImage(ImageHolder.getBattleship(2), rotation);
				shipParts[2] = ImageHolder.rotateImage(ImageHolder.getBattleship(3), rotation);
				shipParts[3] = ImageHolder.rotateImage(ImageHolder.getBattleship(4), rotation);
				break;
			case ("Carrier"):
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
		for (int i = 0; i < shipParts.length; i++)
		{
			gameBoardSquares[x][y].setIcon(ImageHolder.defaultScaleIcon(shipParts[i])); //set icon of current button at x/y coords

			//increment or decrement x or y depending on the rotation
			switch (rotation)
			{
				case ("Up"):
					y++;
					break;
				case ("Down"):
					y--;
					break;
				case ("Left"):
					x++;
					break;
				case ("Right"):
					x--;
					break;
				default:
					break;
			}
		}
	}

	/**
	 * Used to set the image of a button to either the 'HitMarkerFail' or 'HitMarkerSuccess' at a specific location
	 * 
	 * @param x
	 *            X coordinate of attack
	 * @param y
	 *            Y coordinate of attack
	 * @param hit
	 *            true if attack was successful, false if it failed
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
		panel.receiveMessage(
				new String[] { "button", String.valueOf(player), Integer.toString(x), Integer.toString(y) });
	}

}
