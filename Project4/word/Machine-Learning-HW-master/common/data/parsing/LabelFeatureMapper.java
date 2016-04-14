package data.parsing;

import java.util.Map;

class LabelFeatureMapper implements FeatureMapper {
	
	private Map<String, Integer> valueMap;

	/**
	 * @param valueMap Must be a 1-to-1 mapping
	 */
	public LabelFeatureMapper(Map<String, Integer> valueMap) {
		this.valueMap = valueMap;
	}

	@Override
	public double[] map(String input) throws UnlearnedValueException {
		if (!valueMap.containsKey(input)) {
			throw new UnlearnedValueException(input);
		}
		
		int index = valueMap.get(input);
		
		if (isBinary()) {
			return new double[] { index };
		} else {
			double[] ret = new double[valueMap.size()];
			ret[index] = 1;
			return ret;
		}
	}
	
	@Override
	public String toString() {
		return valueMap.toString();
	}

	@Override
	public boolean isBinary() {
		return valueMap.size() <= 2;
	}

	@Override
	public int size() {
		return valueMap.size();
	}

}
