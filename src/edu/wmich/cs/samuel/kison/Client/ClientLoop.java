package edu.wmich.cs.samuel.kison.Client;

import edu.wmich.cs.samuel.kison.Main;
import edu.wmich.cs.samuel.kison.MessageQueue;

/**
 * This class is controlled by a Client object; it can be thought of as part of the 'C' of the MVC structure for the client. Its job is to communicate to and
 * from the current game server, whether it's internal or external. Therefore it needs to forward messages from the Client's MessageQueue either to the internal
 * or external server message queues. <br>
 * <br>
 * If this ClientLoop is currently connected to the local server, then messages will simply be pushed to the internalClientOutput queue, where they will be read
 * by the internal ServerLoop during its next tick. However, if this ClientLoop is connected to an external server, then messages will be pushed to the
 * externalClientOutput, which will then be sent to the ClientTCP object, which will then forward them to the server over TCP. Similarly, if the server is
 * internal, then messages from the server will be read immediately here by the internalServerOutput queue, but if the server is external, then they will be
 * pushed to the externalServerOutput queue by ClientTCP's ClientInputThread object. <br>
 * <br>
 * Code reference (for running a simple game loop): https://stackoverflow.com/questions/18283199/java-main-game-loop
 * 
 * @author Samuel Kison
 *
 */
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

	/**
	 * Initialize the ClientLoop object!
	 * 
	 * @param c
	 *            the Client object that is in control of this ClientLoop (necessary to send messages up to it)
	 * @param newQueue
	 *            MessageQueue that the Client object will push messages to, which will be read & interpreted by this ClientLoop
	 * @param _internalClientOutput
	 *            MessageQueue used to push messages from here to the local server (if connected internally)
	 * @param _internalServerOutput
	 *            MessageQueue used to pop messages from the local server (if connected internally)
	 */
	public ClientLoop(Client c, MessageQueue newQueue, MessageQueue _internalClientOutput,
			MessageQueue _internalServerOutput)
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

		while (running)
		{
			long currentTime = System.nanoTime();
			deltaU += (currentTime - initialTime) / timeU;

			if (deltaU >= 1) //the clock of the loop
			{
				checkInput(); //check input messages from gui and server (and interpret them)
				sendOutput(); //send output to ClientTCP if connected to external server
				deltaU--; //decrement clock(Note that in a larger game, this could potentially be dangerous- it should be set to 0 if it grows too large which might happen if the system sleeps)
			}
		}
	}

	/**
	 * Send messages to the ClientTCP object- only used if server is external. Otherwise messages are simply pushed to local internal queues that are
	 * interpreted directly by ServerLoop
	 */
	private void sendOutput()
	{
		//check if externalClientOutput has a new message
		while (!this.externalClientOutput.isEmpty())
		{
			this.message = this.externalClientOutput.pop();

			if (Main.debug)
			{
				System.out.println("ClientLoop: External Client Output queue has something in it. Must send to server");
				for (int i = 0; i < this.message.length; i++)
				{
					System.out.print(i + ":" + this.message[i] + " ");
				}
				System.out.println(".");
			}

			this.clientTCP.send(this.message); //now the external server will receive all messages
		}
	}

	/**
	 * Check MessageQueues from the Client object and the server (either internal or external)
	 */
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

	/**
	 * When getting a new message from the Client, interpret what to do here
	 * 
	 * @param pMessage
	 *            the message received from the Client object
	 */
	private void interpretClientMessage(String[] pMessage)
	{
		if (Main.debug)
		{
			System.out.print("ClientLoop: Connected to server?: " + this.connectedToExternalServer
					+ "\nClientLoop: New Output from Client:");
			for (int i = 0; i < pMessage.length; i++)
			{
				System.out.print(" " + pMessage[i]);
			}
			System.out.println(".");
		}

		if (!connectedToExternalServer) //not currently connected to external server
		{
			if (pMessage[0].equals("confirm_host") || pMessage[0].equals("cancel_load") || pMessage[0].equals("button")
					|| pMessage[0].equals("quit") || pMessage[0].equals("cancel_join") || pMessage[0].equals("rematch"))
			{
				if (Main.debug)
					System.out.println(
							"ClientLoop: Pushing message to internalClientOutput... going up to ServerLoop...");
				internalClientOutput.push(pMessage); //just forward message to serverloop
			}
			else if (pMessage[0].equals("confirm_join") || pMessage[0].equals("retry_join"))
			{
				pMessage[0] = "confirm_join"; //overwrite pMessage[0] to "confirm_join" just in case it == "retry_join", because only 'confirm_join' is recognized by the server
				String hostIP = pMessage[1]; //get host IP address
				int port = Integer.parseInt(pMessage[2]); //get host port number
				String username = pMessage[3]; //get username to send
				this.clientTCP = new ClientTCP(port, hostIP, this.externalServerOutput, username); //create a new clientTCP object
				if (this.clientTCP.start())//successful connection to server
				{
					if (Main.debug)
						System.out.println("ClientLoop: Client has successfully connected!");
					this.clientTCP.send(pMessage);
					connectedToExternalServer = true; //now send messages to the external server instead
				}
				else //unsuccessful connection
				{
					if (Main.debug)
						System.out.println("ClientLoop: Unable to connect!");
					client.receiveMessage(new String[] { "client", "join_failed" }); //tell Client that the join failed
				}
			}
			else if (pMessage[0].equals("exit"))
			{
				return;//close this thread
			}
		}
		else //currently connected to external server
		{
			if (pMessage[0].equals("button") || pMessage[0].equals("rematch"))
			{
				externalClientOutput.push(pMessage); //simply forward message to serverloop
			}
			else if (pMessage[0].equals("cancel_load") || pMessage[0].equals("quit"))
			{
				externalClientOutput.push(pMessage); //forward message to serverloop
				connectedToExternalServer = false; //now send messages to the internal server
				this.clientTCP.close(); //shut down external server connection
			}
			else if (pMessage[0].equals("exit"))
			{
				return;//close this thread
			}
		}

	}

	/**
	 * When getting a new message from the server, interpret what to do here
	 * 
	 * @param message
	 *            the message sent by the server (either internal or external)
	 */
	private void interpretServerMessage(String[] message)
	{
		if (Main.debug)
		{
			System.out.print("ClientLoop: New Output from ServerLoop:");
			for (int i = 0; i < message.length; i++)
			{
				System.out.print(" " + message[i]);
			}
			System.out.println(".");
		}

		//Message to be sent to Client (if necessary)
		String[] connectMessage = new String[message.length + 1]; //create new message with one extra spot for client identifier
		System.arraycopy(message, 0, connectMessage, 1, message.length);
		connectMessage[0] = "client";

		if (message[0].equals("enemy_connected") || message[0].equals("valid_placement")
				|| message[0].equals("invalid_placement") || message[0].equals("all_ships_placed")
				|| message[0].equals("your_ships_placed") || message[0].equals("player_hit_success")
				|| message[0].equals("player_hit_failure") || message[0].equals("player_ship_sunk")
				|| message[0].equals("game_over") || message[0].equals("request_rematch")
				|| message[0].equals("rematch_accepted"))
		{
			client.receiveMessage(connectMessage);
		}
		else if (message[0].equals("client_quit"))
		{
			connectedToExternalServer = false; //now send messages to the internal server
			try
			{
				this.clientTCP.close(); //shut down external server connection
			}
			catch (NullPointerException e)
			{
				if (Main.debug)
					System.out.println("ClientLoop: clientTCP has already been shut down");
			}

			client.receiveMessage(connectMessage);
		}
	}
}
