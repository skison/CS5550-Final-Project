package edu.wmich.cs.samuel.kison.Server;

import edu.wmich.cs.samuel.kison.MessageQueue;

//Code taken from https://stackoverflow.com/questions/18283199/java-main-game-loop

public class ServerLoop implements Runnable
{
	int UPS = 4; // Just a few updates per second should be good enough for this program
	boolean running = true;
	boolean RENDER_TIME = false;

	// The 2 input queues for each player
	private MessageQueue player1Input;
	// InputQueue player2Input = null;

	// The 2 output queues for each player
	private MessageQueue player1Output;
	// OutputQueue player2Output = null;

	public ServerLoop(MessageQueue client1Input, MessageQueue client1Output)
	{
		// connect player 1 to server
		player1Input = client1Input;
		player1Output = client1Output;
	}

	@Override
	public void run()
	{
		System.out.println("Server started!");

		// player1Input.empty(); //empty input queue
		// player1Output.empty(); //empty output queue

		long initialTime = System.nanoTime();
		final double timeU = 1000000000 / UPS;
		// final double timeF = 1000000000 / FPS;
		double deltaU = 0;
		int frames = 0, ticks = 0;
		int totalTicks = 0;
		long timer = System.currentTimeMillis();

		while (running)
		{
			long currentTime = System.nanoTime();
			deltaU += (currentTime - initialTime) / timeU;
			// deltaF += (currentTime - initialTime) / timeF;
			initialTime = currentTime;

			if (deltaU >= 1)
			{
				//System.out.println("Server running! " + totalTicks);

				checkInput();
				checkOutput();
				// player1Output.push(new OutputObject("placeship", "destroyer", 0, 0));
				// update();
				ticks++;
				totalTicks++;
				deltaU--;
				// System.out.println("Loop ran at " + deltaU);
			}

			/*
			 * if (deltaF >= 1) { render(); frames++; deltaF--; }
			 */

			if (System.currentTimeMillis() - timer > 1000)
			{
				if (RENDER_TIME)
				{
					System.out.println(String.format("UPS: %s, FPS: %s", ticks, frames));
				}
				frames = 0;
				ticks = 0;
				timer += 1000;
			}
		}
	}

	private void checkInput()
	{
		if (!player1Input.isEmpty())
		{
			//System.out.println("New Input from client 1 to server!");
			while (!player1Input.isEmpty())
			{
				interpretPlayerMessage(true, player1Input.pop());
				/*
				 * InputObject tempIn = player1Input.pop();
				 * System.out.println(tempIn.toString()); OutputObject tempOut = new
				 * OutputObject("confirm", "Destroyer", tempIn.getRow(), tempIn.getColumn());
				 * player1Output.push(tempOut);
				 */
			}
		}
		else
		{
			// System.out.println("Nothing");
		}
	}

	private void checkOutput()
	{
		/*
		 * if(!player1Output.isEmpty()) { System.out.println("New Output!");
		 * 
		 * while(!player1Output.isEmpty()) {
		 * System.out.println(player1Output.pop().toString()); } }
		 */
	}

	// TODO: make this work with TCP instead
	/*
	 * public void connect(InputQueue newQueue) { player2Input = newQueue; //connect
	 * player 2 to server }
	 */

	// When getting a new message from the server, interpret what to do here
	private void interpretPlayerMessage(boolean player, String[] message)
	{
		if (player)//Player 1
		{
			//System.out.print("New Input from player 1:");

			for (int i = 0; i < message.length; i++)
			{
				//System.out.print(" " + message[i]);
				switch (message[0])
				{
					case ("exit"):
						return;// close this thread
					case ("join"):
						break;
					case ("host"):
						break;
					default:
						break;
				}
			}

			//System.out.println(".");

			// tell server what we got (for testing!)
			// serverOutput.push(message);
		}

	}
}
