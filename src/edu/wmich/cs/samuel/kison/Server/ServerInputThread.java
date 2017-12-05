package edu.wmich.cs.samuel.kison.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import edu.wmich.cs.samuel.kison.MessageQueue;

/**
 * Thread that continuously reads a MessageQueue object from a constructed Socket in ServerTCP class. 
 * 
 * @author alan_
 *
 */
public class ServerInputThread extends Thread {
	Socket socket;
	ObjectInputStream ois;
	ObjectOutputStream oos; 
	
	int id; // used to differentiate each thread, possibly won't be needed
	String username; // 
	MessageQueue queue;
	
	public ServerInputThread(Socket pSocket, MessageQueue pQueue) {
		this.socket = pSocket;
		this.queue = pQueue;
		System.out.println("ServerToClientThread: Thread is about to create Object Input and Output Streams (ois/oos)...");
		try
		{
			this.oos = new ObjectOutputStream(pSocket.getOutputStream());
			this.ois = new ObjectInputStream(pSocket.getInputStream());
			
			System.out.println("ServerToClientThread: Hanging on reading username from Client...");
			this.username = (String) ois.readObject();
			System.out.println("ServerToClientThread: " + this.username + " has been connected!");
		} 
		catch (IOException e) 
		{
			System.out.println("ServerToClientThread: Exception while creating ois/oos and reading Username");
			return;
		}
		catch (ClassNotFoundException e2) { // not much to do here.... will almost never happen
			}
		
	}
	
	public void run() {
		boolean keepGoing = true;
		while(keepGoing) 
		{
			try
			{
				System.out.println("ServerToClientThread: Hanging at readObject for String[]...");
				this.queue.push((String[]) ois.readObject());
				System.out.println("ServerToClientThread: Successfully readObject for String[]...\nContents: " + this.queue.toString());
			}
			catch (IOException e) 
			{
				System.out.println("ServerToClientThread: Exception on reading String[] object!!!!");
				break;
			}
			catch (ClassNotFoundException e2)
			{
				break; 
			}
		}
	}
	
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
