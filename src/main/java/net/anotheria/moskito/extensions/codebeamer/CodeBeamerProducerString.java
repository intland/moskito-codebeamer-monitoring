package net.anotheria.moskito.extensions.codebeamer;

public class CodeBeamerProducerString extends AbstractCodeBeamerProducer<String> {
    
    public CodeBeamerProducerString(String serverName, String mBeanName, String mBeanId, String attributeName) {
    	super(serverName, mBeanName, mBeanId, attributeName);
    }

    @Override
	public void updateCodeBeamerInfoAttributeValue(Object value) {
		this.stats.updateCodeBeamerInfoAttributeValue(String.valueOf(value));
	}

	@Override
	protected CodeBeamerStats<String> getStats(String producerId) {
		return new CodeBeamerStatsString(producerId);
	}
}
