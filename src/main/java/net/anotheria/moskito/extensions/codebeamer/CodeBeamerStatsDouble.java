package net.anotheria.moskito.extensions.codebeamer;

import net.anotheria.moskito.core.stats.Interval;
import net.anotheria.moskito.core.stats.StatValueTypes;

public class CodeBeamerStatsDouble extends AbstractCodeBeamerStats<Double> {

	public CodeBeamerStatsDouble(){
		super();
	} 
	
	public CodeBeamerStatsDouble(String aName){
		super(aName);
	}

	public CodeBeamerStatsDouble(String aName,  Interval[] selectedIntervals) {
		super(aName, selectedIntervals);
	}
	
	@Override
	public void updateCodeBeamerInfoAttributeValue(Double value) {
		if (value != null) {
			this.value.setValueAsDouble(value);
		}
	}

	public Double getValue(String intervalName) {
		return Double.valueOf(value.getValueAsDouble(intervalName));
	}

	@Override
	protected StatValueTypes getStatValueType() {
		return StatValueTypes.DOUBLE;
	}
}
