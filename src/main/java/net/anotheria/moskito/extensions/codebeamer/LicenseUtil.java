package net.anotheria.moskito.extensions.codebeamer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import net.anotheria.moskito.extensions.codebeamer.MBeanProducer.MBean;

public class LicenseUtil {
	
	public static long obtainValue(String licenseType, String license, String licenseIfno) {
		long value = -1l;
		if (StringUtils.isNotEmpty(licenseIfno)) {
			String[] tokens = licenseIfno.split(";");
			if (ArrayUtils.isNotEmpty(tokens)) {
				String[] licenseTypes = tokens[0].split(":");
				int licenseTypeIndex = -1;
				int counter = 0;
				for (String licenseTypeTmp : licenseTypes) {
					if (licenseTypeTmp.equalsIgnoreCase(licenseType)) {
						licenseTypeIndex = counter;
						break;
					}
					counter++;
				}
				if (licenseTypeIndex > -1 && tokens.length > 1) {
					String[] licensesWithUsers = (String[]) ArrayUtils.subarray(tokens, 1, tokens.length);
					for (String licenseWithUsers : licensesWithUsers) {
						String[] licenseData = licenseWithUsers.split(":");
						if (license.equalsIgnoreCase(licenseData[0])) {
							String[] userNumbers = (String[]) ArrayUtils.subarray(licenseData, 1, licenseData.length);
							value = Long.valueOf(userNumbers[licenseTypeIndex]).longValue();
							break;
						}
					}
				}
			}
		}
		return value;
	}
	
	public static List<String> obtainLicensesWithTypes(MBean mBean)  {
		List<String> licensesWithTypes = new ArrayList<String>();
		String licenseIfno = (String) mBean.getAttributes().get("LicensesInfo");
		if (StringUtils.isNotEmpty(licenseIfno)) {
			String[] tokens = licenseIfno.split(";");
			if (ArrayUtils.isNotEmpty(tokens)) {
				String[] licenseTypes = tokens[0].split(":");
				String[] licensesWithUsers = (String[]) ArrayUtils.subarray(tokens, 1, tokens.length);
				for (String licenseWithUsers : licensesWithUsers) {
					String licenseName = licenseWithUsers.split(":")[0];
					for (String licenseType : licenseTypes) {
						licensesWithTypes.add(licenseName + ";" + licenseType);
					}
				}
			}
		}
		return licensesWithTypes;
	}
}
