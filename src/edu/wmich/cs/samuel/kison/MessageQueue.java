package edu.wmich.cs.samuel.kison;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/*
 * A simple queue that holds messages (arrays of Strings)
 */
public class MessageQueue
{
	private Queue<String[]> messageQueue;
	
	public MessageQueue()
	{
		messageQueue = new ConcurrentLinkedQueue<String[]>(); // implement the queue as a concurrent linked queue (thread safe)
	}
	
	public void push(String[] newMessage)
	{
		messageQueue.add(newMessage);
	}

	public String[] pop()
	{
		return messageQueue.remove();
	}

	public boolean isEmpty()
	{
		return messageQueue.isEmpty();
	}

	// Empty out the queue
	public void empty()
	{
		messageQueue.clear();
	}
}
