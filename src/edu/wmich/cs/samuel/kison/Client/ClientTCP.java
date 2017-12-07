package edu.wmich.cs.samuel.kison.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import edu.wmich.cs.samuel.kison.Main;
import edu.wmich.cs.samuel.kison.MessageQueue;

/**
 * This class has the ability to send a String[] to external <i>host</i> with <b>send(String[])</b>. This class also creates a 'ClientInputThread' that always
 * reads a String[] from an external Server and updates a MessageQueue read by the ClientLoop. <br>
 * <br>
 * Code reference (for setting up TCP socket connections): http://www.dreamincode.net/forums/topic/259777-a-simple-chat-program-with-clientserver-gui-optional/
 * 
 * @author Alan Alvarez
 *
 */
public class ClientTCP
{
	int port;
	String hostIP;
	String username;
	Socket socket;
	MessageQueue serverToClientQueue;

	ObjectInputStream ois;
	ObjectOutputStream oos;

	/**
	 * Initialize this ClientTCP object
	 * 
	 * @param pPort
	 *            port number to open up the socket to
	 * @param pIP
	 *            IP address to open up the socket to
	 * @param pQueue
	 *            the MessageQueue that ClientInputThread will write out to, which will be read by ClientLoop
	 * @param pUsername
	 *            username passed to the server when connecting for the first time, so that both players can see both names
	 */
	public ClientTCP(int pPort, String pIP, MessageQueue pQueue, String pUsername)
	{
		this.port = pPort;
		this.hostIP = pIP;
		this.serverToClientQueue = pQueue;
		this.username = pUsername;
	}

	/**
	 * Method that will 1) start new socket on defined host IP and port number 2) create Object Input/Output streams 3) starts ClientInputThread to continuously
	 * read from Server 4) send our username to the server If no exceptions are caught, then the method returns true. Otherwise, false will be returned.
	 * 
	 * @return false if any exceptions are caught while creating socket, creating OIS/OOS, and sending username. Returns true otherwise.
	 */
	public boolean start()
	{
		// start socket on port
		try
		{
			this.socket = new Socket(this.hostIP, this.port);
		}
		catch (Exception e)
		{
			if (Main.debug)
				System.out.println("ClientTCP: Exception caught at creating Socket at IP:" + this.hostIP
						+ ", and port: " + this.port);
			return false;
		}
		if (Main.debug)
			System.out.println("ClientTCP: Connection accepted! IP from socket: " + this.socket.getInetAddress()
					+ ", Port from socket: " + this.socket.getPort());

		// create Object Input/Output Streams 
		try
		{
			this.ois = new ObjectInputStream(this.socket.getInputStream());
			this.oos = new ObjectOutputStream(this.socket.getOutputStream());
		}
		catch (IOException e)
		{
			if (Main.debug)
				System.out
						.println("ClientTCP: Exception caught while creating ObjectInputStream and ObjectOutputStream");
			return false;
		}
		if (Main.debug)
			System.out.println("ClientTCP: ois and oos have been initialized");

		//start thread
		ClientInputThread c = new ClientInputThread(ois, this.serverToClientQueue);
		c.start();
		if (Main.debug)
			System.out.println("ClientTCP: ClientToServerThread started");

		// send username to the server to confirm OOS works
		try
		{
			this.oos.writeObject(this.username);
		}
		catch (IOException e)
		{
			if (Main.debug)
				System.out.println("ClientTCP: IOException caught while writing username to socket");
		}
		if (Main.debug)
			System.out
					.println("ClientTCP: Sent username to server, all tests have passed!! returning true from start()");

		// if all three steps were successful, then client is successfully connected, return true
		return true;
	}

	/**
	 * Method that writes a message to the Socket using ObjectOutputStream.writeObject
	 * 
	 * @param pMessage
	 *            String[] message to be read by the server
	 */
	public void send(String[] pMessage)
	{
		try
		{
			this.oos.writeObject(pMessage);
		}
		catch (IOException e)
		{
			if (Main.debug)
				System.out.println("ClientTCP: IOException caught during send(Message)");
		}
	}

	/**
	 * Closes all object streams, and socket.
	 * <p>
	 * <b>Catches all exceptions but none are not handled.
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
				System.out.println("ClientTCP: Exception caught during closing of ObjectOutputStream!");
		}
		try
		{
			if (this.ois != null)
				this.ois.close();
		}
		catch (Exception e)
		{
			if (Main.debug)
				System.out.println("ClientTCP: Exception caught during closing of ObjectInputStream!");
		}
		try
		{
			if (this.socket != null)
				this.socket.close();
		}
		catch (Exception e)
		{
			if (Main.debug)
				System.out.println("ClientTCP: Exception caught during closing of socket!");
		}
	}
}
