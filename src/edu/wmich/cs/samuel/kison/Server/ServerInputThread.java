package edu.wmich.cs.samuel.kison.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import edu.wmich.cs.samuel.kison.Main;
import edu.wmich.cs.samuel.kison.MessageQueue;

/**
 * Thread that will continuously read a String[] from the ServerTCP socket from an external client; whenever a new String[] is read, it will push to the
 * MessageQueue read by ServerLoop every tick. <br>
 * <br>
 * Code reference (for setting up TCP socket connections): http://www.dreamincode.net/forums/topic/259777-a-simple-chat-program-with-clientserver-gui-optional/
 * 
 * @author Alan Alvarez
 *
 */
public class ServerInputThread extends Thread
{
	Socket socket;
	ObjectInputStream ois;
	ObjectOutputStream oos;

	int id; //used to differentiate each thread, possibly won't be needed
	String username; //external client's username when first connecting
	MessageQueue queue; //queue for pushing read messages for ServerLoop to read when it ticks

	/**
	 * Initialize the socket and MessageQueue for communication
	 * 
	 * @param pSocket
	 *            socket to read from
	 * @param pQueue
	 *            MessageQueue to push messages to once they have been read from the external client
	 */
	public ServerInputThread(Socket pSocket, MessageQueue pQueue)
	{
		this.socket = pSocket;
		this.queue = pQueue;
		if (Main.debug)
			System.out.println(
					"ServerInputThread: Thread is about to create Object Input and Output Streams (ois/oos)...");
		try
		{
			this.oos = new ObjectOutputStream(pSocket.getOutputStream());
			this.ois = new ObjectInputStream(pSocket.getInputStream());

			if (Main.debug)
				System.out.println("ServerInputThread: Hanging on reading username from Client...");
			this.username = (String) ois.readObject();
			if (Main.debug)
				System.out.println("ServerInputThread: " + this.username + " has been connected!");
		}
		catch (IOException e)
		{
			if (Main.debug)
				System.out.println("ServerInputThread: IOException while creating ois/oos and reading Username");
			return;
		}
		catch (ClassNotFoundException e2)
		{
			if (Main.debug)
				System.out.println(
						"ServerInputThread: ClassNotFoundException while creating ois/oos and reading Username");
		}

	}

	public void run()
	{
		boolean keepGoing = true;
		while (keepGoing)
		{
			try
			{
				if (Main.debug)
					System.out.println("ServerInputThread: Hanging at readObject for String[]...");
				this.queue.push((String[]) ois.readObject());
				if (Main.debug)
					System.out.println("ServerInputThread: Successfully readObject for String[]...\nContents: "
							+ this.queue.toString());
			}
			catch (IOException e)
			{
				if (Main.debug)
					System.out.println("ServerInputThread: IOException on reading String[] object!!!!");
				this.queue.push(new String[] { "quit" }); //let ServerLoop know that client has essentially 'quit' (disconnected)
				break; //exit loop
			}
			catch (ClassNotFoundException e2)
			{
				if (Main.debug)
					System.out.println("ServerInputThread: ClassNotFoundException on reading String[] object!!!!");
				break; //exit loop
			}
		}
	}

	/**
	 * Close everything in this thread
	 */
	public void close()
	{
		try
		{
			if (this.oos != null)
				this.oos.close();
		}
		catch (Exception e)
		{
			if (Main.debug)
				System.out.println("ServerInputThread: Exception caught during closing of ObjectOutputStream!");
		}
		try
		{
			if (this.ois != null)
				this.ois.close();
		}
		catch (Exception e)
		{
			if (Main.debug)
				System.out.println("ServerInputThread: Exception caught during closing of ObjectInputStream!");
		}
		try
		{
			if (this.socket != null)
				this.socket.close();
		}
		catch (Exception e)
		{
			if (Main.debug)
				System.out.println("ServerInputThread: Exception caught during closing of socket!");
		}
	}
}
