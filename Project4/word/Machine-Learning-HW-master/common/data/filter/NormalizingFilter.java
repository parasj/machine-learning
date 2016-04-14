package data.filter;

import shared.DataSet;
import shared.DataSetDescription;
import shared.Instance;
import shared.filt.DataSetFilter;

/**
 * Normalizes the values to between [-1, 1]
 * @author JD Reddaway
 *
 */
public class NormalizingFilter implements DataSetFilter {
	
	private DataSetDescription descriptionToNormalizeUsing;

	public NormalizingFilter(DataSetDescription descriptionToNormalizeUsing) {
		this.descriptionToNormalizeUsing = descriptionToNormalizeUsing;
	}

	@Override
	public void filter(DataSet dataSet) {
		for (Instance instance : dataSet.getInstances()) {
			filter(instance);
		}
	}
	
	public void filter(Instance instance) {
		for (int iFeature = 0; iFeature < instance.size(); iFeature++) {
			double value = instance.getContinuous(iFeature);
			double min = descriptionToNormalizeUsing.getMin(iFeature);
			double range = descriptionToNormalizeUsing.getRange(iFeature);
			double normalizedValue = (value - min) / range * 2 - 1;
			
			instance.getData().set(iFeature, normalizedValue);
		}
	}

}
