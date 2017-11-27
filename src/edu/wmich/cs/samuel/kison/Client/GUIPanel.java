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

import edu.wmich.cs.samuel.kison.MessageQueue;

public class GUIPanel
{
	private final JPanel gui = new JPanel(new BorderLayout(3, 3));
	//private InputQueue playerInput; //important queue that button presses should add to!
	//private final JLabel message = new JLabel("Battleship is ready to play!");
	
	//message queue to loop
	private MessageQueue messageQueue;

	GUIBoard board1, board2;

	GUIPanel(MessageQueue newQueue)
	{
		messageQueue = newQueue;
		initializeGui();
	}

	public final void initializeGui()
	{
		board1 = new GUIBoard(true, messageQueue);
		board2 = new GUIBoard(false, messageQueue);

		// set up the main GUI
		gui.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		JPanel boardConstrain = new JPanel(new GridBagLayout());
		boardConstrain.setBackground(Color.WHITE);

		// TODO: testing
		Color yourColor = new Color(56, 201, 255);
		Color enemyColor = new Color(255, 90, 68);
		Color notifyColor = new Color(92, 249, 147);
		JLabel yourLabel = new JLabel("Your Board", SwingConstants.CENTER);
		yourLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
		yourLabel.setOpaque(true);
		yourLabel.setBackground(yourColor);
		JLabel enemyLabel = new JLabel("Enemy's Board", SwingConstants.CENTER);
		enemyLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
		enemyLabel.setOpaque(true);
		enemyLabel.setBackground(enemyColor);
		JLabel notificationsLabel = new JLabel("Please start or join a game!", SwingConstants.CENTER);
		notificationsLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
		notificationsLabel.setOpaque(true);
		notificationsLabel.setBackground(notifyColor);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.ipady = 40;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.weightx = 0.0;
		boardConstrain.add(notificationsLabel, gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 1;
		gbc.ipady = 25;
		gbc.gridx = 0;
		gbc.gridy = 1;
		boardConstrain.add(yourLabel, gbc);
		gbc.gridx = 1;
		gbc.gridy = 1;
		boardConstrain.add(enemyLabel, gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 2;
		boardConstrain.add(board1.getBoard(), gbc);
		gbc.gridx = 1;
		gbc.gridy = 2;
		boardConstrain.add(board2.getBoard(), gbc);
		gui.add(boardConstrain);

	}

	public JComponent getGui()
	{
		return gui;
	}
	
	/*public void placeShip(String ship, int x, int y, String rotation)
	{
		board1.placeShip(ship, x, y, rotation);
	}*/
	
	public GUIBoard getBoard(boolean player)
	{
		if(player) {return board1;}
		else {return board2;}
	}

}