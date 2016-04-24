package maze;

public class InvalidMazeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidMazeException(String message) {
		super(message);
	}
}
