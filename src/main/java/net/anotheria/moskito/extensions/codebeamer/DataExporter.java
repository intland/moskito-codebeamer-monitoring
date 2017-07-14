package net.anotheria.moskito.extensions.codebeamer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.anotheria.moskito.core.accumulation.AccumulatedValue;
import net.anotheria.moskito.core.accumulation.Accumulator;

public enum DataExporter {

	
	INSTANCE;
	
	private static final Logger log = LoggerFactory.getLogger(DataExporter.class);
	private Object lock = new Object();
	
	public void load(String file, List<Accumulator> accumulators) {
		synchronized (lock) {
			InputStream input = null;
			try {
				File configFile = new File(file);
				if (!configFile.exists()) {
					return ;
				}
				input = new FileInputStream(configFile);
				Accumulator current = null;
				for (LineIterator it = IOUtils.lineIterator(input, StandardCharsets.UTF_8.name()); it.hasNext();) {
					String line = it.nextLine();
					if (StringUtils.isNotEmpty(line)) {
						if (line.startsWith("#") && line.length() > 1) {
							current = null;
							String accumulatorName = line.substring(1);
							for (Accumulator accumulator : accumulators) {
								if (accumulator.getName().equalsIgnoreCase(accumulatorName)) {
									current = accumulator;
									break;
								}
							}
						} else if (current != null) {
							String[] tokens = line.split(";");
							String value = tokens.length == 2 ? tokens[1] : "";
							current.addValue(new AccumulatedValue(value, Long.valueOf(tokens[0]).longValue()));
						}
					}
				}
			} catch (Exception ex) {
				log.error("Error occurred when tried to load accumulators data.", ex);
			} finally {
				IOUtils.closeQuietly(input);
			}
		}
	}
	
	public void export(String file, List<Accumulator> accumulators) {
		synchronized (lock) {
			OutputStream writer = null;
			try {
				writer = new FileOutputStream(new File(file));
				for (Accumulator accumulator : accumulators) {
					List<String> data = new ArrayList<String>();
					data.add(String.format("#%s", accumulator.getName()));
					for (AccumulatedValue accumulatedValue : accumulator.getValues()) {
						data.add(String.format("%s;%s", accumulatedValue.getTimestamp(), accumulatedValue.getValue()));
					}
					IOUtils.writeLines(data, IOUtils.LINE_SEPARATOR, writer, StandardCharsets.UTF_8.name());
				}
			} catch (Exception ex) {
				log.error("Error occured when tried to export accumulator data.", ex);
			} finally {
				IOUtils.closeQuietly(writer);
			}
		}
	}
}
