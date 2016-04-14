package maze;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import dist.Distribution;
import rl.MazeMarkovDecisionProcess;


public class NegativeMazeMDP extends MazeMarkovDecisionProcess {
	public static final char TRAP = '!';
	
	private final Map<Integer, TerminalPoint> traps;

	private final double motionFailureProbability;
	private final double timePenalty;
	private final TerminalPoint goal;

	public NegativeMazeMDP(char[][] maze, TerminalPoint goal, Point start, List<TerminalPoint> traps,
			double motionFailureProbability, double timePenalty) {
		super(maze, goal.col, goal.row, start.col, start.row, motionFailureProbability);
		this.motionFailureProbability = motionFailureProbability;
		this.timePenalty = timePenalty;
		this.goal = goal;
		
		this.traps = new HashMap<>();
		for (TerminalPoint each : traps) {
			int stateNum = stateFor(each.col, each.row);
			this.traps.put(stateNum, each);
		}
	}
	
	public boolean isGoal(int stateNum) {
		return stateFor(goal.col, goal.row) == stateNum;
	}
	
	public boolean isTrap(int stateNum) {
		return traps.containsKey(stateNum);
	}
	
	@Override
	public double reward(int stateNum, int action) {
		if (isTrap(stateNum)) {
			return traps.get(stateNum).reward;
		} else if (isGoal(stateNum)) {
			return goal.reward;
		} else {
			return -timePenalty;
		}
	}
	
	@Override
	public boolean isTerminalState(int stateNum) {
		if (traps.containsKey(stateNum)) {
			return true;
		} else {
			return super.isTerminalState(stateNum);
		}
	}
	
	public static NegativeMazeMDP createMaze(File mazeFile, double motionFailureProbability,
			double trapCost, double goalReward, double timePenalty) throws FileNotFoundException {
		char[][] maze = loadMaze(mazeFile);
		return createMaze(maze, motionFailureProbability, trapCost, goalReward, timePenalty);
	}

	/**
	 * 
	 * @param maze maze[row][col]
	 * @param motionFailureProbability
	 * @param trapCost Should be a positive number. Will become negative later.
	 * @param goalReward the reward for reaching the goal
	 * @param timePenalty the cost for staying alive
	 * @return
	 */
	public static NegativeMazeMDP createMaze(char[][] maze, double motionFailureProbability,
			double trapCost, double goalReward, double timePenalty) {
		TerminalPoint goal = null;
		List<TerminalPoint> traps = new ArrayList<>();
		Point start = null;
		
		int rowSize = maze[0].length;
		for (int row = 0; row < maze.length; row++) {
			if (rowSize != maze[row].length) {
				throw new InvalidMazeException("Line " + (row + 1) + " has a different length than the first.");
			}
			
			for (int col = 0; col < rowSize; col++) {
				char currentChar = maze[row][col];
				if (currentChar == MazeMarkovDecisionProcess.GOAL) {
					TerminalPoint otherGoal = new TerminalPoint(col, row, goalReward);
					if (goal != null) {
						throw new InvalidMazeException("Multiple goals detected. One at " + goal + " and another at " + otherGoal);
					}
					
					goal = otherGoal;
				} else if (currentChar == MazeMarkovDecisionProcess.AGENT) {
					Point otherStart = new Point(col, row);
					if (start != null) {
						throw new InvalidMazeException("Multiple starts detected. One at " + start + " and another at " + otherStart);
					}
					
					start = otherStart;
				} else if (currentChar == TRAP) {
					traps.add(new TerminalPoint(col, row, -trapCost));
				}
			}
		}
		
		return new NegativeMazeMDP(maze, goal, start, traps, motionFailureProbability, timePenalty);
	}
	
	@Override
	public double transitionProbability(int fromState, int toState, int a) {
		Motion motion = Motion.create(this, a);
		MotionProbability probabilities = motion.createMotionProbability(fromState, motionFailureProbability);
		return probabilities.getProbability(toState);
    }
	
	@Override
	public int sampleState(int i, int a) {
		Motion desiredMotion = Motion.create(this, a);

		double leftProbability = motionFailureProbability / 2;
		double rightProbability = motionFailureProbability;
		double coin = Distribution.random.nextDouble();
		Motion actualMotion;
		if (coin < leftProbability) {
			actualMotion = desiredMotion.getLeftMotion();
        } else if (coin < rightProbability) {
        	actualMotion = desiredMotion.getRightMotion();
        } else {
        	actualMotion = desiredMotion;
        }

        int nextState = actualMotion.getForwardState(i);
        
        return nextState;
	}
	
	public boolean isMoveableLocation(int x, int y) {
		boolean xInBounds = x >= 0 && x < getWidth();
		boolean yInBounds = y >= 0 && y < getHeight();
		return xInBounds && yInBounds && !isObstacle(x, y);
	}
	
	public static char[][] loadMaze(File file) throws FileNotFoundException {
		try (Scanner scan = new Scanner(file)) {
			List<List<Character>> mazeList = new ArrayList<>();
			while(scan.hasNextLine()) {
				String line = scan.nextLine();
				List<Character> lineList = new ArrayList<>();
				for (int i = 0; i < line.length(); i++) {
					lineList.add(line.charAt(i));
				}
				mazeList.add(lineList);
			}
			
			char[][] maze = new char[mazeList.size()][];
			for (int i = 0; i < mazeList.size(); i++) {
				List<Character> line = mazeList.get(i);
				maze[i] = new char[line.size()];
				for (int j = 0; j < line.size(); j++) {
					maze[i][j] = line.get(j);
				}
			}
			
			return maze;
		}
	}
}
