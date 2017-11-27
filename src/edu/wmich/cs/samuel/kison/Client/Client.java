package edu.wmich.cs.samuel.kison.Client;

import edu.wmich.cs.samuel.kison.MessageQueue;

/*Client controller class in charge of both the GUI & client loop
 * 
 * */
public class Client
{
	//hold Loop & GUI for the Client
	private ClientLoop loop;
	private GUIFrame gui;
	private MessageQueue guiQueue;
	private Thread loopThread;
	
	public Client(MessageQueue clientOutput, MessageQueue serverOutput) //clientOutput & serverOutput are used in the ClientLoop
	{
		guiQueue = new MessageQueue();
		gui = new GUIFrame(guiQueue);
		loop = new ClientLoop(this, guiQueue, clientOutput, serverOutput);
		loopThread = new Thread(loop);
		loopThread.start();//start loop
	}
	
	//get message from loop, tell GUI what to do
	public void receiveMessage(String[] message)
	{
		/*System.out.print("New Output from loop:");
		
		for(int i = 0; i < message.length; i++)
		{
			System.out.print(" " + message[i]);
		}
		System.out.println(".");*/
		
		switch(message[0])
		{
			case("join"):
				break;
			case("host"):
				break;
			default:
				break;
		}
	}
}
