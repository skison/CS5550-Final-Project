package edu.wmich.cs.samuel.kison.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import edu.wmich.cs.samuel.kison.MessageQueue;

public class ClientToServerThread extends Thread{
	
	ObjectInputStream ois;
	MessageQueue queue;
	
	public ClientToServerThread(ObjectInputStream pOis, MessageQueue pQueue) {
		this.ois = pOis;
		this.queue = pQueue;
	}
	
	public void run() {
		while(true) {
			try
			{
				System.out.println("ClientToServerThread: Hanging on reading MessageQueue object...");
				this.queue = (MessageQueue) this.ois.readObject();
				System.out.println("ClientToServerThread: Successfully readObject for MessageQueue...\nContect: " + this.queue.toString());
			}
			catch(IOException e)
			{
				System.out.println("ClientToServerThread: Exception on reading MessageQueue object!!!!");
				break;
			}
			catch (ClassNotFoundException e2)
			{
				break; 
			}
		}
	}
}
