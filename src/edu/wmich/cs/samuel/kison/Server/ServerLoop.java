package edu.wmich.cs.samuel.kison.Server;

import java.io.IOException;

import edu.wmich.cs.samuel.kison.MessageQueue;
import edu.wmich.cs.samuel.kison.Client.ClientTCP;

//Code taken from https://stackoverflow.com/questions/18283199/java-main-game-loop

public class ServerLoop extends Thread
{
	private int UPS = 4; // Just a few updates per second should be good enough for this program
	private boolean running = true;
	private boolean RENDER_TIME = false;
	private ServerState state = new ServerState(); //hold the current state of the server
	private ServerData data = new ServerData(); //access the data of the game

	// The 2 input queues for each player
	private MessageQueue player1Input;
	private MessageQueue player2Input;

	// The 2 output queues for each player
	private MessageQueue player1Output;
	private MessageQueue player2Output;

	private ServerTCP serverTCP;
	private ClientTCP clientTCP;

	
	private Thread serverTCPThread;
	
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
		System.out.println("Server started!");

		// player1Input.empty(); //empty input queue
		// player1Output.empty(); //empty output queue

		long initialTime = System.nanoTime();
		final double timeU = 1000000000 / UPS;
		// final double timeF = 1000000000 / FPS;
		double deltaU = 0;
		int frames = 0, ticks = 0;
		int totalTicks = 0;
		long timer = System.currentTimeMillis();

		while (running)
		{
			long currentTime = System.nanoTime();
			deltaU += (currentTime - initialTime) / timeU;
			// deltaF += (currentTime - initialTime) / timeF;
			initialTime = currentTime;

			if (deltaU >= 1)
			{
				//System.out.println("Server running! " + totalTicks);
				checkInput();
				sendOutput();
				ticks++;
				totalTicks++;
				deltaU--;
				// System.out.println("Loop ran at " + deltaU);
			}

			/*
			 * if (deltaF >= 1) { render(); frames++; deltaF--; }
			 */

			if (System.currentTimeMillis() - timer > 1000)
			{
				if (RENDER_TIME)
				{
					System.out.println(String.format("UPS: %s, FPS: %s", ticks, frames));
				}
				frames = 0;
				ticks = 0;
				timer += 1000;
			}
		}
	}

	private void checkInput()
	{
		if (!player1Input.isEmpty()) //if there is new input to be had from player 1
		{
			//System.out.println("New Input from client 1 to server!");
			while (!player1Input.isEmpty())
			{
				interpretPlayerMessage(true, player1Input.pop());
				/*
				 * InputObject tempIn = player1Input.pop();
				 * System.out.println(tempIn.toString()); OutputObject tempOut = new
				 * OutputObject("confirm", "Destroyer", tempIn.getRow(), tempIn.getColumn());
				 * player1Output.push(tempOut);
				 */
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
			System.out.println("ServerLoop: New Output to send to external client(Player 2)!");
			while (!player2Output.isEmpty())
			{
				System.out.print("ServerLoop: Contents after pop inside sendOutput(): ");
				String[] message = this.player2Output.pop();

				for (int i = 0; i < message.length; i++)
				{
					System.out.print(i + ":" + message[i] + " ");
				}
				System.out.println(".");

				this.serverTCP.send(message);
			}
		}

	}

	// TODO: make this work with TCP instead
	/*
	 * public void connect(InputQueue newQueue) { player2Input = newQueue; //connect
	 * player 2 to server }
	 */

	// When getting a new message from the server, interpret what to do here
	private void interpretPlayerMessage(boolean player, String[] message)
	{
		if (player)//Player 1
		{
			System.out.print("ServerLoop: New Input from player 1:");
			for (int i = 0; i < message.length; i++)
			{
				System.out.print(i + ":" + message[i] + " ");
			}
			System.out.println(".");

			switch (message[0])
			{
				case ("confirm_host"): //Time for the server to officially start up if closed
					if (state.getCurrentState().equals("Closed"))
					{
						state.setCurrentState("Loading_Game");
						data.reset();
						data.setPlayer1Name(message[2]);

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

						System.out.println("ServerLoop: About to exit server socket... closing serverTCP serverSocket...");
						this.serverTCP.close(); //close connection
					}
					break;

				case ("button"): //Player clicked a button, either to place a ship or to attack
					if (state.getCurrentState().equals("Setup_Game") && message[1].equals("true")) //it was during setup, also double check to make sure correct side of board was clicked
					{
						placeShip(true, message);
					}
					else if(state.getCurrentState().equals("Player1_Turn") && message[1].equals("false")) //during player 1's turn, on enemy's side of board
					{
						takeTurn(true, message);
					}
					break;

				case ("quit"):
					if (state.getCurrentState().equals("Setup_Game") || state.getCurrentState().equals("Player1_Turn")
							|| state.getCurrentState().equals("Player2_Turn"))//can only quit if mid-game
					{
						player1Output.push(new String[] { "client_quit", data.getPlayer2Name() }); //notify player(s) that this player has disconnected!
						player2Output.push(new String[] { "client_quit", data.getPlayer2Name() }); //notify player(s) that this player has disconnected!
						state.setCurrentState("Closed"); //close game
						this.serverTCP.close();
					}
					break;

				default:
					break;

			}
		}
		else //Player 2
		{
			System.out.print("ServerLoop: New Input from player 2:");
			for (int i = 0; i < message.length; i++)
			{
				System.out.print(i + ":" + message[i] + " ");
			}
			System.out.println(".");

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
					else if(state.getCurrentState().equals("Player2_Turn") && message[1].equals("false")) //during player 2's turn, on enemy's side of board
					{
						takeTurn(false, message);
					}
					break;

				case ("quit"):
					if (state.getCurrentState().equals("Setup_Game") || state.getCurrentState().equals("Player1_Turn")
							|| state.getCurrentState().equals("Player2_Turn"))//can only quit if mid-game
					{
						player1Output.push(new String[] { "client_quit", data.getPlayer2Name() }); //notify player(s) that this player has disconnected!
						player2Output.push(new String[] { "client_quit", data.getPlayer2Name() }); //notify player(s) that this player has disconnected!
						state.setCurrentState("Closed"); //close game'
						this.serverTCP.close();
					}
					break;

				default:
					break;
			}
		}

	}
	
	
	/**
	 * Called when a client has clicked a button during the Player1Turn or Player2Turn phase (to attack enemy)
	 * @param player True for player 1, False for player 2
	 * @param message The full message w/side of board and x/y coords
	 */
	private void takeTurn(boolean player, String[] message)
	{
		int xCord = Integer.parseInt(message[2]);
		int yCord = Integer.parseInt(message[3]);
		
		if(!data.hasBeenAttacked(xCord, yCord, player)) //new attack location; invalid otherwise
		{
			if(data.attack(xCord, yCord, !player)) //hit enemy!
			{
				if(player)//player 1
				{
					player1Output.push(new String[] { "player1_hit_success", message[2], message[3]}); //notify player1 that they hit the enemy at this location
					player2Output.push(new String[] { "player1_hit_success", message[2], message[3]}); //notify player2 that they were hit
				}
				else//player 2
				{
					player1Output.push(new String[] { "player2_hit_success", message[2], message[3]}); //notify player1 that they were hit
					player2Output.push(new String[] { "player2_hit_success", message[2], message[3]}); //notify player2 that they hit the enemy at this location
				}
			}
			else //missed!
			{
				if(player)//player 1
				{
					player1Output.push(new String[] { "player1_hit_failure", message[2], message[3]}); //notify player1 that they didn't hit the enemy at this location
					player2Output.push(new String[] { "player1_hit_failure", message[2], message[3]}); //notify player2 that they weren't hit
				}
				else//player 2
				{
					player1Output.push(new String[] { "player2_hit_failure", message[2], message[3]}); //notify player1 that they weren't hit
					player2Output.push(new String[] { "player2_hit_failure", message[2], message[3]}); //notify player2 that they didn't hit the enemy at this location
				}
			}
			
			if(player)
			{
				state.setCurrentState("Player2_Turn"); //set to player2's turn if it was player1's
			}
			else
			{
				state.setCurrentState("Player1_Turn"); //set to player1's turn if it was player2's
			}
		}
		
		
	}
	

	/**
	 * Called when a client has clicked a button during the setup phase (to place a ship)
	 * @param player True for player 1, False for player 2
	 * @param message The full message w/side of board, rotation, and x/y coords
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
				if(player)
				{
					player1Output.push(new String[] { "valid_placement", currentShip, message[4], message[2], message[3] }); //notify player of correct placement
				}
				else
				{
					player2Output.push(new String[] { "valid_placement", currentShip, message[4], message[2], message[3] }); //notify player of correct placement
				}
				

				//Check to see if all ships have been placed by BOTH players; if so, notify both players and change state
				if (data.getCurrentShipToPlace(true).equals("") && data.getCurrentShipToPlace(false).equals(""))
				{
					if(player)
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
					if(player)
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
				if(player)
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
}
