package maze;

public class TerminalPoint extends Point {

	public final double reward;

	public TerminalPoint(int col, int row, double reward) {
		super(col, row);
		this.reward = reward;
	}

}
