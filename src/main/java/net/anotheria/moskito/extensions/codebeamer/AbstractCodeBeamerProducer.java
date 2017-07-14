package net.anotheria.moskito.extensions.codebeamer;

import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.anotheria.moskito.core.util.BuiltinUpdater;
import net.anotheria.moskito.extensions.codebeamer.MBeanProducer.MBean;

public abstract class AbstractCodeBeamerProducer<T> implements CodeBeamerStatsProducer<T> {

	/**
    * Logger.
    */
   private static Logger log = LoggerFactory.getLogger(AbstractCodeBeamerProducer.class);
   /**
    * The id of the producer.
    */
   protected String producerId;
   /**
    * Stats.
    */
   protected CodeBeamerStats<T> stats;
   /**
    * Cached stats list.
    */
   protected List<CodeBeamerStats<T>> statsList;
   
   protected String serverName;
   protected String mBeanName;
   protected String attributeName;
   
   public AbstractCodeBeamerProducer(String serverName, String mBeanName, String mBeanId, String attributeName) {
       this(serverName, mBeanName, mBeanId, attributeName, serverName + "_" + mBeanId + "_" + attributeName);
   }
   
   public AbstractCodeBeamerProducer(String serverName, String mBeanName, String mBeanId, String attributeName, String producerId) {
       this.producerId = producerId;
       this.serverName = serverName;
       this.mBeanName = mBeanName;
       this.attributeName = attributeName;
       statsList = new CopyOnWriteArrayList<CodeBeamerStats<T>>();
       stats = getStats(producerId);
       statsList.add(stats);

       BuiltinUpdater.addTask(new TimerTask() {
           @Override
           public void run() {
               readData();
           }
       });
   }
   
   protected abstract CodeBeamerStats<T> getStats(String producerId);
   
   @Override
   public String getCategory() {
       return "memory";
   }

   @Override
   public String getProducerId() {
       return producerId;
   }

   @Override
   public List<CodeBeamerStats<T>> getStats() {
       return statsList;
   }

   @Override
   public String getSubsystem() {
       return "plugins";
   }

   protected void readData() {
		log.info("read data is called");
	   	MBean mBean = MBeanProducer.INSTANCE.getMbean(serverName, mBeanName);
	   	if (mBean == null) {
	   		log.info("MBean is null, serverName: " + serverName + ", mBeanName: " + mBeanName);
	   		return;
	   	} else {
	   		log.info("read mbean: " + ReflectionToStringBuilder.toString(mBean));
	   	}
	   	
	   	updateCodeBeamerInfoAttributeValue(mBean.getAttributes().get(attributeName));
   }

   @Override
   public String toString(){
       return super.toString()+ ' ' +this.getClass().getSimpleName();
   }
}
