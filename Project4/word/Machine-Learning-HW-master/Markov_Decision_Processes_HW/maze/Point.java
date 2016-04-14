package maze;

public class Point {

	public final int row;
	public final int col;
	
	public Point(int col, int row) {
		this.col = col;
		this.row = row;
	}
	
	@Override
	public String toString() {
		return "(" + col + ", " + row + ")";
	}
}
