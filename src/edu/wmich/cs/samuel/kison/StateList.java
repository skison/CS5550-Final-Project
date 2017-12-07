package edu.wmich.cs.samuel.kison;

import java.util.ArrayList;

/**
 * A VERY simple class that holds an ArrayList of Strings that describe which "game states" exist, and which one is currently active.
 * 
 * @author Samuel Kison
 */
public class StateList
{
	protected int currentState; //index that points to the current state in stateList
	protected ArrayList<String> stateList; //list of all state Strings

	/**
	 * Initialize a new StateList
	 */
	public StateList()
	{
		currentState = 0; //initialize
		stateList = new ArrayList<String>();
	}

	//Getters & Setters

	public String getCurrentState()
	{
		return stateList.get(currentState);
	}

	public int getIndexOfState(String checkState)
	{
		return stateList.indexOf(checkState);
	}

	public ArrayList<String> getStateList()
	{
		return stateList;
	}

	public int getStateListSize()
	{
		return stateList.size();
	}

	public String getState(int index)
	{
		return stateList.get(index);
	}

	public void setCurrentStateIndex(int index)
	{
		currentState = index;
	}

	public void setCurrentState(String newState)
	{
		setCurrentStateIndex(getIndexOfState(newState));
	}
}
