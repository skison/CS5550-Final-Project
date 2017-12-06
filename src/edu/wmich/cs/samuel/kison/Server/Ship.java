package edu.wmich.cs.samuel.kison.Server;

/*
 * This is a very simple object that represents a ship on the board
 * Values:
 * Name: String name of the ship
 * Rotation: String direction the ship is facing
 * Head x, Head y: ints coordinates of the head of the ship
 * Length: int length of ship
 * Hits: array of booleans that describe whether or not that spot has been hit
 * Sunk: boolean that is true when the entire ship has been attacked
 */

public class Ship
{
	private String name;
	private String rotation;
	private int headX;
	private int headY;
	private int length;
	private boolean[] hits;
	private boolean sunk = false;
	private boolean notifiedSunk = false; //Is set to true when checkIfNewlySunk() is called AFTER the ship has sunk

	public Ship(String _name, String _rotation, int _headX, int _headY, int _length)
	{
		name = _name;
		rotation = _rotation;
		headX = _headX;
		headY = _headY;
		length = _length;

		hits = new boolean[length]; //will initialize to all false
	}
	
	/**
	 * Used to check if this ship has just been sunk
	 * @return true if ship is sunk AND this is the first time checking
	 */
	public boolean checkIfNewlySunk()
	{
		if(sunk) //must be sunk
		{
			if(notifiedSunk) //has already notified that this ship is sunk, so it's not 'newly' sunk
			{
				return false;
			}
			else //ship is newly sunk
			{
				notifiedSunk = true;
				return true;
			}
		}
		else //hasn't sunk yet
		{
			return false;
		}
	}

	/*
	 * Set one of the booleans in the hits array to true to simulate attacking the ship (assume attackIndex is valid, should receive it from occUpiesCoords())
	 */
	public void attackIndex(int attackIndex)
	{
		hits[attackIndex] = true;
		checkIfSunk(); //Update the sunk boolean just in case the ship has no remaining parts
	}

	/*
	 * Check every boolean in the hits array; If every one is true, Update the sunk boolean to true
	 */
	private void checkIfSunk()
	{
		boolean tempBool = true;

		for (int i = 0; i < hits.length; i++)
		{
			if (!hits[i])
			{
				tempBool = false;
			} //set the boolean to false if any value is false
		}

		sunk = tempBool;
	}

	/*
	 * Method used to check if part of this ship lays at the test coordinates provided
	 * Returns int occUpies: the index of the ship segment that occUpies this space if any. If it doesn't, returns -1 instead
	 */
	public int occupiesCoords(int testX, int testY)
	{
		int x = headX;//save initial x
		int y = headY;//save initial y
		int occUpies = -1;

		for (int i = 0; i < length; i++)
		{
			if (testX == x && testY == y) //the ship does exist at this spot
			{
				occUpies = i;
				break; //it would be pointless to continue checking at this point, so break
			}

			//increment or decrement x or y depending on the rotation
			switch (rotation)
			{
				case ("Down"):
					y--;
					break;
				case ("Left"):
					x++;
					break;
				case ("Right"):
					x--;
					break;
				default: //assume Up
					y++;
					break;
			}
		}

		return occUpies;
	}

	/*
	 * Custom Getters to find coordinates of backmost part of ship
	 */
	public int getBackX()
	{
		int backX = headX;
		
		//System.out.println("rotation in getBackX: " + rotation);

		switch (rotation)
		{
			case ("Left"):
				backX += (length - 1);
				break;
			case ("Right"):
				backX -= (length - 1);
				break;
			default: //Up or Down
				break;
		}

		return backX;
	}

	public int getBackY()
	{
		int backY = headY;
		
		//System.out.println("rotation in getBackY: " + rotation);

		switch (rotation)
		{
			case ("Down"):
				backY -= (length - 1);
				break;
			case ("Up"):
				backY += (length - 1);
				break;
			default: //Left or Right
				break;
		}

		return backY;
	}

	/*
	 * find the X coordinate at the index of the ship provided (0 is the head of the ship)
	 */
	public int getXatIndex(int index)
	{
		int retX = headX;
		
		if (rotation.equals("Left"))
		{
			retX += index;
		}
		else if (rotation.equals("Right"))
		{
			retX -= index;
		}
		//not affected by Up or Down, so ignore them
		return retX;
	}
	
	/*
	 * find the Y coordinate at the index of the ship provided (0 is the head of the ship)
	 */
	public int getYatIndex(int index)
	{
		int retY = headY;
		
		if (rotation.equals("Up"))
		{
			retY += index;
		}
		else if (rotation.equals("Down"))
		{
			retY -= index;
		}
		//not affected by Left or Right, so ignore them
		return retY;
	}
	
	

	//Getters

	public String getName()
	{
		return name;
	}

	public String getRotation()
	{
		return rotation;
	}

	public int getHeadX()
	{
		return headX;
	}

	public int getHeadY()
	{
		return headY;
	}

	public int getLength()
	{
		return length;
	}

	public boolean isSunk()
	{
		return sunk;
	}
}
