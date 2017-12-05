package edu.wmich.cs.samuel.kison.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import edu.wmich.cs.samuel.kison.MessageQueue;

/**
 * This class has the ability to send a String[] to external <i>client</i> with <b>send(String[])</b>. The class always 
 * reads a String[] from external Client and updates the shared MessageQueue defined in ServerLoop above.
 * 
 * @author alan_
 *
 */
public class ServerTCP extends Thread{
	MessageQueue queue; //defined in ServerLoop above
	int port;
	//private boolean keepGoing;
	ServerSocket serverSocket;
	ServerInputThread clientThread;
	Socket socket;
	
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
			this.socket = serverSocket.accept();
			
			System.out.println("ServerTCP: Client has connected!");
			this.clientThread = new ServerInputThread(socket, this.queue);
			clientThread.start();
		} 
		// could not establish connection
		catch (IOException e) {
			System.out.println("ServerTCP: Could not establish connection on new ServerSocket: " + e + "\n");
		}
	}
	
	public void close() {
		try {
			System.out.println("STOPPING THREAD");
			this.serverSocket.close();
			this.clientThread.ois.close();
			this.clientThread.oos.close();
			this.clientThread.socket.close();
			
			//connect to myself as Client to exit statement ?
			//new Socket("localhost", this.port);
		} catch (IOException e) {
			System.out.println("ServerTCP: Exception on stop()...");
		}
			
	}
	
	//used to send queue messages
	public void send(String[] pMessageToSend) {
		try {
			this.clientThread.oos.writeObject(pMessageToSend);
		} catch (IOException e) {
			System.out.println("ServerTCP: Exception during send(String[]): " + e + "\n");
		}
	}
	
}
