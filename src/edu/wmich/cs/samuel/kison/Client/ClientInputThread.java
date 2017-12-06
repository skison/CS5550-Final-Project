package edu.wmich.cs.samuel.kison.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import edu.wmich.cs.samuel.kison.MessageQueue;

public class ClientInputThread extends Thread{
	
	ObjectInputStream ois;
	MessageQueue queue;
	
	/**
	 * Thread that will continuously read a String[] to the ClientTCP socket from external server and update the shared MessageQueue all the way up from ClientLoop.
	 * @param pOis ObjectInputStream 
	 * @param pQueue Shared MessageQueue defined in ClientLoop
	 */
	public ClientInputThread(ObjectInputStream pOis, MessageQueue pQueue) {
		this.ois = pOis;
		this.queue = pQueue;
	}
	
	public void run() {
		while(true) {
			// always read in a String[] from Server to Client
			try
			{
				System.out.println("ClientToServerThread: Hanging on reading String[] object...");
				String[] newMessage = (String[]) this.ois.readObject();
				this.queue.push(newMessage);
				System.out.println("ClientToServerThread: Successfully readObject for String[]...\nContent: " + newMessage[0]);
			}
			catch(IOException e)
			{
				System.out.println("ClientToServerThread: Exception on reading String[] object!!!!");
				this.queue.push(new String[] {"client_quit"}); //let ClientLoop know that host has essentially 'quit' (disconnected)
				break;
			}
			catch (ClassNotFoundException e2)
			{
				break; 
			}
		}
	}
}
