package net.loyin.jfinal.plugin.quartz;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.jfinal.plugin.IPlugin;
/***
 * 用来处理系统固定的定时器
 * @author 刘声凤
 * 2014年7月3日
 */
public class QuartzPlugin implements IPlugin {
	private static Logger log=Logger.getLogger(QuartzPlugin.class);
	private SchedulerFactory sf = null;
	private Scheduler sched = null;
	private String config = "job.properties";
	private Properties properties;
	public QuartzPlugin(String config) {
		this.config = config;
	}

	public QuartzPlugin() {
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean start() {
		sf = new StdSchedulerFactory();
		try {
			sched = sf.getScheduler();
		} catch (SchedulerException e) {
			new RuntimeException(e); 
		}
		loadProperties();
		Enumeration<?> enums = properties.keys();
		while (enums.hasMoreElements()) {
			String key = enums.nextElement() + "";
			if (!key.endsWith("job")) {
				continue;
			}
			String cronKey = key.substring(0, key.lastIndexOf("job")) + "cron";
			String enable = key.substring(0, key.lastIndexOf("job")) + "enable";
			if (!isEnableJob(enable)) {
				continue;
			} 
			String jobClassName = properties.get(key) + "";
			String jobCronExp = properties.getProperty(cronKey);
			Class clazz;
			try {
				clazz = Class.forName(jobClassName);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
			JobDetail job = newJob(clazz).withIdentity(jobClassName, jobClassName).build();
			CronTrigger trigger = newTrigger().withIdentity(jobClassName, jobClassName)
					.withSchedule(cronSchedule(jobCronExp)).build();
			Date ft = null;
			try {
				ft = sched.scheduleJob(job, trigger);
				sched.start();
			} catch (SchedulerException e) {
				new RuntimeException(e);
			}
			log.info(job.getKey() + " has been scheduled to run at: " + ft + " and repeat based on expression: "
					+ trigger.getCronExpression());
		}
		return true;
	} 


	private boolean isEnableJob(String enableKey) {
		Object enable = properties.get(enableKey);
		if(enable!=null&&"false".equalsIgnoreCase((enable+"").trim())){
			return false;
		}
		return true;
	}

	private void loadProperties() {
		properties = new Properties(); 
		InputStream is = QuartzPlugin.class.getClassLoader().getResourceAsStream(config);
		try {
			properties.load(is);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		log.info("------------load Propteries---------------");
		log.info(properties);
		log.info("------------------------------------------");
	}

	public boolean stop() {
		try {
			sched.shutdown();
		} catch (SchedulerException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
