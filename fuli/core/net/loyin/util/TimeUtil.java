package net.loyin.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

/**
 * 时间工具类
 * 
 * @author 刘声凤 2012-8-28 下午2:38:31
 */
public class TimeUtil {
	public static TimeUtil me=new TimeUtil();
	/***
	 * 友好时间显示
	 * @param req
	 * @param time
	 * @return
	 */
	public static String friendly_time(HttpServletRequest req, Date time) {
		Locale loc = (req != null) ? req.getLocale() : Locale.getDefault();
		return friendly_time(loc, time);
	}
	/***
	 * 友好时间显示
	 * @param loc
	 * @param time
	 * @return
	 */
	public static String friendly_time(Locale loc, Date time) {
		if (time == null)
			return ResourceUtils.getString("ui", "unknown", loc);
		int ct = (int) ((System.currentTimeMillis() - time.getTime()) / 1000);
		if (ct < 3600)
			return ResourceUtils.getStringForLocale(loc, "ui","minutes_before", Math.max(ct / 60, 1));
		if (ct >= 3600 && ct < 86400)
			return ResourceUtils.getStringForLocale(loc, "ui", "hours_before",ct / 3600);
		if (ct >= 86400 && ct < 2592000) { // 86400 * 30
			int day = ct / 86400;
			return ResourceUtils.getStringForLocale(loc, "ui",(day > 1) ? "days_before" : "yesterday", day);
		}
		if (ct >= 2592000 && ct < 31104000) // 86400 * 30
			return ResourceUtils.getStringForLocale(loc, "ui", "months_before",ct / 2592000);
		return ResourceUtils.getStringForLocale(loc, "ui", "years_before",ct / 31104000);
	}
	/***
	 * 友好时间显示
	 * @param req
	 * @param time 
	 * @param sdf
	 * @return
	 */
	public static String friendly_time(HttpServletRequest req, String time,SimpleDateFormat sdf) {
		try {
			return friendly_time(req, sdf.parse(time));
		} catch (ParseException e) {
		}
		return null;
	}
	public static SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
	/**
	 * 获取日期区间
	 * @param f 0昨天 1今天 2:上周 3:本周 4:下周 5:上月 6:本月 7:下月 8:上季度 9:本季度 10:下季度 11:去年 12今年
	 * @return
	 */
	public static Date[] getDays(int f){
		Date now=new Date(),sdate=now,edate=now;
		try{
		Calendar cal=Calendar.getInstance();
//		cal.setTime(now);
		switch(f){
		case -1://明天
			cal.add(Calendar.DATE,1);
			sdate=edate=cal.getTime();
			break;
		case 0://昨天
			cal.add(Calendar.DATE,-1);
			sdate=edate=cal.getTime();
			break;
		case 1://今天
			break;
		case 2://上周
			cal.add(cal.WEEK_OF_MONTH,-1);
			cal.add(Calendar.DATE, -1 * cal.get(Calendar.DAY_OF_WEEK)+2);
			sdate=cal.getTime();
			cal.add(Calendar.DATE, -1 * cal.get(Calendar.DAY_OF_WEEK)+2+6);
			edate=cal.getTime();
			break;
		case 3://本周
			cal.add(cal.WEEK_OF_MONTH,0);
			cal.add(Calendar.DATE, -1 * cal.get(Calendar.DAY_OF_WEEK)+2);
			sdate=cal.getTime();
			cal.add(Calendar.DATE, -1 * cal.get(Calendar.DAY_OF_WEEK)+2+6);
			edate=cal.getTime();
			break;
		case 4://下周
			cal.add(cal.WEEK_OF_MONTH,1);
			cal.add(Calendar.DATE, -1 * cal.get(Calendar.DAY_OF_WEEK)+2);
			sdate=cal.getTime();
			cal.add(Calendar.DATE, -1 * cal.get(Calendar.DAY_OF_WEEK)+2+6);
			edate=cal.getTime();
			break;
		case 5://上月
			cal.set(cal.DATE,1);
			cal.add(cal.MONTH,-1);
			sdate=cal.getTime();
			cal.set(cal.DATE,getMonthDays(sdate.getYear()+1900,sdate.getMonth()+1));
			edate=cal.getTime();
			break;
		case 6://本月
			cal.set(cal.DATE,1);
			sdate=cal.getTime();
			cal.set(cal.DATE,getMonthDays(sdate.getYear()+1900,sdate.getMonth()+1));
			edate=cal.getTime();
			break;
		case 7://下月
			cal.set(cal.DATE,1);
			cal.add(cal.MONTH,1);
			sdate=cal.getTime();
			cal.set(cal.DATE,getMonthDays(sdate.getYear()+1900,sdate.getMonth()+1));
			edate=cal.getTime();
			break;
		case 8://上季
			int nowmonth_1=now.getMonth()+1;
			int year1=now.getYear()+1900;
			if(nowmonth_1<3){
				nowmonth_1=1 ;
				year1=year1-1;
			}else{
				if(nowmonth_1>3&&nowmonth_1<7){
					nowmonth_1=3;
				}else
				if(nowmonth_1>6&&nowmonth_1<10){
					nowmonth_1=6;
				}else
				if(nowmonth_1>9){
					nowmonth_1=9;
				}
			}
			int qsm_=getQuarterStartMonth(nowmonth_1);
			sdate=df.parse(year1+"-"+(qsm_<10?"0"+qsm_:qsm_)+"-01");
			int qem1=getQuarterEndMonth(nowmonth_1);
			edate=df.parse(year1+"-"+(qem1<10?"0"+qem1:qem1)+"-"+getMonthDays(year1, qem1));
			break;
		case 9://本季
			int nowmonth=now.getMonth()+1;
			int year=now.getYear()+1900;
			int qsm=getQuarterStartMonth(nowmonth);
			sdate=df.parse(year+"-"+(qsm<10?"0"+qsm:qsm)+"-01");
			int qem=getQuarterEndMonth(nowmonth);
			edate=df.parse(year+"-"+(qem<10?"0"+qem:qem)+"-"+getMonthDays(year, qem));
			break;
		case 10://下季
			int nowmonth_2=now.getMonth()+1;
			int year2=now.getYear()+1900;
			if(nowmonth_2>9){
				nowmonth_2=1;
				year2=year2+1;
			}else{
				if(nowmonth_2<4){
					nowmonth_2=4;
				}else
				if(nowmonth_2>3&&nowmonth_2<7){
					nowmonth_2=7;
				}else
				if(nowmonth_2>6&&nowmonth_2<10){
					nowmonth_2=10;
				}
			}
			int qsm_1=getQuarterStartMonth(nowmonth_2);
			sdate=df.parse(year2+"-"+(qsm_1<10?"0"+qsm_1:qsm_1)+"-01");
			int qem2=getQuarterEndMonth(nowmonth_2);
			edate=df.parse(year2+"-"+(qem2<10?"0"+qem2:qem2)+"-"+getMonthDays(year2, qem2));
			break;
		case 11://去年
			int year_n=now.getYear()+1900-1;
			sdate=df.parse(year_n+"-01-01");
			edate=df.parse(year_n+"-12-31");
			break;
		case 12://今年
			SimpleDateFormat df=new SimpleDateFormat("yyyy-01-01");
			sdate=df.parse(df.format(now));
			break;
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println();
		return new Date[]{sdate,edate};
	}
	/**某年某月的天数*/
	public static int getMonthDays(int y,int m){
		if(m==1||m==3||m==5||m==7||m==8||m==10||m==12){
			return 31;
		}else if(m==2){
			if(y%4==0){
				return 29;
			}else{
				return 28;
			}
		}
		return 30;
	}
	/**获取某月的起始季度月*/
	public static int getQuarterStartMonth(int nowMonth){
		if (nowMonth <4)
			return 1;
		if (3 < nowMonth && nowMonth <7)
			return 4;
		if (6 < nowMonth && nowMonth <10)
			return 7;
		if (nowMonth > 9)
			return 10;
		return 0;
	}
	/**获取某月的截至季度月*/
	public static int getQuarterEndMonth(int nowMonth){
		if (nowMonth < 4)
			return 3;
		if (3 < nowMonth && nowMonth < 7)
			return 6;
		if (6 < nowMonth && nowMonth <10)
			return 9;
		if (nowMonth > 9)
			return 12;
		return 0;
	}
	/***
	 * 获取两日期内的所有日期。
	 * @param date1
	 * @param date2
	 * @return
	 */
	private static int day=(1000 * 60 * 60 * 24);
	/**
	 * 获取两个日期间的全部天
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static Date[] getDaysFrom2Date(Date date1,Date date2){
		int days= date2.compareTo(date1);
		List<Date> list=new ArrayList<Date>();
		if(days>0){
			days=(int) ((date2.getTime()-date1.getTime())/day);
			for(int i=2;i<=days+1;i++){
				Calendar cal=Calendar.getInstance();
				cal.setTime(date1);
				cal.set(Calendar.DATE,i);
				list.add(cal.getTime());
			}
		}else if(days<0){
			days=(int) ((date1.getTime()-date2.getTime())/day);
			for(int i=2;i<=1-days;i++){
				Calendar cal=Calendar.getInstance();
				cal.setTime(date2);
				cal.set(Calendar.DATE,i);
				list.add(cal.getTime());
			}
		}
		list.add(date2);
		return list.toArray(new Date[list.size()]);
	}
	/***
	 * 获取两日期间的所有月份
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static String[] getMth42Date(Date date1,Date date2){
		int year1= date1.getYear()+1900;
		int mth1=date1.getMonth()+1;
		int year2= date2.getYear()+1900;
		int mth2=date2.getMonth()+1;
		List<String> list=new ArrayList<String>();
		if(year1<year2){
			for(int i=mth1;i<=12;i++){
				list.add(year1+"-"+(i<10?"0":"")+i);
			}
			for(int i=1;i<=mth2;i++){
				list.add(year2+"-"+(i<10?"0":"")+i);
			}
		}else if(year1>year2){
			for(int i=mth2;i<=12;i++){
				list.add(year2+"-"+(i<10?"0":"")+i);
			}
			for(int i=1;i<=mth1;i++){
				list.add(year1+"-"+(i<10?"0":"")+i);
			}
		}else{
			if(mth1>mth2){
				for(int i=mth2;i<=mth1;i++){
					list.add(year1+"-"+(i<10?"0":"")+i);
				}
			}else{
				for(int i=mth1;i<=mth2;i++){
					list.add(year1+"-"+(i<10?"0":"")+i);
				}
			}
		}
		return list.toArray(new String[list.size()]);
	}
	/**
	 * 获取日期
	 * @param date
	 * @param i 增长数量
	 * @param field 处理字段 如 5：天 2：月 1：年
	 * @return
	 */
	public Date getDaty(Date date,int i,int field){
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		cal.add(field,i);
		return cal.getTime();
	}
	@Test
	public void test(){
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
		/*Date[] dates=getDays(3);
		System.out.println(df.format(dates[0])+" -"+df.format(dates[1]));
		try {
			dates=getDaysFrom2Date(df.parse("2014-06-02"),df.parse("2014-06-08"));
			for(Date date:dates){
				System.out.println(df.format(date));
			}
			String[] mths=getMth42Date(df.parse("2013-06-02"), df.parse("2013-06-02"));
			for(String mth:mths){
				System.out.println(mth);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		Date now;
		try {
			now = df.parse("2014-08-25");
			Calendar cal=Calendar.getInstance();
			cal.setTime(now);
			System.out.println(cal.get(cal.WEEK_OF_MONTH));
			System.out.println(cal.get(cal.WEEK_OF_YEAR));
			System.out.println(TimeUtil.me.getDaty(now, -6,cal.DATE).toLocaleString());
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
