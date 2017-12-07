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
	private GUIBoard board1, board2;
	private GUIFrame frame; //parent Frame; used to send messages up to it
	
	//The 3 labels at the top of the GUI
	private JLabel yourLabel;
	private JLabel enemyLabel;
	private JLabel notificationsLabel;

	protected GUIPanel(GUIFrame newFrame)
	{
		frame = newFrame;
		initializeGui();
	}

	protected final void initializeGui()
	{
		board1 = new GUIBoard(true, this);
		board2 = new GUIBoard(false, this);

		// set up the main GUI
		gui.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		JPanel boardConstrain = new JPanel(new GridBagLayout());
		boardConstrain.setBackground(Color.WHITE);

		// TODO: testing
		Color yourColor = new Color(56, 201, 255);
		Color enemyColor = new Color(255, 90, 68);
		Color notifyColor = new Color(92, 249, 147);
		yourLabel = new JLabel("Your Board", SwingConstants.CENTER);
		yourLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
		yourLabel.setOpaque(true);
		yourLabel.setBackground(yourColor);
		enemyLabel = new JLabel("Enemy's Board", SwingConstants.CENTER);
		enemyLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
		enemyLabel.setOpaque(true);
		enemyLabel.setBackground(enemyColor);
		notificationsLabel = new JLabel("Please input your player name!", SwingConstants.CENTER);
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
	
	//reset this panel and children
	protected void reset()
	{
		board1.reset();
		board2.reset();
	}

	
	
	/*
	 * Interpret message from parent GUI (should originate from Client)
	 */
	protected void updatePanel(String[] updateType)
	{
		if (updateType[0].equals("notify"))
		{
			notificationsLabel.setText(updateType[1]);
		}
		else if (updateType[0].equals("player_name"))
		{
			yourLabel.setText(updateType[1] + "'s Board");
		}
		else if (updateType[0].equals("enemy_name"))
		{
			enemyLabel.setText(updateType[1] + "'s Board");
		}
	}
	
	public GUIBoard getBoard(boolean player)
	{
		if(player) {return board1;}
		else {return board2;}
	}
	
	//Used to forward messages to the parent Frame from either of the GUIBoards
	protected void receiveMessage(String[] message)
	{
		switch (message[0])
		{
			case("button"):
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