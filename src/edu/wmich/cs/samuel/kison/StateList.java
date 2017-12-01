package edu.wmich.cs.samuel.kison;

import java.util.ArrayList;

/*
 * A VERY simple class that holds an ArrayList of Strings that describe which "game states" exist,
 * and which one is currently active.
 */

public class StateList
{
	protected int currentState;
	protected ArrayList<String> stateList;
	
	public StateList()
	{
		currentState = 0;
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
