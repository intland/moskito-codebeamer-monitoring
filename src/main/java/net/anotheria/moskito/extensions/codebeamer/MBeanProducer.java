package net.anotheria.moskito.extensions.codebeamer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum MBeanProducer {

	INSTANCE;
	
	private Object lock = new Object();
	private static Logger log = LoggerFactory.getLogger(MBeanProducer.class);
	private static final int THRESHOLD = 3;
	
	private List<MBeanServer> mBeanServers = new ArrayList<MBeanServer>();
	
	private Map<String, Map<String, MBean>> mBeans = new ConcurrentHashMap<String, Map<String, MBean>>();
	
	public static class MBeanServer {
		private String name;
		private String host;
		private long port;
		private String username;
		private String password;
		private List<MBean> mBeans;
		
		public MBeanServer(String name, String host, long port, String username, String password, List<MBean> mBeans) {
			super();
			this.name = name;
			this.host = host;
			this.port = port;
			this.username = username;
			this.password = password;
			this.mBeans = mBeans;
		}

		public String getHost() {
			return host;
		}

		public long getPort() {
			return port;
		}

		public String getUsername() {
			return username;
		}

		public String getPassword() {
			return password;
		}
		
		public String getName() {
			return name;
		}

		public List<MBean> getmBeans() {
			return mBeans;
		}
	}
	
	public static class MBean {

		private String name;
		private Map<String, Object> attributes;
		/**
		 * @param name
		 * @param attributes
		 */
		public MBean(String name, Map<String, Object> attributes) {
			super();
			this.name = name;
			this.attributes = attributes;
		}
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @return the attributes
		 */
		public Map<String, Object> getAttributes() {
			return attributes;
		}
	}
	
	private MBeanProducer() {
		new Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				refreshMBeans();
			}
		}, 0, 60 * 1000);
	}

	public void refreshMBeans() {
		synchronized (lock) {
			for (MBeanServer mBeanServer : mBeanServers) {
				MBeanServerConnection server = null;
				JMXConnector jmxConnector = null;
				for (int i = 0; i < THRESHOLD; ++i) {
					try {
						String url = "service:jmx:rmi:///jndi/rmi://" + mBeanServer.getHost() + ":" + mBeanServer.getPort() + "/jmxrmi";
						JMXServiceURL serviceUrl = new JMXServiceURL(url);
						Map<String, String[]> env = null;
						
						if (StringUtils.isNotEmpty(mBeanServer.getUsername()) && StringUtils.isNotEmpty(mBeanServer.getPassword())) {
							env = new HashMap<String, String[]>();
							String[] credentials = {mBeanServer.getUsername(), mBeanServer.getPassword()};
							env.put(JMXConnector.CREDENTIALS, credentials);
						}
						
						jmxConnector = JMXConnectorFactory.connect(serviceUrl, env);
						
					    server = jmxConnector.getMBeanServerConnection();
	
					    for (MBean mBean : mBeanServer.getmBeans()) {
					    
					    	ObjectName objectName = new ObjectName(mBean.getName());
					    	log.info("object name: " + objectName.getCanonicalName());
					    	if (server.isRegistered(objectName)) {
					    		log.info("object is found");
							    AttributeList attributeList = server.getAttributes(objectName, mBean.getAttributes().keySet().toArray(new String[0]));
								if (attributeList != null && CollectionUtils.isNotEmpty(attributeList.asList())) {
									Map<String, Object> attributeMap = new HashMap<String, Object>();
									for (Attribute attribute : attributeList.asList()) {
										attributeMap.put(attribute.getName(), String.valueOf(attribute.getValue()));
									}
									log.info("add mbean to the mbeans container");
									MBean newMbean = new MBean(mBean.getName(), attributeMap);
									log.info("mbean: " + ReflectionToStringBuilder.toString(newMbean));
									if (!mBeans.containsKey(mBeanServer.getName())) {
										mBeans.put(mBeanServer.getName(), new ConcurrentHashMap<String, MBean>());
									}
									Map<String, MBean> mBeansByServer = mBeans.get(mBeanServer.getName());
									mBeansByServer.put(newMbean.getName(), newMbean);
									log.info("mbeans container after put new mbean:");
									for (Entry<String, Map<String, MBean>> entry : mBeans.entrySet()) {
										log.info("server: " + entry.getKey());
										for (Entry<String, MBean> mbean : entry.getValue().entrySet()) {
											log.info("mbean in container: " + ReflectionToStringBuilder.toString(mbean));
										}
									}
								}
					    	}
					    }
					    break;
					} catch (Exception ex) {
			    		log.error("Error occurred when tried to otain MBean attributes.", ex);
			    		if (i <  THRESHOLD - 1) {
				    		try {
				    			// wait one second and try to connect to the server again
				    		    Thread.sleep(1000);
				    		} catch(InterruptedException iex) {
				    		    Thread.currentThread().interrupt();
				    		}
			    		}
			    	} finally {
						if (jmxConnector != null) {
							try {
								jmxConnector.close();
							} catch (IOException ex) {
								log.error("Error occurred when tried to close jmxConnector.", ex);
							}
						}
					}
				}
			}
		}
	}
	
	public void addMBeanServer(String name, String host, long port, String username, String password, List<MBean> mBeans) {
		MBeanServer server = new MBeanServer(name, host, port, username, password, mBeans);
		mBeanServers.add(server);
	}
	
	public MBean getMbean(String serverName, String mBeanName) {
		if (StringUtils.isEmpty(serverName) || StringUtils.isEmpty(mBeanName)) {
			return null;
		}
		synchronized (lock) {
			if (!mBeans.containsKey(serverName)) {
				return null;
			}
			return mBeans.get(serverName).get(mBeanName);
		}
	}
}
