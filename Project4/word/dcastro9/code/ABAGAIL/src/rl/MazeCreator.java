package rl;

/**
 * A class that creates randomly sized mazes (compatible with our MazeMarkovDecisionProcess.java)
 * @author dcastro9
 * @version 1.0
 */
public class MazeCreator {

	public MazeCreator() {
	}

	public static char[][] createMaze(int width, int height) {
		char[][] maze = new char[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				maze[x][y] = MazeMarkovDecisionProcess.EMPTY;
			}
		}
		return maze;
	}
}