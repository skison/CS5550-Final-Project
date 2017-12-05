package edu.wmich.cs.samuel.kison.Client;

import java.util.Arrays;
import edu.wmich.cs.samuel.kison.MessageQueue;

/*
 * Client controller class in charge of both the GUI & client loop
 */
public class Client
{
	//hold Loop & GUI for the Client
	private ClientLoop loop;
	private GUIFrame gui;
	private MessageQueue clientQueue;
	private Thread loopThread;
	private ClientState state = new ClientState(); //hold the current state of the client

	private String playerName = ""; //custom player name
	private String enemyName = ""; //custom name for player2

	public Client(MessageQueue clientOutput, MessageQueue serverOutput) //clientOutput & serverOutput are used in the ClientLoop
	{
		clientQueue = new MessageQueue();
		gui = new GUIFrame(this/*, clientQueue*/);
		loop = new ClientLoop(this, clientQueue, clientOutput, serverOutput);
		loopThread = new Thread(loop);
		loopThread.start();//start loop
	}

	//get message from loop, tell GUI what to do
	public void receiveMessage(String[] message)
	{
		System.out.print("Client: New Output from " + message[0] + ":");
		for (int i = 1; i < message.length; i++)
		{
			System.out.print(" " + message[i]);
		}
		System.out.println(".");

		if (message[0].equals("gui")) //message was from gui
		{
			String[] actualMessage = Arrays.copyOfRange(message, 1, message.length); //dispose of the first String that is now unnecessary

			switch (actualMessage[0])
			{
				case ("exit"):
					clientQueue.push(actualMessage); //let clientloop know that it is time to exit
					break;
				case ("confirm_name"):
					if (state.getCurrentState().equals("Start")) //only confirm name if in Start state
					{
						playerName = actualMessage[1];
						gui.updateGUI(new String[] { "panel_update", "player_name", playerName });
						//System.out.println("New player name: " + playerName);
						//TODO: send GUI message saying player name accepted/denied (denied if blank)
						if (isPlayerNameValid())
						{
							gui.updateGUI(new String[] { "panel_update", "notify", "Player name accepted!" });
						}
						else
						{
							gui.updateGUI(new String[] { "panel_update", "notify", "Invalid player name!" });
						}
					}
					break;
				case ("join"):
					if (state.getCurrentState().equals("Start")) //only join game if in Start state
					{
						if (isPlayerNameValid()) //name must be valid
						{
							state.setCurrentState("Join_Game_Preparation");//set new state
							gui.updateGUI(new String[] { "join" }); //update GUI to allow joining of game
							gui.updateGUI(new String[] { "panel_update", "notify",
									"Please input a game server's IP/Port #!" });
						}
						else
						{
							gui.updateGUI(new String[] { "panel_update", "notify", "Invalid player name!" });
						}
					}
					break;
				case ("host"):
					if (state.getCurrentState().equals("Start")) //only host game if in Start state
					{
						if (isPlayerNameValid()) //name must be valid
						{
							state.setCurrentState("Host_Game_Preparation");//set new state
							gui.updateGUI(new String[] { "host" }); //update GUI to allow hosting of game
							gui.updateGUI(new String[] { "panel_update", "notify",
									"Choose a port number to open the server up to!" });
						}
						else
						{
							gui.updateGUI(new String[] { "panel_update", "notify", "Invalid player name!" });
						}
					}
					break;
				case ("cancel_join"):
					if (state.getCurrentState().equals("Join_Game_Preparation")) //can only cancel joining game if in this state
					{
						state.setCurrentState("Start");
						gui.updateGUI(new String[] { "start" });
						gui.updateGUI(new String[] { "panel_update", "notify", "Please host or join a game!" });
					}
					break;
				case ("cancel_host"):
					if (state.getCurrentState().equals("Host_Game_Preparation")) //can only cancel hosting game if in this state
					{
						state.setCurrentState("Start");
						gui.updateGUI(new String[] { "start" });
						gui.updateGUI(new String[] { "panel_update", "notify", "Please host or join a game!" });
					}
					break;
				case ("confirm_join"):
					if (state.getCurrentState().equals("Join_Game_Preparation")) //can only join game if in this state
					{
						if (!isIPValid(actualMessage[1]))//invalid ip number
						{
							gui.updateGUI(new String[] { "panel_update", "notify", "Invalid IP address!" });
						}
						else if (!isPortValid(actualMessage[2]))//invalid port number
						{
							gui.updateGUI(new String[] { "panel_update", "notify", "Invalid port number!" });
						}
						else //IP/Port is ok to TRY
						{
							state.setCurrentState("Loading_Game");
							gui.updateGUI(new String[] { "client_load" });
							gui.updateGUI(new String[] { "disable_join_bar" }); //disable the options on the join menu bar (because trying to connect)
							String[] connectMessage = new String[actualMessage.length + 1]; //create new message with one extra spot for playerName
							System.arraycopy(actualMessage, 0, connectMessage, 0, actualMessage.length);
							connectMessage[connectMessage.length - 1] = playerName;
							clientQueue.push(connectMessage); //tell clientLoop that it's time to join game
							gui.updateGUI(new String[] { "panel_update", "notify", "Attempting to join game..." });
						}
					}
					break;
				case ("retry_join"):
					if (state.getCurrentState().equals("Loading_Game")) //can only retry loading if in loading state
					{
						String[] connectMessage = new String[actualMessage.length + 1]; //create new message with one extra spot for playerName
						System.arraycopy(actualMessage, 0, connectMessage, 0, actualMessage.length);
						connectMessage[connectMessage.length - 1] = playerName;
						clientQueue.push(connectMessage); //tell clientLoop that it's time to join game
						gui.updateGUI(new String[] { "disable_join_bar" }); //disable the options on the join menu bar (because trying to connnect)
						gui.updateGUI(new String[] { "panel_update", "notify", "Attempting to join game..." });
					}
					break;
				case ("confirm_host"):
					if (state.getCurrentState().equals("Host_Game_Preparation")) //can only host game if in this state
					{
						if (!isPortValid(actualMessage[1]))//invalid port number
						{
							gui.updateGUI(new String[] { "panel_update", "notify", "Invalid port number!" });
						}
						else //port number is ok to TRY
						{
							state.setCurrentState("Loading_Game");
							gui.updateGUI(new String[] { "host_load" });
							String[] connectMessage = new String[actualMessage.length + 1]; //create new message with one extra spot for playerName
							System.arraycopy(actualMessage, 0, connectMessage, 0, actualMessage.length);
							connectMessage[connectMessage.length - 1] = playerName;
							clientQueue.push(connectMessage); //tell clientLoop that it's time to host game
							gui.updateGUI(new String[] { "panel_update", "notify", "Starting up server..." });
						}
					}
					break;
				case ("cancel_load"):
					if (state.getCurrentState().equals("Loading_Game")) //can only quit loading if in loading state
					{
						state.setCurrentState("Start");
						gui.updateGUI(new String[] { "start" });
						clientQueue.push(actualMessage); //tell clientLoop to cancel loading
						gui.updateGUI(new String[] { "panel_update", "notify", "Please host or join a game!" });
					}
					break;
				case ("button"):
					if (state.getCurrentState().equals("Setup_Game")) //User is placing ships during setup
					{
						if (actualMessage[1].equals("true"))//you have to place ships on your side only
						{
							clientQueue.push(actualMessage); //tell clientLoop where the ship is trying to be placed
						}
					}
					break;
				case ("quit"):
					if (state.getCurrentState().equals("Setup_Game") || state.getCurrentState().equals("Playing_Game")) //need to be mid-game to quit it
					{
						state.setCurrentState("Start");
						gui.updateGUI(new String[] { "start" });
						clientQueue.push(actualMessage); //tell clientLoop to quit game
					}
					break;
				default:
					break;
			}
		}
		else //message was from ClientLoop
		{
			String[] actualMessage = Arrays.copyOfRange(message, 1, message.length); //dispose of the first String that is now unnecessary

			switch (actualMessage[0])
			{
				case ("join_failed"):
					System.out.println("ClientLoop Join failed in state " + state.getCurrentState());
					gui.updateGUI(new String[] { "enable_join_bar" }); //enable the options on the join menu bar (because no longer trying to connect)
					gui.updateGUI(new String[] { "panel_update", "notify", "Failed to join game!" });
					break;
				case ("enemy_connected"):
					if (state.getCurrentState().equals("Loading_Game"))//can only connect with enemy if in Loading_Game state
					{
						state.setCurrentState("Setup_Game");
						gui.updateGUI(new String[] { "setup" });
						enemyName = actualMessage[1]; //set enemy's name
						gui.updateGUI(new String[] { "panel_update", "enemy_name", enemyName });//set enemy's name on gui
						gui.updateGUI(new String[] { "panel_update", "notify", "Place your ships!" });
					}
					break;
				case ("valid_placement"):
					if (state.getCurrentState().equals("Setup_Game")) //can only setup ships during setup period
					{
						gui.getPanel().getBoard(true).placeShip(actualMessage[1], Integer.parseInt(actualMessage[3]),
								Integer.parseInt(actualMessage[4]), actualMessage[2]);
					}
				case ("your_ships_placed"):
					if (state.getCurrentState().equals("Setup_Game")) //must be during setup period
					{
						gui.updateGUI(new String[] { "panel_update", "notify",
								"Waiting for enemy to finish placing ships..." }); //let player know that server is waiting for other player
					}
					break;
				case ("all_ships_placed"):
					if (state.getCurrentState().equals("Setup_Game")) //must be during setup period
					{
						state.setCurrentState("Playing_Game");//change state to 'playing'
						if (actualMessage[1].equals("true")) //it is this player's turn
						{
							gui.updateGUI(new String[] { "panel_update", "notify", playerName + "'s turn!" });
						}
						else //enemy's turn
						{
							gui.updateGUI(new String[] { "panel_update", "notify", enemyName + "'s turn!" });
						}
					}
					break;
				default:
					break;
			}
		}
	}

	//Check if player name is ok to use!
	private boolean isPlayerNameValid()
	{
		boolean retBool = true;
		if (playerName.equals(""))
		{
			retBool = false;
		}
		return retBool;
	}

	//Check if a given port number is ok to TRY to use
	private boolean isPortValid(String portNum)
	{
		boolean retBool = true;
		try
		{
			if (Integer.parseInt(portNum) <= 0) //int must be > 0
			{
				retBool = false;
			}
		}
		catch (NumberFormatException e) //value must be an int
		{
			retBool = false;
		}

		return retBool;
	}

	//Check if a given IP address is ok to TRY to use
	private boolean isIPValid(String ip)
	{
		boolean retBool = true;
		if (ip.equals(""))
		{
			retBool = false;
		}
		return retBool;
	}
}
