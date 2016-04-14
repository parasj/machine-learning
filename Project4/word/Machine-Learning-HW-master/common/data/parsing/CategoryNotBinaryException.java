package data.parsing;

public class CategoryNotBinaryException extends Exception {

	private static final long serialVersionUID = 1L;

	public CategoryNotBinaryException(FeatureMapper feature) {
		super("Possible values: " + feature.toString());
	}
}
