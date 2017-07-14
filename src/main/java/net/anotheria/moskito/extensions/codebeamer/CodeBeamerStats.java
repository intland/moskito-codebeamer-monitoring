package net.anotheria.moskito.extensions.codebeamer;

import net.anotheria.moskito.core.producers.IStats;

public interface CodeBeamerStats<T> extends IStats {

	void updateCodeBeamerInfoAttributeValue(T value);
}
