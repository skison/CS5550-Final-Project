package edu.wmich.cs.samuel.kison.Client;

import java.io.IOException;
import java.io.ObjectInputStream;

import edu.wmich.cs.samuel.kison.Main;
import edu.wmich.cs.samuel.kison.MessageQueue;

/**
 * Thread that will continuously read a String[] from the ClientTCP socket from an external server; whenever a new String[] is read, it will push to the
 * MessageQueue read by ClientLoop every tick. <br>
 * <br>
 * Code reference (for setting up TCP socket connections): http://www.dreamincode.net/forums/topic/259777-a-simple-chat-program-with-clientserver-gui-optional/
 * 
 * @author Alan Alvarez
 *
 */
public class ClientInputThread extends Thread
{

	ObjectInputStream ois;
	MessageQueue queue;

	/**
	 * Initialize the ObjectInputStream and Message Queue
	 * 
	 * @param pOis
	 *            ObjectInputStream
	 * @param pQueue
	 *            MessageQueue read by ClientLoop whenever a new message is read in from the server
	 */
	public ClientInputThread(ObjectInputStream pOis, MessageQueue pQueue)
	{
		this.ois = pOis;
		this.queue = pQueue;
	}

	public void run()
	{
		while (true)
		{
			// always read in a String[] from Server to Client
			try
			{
				if (Main.debug)
					System.out.println("ClientInputThread: Hanging on reading String[] object...");
				String[] newMessage = (String[]) this.ois.readObject();
				this.queue.push(newMessage);
				if (Main.debug)
					System.out.println(
							"ClientInputThread: Successfully readObject for String[]...\nContent: " + newMessage[0]);
			}
			catch (IOException e)
			{
				if (Main.debug)
					System.out.println("ClientInputThread: Exception on reading String[] object!!!!");
				this.queue.push(new String[] { "client_quit" }); //let ClientLoop know that host has essentially 'quit' (disconnected)
				break;
			}
			catch (ClassNotFoundException e2)
			{
				break;
			}
		}
	}
}
