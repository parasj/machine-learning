package data.parsing;

public class NumericFeatureLearner implements FeatureLearner {
	
	private boolean isBinary = true;

	@Override
	public void learn(String input) {
		double value = Double.parseDouble(input);
		if (value != 0 || value != 1) {
			isBinary = false;
		}
	}

	@Override
	public FeatureMapper createMapper() {
		return new NumericFeatureMapper(isBinary);
	}
}
