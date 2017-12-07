package edu.wmich.cs.samuel.kison.Client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * This is the first child of the main client GUI object. It contains a notificationLabel for notifying the player of a game update, 2 name panels (one for the
 * player, one for the enemy), and 2 GUIBoards (one for each player). To update any of these objects, a message must be forwarded from the parent GUIFrame down
 * to this GUI object.
 * <br><br>
 * Code reference (for GridBag layout): http://www.math.uni-hamburg.de/doc/java/tutorial/uiswing/layout/gridbag.html
 * 
 * @author Samuel Kison
 *
 */
public class GUIPanel
{
	private final JPanel gui = new JPanel(new BorderLayout(3, 3)); //create the new BorderLayout to contain everything
	private GUIBoard board1, board2; //create each player's boards
	private GUIFrame frame; //parent Frame; used to send messages up to it

	//Colors used in the panels
	Color yourColor = new Color(56, 201, 255); //blueish
	Color enemyColor = new Color(255, 90, 68); //redish
	Color notifyColor = new Color(92, 249, 147); //greenish

	String startingPlayerBoardName = "Your Board";
	String startingEnemyBoardName = "Enemy's Board";
	String startingNotification = "Please input your player name!";

	//The 3 labels at the top of the GUI
	private JLabel yourLabel;
	private JLabel enemyLabel;
	private JLabel notificationsLabel;

	/**
	 * Initialize the GUIPanel
	 * 
	 * @param newFrame
	 *            the parent GUIFrame
	 */
	protected GUIPanel(GUIFrame newFrame)
	{
		frame = newFrame;
		initializeGui();
	}

	/**
	 * The actual method used to initialize everything
	 */
	protected final void initializeGui()
	{
		board1 = new GUIBoard(true, this);
		board2 = new GUIBoard(false, this);

		// set up the main GUI
		gui.setBorder(new EmptyBorder(5, 5, 5, 5));

		JPanel boardConstrain = new JPanel(new GridBagLayout()); //create the GridBagLayout to organize everything
		boardConstrain.setBackground(Color.WHITE);

		yourLabel = new JLabel(this.startingPlayerBoardName, SwingConstants.CENTER); //setup the player's name panel
		yourLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
		yourLabel.setOpaque(true);
		yourLabel.setBackground(yourColor);
		enemyLabel = new JLabel(this.startingEnemyBoardName, SwingConstants.CENTER); //setup the enemy's name panel
		enemyLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
		enemyLabel.setOpaque(true);
		enemyLabel.setBackground(enemyColor);
		notificationsLabel = new JLabel(this.startingNotification, SwingConstants.CENTER); //setup the notification panel
		notificationsLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
		notificationsLabel.setOpaque(true);
		notificationsLabel.setBackground(notifyColor);

		GridBagConstraints gbc = new GridBagConstraints();//define the constraints of the layout
		gbc.fill = GridBagConstraints.HORIZONTAL; //first horizontal line is 40px tall with just the notifications panel
		gbc.ipady = 40;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.weightx = 0.0;
		boardConstrain.add(notificationsLabel, gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL; //second horizontal line is 25px tall with both player's name panels
		gbc.gridwidth = 1;
		gbc.ipady = 25;
		gbc.gridx = 0;
		gbc.gridy = 1;
		boardConstrain.add(yourLabel, gbc);
		gbc.gridx = 1;
		gbc.gridy = 1;
		boardConstrain.add(enemyLabel, gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL; //third horizontal line is tall enough to hold the size of the GUIBoards for both players
		gbc.gridx = 0;
		gbc.gridy = 2;
		boardConstrain.add(board1.getBoard(), gbc);
		gbc.gridx = 1;
		gbc.gridy = 2;
		boardConstrain.add(board2.getBoard(), gbc);

		gui.add(boardConstrain);//add the constraints
	}

	/**
	 * reset this panel and children
	 */
	protected void reset()
	{
		board1.reset();
		board2.reset();
	}

	/**
	 * Interpret message from parent GUI (should originate from Client)
	 * 
	 * @param updateType
	 *            the message sent from the parent GUI that will be interpreted to change this panel or the child boards
	 */
	protected void updatePanel(String[] updateType)
	{
		if (updateType[0].equals("notify"))
		{
			notificationsLabel.setText(updateType[1]); //update the notification panel
		}
		else if (updateType[0].equals("player_name"))
		{
			yourLabel.setText(updateType[1] + "'s Board"); //set the player's name
		}
		else if (updateType[0].equals("enemy_name"))
		{
			enemyLabel.setText(updateType[1] + "'s Board"); //set the enemy's name
		}
		else if (updateType[0].equals("place_ship"))
		{
			board1.placeShip(updateType[1], Integer.parseInt(updateType[2]), Integer.parseInt(updateType[3]),
					updateType[4]); //place a new ship on the player's board
		}
		else if (updateType[0].equals("attack"))
		{
			if (updateType[1].equals("true")) //player1's board
			{
				board1.attackLocation(Integer.parseInt(updateType[2]), Integer.parseInt(updateType[3]),
						Boolean.parseBoolean(updateType[4]));
			}
			else //player2's board
			{
				board2.attackLocation(Integer.parseInt(updateType[2]), Integer.parseInt(updateType[3]),
						Boolean.parseBoolean(updateType[4]));
			}
		}
	}

	/**
	 * Used to forward messages to the parent Frame from either of the GUIBoards
	 * 
	 * @param message
	 *            the message to be sent up to GUIFrame from a GUIBoard
	 */
	protected void receiveMessage(String[] message)
	{
		switch (message[0])
		{
			case ("button"):
				frame.receiveMessage(message);
				break;
			default:
				break;
		}
	}

	//Getter
	protected JComponent getGui()
	{
		return gui;
	}

}