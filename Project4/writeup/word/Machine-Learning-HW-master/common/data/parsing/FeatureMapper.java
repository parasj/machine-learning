package data.parsing;

public interface FeatureMapper {

	public double[] map(String input) throws UnlearnedValueException;
	
	public boolean isBinary();

	/**
	 * @return The number of columns needed to represent this feature.
	 */
	public int size();
}
