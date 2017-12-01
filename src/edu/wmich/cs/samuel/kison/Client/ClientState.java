package edu.wmich.cs.samuel.kison.Client;

import edu.wmich.cs.samuel.kison.StateList;

/*
 * This class is used to keep track of the current 'state' the Client is in. This is necessary to make sure
 * that certain actions only take place when they are meant to. (like starting/joining a server)
 * 
 * Simplified diagram of Client states:
 * 
 * Start <-------------> Join_Game_Preparation
 *   ^                            |
 *   |                            |
 *   v                            v
 * Host_Game_Preparation ---> Loading_Game
 *                                |
 *                                v
 *                            Setup_Game
 * 	                              |
 *                                v
 *                            Playing_Game
 *                                |
 *                                v
 *                             End_Game ---> Back to Setup_Game if chose 'rematch', otherwise back to Start
 */

public class ClientState extends StateList
{
	public ClientState()
	{
		super();//call StateList constructor
		
		//Set up Nodes as shown above
		stateList.add("Start");
		stateList.add("Join_Game_Preparation");
		stateList.add("Host_Game_Preparation");
		stateList.add("Loading_Game");
		stateList.add("Setup_Game");
		stateList.add("Playing_Game");
		stateList.add("End_Game");
	}
}
