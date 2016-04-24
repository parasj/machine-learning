package maze;

import rl.MazeMarkovDecisionProcess;

public class Motion {
	
	public final int dx, dy;
	private final NegativeMazeMDP maze;
	
	public Motion(NegativeMazeMDP maze, int dx, int dy) {
		this.maze = maze;
		this.dx = dx;
		this.dy = dy;
	}

	public static Motion create(NegativeMazeMDP maze, int action) {
		int dx, dy;
		switch(action) {
            case MazeMarkovDecisionProcess.MOVE_UP:
                dx = 0; dy = -1;
                break;
            case MazeMarkovDecisionProcess.MOVE_DOWN:
                dx = 0; dy = 1;
                break;
            case MazeMarkovDecisionProcess.MOVE_LEFT:
                dx = -1; dy = 0;
                break;
            case MazeMarkovDecisionProcess.MOVE_RIGHT:
                dx = 1; dy = 0;
                break;
            default:
            	throw new RuntimeException("Action " + action + " is not recognized.");
        }
		
		return new Motion(maze, dx, dy);
	}
	
	/**
	 * 
	 * @return The state forward relative to this motion
	 */
	public int getForwardState(int currentState) {
		int newX = maze.xFor(currentState) + dx;
		int newY = maze.yFor(currentState) + dy;
		
		if (maze.isMoveableLocation(newX, newY)) {
			return maze.stateFor(newX, newY);
		} else {
			return currentState;
		}
	}
	
	public int getLeftState(int currentState) {
		return getLeftMotion().getForwardState(currentState);
	}
	
	public int getRightState(int currentState) {
		return getRightMotion().getForwardState(currentState);
	}
	
	public int getReverseState(int currentState) {
		return getReverseMotion().getForwardState(currentState);
	}
	
	public Motion getLeftMotion() {
		int leftDx, leftDy;
		
		if (dx == 0 && dy == 0) {
			leftDx = 0;
			leftDy = 0;
		} else if (dx == 0) {
			if (dy < 0) {
				leftDx = -1;
			} else {
				leftDx = 1;
			}
			leftDy = 0;
		} else if (dy == 0) {
			if (dx < 0) {
				leftDy = 1;
			} else { 
				leftDy = -1;
			}
			leftDx = 0;
		} else {
			throw new RuntimeException("Neither dx nor dy were 0. Do not know what left is.");
		}
		
		return new Motion(maze, leftDx, leftDy);
	}
	
	public Motion getReverseMotion() {
		return new Motion(maze, -dx, -dy);
	}
	
	public Motion getRightMotion() {
		return getLeftMotion().getReverseMotion();
	}
	
	public MotionProbability createMotionProbability(int currentState, double motionFailureProbability) {
		int forwardState = getForwardState(currentState);
		int leftState = getLeftState(currentState);
		int rightState = getRightState(currentState);

		MotionProbability probability = new MotionProbability();
		probability.addWeight(forwardState, 1 - motionFailureProbability);
		probability.addWeight(leftState, motionFailureProbability / 2);
		probability.addWeight(rightState, motionFailureProbability / 2);

		return probability;
	}
	
	@Override
	public boolean equals(Object other) { 
		if (!(other instanceof Motion)) {
			return false;
		}
		
		Motion otherMotion = (Motion) other;
		return this.dx == otherMotion.dx && this.dy == otherMotion.dy;
	}
	
	@Override
	public int hashCode() {
		return Integer.hashCode(dx) ^ Integer.hashCode(dy);
	}
	
	@Override
	public String toString() {
		return "(" + dx + "," + dy + ")";
	}
}
