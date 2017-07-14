package net.anotheria.moskito.extensions.codebeamer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.anotheria.moskito.extensions.codebeamer.MBeanProducer.MBean;

public class CodeBeamerProducerFactory {

	/**
	 * Logger.
	 */
	private static Logger log = LoggerFactory.getLogger(AbstractCodeBeamerProducer.class);
	   
	public static enum Type {
		LONG("long"), DOUBLE("double"), LICENSE_INFO("licenseInfo"), STRING("String");
		
		private String type;
		
		private Type(String type) {
			this.type = type;
		}
		
		public String getType() {
			return type;
		}
		
		public static Type getInstance(String type) {
			Type instance = null;
			if (StringUtils.isEmpty(type)) {
				return instance;
			}
			for (Type tmp : Type.values()) {
				if (tmp.getType().equalsIgnoreCase(type)) {
					instance = tmp;
					break;
				}
			}
			return instance;
		}
	}
	
	public static List<CodeBeamerStatsProducer<?>> createProducers(Type type, String name, String mBeanName, String mBeanId, String attributeName) {
		List<CodeBeamerStatsProducer<?>> producers = new ArrayList<CodeBeamerStatsProducer<?>>();
		switch (type) {
			case LONG: {
				producers.add(new CodeBeamerProducerLong(name, mBeanName, mBeanId, attributeName));
			} break;
			case DOUBLE: {
				producers.add(new CodeBeamerProducerDouble(name, mBeanName, mBeanId, attributeName));
			} break;
	
			case STRING: {
				producers.add(new CodeBeamerProducerString(name, mBeanName, mBeanId, attributeName));
			} break;

			case LICENSE_INFO: {
				MBeanProducer mBeanProducer = MBeanProducer.INSTANCE;
				mBeanProducer.refreshMBeans();
				MBean mBean = mBeanProducer.getMbean(name, mBeanName);
				log.info("licnse info mbean: " + ReflectionToStringBuilder.toString(mBean));
				List<String> licenses = LicenseUtil.obtainLicensesWithTypes(mBean);
				log.info("licenses: " + ReflectionToStringBuilder.toString(licenses));
				for (String license : licenses) {
					log.info("add license info producer: " + license);
					String[] tokens = license.split(";");
					String producerId = name + "_" + mBeanId + "_" + attributeName + "_" + tokens[0] + "_" + tokens[1];
					CodeBeamerProducerLicenseInfo codeBeamerProducerLicenseInfo = new CodeBeamerProducerLicenseInfo(name, mBeanName, mBeanId, attributeName, producerId);
					codeBeamerProducerLicenseInfo.setLicenseName(tokens[0]);
					codeBeamerProducerLicenseInfo.setLicenseType(tokens[1]);
					producers.add(codeBeamerProducerLicenseInfo);
				}
			} break;
		}
		return producers;
	}
}
