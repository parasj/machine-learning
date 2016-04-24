package data.parsing;

public class CategoryMapper implements FeatureMapper {
	
	private BinaryClassConverter converter;

	public CategoryMapper(BinaryClassConverter converter) {
		this.converter = converter;
	}

	@Override
	public double[] map(String input) throws UnlearnedValueException {
		double category = converter.getCategory(input) ? 1 : 0;
		return new double[] { category };
	}

	@Override
	public boolean isBinary() {
		return true;
	}

	@Override
	public int size() {
		return 1;
	}

}
