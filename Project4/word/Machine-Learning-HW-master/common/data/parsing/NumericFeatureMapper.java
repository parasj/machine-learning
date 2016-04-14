package data.parsing;

class NumericFeatureMapper implements FeatureMapper {

	private boolean isBinary;

	public NumericFeatureMapper(boolean isBinary) {
		this.isBinary = isBinary;
	}

	@Override
	public double[] map(String input) {
		return new double[] { Double.parseDouble(input) };
	}

	@Override
	public boolean isBinary() {
		return isBinary;
	}

	@Override
	public int size() {
		return 1;
	}
}
