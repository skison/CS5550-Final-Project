package edu.wmich.cs.samuel.kison.Client;

import java.util.Arrays;

import edu.wmich.cs.samuel.kison.Main;
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
		if(Main.debug)
		{
			System.out.print("Client: New Output from " + message[0] + ":");
			for (int i = 1; i < message.length; i++)
			{
				System.out.print(" " + message[i]);
			}
			System.out.println(".");
		}

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
					else if(state.getCurrentState().equals("Playing_Game")) //user it attacking enemy
					{
						if (actualMessage[1].equals("false"))//you have to attack enemy's ships only
						{
							clientQueue.push(actualMessage); //tell clientLoop where the ship is being attacked
						}
					}
					break;
					
				case ("quit"):
					if (state.getCurrentState().equals("Setup_Game") || state.getCurrentState().equals("Playing_Game") || state.getCurrentState().equals("End_Game")) //need to be in-game to quit it
					{
						state.setCurrentState("Start");
						gui.updateGUI(new String[] { "start" });
						gui.updateGUI(new String[] { "panel_update", "notify", "You have quit the game!" });
						clientQueue.push(actualMessage); //tell clientLoop to quit game
					}
					break;
					
				case ("rematch"):
					if (state.getCurrentState().equals("End_Game")) //need to be in end-game to request a rematch!
					{
						gui.updateGUI(new String[] { "panel_update", "notify", "Requesting rematch!" });
						clientQueue.push(actualMessage); //tell clientLoop to try to rematch enemy
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
					break;
					
				case ("your_ships_placed"):
					if (state.getCurrentState().equals("Setup_Game")) //must be during setup period
					{
						gui.updateGUI(new String[] { "panel_update", "notify", "Waiting for enemy to finish placing ships..." }); //let player know that server is waiting for other player
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
					
				case ("player_hit_success"):
					if(state.getCurrentState().equals("Playing_Game")) //must be playing game to attack ship
					{
						if (actualMessage[1].equals("true")) //player hit the enemy!
						{
							gui.updateGUI(new String[] { "panel_update", "notify", "You hit " + enemyName + "'s ship! Waiting for " + enemyName + " to take their turn..." });
							gui.getPanel().getBoard(false).attackLocation(Integer.parseInt(actualMessage[2]), Integer.parseInt(actualMessage[3]), true);
						}
						else //player got hit by the enemy!
						{
							gui.updateGUI(new String[] { "panel_update", "notify", enemyName + " hit your ship, now make your move!" });
							gui.getPanel().getBoard(true).attackLocation(Integer.parseInt(actualMessage[2]), Integer.parseInt(actualMessage[3]), true);
						}
					}
					break;
					
				case ("player_hit_failure"):
					if(state.getCurrentState().equals("Playing_Game")) //must be playing game to attack ship
					{
						if (actualMessage[1].equals("true")) //player missed the enemy!
						{
							gui.updateGUI(new String[] { "panel_update", "notify", "You missed! Waiting for " + enemyName + " to take their turn..." });
							gui.getPanel().getBoard(false).attackLocation(Integer.parseInt(actualMessage[2]), Integer.parseInt(actualMessage[3]), false);
						}
						else //enemy missed the player!
						{
							gui.updateGUI(new String[] { "panel_update", "notify", enemyName + " missed, now make your move!" });
							gui.getPanel().getBoard(true).attackLocation(Integer.parseInt(actualMessage[2]), Integer.parseInt(actualMessage[3]), false);
						}
					}
					break;
					
				case ("player_ship_sunk"):
					if(state.getCurrentState().equals("Playing_Game")) //must be playing game to attack ship
					{
						if (actualMessage[1].equals("true")) //player hit the enemy!
						{
							gui.updateGUI(new String[] { "panel_update", "notify", "You sunk " + enemyName + "'s " + actualMessage[2] + "! Waiting for " + enemyName + " to take their turn..." });
						}
						else //player got hit by the enemy!
						{
							gui.updateGUI(new String[] { "panel_update", "notify", enemyName + " sunk your " + actualMessage[2] + ", now make your move!" });
						}
					}
					break;
					
				case("game_over"):
					if(state.getCurrentState().equals("Playing_Game")) //must be playing game to finish it
					{
						state.setCurrentState("End_Game"); //set to end state
						gui.updateGUI(new String[] { "end" }); //update GUI's menu bar
						
						if (actualMessage[1].equals("true")) //player won!
						{
							gui.updateGUI(new String[] { "panel_update", "notify", "You defeated " + enemyName + "! Click the rematch button to play against them again, or quit the game!" });
						}
						else //enemy won
						{
							gui.updateGUI(new String[] { "panel_update", "notify", enemyName + " defeated you! Click the rematch button to play against them again, or quit the game!" });
						}
					}
					break;
					
				case("client_quit"):
					if (state.getCurrentState().equals("Setup_Game") || state.getCurrentState().equals("Playing_Game") || state.getCurrentState().equals("End_Game")) //must be during the game
					{
						state.setCurrentState("Start"); //set state back to start
						gui.updateGUI(new String[] { "start" }); //change gui's menubar to start bar
						gui.updateGUI(new String[] { "panel_update", "notify", enemyName + " has quit the game!" });
					}
					break;
					
				case("request_rematch"):
					if(state.getCurrentState().equals("End_Game"))
					{
						gui.updateGUI(new String[] { "panel_update", "notify", enemyName + " is requesting a rematch! Click 'Rematch' to accept!" });
					}
					break;
					
				case("rematch_accepted"):
					if(state.getCurrentState().equals("End_Game")) //start a new game if in end game state
					{
						state.setCurrentState("Setup_Game");
						gui.updateGUI(new String[] { "setup" });
						gui.updateGUI(new String[] { "panel_update", "notify", "Place your ships!" });
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
