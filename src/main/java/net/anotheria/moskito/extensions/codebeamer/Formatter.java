package net.anotheria.moskito.extensions.codebeamer;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public enum Formatter {
	INSTANCE;
	
	private Map<String, Map<FormatterAttribute, String>> formatterAttributes = new HashMap<String, Map<FormatterAttribute, String>>();
	
	public void addAttribute(String mBeanServerName, String mBeanId, String attributeName, FormatterAttribute attribute, String value) {
		final String key = String.format("%s %s %s", mBeanServerName, mBeanId, attributeName);
		if (!formatterAttributes.containsKey(key)) {
			formatterAttributes.put(key, new EnumMap<FormatterAttribute, String>(FormatterAttribute.class));
		}
		Map<FormatterAttribute, String> formatterAttribute = formatterAttributes.get(key);
		formatterAttribute.put(attribute, value);
	}
	
	public Map<String, Map<FormatterAttribute, String>> getFormatterAttributes() {
		return formatterAttributes;
	}
}
