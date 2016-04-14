package data.parsing;

public class SplitBinaryConverter implements BinaryClassConverter {
	
	private double middle;

	public SplitBinaryConverter(double middle) {
		this.middle = middle;
	}

	@Override
	public boolean getCategory(String input) {
		return Double.parseDouble(input) < middle;
	}

}
