package edu.wmich.cs.samuel.kison.Server;

import edu.wmich.cs.samuel.kison.StateList;

/*
 * This class is used to keep track of the current 'state' the Server is in. This is necessary to make sure
 * that certain actions only take place when they are meant to.
 * 
 * Simplified diagram of Server states:
 * 
 * Closed
 *   ^
 *   |
 *   v
 * Loading_Game
 *   ^
 *   |
 *   v
 * Setup_Game ---> Player2_Turn
 * 	 |              ^    |
 *   v              |    |
 * Player1_Turn <----    |
 *   |                   |
 *   v                   |
 * End_Game <-------------
 *   |
 *   v
 * Back to Setup_Game if chose 'rematch', otherwise back to Closed
 */

public class ServerState extends StateList
{
	public ServerState()
	{
		super();//call StateList constructor
		
		//Set up Nodes as shown above
		stateList.add("Closed");
		stateList.add("Loading_Game");
		stateList.add("Setup_Game");
		stateList.add("Player1_Turn");
		stateList.add("Player2_Turn");
		stateList.add("End_Game");
	}
}
