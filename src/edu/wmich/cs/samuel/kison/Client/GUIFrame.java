package edu.wmich.cs.samuel.kison.Client;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextField;

import edu.wmich.cs.samuel.kison.MessageQueue;

public class GUIFrame
{
	//hold the inner panels
	private GUIPanel guiPanel;
	
	//message queue to loop
	private MessageQueue messageQueue;
	
	public GUIFrame(MessageQueue newQueue)
	{
		messageQueue = newQueue;
		
		guiPanel = new GUIPanel(messageQueue);

		JFrame f = new JFrame("Battleship");
		f.add(guiPanel.getGui());

		// add menu options
		JMenu divider1 = new JMenu("|");
		divider1.setEnabled(false);
		JMenu divider2 = new JMenu("|");
		divider2.setEnabled(false);

		JMenuBar menubar = new JMenuBar();
		JMenu NewGameMenu = new JMenu("New Game");

		// action that occurs when starting up a game server
		JMenuItem hostOption = new JMenuItem("Host Server");
		hostOption.addActionListener((ActionEvent event) ->
		{
			//gameLoopThread.start();
			messageQueue.push(new String[]{"host"});
			//guiPanel.setupNewGame();
		});
		
		/*guiPanel.placeShip("Destroyer", 6, 1, "right");
		guiPanel.placeShip("Submarine", 6, 2, "right");
		guiPanel.placeShip("Cruiser", 6, 3, "right");
		guiPanel.placeShip("Battleship", 6, 4, "right");
		guiPanel.placeShip("Carrier", 6, 5, "right");*/
		
		// action that occurs when joining a game server
		JMenuItem joinOption = new JMenuItem("Join Server");
		joinOption.addActionListener((ActionEvent event) ->
		{
			//safeClose();
			messageQueue.push(new String[]{"join"});
		});

		NewGameMenu.add(hostOption);
		NewGameMenu.add(joinOption);
		menubar.add(NewGameMenu);

		menubar.add(divider1);

		JTextField statusDescription = new JTextField("", 5);
		statusDescription.setMaximumSize(statusDescription.getPreferredSize());
		statusDescription.setEditable(false);
		menubar.add(statusDescription);
		
		JTextField statusText = new JTextField("", 25);
		statusText.setMaximumSize(statusText.getPreferredSize());
		statusText.setEditable(false);
		menubar.add(statusText);

		menubar.add(divider2);

		JButton confirmButton = new JButton("confirm");
		confirmButton.setEnabled(false);
		menubar.add(confirmButton);

		f.setJMenuBar(menubar);

		// Ensures JVM closes after frame(s) closed and
		// all non-daemon threads are finished
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		//See https://stackoverflow.com/questions/16372241/run-function-on-jframe-close
		f.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent event)
			{
				messageQueue.push(new String[]{"exit"});
				System.exit(0);
			}
		});
		// See https://stackoverflow.com/a/7143398/418556 for demo.
		f.setLocationByPlatform(true);
		f.setResizable(false); // don't allow resizing

		// ensures the frame is the minimum size it needs to be
		// in order display the components within it
		f.pack();
		// ensures the minimum size is enforced.
		f.setMinimumSize(f.getSize());
		f.setVisible(true);
	}
	
	public GUIPanel getPanel()
	{
		return guiPanel;
	}
}
