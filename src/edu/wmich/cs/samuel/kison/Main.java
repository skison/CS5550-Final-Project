package edu.wmich.cs.samuel.kison;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextField;

import edu.wmich.cs.samuel.kison.Client.Client;
import edu.wmich.cs.samuel.kison.Client.GUIPanel;
import edu.wmich.cs.samuel.kison.Server.Server;
import edu.wmich.cs.samuel.kison.Server.ServerLoop;

/*Start the main loop*/

public class Main
{
	public static void main(String[] args)
	{
		//Load up the images for use within GUIs
		ImageHolder.loadImages();
		
		//Create messageQueues used to communicate between the client & server
		MessageQueue clientOutput = new MessageQueue();
		MessageQueue serverOutput = new MessageQueue();
		
		//Create client & server
		Client client = new Client(clientOutput, serverOutput);
		Server server = new Server(clientOutput, serverOutput);

	}
}
