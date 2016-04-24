package data.parsing;

public interface BinaryClassConverter {

	/**
	 * 
	 * @param input The input class (i.e. label)
	 * @return The new class (i.e. label)
	 */
	public boolean getCategory(String input);
}
