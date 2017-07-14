package net.anotheria.moskito.extensions.codebeamer;

public class CodeBeamerProducerLong extends AbstractCodeBeamerProducer<Long> {
    
    public CodeBeamerProducerLong(String serverName, String mBeanName, String mBeanId, String attributeName) {
    	super(serverName, mBeanName, mBeanId, attributeName);
    }

    @Override
	public void updateCodeBeamerInfoAttributeValue(Object value) {
		this.stats.updateCodeBeamerInfoAttributeValue(Long.valueOf(String.valueOf(value)));
	}

	@Override
	protected CodeBeamerStats<Long> getStats(String producerId) {
		return new CodeBeamerStatsLong(producerId);
	}
}
