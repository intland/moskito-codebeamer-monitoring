package net.anotheria.moskito.extensions.codebeamer;

public class CodeBeamerProducerDouble extends AbstractCodeBeamerProducer<Double> {

    public CodeBeamerProducerDouble(String serverName, String mBeanName, String mBeanId, String attributeName) {
        super(serverName, mBeanName, mBeanId, attributeName);
    }

	@Override
	public void updateCodeBeamerInfoAttributeValue(Object value) {
		this.stats.updateCodeBeamerInfoAttributeValue(Double.valueOf(String.valueOf(value)));
	}

	@Override
	protected CodeBeamerStats<Double> getStats(String producerId) {
		return new CodeBeamerStatsDouble(producerId);
	}
}
