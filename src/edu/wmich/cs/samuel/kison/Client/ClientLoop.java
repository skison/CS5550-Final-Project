package edu.wmich.cs.samuel.kison.Client;

import edu.wmich.cs.samuel.kison.MessageQueue;

public class ClientLoop implements Runnable
{
	private Client client; // reference to Client so messages can be instantly interpreted by it
	private MessageQueue clientInput; // message queue from client (forwarded from GUI)
	
	//for client that is also hosting the game
	private MessageQueue internalClientOutput; // internal message queue from here to server
	private MessageQueue internalServerOutput; // internal message queue from server to here
	
	//for client that is joining an existing game
	private MessageQueue externalClientOutput; // external message queue from here to server
	private MessageQueue externalServerOutput; // external message queue from server to here

	int UPS = 4; // Just a few updates per second should be good enough for this program
	boolean running = true;
	boolean connectedToExternalServer = false;
	String[] message;
	
	ClientTCP clientTCP;

	public ClientLoop(Client c, MessageQueue newQueue, MessageQueue _internalClientOutput, MessageQueue _internalServerOutput)
	{
		client = c;
		clientInput = newQueue;
		internalClientOutput = _internalClientOutput;
		internalServerOutput = _internalServerOutput;
		
		this.externalClientOutput = new MessageQueue();
		this.externalServerOutput = new MessageQueue();
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
				//System.out.println("Client tick");
				
				checkInput();// check input messages from gui and server (and interpret them)
				
				sendOutput();
				ticks++;
				deltaU--;
			}
		}
	}

	/**
	 * Only used if server is external. Otherwise messages are simply pushed to local internal queues that are interpreted directly by ServerLoop
	 */
	private void sendOutput() {
		//check if externalClientOutput has a new message
		
		while(!this.externalClientOutput.isEmpty())
		{
			System.out.println("ClientLoop: External Client Output queue has something in it. Must send to server");
			this.message = this.externalClientOutput.pop();
			for (int i = 0; i < this.message.length; i++)
			{
				System.out.print(i+ ":" + this.message[i] + " ");
			}
			System.out.println(".");
			
			
			this.clientTCP.send(this.message); //now the external server will receive all messages
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

		if (!internalServerOutput.isEmpty())
		{
			while (!internalServerOutput.isEmpty())
			{
				interpretServerMessage(internalServerOutput.pop());
			}
		}
		
		if (!externalServerOutput.isEmpty())
		{
			while (!externalServerOutput.isEmpty())
			{
				interpretServerMessage(externalServerOutput.pop());
			}
		}
	}

	// When getting a new message from the Client, interpret what to do here
	private void interpretClientMessage(String[] pMessage)
	{
		System.out.print("ClientLoop: Connected to server?: " + this.connectedToExternalServer + "\nClientLoop: New Output from Client:");
		for (int i = 0; i < pMessage.length; i++)
		{
			System.out.print(" " + pMessage[i]);
		}
		System.out.println(".");
		
		if(!connectedToExternalServer) //internal server
		{
			if(pMessage[0].equals("confirm_host") 
					|| pMessage[0].equals("cancel_load") 
					|| pMessage[0].equals("button") 
					|| pMessage[0].equals("quit")
					|| pMessage[0].equals("cancel_join"))
			{
				System.out.println("ClientLoop: Pushing message to internalClientOutput... going up to ServerLoop...");
				internalClientOutput.push(pMessage); //forward message to serverloop
			}
			else if(pMessage[0].equals("confirm_join") || pMessage[0].equals("retry_join"))
			{
				//overwrite pMessage[0] to "confirm_join" just in case it == "retry_join"
				pMessage[0] = "confirm_join";
				String hostIP = pMessage[1];
				int port = Integer.parseInt(pMessage[2]);
				String username = pMessage[3];
				this.clientTCP = new ClientTCP(port, hostIP, this.externalServerOutput, username);
				if (this.clientTCP.start())
				{
					System.out.println("ClientLoop: Client has successfully connected!");
					this.clientTCP.send(pMessage);
					connectedToExternalServer = true; //now send messages to the external server instead
				}
				else
				{
					System.out.println("ClientLoop: Unable to connect!");
					client.receiveMessage(new String[] {"client", "join_failed"}); //tell Client that the join failed
				}
			}
			else if(pMessage[0].equals("exit"))
			{
				return;//close this thread
			}
		}
		else //external server
		{
			if(pMessage[0].equals("button"))
			{
				externalClientOutput.push(pMessage); //forward message to serverloop
			}
			else if(pMessage[0].equals("cancel_load") 
					|| pMessage[0].equals("quit"))
			{
				externalClientOutput.push(pMessage); //forward message to serverloop
				connectedToExternalServer = false; //now send messages to the internal server
				this.clientTCP.close();
			}
			else if(pMessage[0].equals("exit"))
			{
				return;//close this thread
			}
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
