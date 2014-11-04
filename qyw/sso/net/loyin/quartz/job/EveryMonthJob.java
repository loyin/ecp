package net.loyin.quartz.job;

import java.util.Calendar;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
/***
 * 每月定时器
 * @author 龙影
 * 2014年10月12日
 */
public class EveryMonthJob implements Job {
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Calendar cal=Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MONTH,-3);
	}
}
