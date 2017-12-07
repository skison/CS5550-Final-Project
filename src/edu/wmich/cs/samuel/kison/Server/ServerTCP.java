package edu.wmich.cs.samuel.kison.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import edu.wmich.cs.samuel.kison.Main;
import edu.wmich.cs.samuel.kison.MessageQueue;

/**
 * This class has the ability to send a String[] to external <i>client</i> with <b>send(String[])</b>. This class also creates a 'ServerInputThread' that always
 * reads a String[] from an external Server and updates a MessageQueue read by the ClientLoop. <br>
 * <br>
 * Code reference (for setting up TCP socket connections): http://www.dreamincode.net/forums/topic/259777-a-simple-chat-program-with-clientserver-gui-optional/
 * 
 * @author Alan Alvarez
 *
 */
public class ServerTCP implements Runnable
{
	MessageQueue queue; //defined by serverLoop; push messages to it for it to read
	int port;
	ServerSocket serverSocket;
	ServerInputThread serverThread;
	Socket socket;

	/**
	 * Initialize the ServerTCP Object
	 * 
	 * @param pPort
	 *            port number to open the ServerSocket to
	 * @param pQueue
	 *            the MessageQueue that ServerInputThread will write out to, which will be read by ServerLoop
	 */
	public ServerTCP(int pPort, MessageQueue pQueue)
	{
		this.queue = pQueue;
		this.port = pPort;
	}

	/**
	 * Close everything!
	 */
	public void close()
	{
		if (Main.debug)
			System.out.println("ServerTCP: closing everything");
		try
		{

			this.serverSocket.close();
			this.serverThread.ois.close();
			this.serverThread.oos.close();
			this.serverThread.socket.close();
		}
		catch (IOException e)
		{
			if (Main.debug)
				System.out.println("ServerTCP: IOException on stop()...");
		}
		catch (NullPointerException e)
		{
			if (Main.debug)
				System.out.println("ServerTCP: Unable to close socket/threads, must not have been open");
		}
	}

	/**
	 * Method that writes a message to the ServerSocket using ObjectOutputStream.writeObject
	 * 
	 * @param pMessageToSend
	 *            String[] message to be read by the client
	 */
	public void send(String[] pMessageToSend)
	{
		try
		{
			this.serverThread.oos.writeObject(pMessageToSend);
		}
		catch (IOException e)
		{
			if (Main.debug)
				System.out.println("ServerTCP: Exception during send(String[]): " + e + "\n");
		}
	}

	@Override
	public void run()
	{
		try
		{
			this.serverSocket = new ServerSocket(this.port);
			if (Main.debug)
				System.out.println("ServerTCP: Waiting for Client to connect on port: " + this.port);
			this.socket = serverSocket.accept();

			if (Main.debug)
				System.out.println("ServerTCP: Client has connected!");
			this.serverThread = new ServerInputThread(socket, this.queue);
			serverThread.start();
		}
		catch (IOException e)// could not establish connection
		{
			if (Main.debug)
				System.out.println("ServerTCP: Could not establish connection on new ServerSocket: " + e + "\n");
		}
	}

}
