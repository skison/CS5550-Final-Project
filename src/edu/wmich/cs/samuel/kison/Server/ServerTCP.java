package edu.wmich.cs.samuel.kison.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import edu.wmich.cs.samuel.kison.MessageQueue;

public class ServerTCP {
	MessageQueue queue;
	int port;
	//private boolean keepGoing;
	ServerSocket serverSocket;
	ServerToClientThread clientThread;
	
	public ServerTCP(int pPort, MessageQueue pQueue) {
		this.queue = pQueue;
		this.port = pPort;
	}
	
	public void start() {
		//keepGoing = true;
		try
		{
			this.serverSocket = new ServerSocket(this.port);
			System.out.println("ServerTCP: Waiting for Client to connect on port: " + this.port);
			Socket socket = serverSocket.accept();
			
			System.out.println("ServerTCP: Client has connected!");
			this.clientThread = new ServerToClientThread(socket, this.queue);
			clientThread.start();
		} 
		// could not establish connection
		catch (IOException e) {
			System.out.println("ServerTCP: Could not establish connection on new ServerSocket: " + e + "\n");
		}
	}
	
	public void stop() {
		try {
			this.serverSocket.close();
			this.clientThread.ois.close();
			this.clientThread.oos.close();
			this.clientThread.socket.close();
			
			//connect to myself as Client to exit statement ?
			new Socket("localhost", this.port);
		} catch (IOException e) {
			System.out.println("ServerTCP: Exception on stop()...");
		}
			
	}
	
	//used to send queue messages
	public void send(MessageQueue pQueueToSend) {
		try {
			this.clientThread.oos.writeObject(pQueueToSend);
		} catch (IOException e) {
			System.out.println("ServerTCP: Exception during send(MessageQueue): " + e + "\n");
		}
	}
}
