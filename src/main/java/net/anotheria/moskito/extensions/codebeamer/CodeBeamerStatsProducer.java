package net.anotheria.moskito.extensions.codebeamer;

import net.anotheria.moskito.core.producers.IStatsProducer;

public interface CodeBeamerStatsProducer<T> extends IStatsProducer<CodeBeamerStats<T>> {

	void updateCodeBeamerInfoAttributeValue(Object value);
}
