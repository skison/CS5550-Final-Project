package edu.wmich.cs.samuel.kison.Server;

import java.util.ArrayList;

/**
 * This class stores Ship instances for every ship on the board for both players,
 * as well as a 2-dimensional boolean array for each player keeping track of which
 * locations have been hit.
 */

public class ServerData
{
	private ArrayList<Ship> player1Ships;//all of player1's Ship objects
	private ArrayList<Ship> player2Ships;//^ for player2
	private boolean[][] player1BoardHits; //automatically initialized to false
	private boolean[][] player2BoardHits; //^
	private String player1Name; //player1's name
	private String player2Name; //player2's name
	private int player1ShipCount; //number of ships placed by player1
	private int player2ShipCount; //^ for player2

	public ServerData()
	{
		reset(); //initialize everything
	}

	/*
	 * Add a new ship to one of the player's ArrayLists.
	 * If player == true, player1. Else, player 2
	 */
	public void addShip(Ship newShip, boolean player)
	{
		if (player) //player1
		{
			player1Ships.add(newShip);
		}
		else //player 2
		{
			player2Ships.add(newShip);
		}
	}

	/*
	 * Used to check if a player's board has already been attacked at a specific spot (essentially the same as the getters below)
	 */
	public boolean hasBeenAttacked(int x, int y, boolean player)
	{
		if (player)
		{
			return getPlayer1BoardHits(x, y);
		}
		else
		{
			return getPlayer2BoardHits(x, y);
		}
	}

	/**
	 * Used to attack a player's board at a specific spot
	 * 
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param player PLAYER YOU ARE ATTACKING!!! (true = player 1, false = player 2)
	 * @return true if attack hit an enemy's ship
	 */
	public boolean attack(int x, int y, boolean player)
	{
		ArrayList<Ship> currentShips;
		boolean hitShip = false;

		if (player)
		{
			player1BoardHits[x][y] = true;
			currentShips = player1Ships;
		}
		else
		{
			player2BoardHits[x][y] = true;
			currentShips = player2Ships;
		}

		for (Ship i : currentShips)
		{
			int attackIndex = i.occupiesCoords(x, y);
			if (attackIndex > -1)
			{
				i.attackIndex(attackIndex);
				hitShip = true;
			}
		}

		return hitShip;
	}
	
	/**
	 * Used to check for a newly destroyed ship
	 * 
	 * @param player true to check player1's ships, false to check player2's ships
	 * @return String name of newly sunk ship of player, or empty ("") if none
	 */
	public String checkForNewSunkShip(boolean player)
	{
		ArrayList<Ship> currentShips;
		String retShip = "";
		
		if(player)
		{
			currentShips = player1Ships;
		}
		else
		{
			currentShips = player2Ships;
		}
		
		for (Ship i : currentShips)
		{
			if(i.checkIfNewlySunk()) //this ship is newly sunk!
			{
				retShip = i.getName();
				break; //no need to continue this pointless loop
			}
		}
		
		return retShip;
	}

	//Reset everything
	public void reset()
	{
		player1Ships = new ArrayList<Ship>(); //all of player1's Ship objects
		player2Ships = new ArrayList<Ship>(); //^ for player2
		player1BoardHits = new boolean[10][10]; //automatically initialized to false
		player2BoardHits = new boolean[10][10]; //^
		player1Name = ""; //player1's name
		player2Name = ""; //player2's name
		player1ShipCount = 0;
		player2ShipCount = 0;
	}
	
	//Similar to reset(), but used when rematching a player
	public void rematch()
	{
		player1Ships = new ArrayList<Ship>(); //all of player1's Ship objects
		player2Ships = new ArrayList<Ship>(); //^ for player2
		player1BoardHits = new boolean[10][10]; //automatically initialized to false
		player2BoardHits = new boolean[10][10]; //^
		player1ShipCount = 0;
		player2ShipCount = 0;
	}

	/*
	 * Used to figure out if a ship is considered valid (stays completely inside the board and doesn't overlap any part of any other ship) for either player
	 */
	public boolean couldShipFit(Ship testShip, boolean player)
	{
		boolean canFit = true; //init to true, turn to false if any condition fails
		ArrayList<Ship> currentShips;
		if (player)
		{
			currentShips = player1Ships;
		}
		else
		{
			currentShips = player2Ships;
		}

		//First, get frontmost coordinates and backmost coordinates
		int backX = testShip.getBackX();
		int backY = testShip.getBackY();
		int frontX = testShip.getHeadX();
		int frontY = testShip.getHeadY();
		
		//Determine whether the X coords are the same, or if it's the Y coords (true = X, false = Y)
		boolean sameCoords = false;
		if (backX == frontX)
		{
			sameCoords = true;
		}

		//Now make sure each coordinate is within the bounds of the board
		if (frontX < 0 || frontX > 9 || frontY < 0 || frontY > 9 || backX < 0 || backX > 9 || backY < 0 || backY > 9)
		{
			canFit = false;
		}

		//Now check each coordinate pair of the ship to see if it overlaps the ships already on the board
		for (Ship i : currentShips)
		{
			for (int j = 0; j < testShip.getLength(); j++) //loop through each coordinate pair in the ship
			{
				if (i.occupiesCoords(testShip.getXatIndex(j), testShip.getYatIndex(j)) > -1)
				{
					canFit = false;
					break; //no need to continue this pointless loop
				}
			}
			
			if (!canFit)//no need to continue this pointless loop
			{
				break;
			}
		}

		return canFit;
	}

	//Get name of current ship that needs to be placed for either player 1 or player 2
	public String getCurrentShipToPlace(boolean player)
	{
		if (player)
		{
			return translateShipIndex(player1ShipCount);
		}
		else
		{
			return translateShipIndex(player2ShipCount);
		}
	}
	
	//Increment the amount of ships this player has
	public void incShipCount(boolean player)
	{
		if (player)
		{
			player1ShipCount++;
		}
		else
		{
			player2ShipCount++;
		}
	}

	/*
	 * translate a Ship index variable into a String (name of ship that needs to be placed)
	 */
	private String translateShipIndex(int testInt)
	{
		String curShip = "";

		switch (testInt)
		{
			case (0):
				curShip = "Carrier";
				break;
			case (1):
				curShip = "Battleship";
				break;
			case (2):
				curShip = "Cruiser";
				break;
			case (3):
				curShip = "Submarine";
				break;
			case (4):
				curShip = "Destroyer";
				break;
			default:
				break;
		}
		return curShip;
	}

	/*
	 *  Find out how long a ship SHOULD be based off of its name
	 */
	public int getShipLength(String shipName)
	{
		int retLength = -1;

		switch (shipName)
		{
			case ("Carrier"):
				retLength = 5;
				break;
			case ("Battleship"):
				retLength = 4;
				break;
			case ("Cruiser"):
				retLength = 3;
				break;
			case ("Submarine"):
				retLength = 3;
				break;
			case ("Destroyer"):
				retLength = 2;
				break;
			default:
				break;
		}
		return retLength;
	}
	
	/**
	 * Used to check if ALL of a player's ships have been sunk
	 * @param player true for player1, false for player2
	 * @return true if ALL ships are sunk, false otherwise
	 */
	public boolean isAllSunk(boolean player)
	{
		ArrayList<Ship> currentShips;
		boolean retBool = true; //start true, turn to false if any ships aren't sunk
		
		if(player)
		{
			currentShips = player1Ships;
		}
		else
		{
			currentShips = player2Ships;
		}
		
		for (Ship i : currentShips) //go through all ships
		{
			if(!i.isSunk()) //if this ship isn't sunk
			{
				retBool = false;
				break; //no need to continue this pointless loop
			}
		}
		
		return retBool;
	}
	

	//Getters & Setters

	public ArrayList<Ship> getPlayer1Ships()
	{
		return player1Ships;
	}

	public ArrayList<Ship> getPlayer2Ships()
	{
		return player2Ships;
	}

	public boolean[][] getPlayer1BoardHits()
	{
		return player1BoardHits;
	}

	public boolean[][] getPlayer2BoardHits()
	{
		return player2BoardHits;
	}

	public boolean getPlayer1BoardHits(int x, int y)
	{
		return player1BoardHits[x][y];
	}

	public boolean getPlayer2BoardHits(int x, int y)
	{
		return player2BoardHits[x][y];
	}

	public String getPlayer1Name()
	{
		return player1Name;
	}

	public void setPlayer1Name(String player1Name)
	{
		this.player1Name = player1Name;
	}

	public String getPlayer2Name()
	{
		return player2Name;
	}

	public void setPlayer2Name(String player2Name)
	{
		this.player2Name = player2Name;
	}

}
