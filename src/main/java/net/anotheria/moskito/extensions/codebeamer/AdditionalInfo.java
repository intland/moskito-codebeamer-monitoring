package net.anotheria.moskito.extensions.codebeamer;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.anotheria.moskito.core.accumulation.AccumulatedValue;
import net.anotheria.moskito.core.accumulation.Accumulator;

public enum AdditionalInfo {

	INSTANCE;
	
	private Map<String, Map<String, String>> additionalInfoForAccumulators = new HashMap<String, Map<String, String>>();
	private List<Accumulator> accumulators = null;
	private Map<String, String> additionalInfoAccumulators = new HashMap<String, String>();
	
	public void addAdditionalInfo(String accumulatorName, Map<String, String> additionalInfoAccumulatorName) {
		additionalInfoForAccumulators.put(accumulatorName, additionalInfoAccumulatorName);
	}
	
	public void addAdditionalInfoAccumulator(String accumulatorName, String format) {
		additionalInfoAccumulators.put(accumulatorName, format);
	}

	public void setAccumulators(List<Accumulator> accumulators) {
		this.accumulators = accumulators;
	}
	
	public Map<String, Map<String, String>> getAdditionalInfoForAccumulators() {
		return additionalInfoForAccumulators;
	}
	
	public Map<String, Map<String, String>> getAdditionalInfoForAccumulator(String additionalAccumulatorName, String timestamp) {
		Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();
		long timestampInLong = Long.valueOf(timestamp);
		for (Accumulator accumulator : accumulators) {
			if (accumulator.getName().equalsIgnoreCase(additionalAccumulatorName)) {
				for (AccumulatedValue accumulatedValue : accumulator.getValues()) {
					// trunc timestamp to minute
					Date date = new Date(accumulatedValue.getTimestamp());
					Calendar cal = Calendar.getInstance();
					cal.setTime(date);
					cal.set(Calendar.MILLISECOND, 0);
			        cal.set(Calendar.SECOND, 0);

			        // check cal
					if (cal.getTimeInMillis() == timestampInLong) {
						Map<String, String> data = new HashMap<String, String>();
						data.put("value", accumulatedValue.getValue());
						data.put("format", additionalInfoAccumulators.get(additionalAccumulatorName));
						result.put(additionalAccumulatorName, data);
					}
				}
				break;
			}
		}
		return result;
	}
}
