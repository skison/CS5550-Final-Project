package edu.wmich.cs.samuel.kison.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import edu.wmich.cs.samuel.kison.Main;
import edu.wmich.cs.samuel.kison.MessageQueue;

/**
 * This class has the ability to send a String[] to external <i>client</i> with <b>send(String[])</b>. The class always reads a String[] from external Client
 * and updates the shared MessageQueue defined in ServerLoop above.
 * 
 * @author alan_
 *
 */
public class ServerTCP implements Runnable
{
	MessageQueue queue; //defined in ServerLoop above
	int port;
	//private boolean keepGoing;
	ServerSocket serverSocket;
	ServerInputThread serverThread;
	Socket socket;

	public ServerTCP(int pPort, MessageQueue pQueue)
	{
		this.queue = pQueue;
		this.port = pPort;
	}

	/*public void start()
	{
		
	}*/

	public void close()
	{
		if(Main.debug)System.out.println("ServerTCP: closing everything");
		try
		{

			this.serverSocket.close();
			this.serverThread.ois.close();
			this.serverThread.oos.close();
			this.serverThread.socket.close();
		}
		catch (IOException e)
		{
			if(Main.debug)System.out.println("ServerTCP: IOException on stop()...");
		}
		catch (NullPointerException e)
		{
			if(Main.debug)System.out.println("ServerTCP: Unable to close socket/threads, must not have been open");
		}
	}

	//used to send queue messages
	public void send(String[] pMessageToSend)
	{
		try
		{
			this.serverThread.oos.writeObject(pMessageToSend);
		}
		catch (IOException e)
		{
			if(Main.debug)System.out.println("ServerTCP: Exception during send(String[]): " + e + "\n");
		}
	}

	@Override
	public void run()
	{
		//keepGoing = true;
		try
		{
			this.serverSocket = new ServerSocket(this.port);
			if(Main.debug)System.out.println("ServerTCP: Waiting for Client to connect on port: " + this.port);
			this.socket = serverSocket.accept();

			if(Main.debug)System.out.println("ServerTCP: Client has connected!");
			this.serverThread = new ServerInputThread(socket, this.queue);
			serverThread.start();
		}
		// could not establish connection
		catch (IOException e)
		{
			if(Main.debug)System.out.println("ServerTCP: Could not establish connection on new ServerSocket: " + e + "\n");
		}
	}

}
