package data.parsing;

public interface FeatureLearner {
	
	public void learn(String input);
	
	public FeatureMapper createMapper();
}
