package edu.wmich.cs.samuel.kison;

import edu.wmich.cs.samuel.kison.Client.Client;
import edu.wmich.cs.samuel.kison.Server.ServerLoop;

/*Start the main loop*/

public class Main
{
	public static boolean debug = false; //boolean that determines if print statements should happen
	public static int squareSize = 64; //resolution of every square/tile in the game; pass in the arg squareSize=x to change the resolution (good for small screens)
	
	public static void main(String[] args)
	{
		if(args.length > 0)
		{
			System.out.println("Input on startup: ");
			for(int i = 0; i < args.length; i++)
			{
				System.out.println("> " + args[i]);
				if(args[i].equalsIgnoreCase("debug"))
				{
					debug = true;
				}
				else if(args[i].startsWith("squareSize"))
				{
					String sizeString = args[i].substring(11);
					System.out.println("New squareSize: " + sizeString);
					squareSize = Integer.parseInt(sizeString);
				}
			}
		}
		//Load up the images for use within GUIs
		ImageHolder.loadImages();
		
		//Create messageQueues used to communicate between the client & server
		MessageQueue clientOutput = new MessageQueue();
		MessageQueue serverOutput = new MessageQueue();
		
		//Create client & server
		Client client = new Client(clientOutput, serverOutput);

		ServerLoop gameLoop = new ServerLoop(clientOutput, serverOutput);
		Thread gameLoopThread = new Thread(gameLoop);
		gameLoopThread.start();
	}
}
