package net.loyin.quartz.job;

import java.util.Calendar;
import java.util.Date;

import net.loyin.model.BonusRecord;
import net.loyin.model.GoldTransDetail;
import net.loyin.model.Notice;
import net.loyin.model.Trans;

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
		//奖金记录每个月清除一次前3个月数据
		BonusRecord.dao.clear(cal.getTime());
		//玩家公告记录每个月清除一次前3个月数据
		Notice.dao.clear(cal.getTime());
		//金币记录明细
		GoldTransDetail.dao.clear(cal.getTime());
		//交易记录明细
		Trans.dao.clear(cal.getTime());
	}
}
