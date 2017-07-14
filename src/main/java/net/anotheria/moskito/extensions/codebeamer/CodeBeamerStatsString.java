package net.anotheria.moskito.extensions.codebeamer;

import net.anotheria.moskito.core.stats.Interval;
import net.anotheria.moskito.core.stats.StatValueTypes;

public class CodeBeamerStatsString extends AbstractCodeBeamerStats<String> {
	
	public CodeBeamerStatsString(){
		super();
	} 
	
	public CodeBeamerStatsString(String aName){
		super(aName);
	}

	public CodeBeamerStatsString(String aName, Interval[] selectedIntervals) {
		super(aName, selectedIntervals);
	}
	
	@Override
	public void updateCodeBeamerInfoAttributeValue(String value) {
		if (value != null) {
			this.value.setValueAsString(value);
		}
	}

	public String getValue(String intervalName) {
		return value.getValueAsString(intervalName);
	}

	@Override
	protected StatValueTypes getStatValueType() {
		return StatValueTypes.STRING;
	}
}
