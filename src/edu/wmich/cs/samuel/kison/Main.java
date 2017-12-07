package edu.wmich.cs.samuel.kison;

import edu.wmich.cs.samuel.kison.Client.Client;
import edu.wmich.cs.samuel.kison.Server.ServerLoop;

/**
 * This is the starting point for the program. It simply starts up a new 'Client', as well as a 'ServerLoop' running on its own thread. <br>
 * <br>
 * Pass in 'debug' as an arg to print debug messages to the console <br>
 * Pass in 'squareSize=x' as an arg where x is an integer (preferably less than 64) that defines the resolution of every square image in the game. Useful for
 * when your screen is too small to support the default 64x64 size images. <br>
 * <br>
 * Example startup on Windows 10 cmd: 'java -jar BattleshipGame.jar debug squareSize=32' will print out all messages to the command prompt, as well as
 * significantly reduce the size of the game's frame.
 * 
 * @author Samuel Kison
 *
 */
public class Main
{
	public static boolean debug = false; //boolean that determines if print statements should happen
	public static int squareSize = 64; //resolution of every square/tile in the game; pass in the arg squareSize=x to change the resolution (good for small screens)

	public static void main(String[] args)
	{
		if (args.length > 0)
		{
			System.out.println("Input on startup: ");
			for (int i = 0; i < args.length; i++)
			{
				System.out.println("> " + args[i]);
				if (args[i].equalsIgnoreCase("debug"))
				{
					debug = true;
				}
				else if (args[i].startsWith("squareSize"))
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
		new Client(clientOutput, serverOutput);

		ServerLoop gameLoop = new ServerLoop(clientOutput, serverOutput);
		Thread gameLoopThread = new Thread(gameLoop);
		gameLoopThread.start();
	}
}
