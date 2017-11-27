package edu.wmich.cs.samuel.kison.Server;

import edu.wmich.cs.samuel.kison.MessageQueue;

public class Server
{
	private ServerLoop gameLoop;
	private Thread gameLoopThread;
	
	public Server(MessageQueue clientOutput, MessageQueue serverOutput) //clientOutput & serverOutput are used in the ServerLoop
	{
		gameLoop = new ServerLoop(clientOutput, serverOutput);
		gameLoopThread = new Thread(gameLoop);
		gameLoopThread.start();
	}
}
