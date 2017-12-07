package edu.wmich.cs.samuel.kison;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A simple queue that holds messages (arrays of Strings)
 * 
 * @author Samuel Kison
 *
 */
public class MessageQueue
{
	private Queue<String[]> messageQueue;

	/**
	 * Initialize a new MessageQueue
	 */
	public MessageQueue()
	{
		messageQueue = new ConcurrentLinkedQueue<String[]>(); // implement the queue as a concurrent linked queue (thread safe)
	}

	/**
	 * Push a new message to the queue
	 * 
	 * @param newMessage
	 *            the new message to be pushed to the queue
	 */
	public void push(String[] newMessage)
	{
		messageQueue.add(newMessage);
	}

	/**
	 * pop a message off the queue (does not perform error checking)
	 * 
	 * @return String[] the message that was the head of the queue
	 */
	public String[] pop()
	{
		return messageQueue.remove();
	}

	/**
	 * Checks to see if the queue has any messages
	 * 
	 * @return boolean true if no messages, false if at least 1 message
	 */
	public boolean isEmpty()
	{
		return messageQueue.isEmpty();
	}

	/**
	 * Remove everything from the queue
	 */
	public void empty()
	{
		messageQueue.clear();
	}
}
