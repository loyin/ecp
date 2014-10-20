package net.loyin.quartz.job;

import net.loyin.model.BonusRecord;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
/***
 * 奖金每天定时器
 * @author 龙影
 * 2014年10月12日
 */
public class EveryDayJob implements Job {
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try{
			BonusRecord.dao.fa();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
