package edu.wmich.cs.samuel.kison.Server;

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
	private MessageQueue player2Input = null;

	// The 2 output queues for each player
	private MessageQueue player1Output;
	private MessageQueue player2Output = null;

	private ServerTCP serverTCP;
	private ClientTCP clientTCP;
	
	public ServerLoop(MessageQueue client1Input, MessageQueue client1Output)
	{
		// connect player 1 to server
		player1Input = client1Input;
		player1Output = client1Output;
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
				//update();

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
		/*if (!player2Input.isEmpty()) //if there is new input to be had from player 2
		{
			while (!player2Input.isEmpty())
			{
				interpretPlayerMessage(true, player2Input.pop());
			}
		}*/
	}

	private void checkOutput()
	{
		/*
		 * if(!player1Output.isEmpty()) { System.out.println("New Output!");
		 * 
		 * while(!player1Output.isEmpty()) {
		 * System.out.println(player1Output.pop().toString()); } }
		 */
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
				System.out.print(i+ ":" + message[i] + " ");
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
						//TODO: allow player 2 connection to update state to Start_Game_Preparation. For now, just pretend like a second player has connected
						
						//get port number! that is on message[1]
						int port = Integer.parseInt(message[1]);
						this.serverTCP = new ServerTCP(port, this.player2Input);
						this.serverTCP.start();
						
						
						
						
						
//						data.setPlayer2Name("Example Enemy");
//						state.setCurrentState("Setup_Game");
//						player1Output.push(new String[] { "enemy_connected", data.getPlayer2Name() });
					}
					break;
					
					
					
					
				case ("confirm_join"): //waiting for the server to get client info
					state.setCurrentState("Setup_Game");
					
				
					//this is where Client will start their Client Loop with TCP connection
					
				
				
				
//					player1Output.push(new String[] { "enemy_connected", data.getPlayer2Name() });
//					player2Output.push(new String[] { "enemy_connected", data.getPlayer1Name() });
					break;
					
					
					
					
				case ("cancel_load"): //Time for server to go back to a closed state if loading
					if (state.getCurrentState().equals("Loading_Game"))
					{
						state.setCurrentState("Closed");
					}
					break;
				case ("button"): //Player clicked a button, either to place a ship or to attack
					if (state.getCurrentState().equals("Setup_Game") && message[1].equals("true")) //it was during setup, also double check to make sure correct side of board was clicked
					{
						String currentShip = data.getCurrentShipToPlace(true);//grab current ship name
						int currentShipLength = data.getShipLength(currentShip);
						if (!currentShip.equals("")) //make sure name is valid
						{
							Ship testShip = new Ship(currentShip, message[4], Integer.parseInt(message[2]),
									Integer.parseInt(message[3]), currentShipLength);

							if (data.couldShipFit(testShip, true)) //valid ship placement!
							{
								data.addShip(testShip, true); //add in the new ship
								data.incShipCount(true); //increment the counter for number of ships placed
								player1Output.push(new String[] {"valid_placement", currentShip, message[4], message[2], message[3]}); //notify player of correct placement
								
								//For testing purposes, also place player2's ships
								//TODO: get rid of this logic
								data.addShip(new Ship(data.getCurrentShipToPlace(false), message[4], Integer.parseInt(message[2]),
										Integer.parseInt(message[3]), data.getShipLength(data.getCurrentShipToPlace(false))), false);
								data.incShipCount(false);
								
								
								//Check to see if all ships have been placed by BOTH players; if so, notify both players and change state
								if(data.getCurrentShipToPlace(true).equals("") && data.getCurrentShipToPlace(false).equals(""))
								{
									player1Output.push(new String[] {"all_ships_placed", "true"}); //notify player 1 that it is now their turn
									//TODO: send player2 the same message
									//player2Output.push(new String[] {"all_ships_placed", "false"}); //notify player 2 that it isn't their turn
									state.setCurrentState("Player1_Turn");//Move to playing state; reward player 1 for placing all ships first
								}
								else if(data.getCurrentShipToPlace(true).equals("")) //if ONLY this player has placed all their ships, let them know!
								{
									player1Output.push(new String[] {"your_ships_placed"});
								}
							}
							else //invalid ship placement
							{
								player1Output.push(new String[] {"invalid_placement", currentShip}); //notify player of incorrect placement
							}
						}
					}
					break;
				case("quit"):
					if(state.getCurrentState().equals("Setup_Game") || state.getCurrentState().equals("Player1_Turn") || state.getCurrentState().equals("Player2_Turn"))//can only quit if mid-game
					{
						//TODO: notify player(s) that this player has disconnected!
						//player2Output.push(new String[] {"client_quit", data.getPlayer2Name})
						state.setCurrentState("Closed"); //close game
					}
					break;
				default:
					break;

			}

			//System.out.println("Current state: " + state.getCurrentState());

			// tell server what we got (for testing!)
			// serverOutput.push(message);
		}
		else //Player 2
		{
			System.out.print("ServerLoop: New Input from player 2:");
			for (int i = 0; i < message.length; i++)
			{
				System.out.print(" " + message[i]);
			}
			System.out.println(".");
		}

	}

	/*
	 * Do something depending on the state of the server
	 */
	/*private void update()
	{
		if(state.getCurrentState().equals("Loading_Game"))
		{
			//Check if 
		}
	}*/
}
