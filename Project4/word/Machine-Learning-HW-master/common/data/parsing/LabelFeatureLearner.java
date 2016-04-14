package data.parsing;

import java.util.HashMap;
import java.util.Map;

public class LabelFeatureLearner implements FeatureLearner {
	
	private Map<String, Integer> valueMap = new HashMap<>();

	@Override
	public void learn(String input) {
		if (!valueMap.containsKey(input)) {
			valueMap.put(input, valueMap.size());
		}
	}

	@Override
	public FeatureMapper createMapper() {
		return new LabelFeatureMapper(valueMap);
	}

}
