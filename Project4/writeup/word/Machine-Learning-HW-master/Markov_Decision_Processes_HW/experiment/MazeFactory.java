package experiment;

import java.util.function.BiFunction;
import maze.NegativeMazeMDP;

public class MazeFactory implements BiFunction<char[][], Double, NegativeMazeMDP> {
	
	private final BiFunction<char[][], Double, NegativeMazeMDP> factory;

	public MazeFactory(BiFunction<char[][], Double, NegativeMazeMDP> factory) {
		this.factory = factory;
	}

	@Override
	public NegativeMazeMDP apply(char[][] maze, Double t) {
		return factory.apply(maze, t);
	}

}
