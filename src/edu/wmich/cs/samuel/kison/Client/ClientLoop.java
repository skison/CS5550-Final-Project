package edu.wmich.cs.samuel.kison.Client;

import edu.wmich.cs.samuel.kison.MessageQueue;

public class ClientLoop implements Runnable
{
	private Client client; // reference to Client so messages can be instantly interpreted by it
	private MessageQueue clientInput; // message queue from client (forwarded from GUI)
	private MessageQueue clientOutput; // message queue from here to server
	private MessageQueue serverOutput; // message queue from server to here

	int UPS = 4; // Just a few updates per second should be good enough for this program
	boolean running = true;

	public ClientLoop(Client c, MessageQueue newQueue, MessageQueue _clientOutput, MessageQueue _serverOutput)
	{
		client = c;
		clientInput = newQueue;
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
		if (!clientInput.isEmpty())
		{
			while (!clientInput.isEmpty())
			{
				interpretClientMessage(clientInput.pop());
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

	// When getting a new message from the Client, interpret what to do here
	private void interpretClientMessage(String[] message)
	{
		System.out.print("ClientLoop: New Output from Client:");
		for (int i = 0; i < message.length; i++)
		{
			System.out.print(" " + message[i]);
		}
		System.out.println(".");
		
		if(message[0].equals("confirm_host") || message[0].equals("cancel_load") || message[0].equals("button") || message[0].equals("quit"))
		{
			clientOutput.push(message); //forward message to serverloop
		}
		else if(message[0].equals("exit"))
		{
			return;//close this thread
		}
	}

	// When getting a new message from the server, interpret what to do here
	private void interpretServerMessage(String[] message)
	{
		System.out.print("ClientLoop: New Output from ServerLoop:");
		for (int i = 0; i < message.length; i++)
		{
			System.out.print(" " + message[i]);
		}
		System.out.println(".");
		
		//Message to be sent to Client (if necessary)
		String[] connectMessage = new String[message.length + 1]; //create new message with one extra spot for client identifier
		System.arraycopy(message, 0, connectMessage, 1, message.length);
		connectMessage[0] = "client";

		if(message[0].equals("enemy_connected") || message[0].equals("valid_placement") || message[0].equals("invalid_placement")
				|| message[0].equals("all_ships_placed") || message[0].equals("your_ships_placed"))
		{
			client.receiveMessage(connectMessage);
		}
	}
}
