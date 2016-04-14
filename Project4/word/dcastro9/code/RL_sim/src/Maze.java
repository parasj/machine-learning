
//import java.io.*;

import java.awt.Point;
import java.io.Serializable;
import java.util.Vector;

public class Maze implements Serializable
{
	//Attributes
	public int height, width; //height and width of the maze
	public Vector goals;
	public Vector starts;
	Vector walls;
//	private double[][] rewards;
//	double boundaryPenalty;

	public Maze(int _width, int _height) {
		width = _width;
		height = _height;

		walls = new Vector();
		goals = new Vector();
		starts = new Vector();

//		rewards = new double[width][height];
	}

//	void setBoundaryPenalty(double _penalty) 
//	{
//		boundaryPenalty = _penalty;
//	}

//	void setDimensions(int _width, int _height) 
//	{
//		height = _height;
//		width = _width;
//	}

	/*
	 * adds a start state to the maze
	 */
	void addStart(State st)
	{
		starts.add(st);
	}

	/* adds the goal state to the maze. if a goal already exists at that position
	 * then the goal is removed from that location. this enables goal addition and 
	 * deletion using the GUI
	 */ 
	void addGoal(State st) 
	{
		//dont add goals if they lie outside the maze dimensions
		if (st.x<0 || st.y<0 || st.x>=width || st.y>=height)
			return;
		
		if (!goals.contains(st))
			goals.add(st);
		else goals.remove(st);
	}

	/*
	 * this is a debugging function used to display the maze status on the console
	 */
	void display()
	{
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Point curr = new Point(y, x);
				if (walls.indexOf(curr) == -1)
					System.out.print("0 ");
				else
					System.out.print("1 ");
			}
			System.out.println();
		}
	}

	/*
	 * determines if the transition from current state to next state is a valid transition
	 * a transition is invalid when there is a wall between the current state and next state OR 
	 * the next state lies outside the maze boundary
	 */
	public boolean isValidTransition(State curr, State st)
	{	
		Wall possibleWall = new Wall(curr.x, curr.y, getDirection(curr, st));
	
		if (walls.contains(possibleWall)) // A wall
			return false;

		// Niether wall not outside boundary
		return true;
	}

	/*
	 * returns the direction (UP=0, RIGHT=1, DOWN=2, LEFT=3) in which state 'st' lies with respect 
	 * to the state 'curr'.
	 */
	public int getDirection(State curr, State st)
	{
		switch (curr.x - st.x) {
		case -1:
			return Wall.RIGHT;
		case 1:
			return Wall.LEFT;
		}
		switch (curr.y - st.y) {
		case -1:
			return Wall.UP;
		case 1:
			return Wall.DOWN;
		}
		return Wall.UP; //returning something by default, exception shud be used here
	}

	/*
	 * returns all possible valid successors of the state 'currState'
	 */
	Vector getValidSuccessors(State currState)
	{
		Vector succs = new Vector();
		for (int i = 0; i < Action.numActions; i++) {
			State newState = Action.performAction(currState, i);
			if (isValidTransition(currState, newState))
				succs.add(newState);
		}
		return succs;
	}

	/*
	 * returns all the successors (valid successors as well as invalid successors) 
	 * of the state 'currState'
	 */
	Vector getSuccessors(State currState)
	{
		Vector succs = new Vector();

		for (int i = 0; i < Action.numActions; i++) {
			State newState = Action.performAction(currState, i);
			succs.add(newState);
		}
		return succs;
	}

	/*
	 * adds a wall to the maze
	 */
	void addWall(Wall newWall)
	{
		//dont add walls if they lie outside the maze dimensions
		if (newWall.x<0 || newWall.y<0 || newWall.x>=width || newWall.y>=height)
			return;
		
		if (!isWallPresent(newWall)){
			walls.add(newWall);
		}
		else {
			//remove the wall at that location
			walls.remove(newWall);
		}
	}
	
	/*
	 * returns the reward which will result if there is an attempt to make a transition from 
	 * state 'curr' to state 'st'. returns penalty if the transition is invalid else returns zero
	 */
	double getReward(State curr, State st)
	{
		Wall possibleWall = new Wall(curr.x,curr.y,getDirection(curr,st));
		int index = walls.indexOf(possibleWall);
		if(index!=-1) {
			Wall w = (Wall)walls.get(index);
			return w.penalty;
		}
		return 0;
	}
		
	/*
	 * returns true if a wall is present at the specified location else returns false.
	 */
	boolean isWallPresent(Wall newWall)
	{
		boolean wallPresent = false;
		Wall w = new Wall();
		for (int i = 0; i < walls.size(); i++) {
			w = (Wall)walls.get(i);
			if (w.x == newWall.x && w.y == newWall.y && w.dir == newWall.dir) {
				wallPresent = true;
				break;
			}
		}
		return wallPresent;
	}

	/*only for debugging purposes...
	 * prints on the console all the walls that are present in the maze
	 */
	void printWalls() {
		Wall w = new Wall();
		Utility.show("------------printing walls------------");
		if (walls.isEmpty())
		    Utility.show("no walls yet");

		for (int i = 0; i < walls.size(); i++) {
			w = (Wall) walls.get(i);
			Utility.show("wall at " + w.x + " " + w.y + " " + w.dir + " "
					+ w.penalty);
		}
		Utility.show("------------done------------");
	}

	/*only for debugging purposes...
	 * prints on the console all the goals that are present in the maze
	 */
	void printGoals() {
		State st = new State(0, 0);
		Utility.show("-------------------------");
		if (goals.isEmpty())
		    Utility.show("no goals yet");
		for (int i = 0; i < goals.size(); i++) {
			st = (State) goals.get(i);
			Utility.show("goal at " + st.x + " " + st.y);
		}
	}
}