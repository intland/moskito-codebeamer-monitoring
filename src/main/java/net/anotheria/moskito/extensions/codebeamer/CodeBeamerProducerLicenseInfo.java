package net.anotheria.moskito.extensions.codebeamer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CodeBeamerProducerLicenseInfo extends AbstractCodeBeamerProducer<Long> {
    
	/**
	 * Logger.
	 */
	private static Logger log = LoggerFactory.getLogger(AbstractCodeBeamerProducer.class);
	
	private String licenseName = "";
	private String licenseType = "";

    public CodeBeamerProducerLicenseInfo(String serverName, String mBeanName, String mBeanId, String attributeName, String producerId) {
    	super(serverName, mBeanName, mBeanId, attributeName, producerId);
    }

    @Override
	public void updateCodeBeamerInfoAttributeValue(Object value) {
	   	log.info("Read licenseInfo attribute");
	   	String licenseInfoAttribute = String.valueOf(value);
	   	log.info("licenseInfo: " + licenseInfoAttribute);
	   	log.info("Obtain license value");
	   	Long licenseValue = LicenseUtil.obtainValue(licenseType, licenseName, licenseInfoAttribute);
	   	log.info("License value: " + licenseValue);
	   	
		this.stats.updateCodeBeamerInfoAttributeValue(licenseValue);
	}

	@Override
	protected CodeBeamerStats<Long> getStats(String producerId) {
		return new CodeBeamerStatsLong(producerId);
	}

	public String getLicenseName() {
		return licenseName;
	}

	public void setLicenseName(String licenseName) {
		this.licenseName = licenseName;
	}
	
	public String getLicenseType() {
		return licenseType;
	}
	public void setLicenseType(String licenseType) {
		this.licenseType = licenseType;
	}
}
