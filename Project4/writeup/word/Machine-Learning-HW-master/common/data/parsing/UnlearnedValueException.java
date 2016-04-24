package data.parsing;

public class UnlearnedValueException extends Exception {

	private static final long serialVersionUID = 1L;

	public UnlearnedValueException(String value) {
		super("Do not recognize " + value);
	}
}
