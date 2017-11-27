package edu.wmich.cs.samuel.kison.Client;

import edu.wmich.cs.samuel.kison.MessageQueue;

public class ClientLoop implements Runnable
{
	private Client client; // reference to Client so messages can be instantly interpreted by it
	private MessageQueue guiQueue; // message queue from gui
	private MessageQueue clientOutput; // message queue from here to server
	private MessageQueue serverOutput; // message queue from server to here

	int UPS = 4; // Just a few updates per second should be good enough for this program
	boolean running = true;

	public ClientLoop(Client c, MessageQueue newQueue, MessageQueue _clientOutput, MessageQueue _serverOutput)
	{
		client = c;
		guiQueue = newQueue;
		clientOutput = _clientOutput;
		serverOutput = _serverOutput;
	}

	// Loop that checks gui's queue and server's queue (if connected)
	@Override
	public void run()
	{
		long initialTime = System.nanoTime();
		final double timeU = 1000000000 / UPS;
		double deltaU = 0;
		int frames = 0, ticks = 0;
		long timer = System.currentTimeMillis();

		while (running)
		{
			long currentTime = System.nanoTime();
			deltaU += (currentTime - initialTime) / timeU;

			if (deltaU >= 1)
			{
				checkInput();// check input messages from gui and server (and interpret them)

				ticks++;
				deltaU--;
			}
		}
	}

	private void checkInput()
	{
		if (!guiQueue.isEmpty())
		{
			while (!guiQueue.isEmpty())
			{
				interpretGUIMessage(guiQueue.pop());
			}
		}

		if (!serverOutput.isEmpty())
		{
			while (!serverOutput.isEmpty())
			{
				interpretServerMessage(serverOutput.pop());
			}
		}
	}

	// When getting a new message from the GUI, interpret what to do here
	private void interpretGUIMessage(String[] message)
	{
		System.out.print("New Input from gui:");

		for (int i = 0; i < message.length; i++)
		{
			System.out.print(" " + message[i]);
		}

		System.out.println(".");

		/*if (message[0].equals("exit"))
		{
			return;// close this thread
		}
		else
		{
			// tell client what we got (for testing!)
			client.receiveMessage(message);
			//also send message to the server!
			clientOutput.push(message);
		}*/
		
		switch(message[0])
		{
			case("exit"):
				return;// close this thread
			case("join"):
				// tell client what we got (for testing!)
				client.receiveMessage(message);
				break;
			case("host"):
				// tell client what we got (for testing!)
				client.receiveMessage(message);
				//Let server know that it is time to start!
				clientOutput.push(message);
				break;
			default:
				
				//also send message to the server!
				clientOutput.push(message);
				break;
		}
	}

	// When getting a new message from the server, interpret what to do here
	private void interpretServerMessage(String[] message)
	{
		/*System.out.print("New Input from server:");

		for (int i = 0; i < message.length; i++)
		{
			System.out.print(" " + message[i]);
		}

		System.out.println(".");*/
		
		// tell server what we got (for testing!)
		//serverOutput.push(message);
	}
}
