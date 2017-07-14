package net.anotheria.moskito.extensions.codebeamer;

import org.apache.commons.lang.StringUtils;

public enum FormatterAttribute {

	NAME("statName"), PREFIX("prefix"), SUFFIX("suffix"), DECIMAL_PLACES("decimalPlaces"), VALUE("value");
	
	private String name;
	
	private FormatterAttribute(String name) {
		this.name = name;
	}
	
	public static FormatterAttribute getInstance(String name) {
		FormatterAttribute instance = null;
		if (StringUtils.isEmpty(name)) {
			return instance;
		}
		for (FormatterAttribute formatterAttribute : FormatterAttribute.values()) {
			if (formatterAttribute.getName().equalsIgnoreCase(name)) {
				instance = formatterAttribute;
				break;
			}
		}
		return instance;
	}
	
	public String getName() {
		return name;
	}
}
