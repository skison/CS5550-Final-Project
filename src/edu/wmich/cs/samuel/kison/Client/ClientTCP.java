package edu.wmich.cs.samuel.kison.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import edu.wmich.cs.samuel.kison.MessageQueue;

public class ClientTCP {
	int port;
	String hostIP;
	String username;
	Socket socket;
	MessageQueue queue;
	
	ObjectInputStream ois;
	ObjectOutputStream oos;
	
	public ClientTCP(int pPort, MessageQueue pQueue, String pIP, String pUsername) {
		this.port = pPort;
		this.queue = pQueue;
		this.hostIP = pIP;
		this.username = pUsername;
	}
	
	/**
	 * Method that will 1) start new socket on defined host IP and port number
	 * 					2) create Object Input/Output streams
	 * 					3) starts ClientToServerThread to continuously read from Server
	 * 					4) send our username to the server
	 * If no exceptions are caught, then the method returns true. Otherwise, false will be returned. 
	 * 
	 * @return false if any exceptions are caught while creating socket, creating OIS/OOS, and sending username. Returns true otherwise. 
	 */
	public boolean start() {
		// start socket on port
		try
		{
			this.socket = new Socket(this.hostIP, this.port);
		}
		catch(Exception e) 
		{
			System.out.println("ClientTCP: Exception caught at creating Socket at IP:" + this.hostIP + ", and port: " + this.port);
			return false;
		}
		System.out.println("ClientTCP: Connection accepted! IP from socket: " + this.socket.getInetAddress() + ", Port from socket: " + this.socket.getPort());
		
		// create Object Input/Output Streams 
		try
		{
			this.ois = new ObjectInputStream(this.socket.getInputStream());
			this.oos = new ObjectOutputStream(this.socket.getOutputStream());
		}
		catch(IOException e)
		{
			System.out.println("ClientTCP: Exception caught while creating ObjectInputStream and ObjectOutputStream");
			return false;
		}
		System.out.println("ClientTCP: ois and oos have been initialized");
		
		//start thread
		ClientToServerThread c = new ClientToServerThread(ois, queue);
		c.start();
		System.out.println("ClientTCP: ClientToServerThread started");
				
		// send username to the server to confirm OOS works
		try
		{
			this.oos.writeObject(this.username);
		}
		catch(IOException e)
		{
			System.out.println("ClientTCP: Exception caught while writing username to socket");
		}
		System.out.println("ClientTCP: Sent username to server, all tests have passed!! returning true from start()");
		
		// if all three steps were successful, then client is successfully connected, return true
		return true;
	}
	
	/**
	 * Method that writes the pQueue to the Socket using ObjectOutputStream.writeObject
	 * 
	 * @param pQueue MessageQueue object
	 */
	public void send(MessageQueue pQueue) {
		try 
		{
			this.oos.writeObject(pQueue);
		} 
		catch (IOException e) 
		{
			System.out.println("ClientTCP: Exception caught during send(MessageQueue)");
		}
	}
	
	/**
	 * Closes all object streams, and socket. <p><b>Catches all exceptions but none are not handled. 
	 */
	public void close() {
		try {
			if(this.oos != null) this.oos.close();
		}
		catch(Exception e) {}
		try {
			if(this.ois != null) this.ois.close();
		}
		catch(Exception e) {};
		try {
			if(this.socket != null) this.socket.close();
		}
		catch (Exception e) {}
	}
}
