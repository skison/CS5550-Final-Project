package edu.wmich.cs.samuel.kison.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import edu.wmich.cs.samuel.kison.MessageQueue;

public class ClientInputThread extends Thread{
	
	ObjectInputStream ois;
	MessageQueue serverToClientQueue;
	
	/**
	 * Thread that will continuously read a String[] to the ClientTCP socket from external server and update the shared MessageQueue all the way up from ClientLoop.
	 * @param pOis ObjectInputStream 
	 * @param pQueue Shared MessageQueue defined in ClientLoop
	 */
	public ClientInputThread(ObjectInputStream pOis, MessageQueue pQueue) {
		this.ois = pOis;
		this.serverToClientQueue = pQueue;
	}
	
	public void run() {
		while(true) {
			// always read in a String[] from Server to Client
			try
			{
				System.out.println("ClientToServerThread: Hanging on reading String[] object...");
				this.serverToClientQueue.push((String[]) this.ois.readObject());
				System.out.println("ClientToServerThread: Successfully readObject for String[]...\nContect: ");
			}
			catch(IOException e)
			{
				System.out.println("ClientToServerThread: Exception on reading String[] object!!!!");
				break;
			}
			catch (ClassNotFoundException e2)
			{
				break; 
			}
		}
	}
}
