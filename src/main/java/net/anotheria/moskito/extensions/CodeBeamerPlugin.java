package net.anotheria.moskito.extensions;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.configureme.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import net.anotheria.anoplass.api.APIException;
import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.moskito.core.accumulation.Accumulator;
import net.anotheria.moskito.core.accumulation.Accumulators;
import net.anotheria.moskito.core.config.dashboards.ChartConfig;
import net.anotheria.moskito.core.config.dashboards.DashboardConfig;
import net.anotheria.moskito.core.plugins.AbstractMoskitoPlugin;
import net.anotheria.moskito.core.registry.ProducerRegistryFactory;
import net.anotheria.moskito.extensions.codebeamer.AdditionalInfo;
import net.anotheria.moskito.extensions.codebeamer.CBAccumulatorApiFactory;
import net.anotheria.moskito.extensions.codebeamer.CodeBeamerProducerFactory;
import net.anotheria.moskito.extensions.codebeamer.CodeBeamerProducerFactory.Type;
import net.anotheria.moskito.extensions.codebeamer.CodeBeamerProducerLicenseInfo;
import net.anotheria.moskito.extensions.codebeamer.CodeBeamerStatsProducer;
import net.anotheria.moskito.extensions.codebeamer.DataExporter;
import net.anotheria.moskito.extensions.codebeamer.Formatter;
import net.anotheria.moskito.extensions.codebeamer.FormatterAttribute;
import net.anotheria.moskito.extensions.codebeamer.MBeanProducer;
import net.anotheria.moskito.webui.accumulators.api.AccumulatorAPI;
import net.anotheria.moskito.webui.dashboards.api.DashboardAPI;

public class CodeBeamerPlugin extends AbstractMoskitoPlugin {

    /**
     * Logger.
     */
    private static Logger log = LoggerFactory.getLogger(CodeBeamerPlugin.class);
    private static final long DATA_EXPORT_DELAY = 60 * 1000;
    
    public static class PluginConfig {
    	
    	private String dataFilePath;
    	private Integer exportPeriod;
    	private Integer sizeOfHistory;
    	private MBeanServer[] mBeanServers;
    	private Dashboard[] dashboards;

		public MBeanServer[] getMBeanServers() {
			return mBeanServers;
		}

		public String getDataFilePath() {
			return dataFilePath;
		}

		public void setDataFilePath(String dataFilePath) {
			this.dataFilePath = dataFilePath;
		}

		public Integer getExportPeriod() {
			return exportPeriod;
		}

		public void setExportPeriod(Integer exportPeriod) {
			this.exportPeriod = exportPeriod;
		}

		public void setMBeanServers(MBeanServer[] mBeanServers) {
			this.mBeanServers = mBeanServers;
		}

		public Dashboard[] getDashboards() {
			return dashboards;
		}

		public void setDashboards(Dashboard[] dashboards) {
			this.dashboards = dashboards;
		}

		public Integer getSizeOfHistory() {
			return sizeOfHistory;
		}

		public void setSizeOfHistory(Integer sizeOfHistory) {
			this.sizeOfHistory = sizeOfHistory;
		}

		@Override
		public String toString() {
			return "PluginConfig [dataFilePath=" + dataFilePath + ", exportPeriod=" + exportPeriod + ", sizeOfHistory="
					+ sizeOfHistory + ", mBeanServers=" + Arrays.toString(mBeanServers) + ", dashboards="
					+ Arrays.toString(dashboards) + "]";
		}
    }
    
    public static class MBeanServer {
    	
    	private String name;
    	private String host;
    	private String port;
    	private String username;
    	private String password;
    	private MBean[] mBeans;
    	
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getHost() {
			return host;
		}
		public void setHost(String host) {
			this.host = host;
		}
		public String getPort() {
			return port;
		}
		public void setPort(String port) {
			this.port = port;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		public MBean[] getMBeans() {
			return mBeans;
		}
		public void setMBeans(MBean[] mBeans) {
			this.mBeans = mBeans;
		}
		
		@Override
		public String toString() {
			return "MBeanServer [name=" + name + ", host=" + host + ", port=" + port + ", username=" + username
					+ ", password=" + password + ", mBeans=" + Arrays.toString(mBeans) + "]";
		}
    }
    
    public static class MBean {
    	
    	private String mBeanName;
    	private String mBeanId;
    	private Attribute[] attributes;
    	
		public String getMBeanName() {
			return mBeanName;
		}
		public void setMBeanName(String mBeanName) {
			this.mBeanName = mBeanName;
		}
		public String getMBeanId() {
			return mBeanId;
		}
		public void setMBeanId(String mBeanId) {
			this.mBeanId = mBeanId;
		}
		public Attribute[] getAttributes() {
			return attributes;
		}
		public void setAttributes(Attribute[] attributes) {
			this.attributes = attributes;
		}
		
		@Override
		public String toString() {
			return "MBean [mBeanName=" + mBeanName + ", mBeanId=" + mBeanId + ", attributes="
					+ Arrays.toString(attributes) + "]";
		}
    }
    
    public static class Attribute {
    	
    	private String attribute;
    	private String type;
    	private String statName;
    	private String prefix;
    	private String suffix;
    	private String decimalPlaces;
    	private String value;
    	private String additionalInfo;
    	
		public String getAttribute() {
			return attribute;
		}
		public void setAttribute(String attribute) {
			this.attribute = attribute;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getStatName() {
			return statName;
		}
		public void setStatName(String statName) {
			this.statName = statName;
		}
		
		public String getPrefix() {
			return prefix;
		}
		
		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}
		
		public String getSuffix() {
			return suffix;
		}
		
		public void setSuffix(String suffix) {
			this.suffix = suffix;
		}
		
		public String getDecimalPlaces() {
			return decimalPlaces;
		}
		
		public void setDecimalPlaces(String decimalPlaces) {
			this.decimalPlaces = decimalPlaces;
		}
		
		public String getValue() {
			return value;
		}
		
		public void setValue(String value) {
			this.value = value;
		}
		
		public String getAdditionalInfo() {
			return additionalInfo;
		}
		
		public void setAdditionalInfo(String additionalInfo) {
			this.additionalInfo = additionalInfo;
		}
		
		@Override
		public String toString() {
			return "Attribute [attribute=" + attribute + ", type=" + type + ", statName=" + statName + ", prefix="
					+ prefix + ", suffix=" + suffix + ", decimalPlaces=" + decimalPlaces + ", value=" + value
					+ ", additionalInfo=" + additionalInfo + "]";
		}
    }
    
    public static class Dashboard {
    	private String name;
    	private Chart[] charts;
    	
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Chart[] getCharts() {
			return charts;
		}
		public void setCharts(Chart[] charts) {
			this.charts = charts;
		}
		
		@Override
		public String toString() {
			return "Dashboard [name=" + name + ", charts=" + Arrays.toString(charts) + "]";
		}
    }
    
    public static class Chart {
    	private String caption;
    	private String[] accumulatorNames;

		public String getCaption() {
			return caption;
		}

		public void setCaption(String caption) {
			this.caption = caption;
		}

		public String[] getAccumulatorNames() {
			return accumulatorNames;
		}

		public void setAccumulatorNames(String[] accumulatorNames) {
			this.accumulatorNames = accumulatorNames;
		}

		@Override
		public String toString() {
			return "Chart [caption=" + caption + ", accumulatorNames=" + Arrays.toString(accumulatorNames) + "]";
		}
    }
    
    @Override
    public void initialize() {
    	log.info("Iinitialize CodeBeamerPlugin");
    	
		try {
			// load config
			InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("codebeamer-monitoring-plugin.json");
			final PluginConfig pluginConfig = new Gson().fromJson(IOUtils.readInputStreamBufferedAsString(stream, "UTF-8"), PluginConfig.class);
			
			// Factory hack
			APIFinder.cleanUp();
			// add custom factory
			APIFinder.addAPIFactory(AccumulatorAPI.class, new CBAccumulatorApiFactory(pluginConfig.getSizeOfHistory()));
			
			AccumulatorAPI accAPI = APIFinder.findAPI(AccumulatorAPI.class);
			log.info("Custom cb Accumulator API: " + accAPI.getClass());
			
			log.info("Configuration: " + ReflectionToStringBuilder.toString(pluginConfig));
			//read formatters for attributes
			readFromatters(pluginConfig);
			// read server configs and mbeans data
			readServerAndMBeansData(pluginConfig);
			// obtain mbeans from servers
			MBeanProducer.INSTANCE.refreshMBeans();
			
			// create producers and accumulators
			final List<Accumulator> accumulators = createProducersAndAccumulators(pluginConfig);
			// set accumulators in additional info provider
			AdditionalInfo.INSTANCE.setAccumulators(accumulators);
			
			// load accumulator data from file
			DataExporter.INSTANCE.load(pluginConfig.getDataFilePath(), accumulators);
			
			// create timer for exporting accumulators data
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					DataExporter.INSTANCE.export(pluginConfig.getDataFilePath(), accumulators);
				}
			}, DATA_EXPORT_DELAY, pluginConfig.getExportPeriod() * 1000l);
			
			// create dashboards
			createDashboards(pluginConfig);
		} catch (Exception ex) {
			log.error("Error occurred when tried to parse configuration file.", ex);
		}
    }

    private void readFromatters(PluginConfig pluginConfig) {
    	for (MBeanServer server : pluginConfig.getMBeanServers()) {
    		String mBeanServerName = server.getName();
    		for (MBean mBean : server.getMBeans()) {
    			String mBeanId = mBean.getMBeanId();
    			for (Attribute attribute : mBean.getAttributes()) {
    				String attributeName = attribute.getStatName();
    				Formatter.INSTANCE.addAttribute(mBeanServerName, mBeanId, attributeName, FormatterAttribute.NAME, attribute.getStatName());
    				Formatter.INSTANCE.addAttribute(mBeanServerName, mBeanId, attributeName, FormatterAttribute.PREFIX, attribute.getPrefix());
    				Formatter.INSTANCE.addAttribute(mBeanServerName, mBeanId, attributeName, FormatterAttribute.SUFFIX, attribute.getSuffix());
    				Formatter.INSTANCE.addAttribute(mBeanServerName, mBeanId, attributeName, FormatterAttribute.DECIMAL_PLACES, attribute.getDecimalPlaces());
    				Formatter.INSTANCE.addAttribute(mBeanServerName, mBeanId, attributeName, FormatterAttribute.VALUE, attribute.getValue());
    				if (StringUtils.isNotEmpty(attribute.getAdditionalInfo())) {
    					String[] accNames = attribute.getAdditionalInfo().split(",");
    					Map<String, String> generatedAccNames = new HashMap<String, String>();
    					for (String accName : accNames) {
    						String[] tokens = accName.split(":");
    						generatedAccNames.put(String.format("%s %s %s", tokens[0], tokens[1], tokens[2]), tokens[2]);
    					}
    					log.info("Add additional info: " + generatedAccNames.toString());
    					AdditionalInfo.INSTANCE.addAdditionalInfo(String.format("%s %s %s", mBeanServerName, mBeanId, attributeName), generatedAccNames);
    				}
    				if (StringUtils.isNotEmpty(attribute.getValue())) {
    					AdditionalInfo.INSTANCE.addAdditionalInfoAccumulator(String.format("%s %s %s", mBeanServerName, mBeanId, attributeName), attribute.getValue());
    				}
    			}
    		}
    	}
    }
    
	private void createDashboards(final PluginConfig pluginConfig) {
		// wait for creating dashboards
		new Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				DashboardAPI api = APIFinder.findAPI(DashboardAPI.class);
				for (Dashboard dashboard : pluginConfig.getDashboards()) {
					try {
						String name = dashboard.getName();
						log.info("Create dashboard: " + name);
						api.createDashboard(name);
						List<String> chartCaptions = new ArrayList<>();
						for (Chart chart : dashboard.getCharts()) {
							String[] accumulatorNames = chart.getAccumulatorNames();
							api.addChartToDashboard(name, accumulatorNames);
							chartCaptions.add(chart.getCaption());
						}
						DashboardConfig dashboardConfig = api.getDashboardConfig(name);
						int counter = 0;
						for (ChartConfig chart : dashboardConfig.getCharts()) {
							chart.setCaption(chartCaptions.get(counter++));
						}
					} catch (APIException ex) {
						log.error("Error occurred when tried to add accumulators to dashboard.", ex);
					}
				}
			}
		}, 10 * 1000l);
	}

	private List<Accumulator> createProducersAndAccumulators(PluginConfig pluginConfig) {
		List<Accumulator> accumulators = new ArrayList<Accumulator>();
		for (MBeanServer server : pluginConfig.mBeanServers) {
			for (MBean mBean : server.getMBeans()) {
				for (Attribute attribute : mBean.getAttributes()) {
					Type type = Type.getInstance(attribute.getType());
					if (type != null) {
						List<CodeBeamerStatsProducer<?>> producers = CodeBeamerProducerFactory.createProducers(type, server.getName(), mBean.getMBeanName(), mBean.getMBeanId(), attribute.getAttribute());
						for (CodeBeamerStatsProducer<?> producer : producers) {
							log.info("Register producer: " + producer.getProducerId());
							ProducerRegistryFactory.getProducerRegistryInstance().registerProducer(producer);
							log.info("Create accumulator.");
							String accumulatorName = server.getName() + " " + mBean.getMBeanId() + " " + attribute.getStatName();
							if (producer instanceof CodeBeamerProducerLicenseInfo) {
								accumulatorName = producer.getProducerId().replaceAll("_", " ");
							}
							Accumulator accumulator = Accumulators.createAccumulator(accumulatorName, producer.getProducerId(), producer.getProducerId(), "value", "1m");
							accumulator.getDefinition().setAccumulationAmount(pluginConfig.getSizeOfHistory());
							log.info("created accumulator: " + accumulator.getName());
							accumulators.add(accumulator);
							
							// For testing with random generated data
							/*for (int i = 30000; i > 0; --i) {
								accumulator.addValue(new AccumulatedValue(String.valueOf((long)(Math.random() * 10)), ((long)(time - (i * 1000 * 60)))));
							}*/
						}
					}
				}
			}
		}
		return accumulators;
	}

	private void readServerAndMBeansData(PluginConfig pluginConfig) {
		for (MBeanServer server : pluginConfig.mBeanServers) {
			List<net.anotheria.moskito.extensions.codebeamer.MBeanProducer.MBean> mBeans = new ArrayList<net.anotheria.moskito.extensions.codebeamer.MBeanProducer.MBean>();
			for (MBean mBean : server.getMBeans()) {
				Map<String, Object> attributes = new HashMap<String, Object>();
				for (Attribute attribute : mBean.getAttributes()) {
					attributes.put(attribute.getAttribute(), "");
				}
				mBeans.add(new net.anotheria.moskito.extensions.codebeamer.MBeanProducer.MBean(mBean.getMBeanName(), attributes));
			}
			MBeanProducer.INSTANCE.addMBeanServer(server.getName(), server.getHost(), Long.valueOf(server.getPort()).longValue(), server.getUsername(), server.getPassword(), mBeans);
		}
	}

    @Override
    public void deInitialize() {
        super.deInitialize();
    }
    
    
}
