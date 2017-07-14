package net.anotheria.moskito.extensions.codebeamer;

import net.anotheria.moskito.core.predefined.Constants;
import net.anotheria.moskito.core.producers.GenericStats;
import net.anotheria.moskito.core.stats.Interval;
import net.anotheria.moskito.core.stats.StatValueTypes;
import net.anotheria.moskito.core.stats.TimeUnit;
import net.anotheria.moskito.core.stats.TypeAwareStatValue;
import net.anotheria.moskito.core.stats.impl.StatValueFactory;

public abstract class AbstractCodeBeamerStats<T> extends GenericStats implements CodeBeamerStats<T> {

	protected TypeAwareStatValue value;
	
	public AbstractCodeBeamerStats() {
		this("unnamed", Constants.getDefaultIntervals());
	} 
	
	public AbstractCodeBeamerStats(String aName){
		this(aName, Constants.getDefaultIntervals());
	}

	public AbstractCodeBeamerStats(String aName,  Interval[] selectedIntervals) {
		super(aName);
		value = StatValueFactory.createStatValue(getStatValueType(), "value", selectedIntervals);

		putValue(value);
	}
	
	protected abstract StatValueTypes getStatValueType();
	
	@Override
	public String toStatsString(String intervalName, TimeUnit unit) {
		StringBuilder sb = new StringBuilder();
		sb.append(" value: ").append(getValue(intervalName));
		return sb.toString();
	}
	
	public abstract T getValue(String intervalName);

	@Override
	public String getValueByNameAsString(String valueName, String intervalName, TimeUnit timeUnit) {
		if (valueName==null || valueName.isEmpty())
			throw new AssertionError("Value name can not be empty");
		valueName = valueName.toLowerCase();

		if (valueName.equals("value")) {
			return String.valueOf(getValue(intervalName));
		}

		return null;
	}
}
