package edu.wmich.cs.samuel.kison.Client;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;

import edu.wmich.cs.samuel.kison.ImageHolder;

/**
 * This is the main GUI object for the client. It can be thought of as the main 'V' of the MVC structure for the client. This class holds the game's JFrame
 * which contains all of the other GUI elements within it. Notably, it has a MenuBar which changes depending on the current game state (dictated by the Client
 * object), and a GUIPanel object which holds 2 GUIBoard objects. Any time the Client object wants to change something on the GUI, it should call the updateGUI
 * method with a message describing what to do.
 * 
 * <br>
 * <br>
 * Code reference (for custom closing actions): https://stackoverflow.com/questions/16372241/run-function-on-jframe-close <br>
 * Code reference (for packing JFrame): https://stackoverflow.com/a/7143398/418556
 * 
 * @author Samuel Kison
 *
 */
public class GUIFrame
{
	//hold the inner panels
	private GUIPanel guiPanel;

	private String gameName = "Battleship"; //the name of the game's JFrame

	//Menubars (swapped out depending on game state) plus some of their elements which need to be accessible in this scope! (the rest are local scope only)
	private JMenuBar startMenuBar; //the first menubar that appears at the top of the screen
	private JMenuBar joinMenuBar; //menubar used when preparing to join a game
	private JTextField ipTextBox; //stored in this scope so that they can be reset from another method
	private JTextField portTextBox; //^
	private JMenuBar hostMenuBar; //menubar used when preparing to host a game
	private JMenuBar hostLoadingMenuBar; //menubar used when creating a server
	private JMenuBar clientLoadingMenuBar; //menubar used when joining a server
	private JButton clientLoadCancelButton; //stored in this scope so that they can be reset from another method
	private JButton clientLoadRetryButton; //^
	private JMenuBar setupMenuBar; //menubar used when setting up ships at start of game
	private JMenu rotation = null; //this is the JMenu object within setupMenuBar that holds the current rotation for the ship
	private JRadioButtonMenuItem rotationUp; //stored in this scope so that they can be reset from another method
	private JRadioButtonMenuItem rotationDown;//^
	private JRadioButtonMenuItem rotationLeft;//^
	private JRadioButtonMenuItem rotationRight;//^
	private JMenuBar playMenuBar; //menubar used when playing the game
	private JMenuBar endMenuBar; //menubar used when the game has ended

	private JFrame f; //the JFrame for this GUI

	private Client client; // reference to Client so messages can be instantly interpreted by it

	/**
	 * Setup the GUI & initialize everything GUI-related
	 * 
	 * @param c
	 *            the Client object that controls this GUIFrame
	 */
	public GUIFrame(Client c)
	{
		client = c;
		guiPanel = new GUIPanel(this); //create the GUIPanel

		f = new JFrame(gameName); //set the game's application name
		f.setIconImage(ImageHolder.getGameIcon()); //set the game's icon
		f.add(guiPanel.getGui()); //add the GUIPanel to the Frame

		constructMenuBars();//Prepare the menu bars

		f.setJMenuBar(startMenuBar); //start with the startMenuBar

		// Ensures JVM closes after frame(s) closed and all non-daemon threads are finished
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent event)
			{
				//messageQueue.push(new String[]{"exit"});
				client.receiveMessage(new String[] { "gui", "exit" });
				System.exit(0);
			}
		});
		//configure JFrame
		f.setLocationByPlatform(true);
		f.setResizable(false); //don't allow resizing

		f.pack(); //ensures the frame is the minimum size it needs to be in order display the components within it		
		f.setMinimumSize(f.getSize()); //ensures the minimum size is enforced.
		f.setVisible(true); //make frame visible
	}

	/**
	 * Used to reset the rotation options in the setupMenuBar
	 */
	private void resetRotation()
	{
		rotation.setText("Up"); //reset rotation to up
		rotationUp.setSelected(true);
		rotationDown.setSelected(false);
		rotationLeft.setSelected(false);
		rotationRight.setSelected(false);
	}

	/**
	 * Called by the Client object whenever it wants to modify the GUI
	 * 
	 * @param updateType
	 */
	public void updateGUI(String[] updateType)
	{
		if (updateType[0].equals("start")) //switch to startMenuBar & reset
		{
			f.setJMenuBar(startMenuBar);
			guiPanel.reset();//Reset GUIPanel & its children
			resetRotation();
			f.pack();
		}
		else if (updateType[0].equals("host")) //switch to hostMenuBar
		{
			f.setJMenuBar(hostMenuBar);
			f.pack();
		}
		else if (updateType[0].equals("join")) //switch to joinMenuBar
		{
			f.setJMenuBar(joinMenuBar);
			f.pack();
		}
		else if (updateType[0].equals("host_load")) //switch to hostLoadingMenuBar
		{
			f.setJMenuBar(hostLoadingMenuBar);
			f.pack();
		}
		else if (updateType[0].equals("client_load")) //switch to clientLoadingMenuBar
		{
			f.setJMenuBar(clientLoadingMenuBar);
			f.pack();
		}
		else if (updateType[0].equals("disable_join_bar")) //disable the clientLoadingMenuBar buttons
		{
			enableClientLoadingMenuBar(false);
		}
		else if (updateType[0].equals("enable_join_bar")) //enable the clientLoadingMenuBar buttons
		{
			enableClientLoadingMenuBar(true);
		}
		else if (updateType[0].equals("setup")) //switch to the setupMenuBar & reset
		{
			f.setJMenuBar(setupMenuBar);
			guiPanel.reset();//Reset GUIPanel & its children
			resetRotation();
			f.pack();
		}
		else if (updateType[0].equals("end")) //switch to the endMenuBar
		{
			f.setJMenuBar(endMenuBar);
			f.pack();
		}
		else if (updateType[0].equals("panel_update")) //forward message to GUIPanel to update
		{
			String[] panelUpdate = Arrays.copyOfRange(updateType, 1, updateType.length); //dispose of the first String that is now unnecessary
			guiPanel.updatePanel(panelUpdate);
		}
	}

	/**
	 * Build up all the menubars by calling their individual construction methods
	 */
	private void constructMenuBars()
	{
		constructStartMenuBar();//startMenuBar
		constructJoinMenuBar();//joinMenuBar
		constructHostMenuBar();//hostMenuBar
		constructHostLoadingMenuBar();//hostLoadingMenuBar
		constructClientLoadingMenuBar();//clientloadingMenuBar
		constructSetupMenuBar();//setupMenuBar
		constructPlayMenuBar();//playMenuBar
		constructEndMenuBar();//endMenuBar
	}

	/**
	 * Construct the startMenuBar
	 */
	private void constructStartMenuBar()
	{
		startMenuBar = new JMenuBar();
		JMenu NewGameMenu = new JMenu("New Game"); //New Game Menu
		JMenuItem hostOption = new JMenuItem("Host Server"); //Host option
		hostOption.addActionListener((ActionEvent event) ->
		{
			client.receiveMessage(new String[] { "gui", "host" });
		});
		JMenuItem joinOption = new JMenuItem("Join Server"); //Join option
		joinOption.addActionListener((ActionEvent event) ->
		{
			client.receiveMessage(new String[] { "gui", "join" });
		});
		NewGameMenu.add(hostOption);
		NewGameMenu.add(joinOption);
		startMenuBar.add(NewGameMenu);
		JMenu divider = new JMenu("|"); //visual divider
		divider.setEnabled(false);
		startMenuBar.add(divider);
		JMenu playerNamePrompt = new JMenu("Username:"); //player name prompt
		playerNamePrompt.setEnabled(false);
		startMenuBar.add(playerNamePrompt);
		JTextField playerNameTextBox = new JTextField("", 10); //player name textbox
		playerNameTextBox.setMaximumSize(playerNameTextBox.getPreferredSize());
		startMenuBar.add(playerNameTextBox);
		JMenu divider2 = new JMenu("|"); //visual divider
		divider2.setEnabled(false);
		startMenuBar.add(divider2);
		JButton confirmButton = new JButton("Confirm Name"); //confirm name button
		confirmButton.addActionListener((ActionEvent event) ->
		{
			client.receiveMessage(new String[] { "gui", "confirm_name", playerNameTextBox.getText() });
		});
		startMenuBar.add(confirmButton);
	}

	/**
	 * Construct the joinMenuBar
	 */
	private void constructJoinMenuBar()
	{
		joinMenuBar = new JMenuBar();
		JMenu ipPrompt = new JMenu("Host IP:"); //IP prompt
		ipPrompt.setEnabled(false);
		joinMenuBar.add(ipPrompt);
		ipTextBox = new JTextField("localhost", 10); //IP textbox (localhost by default)
		ipTextBox.setMaximumSize(ipTextBox.getPreferredSize());
		joinMenuBar.add(ipTextBox);
		JMenu portPrompt = new JMenu("Host Port #:"); //Port prompt
		portPrompt.setEnabled(false);
		joinMenuBar.add(portPrompt);
		portTextBox = new JTextField("2200", 4); //Port textbox (2200 by default)
		portTextBox.setMaximumSize(portTextBox.getPreferredSize());
		joinMenuBar.add(portTextBox);
		JMenu divider = new JMenu("|"); //visual divider
		divider.setEnabled(false);
		joinMenuBar.add(divider);
		JButton confirmButton = new JButton("Join!"); //confirm Join button
		confirmButton.addActionListener((ActionEvent event) ->
		{
			client.receiveMessage(new String[] { "gui", "confirm_join", ipTextBox.getText(), portTextBox.getText() });
		});
		joinMenuBar.add(confirmButton);
		JMenu divider2 = new JMenu("|"); //visual divider
		divider2.setEnabled(false);
		joinMenuBar.add(divider2);
		JButton cancelButton = new JButton("Cancel"); //cancel button
		cancelButton.addActionListener((ActionEvent event) ->
		{
			client.receiveMessage(new String[] { "gui", "cancel_join" });
		});
		joinMenuBar.add(cancelButton);
	}

	/**
	 * Construct the hostMenuBar
	 */
	private void constructHostMenuBar()
	{
		hostMenuBar = new JMenuBar();
		JMenu portPrompt = new JMenu("Server Port #:"); //Port prompt
		portPrompt.setEnabled(false);
		hostMenuBar.add(portPrompt);
		JTextField portTextBox = new JTextField("2200", 4); //Port textbox (2200 by default)
		portTextBox.setMaximumSize(portTextBox.getPreferredSize());
		hostMenuBar.add(portTextBox);
		JMenu divider = new JMenu("|"); //visual divider
		divider.setEnabled(false);
		hostMenuBar.add(divider);
		JButton confirmButton = new JButton("Host!"); //confirm Host button
		confirmButton.addActionListener((ActionEvent event) ->
		{
			client.receiveMessage(new String[] { "gui", "confirm_host", portTextBox.getText() });
		});
		hostMenuBar.add(confirmButton);
		JMenu divider2 = new JMenu("|"); //visual divider
		divider2.setEnabled(false);
		hostMenuBar.add(divider2);
		JButton cancelButton = new JButton("Cancel"); //cancel button
		cancelButton.addActionListener((ActionEvent event) ->
		{
			client.receiveMessage(new String[] { "gui", "cancel_host" });
		});
		hostMenuBar.add(cancelButton);
	}

	/**
	 * Construct the hostLoadingMenuBar
	 */
	private void constructHostLoadingMenuBar()
	{
		hostLoadingMenuBar = new JMenuBar();
		JButton cancelButton = new JButton("Cancel"); //cancel button
		cancelButton.addActionListener((ActionEvent event) ->
		{
			client.receiveMessage(new String[] { "gui", "cancel_load" });
		});
		hostLoadingMenuBar.add(cancelButton);
	}

	/**
	 * Construct the clientLoadingMenuBar
	 */
	private void constructClientLoadingMenuBar()
	{
		clientLoadingMenuBar = new JMenuBar();
		clientLoadCancelButton = new JButton("Cancel"); //cancel button
		clientLoadCancelButton.addActionListener((ActionEvent event) ->
		{
			client.receiveMessage(new String[] { "gui", "cancel_load" });
		});
		clientLoadingMenuBar.add(clientLoadCancelButton);
		JMenu divider = new JMenu("|"); //visual divider
		divider.setEnabled(false);
		clientLoadingMenuBar.add(divider);
		clientLoadRetryButton = new JButton("Retry"); //retry button
		clientLoadRetryButton.addActionListener((ActionEvent event) ->
		{
			client.receiveMessage(new String[] { "gui", "retry_join", ipTextBox.getText(), portTextBox.getText() });
		});
		clientLoadingMenuBar.add(clientLoadRetryButton);
	}

	/**
	 * Construct the setupMenuBar
	 */
	private void constructSetupMenuBar()
	{
		setupMenuBar = new JMenuBar();
		JButton quitButton = new JButton("RageQuit Game"); //quit button
		quitButton.addActionListener((ActionEvent event) ->
		{
			client.receiveMessage(new String[] { "gui", "quit" });
		});
		setupMenuBar.add(quitButton);
		JMenu divider = new JMenu("|"); //visual divider
		divider.setEnabled(false);
		setupMenuBar.add(divider);
		JMenu rotationHeader = new JMenu("Ship Rotation:"); //Rotation header
		rotationHeader.setEnabled(false);
		rotation = new JMenu("Up"); //Ship rotation
		rotation.setEnabled(false);
		//NOTE: adding rotation & rotationHeader in AFTER the selection menu!
		JMenu shipRotationMenu = new JMenu("Rotate");
		ButtonGroup rotationGroup = new ButtonGroup();
		rotationUp = new JRadioButtonMenuItem("Up"); //Up rotation option
		rotationUp.setSelected(true);
		shipRotationMenu.add(rotationUp);
		rotationUp.addItemListener((ItemEvent e) ->
		{
			if (e.getStateChange() == ItemEvent.SELECTED)
			{
				rotation.setText("Up");
			}
		});
		rotationDown = new JRadioButtonMenuItem("Down"); //Down rotation option
		shipRotationMenu.add(rotationDown);
		rotationDown.addItemListener((ItemEvent e) ->
		{
			if (e.getStateChange() == ItemEvent.SELECTED)
			{
				rotation.setText("Down");
			}
		});
		rotationLeft = new JRadioButtonMenuItem("Left"); //Left rotation option
		shipRotationMenu.add(rotationLeft);
		rotationLeft.addItemListener((ItemEvent e) ->
		{
			if (e.getStateChange() == ItemEvent.SELECTED)
			{
				rotation.setText("Left");
			}
		});
		rotationRight = new JRadioButtonMenuItem("Right"); //Right rotation option
		shipRotationMenu.add(rotationRight);
		rotationRight.addItemListener((ItemEvent e) ->
		{
			if (e.getStateChange() == ItemEvent.SELECTED)
			{
				rotation.setText("Right");
			}
		});
		rotationGroup.add(rotationUp);
		rotationGroup.add(rotationDown);
		rotationGroup.add(rotationLeft);
		rotationGroup.add(rotationRight);
		setupMenuBar.add(shipRotationMenu); //Add the options to this menu
		JMenu divider2 = new JMenu("|"); //visual divider
		divider2.setEnabled(false);
		setupMenuBar.add(divider2);
		//Add rotation & rotationHeader as described above
		setupMenuBar.add(rotationHeader);
		setupMenuBar.add(rotation);
	}

	/**
	 * Construct the playMenuBar
	 */
	private void constructPlayMenuBar()
	{
		playMenuBar = new JMenuBar();
		JButton quitButton = new JButton("RageQuit Game"); //quit button
		quitButton.addActionListener((ActionEvent event) ->
		{
			client.receiveMessage(new String[] { "gui", "quit" });
		});
		playMenuBar.add(quitButton);
	}

	/**
	 * Construct the endMenuBar
	 */
	private void constructEndMenuBar()
	{
		endMenuBar = new JMenuBar();
		JButton quitButton = new JButton("End Game"); //quit button
		quitButton.addActionListener((ActionEvent event) ->
		{
			client.receiveMessage(new String[] { "gui", "quit" });
		});
		endMenuBar.add(quitButton);
		JMenu divider = new JMenu("|"); //visual divider
		divider.setEnabled(false);
		endMenuBar.add(divider);
		JButton rematchButton = new JButton("Rematch!"); //rematch button
		rematchButton.addActionListener((ActionEvent event) ->
		{
			client.receiveMessage(new String[] { "gui", "rematch" });
		});
		endMenuBar.add(rematchButton);
	}

	/**
	 * Used to forward messages to Client from the child GUI objects
	 * 
	 * @param message
	 *            the message from a child GUI object to be sent to the Client object
	 */
	protected void receiveMessage(String[] message)
	{
		String[] connectMessage = new String[message.length + 1]; //create new message with one extra spot for gui identifier
		System.arraycopy(message, 0, connectMessage, 1, message.length);
		connectMessage[0] = "gui";

		switch (connectMessage[1])//the first actual String to care about
		{
			case ("button"):
				if (connectMessage[2].equals("true")) //player 1's board
				{
					//Need to append the current rotation to the array
					String[] shipMessage = new String[connectMessage.length + 1]; //create new message with one extra spot for rotation
					System.arraycopy(connectMessage, 0, shipMessage, 0, connectMessage.length);
					shipMessage[shipMessage.length - 1] = rotation.getText();
					client.receiveMessage(shipMessage);
				}
				else //player 2's board
				{
					client.receiveMessage(connectMessage); //simply send new message
				}
				break;
			default:
				break;
		}
	}

	/**
	 * Simple method that enables/disables the JButtons in the clientLoadingMenuBar
	 * 
	 * @param shouldEnable
	 *            true to enable the clientLoadingMenuBar buttons, false to disable them
	 */
	private void enableClientLoadingMenuBar(boolean shouldEnable)
	{
		if (shouldEnable)
		{
			clientLoadCancelButton.setEnabled(true);
			clientLoadRetryButton.setEnabled(true);
		}
		else
		{
			clientLoadCancelButton.setEnabled(false);
			clientLoadRetryButton.setEnabled(false);
		}
	}
}
