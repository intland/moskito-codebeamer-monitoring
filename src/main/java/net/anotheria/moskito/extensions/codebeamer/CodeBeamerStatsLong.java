package net.anotheria.moskito.extensions.codebeamer;

import net.anotheria.moskito.core.stats.Interval;
import net.anotheria.moskito.core.stats.StatValueTypes;

public class CodeBeamerStatsLong extends AbstractCodeBeamerStats<Long> {
	
	public CodeBeamerStatsLong(){
		super();
	} 
	
	public CodeBeamerStatsLong(String aName){
		super(aName);
	}

	public CodeBeamerStatsLong(String aName, Interval[] selectedIntervals) {
		super(aName, selectedIntervals);
	}
	
	@Override
	public void updateCodeBeamerInfoAttributeValue(Long value) {
		if (value != null) {
			this.value.setValueAsLong(value);
		}
	}

	public Long getValue(String intervalName) {
		return value.getValueAsLong(intervalName);
	}

	@Override
	protected StatValueTypes getStatValueType() {
		return StatValueTypes.LONG;
	}
}
