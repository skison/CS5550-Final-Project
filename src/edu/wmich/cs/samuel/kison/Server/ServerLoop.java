package edu.wmich.cs.samuel.kison.Server;

import edu.wmich.cs.samuel.kison.Main;
import edu.wmich.cs.samuel.kison.MessageQueue;

/**
 * This is the controller of the Server. Unlike the ClientLoop, THIS object is the main 'C' of the server's MVC structure; it has no parent class that
 * initializes it besides Main(). Previously there used to be a Server class above this one, but it turned out to be completely unnecessary <br>
 * <br>
 * The main 'M' part of the MVC structure is controlled by a ServerData object, initialized by this class. It holds both player's boards, ships, and names. The
 * only data that THIS class is in charge of is the current game state (ServerState), and 2 booleans that determine whether or not both players want to rematch
 * after the round ends. <br>
 * <br>
 * When the game first starts up, The ServerLoop is able to communicate with the local ClientLoop via the player1Input and player1Output queues. Each tick,
 * messages are popped from the player1Input queue (if there are any), and new messages are pushed to the player1Output queue if the server needs to notify
 * player1 of anything. Once player 1 confirms that it would like to be a host, the serverTCPThread will be started, allowing for external communication with
 * another client (player2). Messages from player 2 will be popped from the player2Input queue (originally pushed to by ServerInputThread). Messages to player 2
 * will be pushed to the player2Output queue, which will then be sent by the ServerTCP object.<br>
 * <br>
 * Code reference (for running a simple game loop): https://stackoverflow.com/questions/18283199/java-main-game-loop
 * 
 * @author Samuel Kison
 *
 */
public class ServerLoop extends Thread
{
	private int UPS = 4; // Just a few updates per second should be good enough for this program
	private boolean running = true;
	private ServerState state = new ServerState(); //hold the current state of the server
	private ServerData data = new ServerData(); //access the data of the game

	// The 2 input queues for each player
	private MessageQueue player1Input;
	private MessageQueue player2Input;

	// The 2 output queues for each player
	private MessageQueue player1Output;
	private MessageQueue player2Output;

	private ServerTCP serverTCP; //the TCP object to communicate with an external client

	boolean player1Rematch = false; //when both of these are true, start up a new match for the connected players!
	boolean player2Rematch = false; //^

	private Thread serverTCPThread;

	/**
	 * Initialize the queues used by this loop
	 * 
	 * @param client1Input
	 *            MessageQueue for reading messages from the internal client
	 * @param client1Output
	 *            MessageQueue for writing messages to the internal client
	 */
	public ServerLoop(MessageQueue client1Input, MessageQueue client1Output)
	{
		// connect player 1 to server
		player1Input = client1Input;
		player1Output = client1Output;

		player2Input = new MessageQueue();
		player2Output = new MessageQueue();
	}

	@Override
	public void run()
	{
		if (Main.debug)
			System.out.println("Server started!");

		long initialTime = System.nanoTime();
		final double timeU = 1000000000 / UPS;
		double deltaU = 0;

		while (running)
		{
			long currentTime = System.nanoTime();
			deltaU += (currentTime - initialTime) / timeU;
			initialTime = currentTime;

			if (deltaU >= 1)
			{
				checkInput();
				sendOutput();
				deltaU--;
			}
		}
	}

	/**
	 * Check for new messages from player 1 or player 2
	 */
	private void checkInput()
	{
		if (!player1Input.isEmpty()) //if there is new input to be had from player 1
		{
			while (!player1Input.isEmpty())
			{
				interpretPlayerMessage(true, player1Input.pop());
			}
		}
		if (!player2Input.isEmpty()) //if there is new input to be had from player 2
		{
			while (!player2Input.isEmpty())
			{
				interpretPlayerMessage(false, player2Input.pop());
			}
		}
	}

	/**
	 * Used only to send String[] to external client
	 */
	private void sendOutput()
	{

		if (!player2Output.isEmpty())
		{
			if (Main.debug)
				System.out.println("ServerLoop: New Output to send to external client(Player 2)!");
			while (!player2Output.isEmpty())
			{
				String[] message = this.player2Output.pop();

				if (Main.debug)
				{
					System.out.print("ServerLoop: Contents after pop inside sendOutput(): ");
					for (int i = 0; i < message.length; i++)
					{
						System.out.print(i + ":" + message[i] + " ");
					}
					System.out.println(".");
				}

				this.serverTCP.send(message);
			}
		}

	}

	/**
	 * When getting a new message from a player, interpret what to do here
	 * 
	 * @param player
	 *            true = player1 (internal), false = player2 (external)
	 * @param message
	 *            the player message to be interpreted
	 */
	private void interpretPlayerMessage(boolean player, String[] message)
	{
		if (player)//Player 1
		{
			if (Main.debug)
			{
				System.out.print("ServerLoop: New Input from player 1:");
				for (int i = 0; i < message.length; i++)
				{
					System.out.print(i + ":" + message[i] + " ");
				}
				System.out.println(".");
			}

			switch (message[0])
			{
				case ("confirm_host"): //Time for the server to officially start up if closed
					if (state.getCurrentState().equals("Closed"))
					{
						state.setCurrentState("Loading_Game");
						data.reset();
						data.setPlayer1Name(message[2]);

						if (Main.debug)
							System.out.println("ServerLoop: About to create & run serverTCP!");

						//get port number! that is on message[1]
						int port = Integer.parseInt(message[1]);
						this.serverTCP = new ServerTCP(port, this.player2Input);
						this.serverTCPThread = new Thread(this.serverTCP);
						this.serverTCPThread.start();
					}
					break;

				case ("cancel_load"): //Time for server to go back to a closed state if loading
					if (state.getCurrentState().equals("Loading_Game"))
					{
						state.setCurrentState("Closed");

						if (Main.debug)
							System.out.println(
									"ServerLoop: About to exit server socket... closing serverTCP serverSocket...");
						this.serverTCP.close(); //close connection
					}
					break;

				case ("button"): //Player clicked a button, either to place a ship or to attack
					if (state.getCurrentState().equals("Setup_Game") && message[1].equals("true")) //it was during setup, also double check to make sure correct side of board was clicked
					{
						placeShip(true, message);
					}
					else if (state.getCurrentState().equals("Player1_Turn") && message[1].equals("false")) //during player 1's turn, on enemy's side of board
					{
						takeTurn(true, message);
					}
					break;

				case ("quit"):
					if (state.getCurrentState().equals("Setup_Game") || state.getCurrentState().equals("Player1_Turn")
							|| state.getCurrentState().equals("Player2_Turn")
							|| state.getCurrentState().equals("End_Game"))//can only quit if in-game
					{
						player1Output.push(new String[] { "client_quit" }); //notify player2 that they have disconnected!
						state.setCurrentState("Closed"); //close game
						this.serverTCP.close();
					}
					break;

				case ("rematch"):
					if (state.getCurrentState().equals("End_Game")) //need to be in end game to rematch
					{
						rematchGame(true);
					}
					break;

				default:
					break;

			}
		}
		else //Player 2
		{
			if (Main.debug)
			{
				System.out.print("ServerLoop: New Input from player 2:");
				for (int i = 0; i < message.length; i++)
				{
					System.out.print(i + ":" + message[i] + " ");
				}
				System.out.println(".");
			}

			switch (message[0])
			{

				case ("confirm_join"):
					data.setPlayer2Name(message[3]);
					state.setCurrentState("Setup_Game");
					this.player1Output.push(new String[] { "enemy_connected", data.getPlayer2Name() });
					this.player2Output.push(new String[] { "enemy_connected", data.getPlayer1Name() });
					break;

				case ("cancel_load"): //Time for server to go back to a closed state if loading
					if (state.getCurrentState().equals("Loading_Game"))
					{
						state.setCurrentState("Closed");
						this.serverTCP.close();
					}
					break;

				case ("button"): //Player clicked a button, either to place a ship or to attack
					if (state.getCurrentState().equals("Setup_Game") && message[1].equals("true")) //it was during setup, also double check to make sure correct side of board was clicked
					{
						placeShip(false, message);
					}
					else if (state.getCurrentState().equals("Player2_Turn") && message[1].equals("false")) //during player 2's turn, on enemy's side of board
					{
						takeTurn(false, message);
					}
					break;

				case ("quit"):
					if (state.getCurrentState().equals("Setup_Game") || state.getCurrentState().equals("Player1_Turn")
							|| state.getCurrentState().equals("Player2_Turn")
							|| state.getCurrentState().equals("End_Game"))//can only quit if in-game
					{
						player1Output.push(new String[] { "client_quit" }); //notify player1 that this player2 has disconnected!
						state.setCurrentState("Closed"); //close game
						this.serverTCP.close();
					}
					break;

				case ("rematch"):
					if (state.getCurrentState().equals("End_Game")) //need to be in end game to rematch
					{
						rematchGame(false);
					}
					break;

				default:
					break;
			}
		}

	}

	/**
	 * Called when a client has clicked a button during the Player1Turn or Player2Turn phase (to attack enemy)
	 * 
	 * @param player
	 *            True for player 1, False for player 2
	 * @param message
	 *            The full message w/side of board and x/y coords
	 */
	private void takeTurn(boolean player, String[] message)
	{
		int xCord = Integer.parseInt(message[2]);
		int yCord = Integer.parseInt(message[3]);

		if (!data.hasBeenAttacked(xCord, yCord, !player)) //new attack location; invalid otherwise
		{
			if (data.attack(xCord, yCord, !player)) //hit enemy!
			{
				if (player)//player 1
				{
					player1Output.push(new String[] { "player_hit_success", "true", message[2], message[3] }); //notify player1 that they hit the enemy at this location
					player2Output.push(new String[] { "player_hit_success", "false", message[2], message[3] }); //notify player2 that they were hit
				}
				else//player 2
				{
					player1Output.push(new String[] { "player_hit_success", "false", message[2], message[3] }); //notify player1 that they were hit
					player2Output.push(new String[] { "player_hit_success", "true", message[2], message[3] }); //notify player2 that they hit the enemy at this location
				}

				String newlySunkShip = data.checkForNewSunkShip(!player);

				if (!newlySunkShip.equals("")) //check if a ship was just destroyed (not blank); if so, notify the players
				{
					if (player)//player 1
					{
						player1Output.push(new String[] { "player_ship_sunk", "true", newlySunkShip }); //notify player1 that they sunk an enemy's ship
						player2Output.push(new String[] { "player_ship_sunk", "false", newlySunkShip }); //notify player2 that their ship was sunk
					}
					else//player 2
					{
						player1Output.push(new String[] { "player_ship_sunk", "false", newlySunkShip }); //notify player1 that their ship was sunk
						player2Output.push(new String[] { "player_ship_sunk", "true", newlySunkShip }); //notify player2 that they sunk an enemy's ship
					}

					//Now, finally, check if ALL ships have been sunk of the oposite player; if so, game can end
					if (data.isAllSunk(!player))
					{
						state.setCurrentState("End_Game"); //set state to end state

						if (player)//player 1
						{
							player1Output.push(new String[] { "game_over", "true" }); //notify player1 that they won
							player2Output.push(new String[] { "game_over", "false" }); //notify player2 that they lost
						}
						else//player 2
						{
							player1Output.push(new String[] { "game_over", "false" }); //notify player1 that they lost
							player2Output.push(new String[] { "game_over", "true" }); //notify player2 that they won
						}
					}
				}
			}
			else //missed!
			{
				if (player)//player 1
				{
					player1Output.push(new String[] { "player_hit_failure", "true", message[2], message[3] }); //notify player1 that they didn't hit the enemy at this location
					player2Output.push(new String[] { "player_hit_failure", "false", message[2], message[3] }); //notify player2 that they weren't hit
				}
				else//player 2
				{
					player1Output.push(new String[] { "player_hit_failure", "false", message[2], message[3] }); //notify player1 that they weren't hit
					player2Output.push(new String[] { "player_hit_failure", "true", message[2], message[3] }); //notify player2 that they didn't hit the enemy at this location
				}
			}

			if (!state.getCurrentState().equals("End_Game")) //toggle player turn if not end of game
			{
				if (player)
				{
					state.setCurrentState("Player2_Turn"); //set to player2's turn if it was player1's
				}
				else
				{
					state.setCurrentState("Player1_Turn"); //set to player1's turn if it was player2's
				}
			}

		}

	}

	/**
	 * Called when a client has clicked a button during the setup phase (to place a ship)
	 * 
	 * @param player
	 *            True for player 1, False for player 2
	 * @param message
	 *            The full message w/side of board, rotation, and x/y coords
	 */
	private void placeShip(boolean player, String[] message)
	{
		String currentShip = data.getCurrentShipToPlace(player);//grab current ship name
		int currentShipLength = data.getShipLength(currentShip);
		if (!currentShip.equals("")) //make sure name is valid
		{
			Ship testShip = new Ship(currentShip, message[4], Integer.parseInt(message[2]),
					Integer.parseInt(message[3]), currentShipLength);

			if (data.couldShipFit(testShip, player)) //valid ship placement!
			{
				data.addShip(testShip, player); //add in the new ship
				data.incShipCount(player); //increment the counter for number of ships placed
				if (player)
				{
					player1Output
							.push(new String[] { "valid_placement", currentShip, message[4], message[2], message[3] }); //notify player of correct placement
				}
				else
				{
					player2Output
							.push(new String[] { "valid_placement", currentShip, message[4], message[2], message[3] }); //notify player of correct placement
				}

				//Check to see if all ships have been placed by BOTH players; if so, notify both players and change state
				if (data.getCurrentShipToPlace(true).equals("") && data.getCurrentShipToPlace(false).equals(""))
				{
					if (player)
					{
						player1Output.push(new String[] { "all_ships_placed", "false" }); //notify player 1 that it is now enemy's turn
						player2Output.push(new String[] { "all_ships_placed", "true" }); //notify player 2 that it is their turn
						state.setCurrentState("Player2_Turn");//Move to playing state; reward player 2 for placing all ships first
					}
					else
					{
						player2Output.push(new String[] { "all_ships_placed", "false" }); //notify player 2 that it is now enemy's turn
						player1Output.push(new String[] { "all_ships_placed", "true" }); //notify player 1 that it is their turn
						state.setCurrentState("Player1_Turn");//Move to playing state; reward player 1 for placing all ships first
					}

				}
				else if (data.getCurrentShipToPlace(player).equals("")) //if ONLY this player has placed all their ships, let them know!
				{
					if (player)
					{
						player1Output.push(new String[] { "your_ships_placed" });
					}
					else
					{
						player2Output.push(new String[] { "your_ships_placed" });
					}
				}
			}
			else //invalid ship placement
			{
				if (player)
				{
					player1Output.push(new String[] { "invalid_placement", currentShip }); //notify player of incorrect placement
				}
				else
				{
					player2Output.push(new String[] { "invalid_placement", currentShip }); //notify player of incorrect placement
				}
			}
		}
	}

	/**
	 * Called when a player wants a rematch! Used to start up a new round if both players want to
	 * 
	 * @param player
	 *            true = player1, false = player2
	 */
	private void rematchGame(boolean player)
	{
		if (player)
		{
			this.player1Rematch = true;
		}
		else
		{
			this.player2Rematch = true;
		}

		if (this.player1Rematch && this.player2Rematch) //both players want a rematch
		{
			state.setCurrentState("Setup_Game");
			data.rematch();

			this.player1Output.push(new String[] { "rematch_accepted" });
			this.player2Output.push(new String[] { "rematch_accepted" });

			this.player1Rematch = false; //reset these booleans
			this.player2Rematch = false; //^
		}
		else //only 1 player wants a rematch so far, so notify the other player
		{
			if (player) //came from player 1, so notify player 2
			{
				player2Output.push(new String[] { "request_rematch" });
			}
			else//came from player 2, so notify player 1
			{
				player1Output.push(new String[] { "request_rematch" });
			}
		}
	}
}
