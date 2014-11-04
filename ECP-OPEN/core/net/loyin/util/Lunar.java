package net.loyin.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/**
 * 天干地支计算
 * @author loyin
 */
public class Lunar {
	private static final SimpleDateFormat dateformart=new SimpleDateFormat("yyyy-MM-dd");
	private int year;
	private int month;
	private int day;
	private boolean leap;
	final static String chineseNumber[] = { "正", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "腊" };
	final static String chineseNumber1[] = { "一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二" };
	final static String[] gan = { "甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸" };
	final static String[] zhi = { "子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥" };
	static SimpleDateFormat chineseDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
	final static long[] lunarInfo = new long[] { 0x04bd8, 0x04ae0, 0x0a570, 0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2, 0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250,
			0x1d255, 0x0b540, 0x0d6a0, 0x0ada2, 0x095b0, 0x14977, 0x04970, 0x0a4b0, 0x0b4b5, 0x06a50, 0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970, 0x06566, 0x0d4a0,
			0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0, 0x1c8d7, 0x0c950, 0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4, 0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557,
			0x06ca0, 0x0b550, 0x15355, 0x04da0, 0x0a5d0, 0x14573, 0x052d0, 0x0a9a8, 0x0e950, 0x06aa0, 0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260, 0x0f263, 0x0d950,
			0x05b57, 0x056a0, 0x096d0, 0x04dd5, 0x04ad0, 0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b5a0, 0x195a6, 0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50,
			0x06d40, 0x0af46, 0x0ab60, 0x09570, 0x04af5, 0x04970, 0x064b0, 0x074a3, 0x0ea50, 0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0, 0x0c960, 0x0d954, 0x0d4a0, 0x0da50,
			0x07552, 0x056a0, 0x0abb7, 0x025d0, 0x092d0, 0x0cab5, 0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9, 0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930, 0x07954, 0x06aa0,
			0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65, 0x0d530, 0x05aa0, 0x076a3, 0x096d0, 0x04bd7, 0x04ad0, 0x0a4d0, 0x1d0b6, 0x0d250, 0x0d520, 0x0dd45,
			0x0b5a0, 0x056d0, 0x055b2, 0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0 };

	final static Map<Integer, Object[]> xingsu = new HashMap<Integer, Object[]>();
	static {
		xingsu.put(1, ("室 壁 奎 娄 胄 昂 毕 觜 参 井 鬼 柳 星 张 翼 轸 角 亢 氐 房 心 尾 箕 斗 女 虚 危 室 壁 奎".split("\\s+")));
		xingsu.put(2, ("奎 娄 胄 昂 毕 觜 参 井 鬼 柳 星 张 翼 轸 角 亢 氐 房 心 尾 箕 斗 女 虚 危 室 壁 奎 娄 胄".split("\\s+")));
		xingsu.put(3, ("胄 昂 毕 觜 参 井 鬼 柳 星 张 翼 轸 角 亢 氐 房 心 尾 箕 斗 女 虚 危 室 壁 奎 娄 胄 昂 毕".split("\\s+")));
		xingsu.put(4, ("毕 觜 参 井 鬼 柳 星 张 翼 轸 角 亢 氐 房 心 尾 箕 斗 女 虚 危 室 壁 奎 娄 胄 昂 毕 觜 参".split("\\s+")));
		xingsu.put(5, ("参 井 鬼 柳 星 张 翼 轸 角 亢 氐 房 心 尾 箕 斗 女 虚 危 室 壁 奎 娄 胄 昂 毕 觜 参 井 鬼".split("\\s+")));
		xingsu.put(6, ("鬼 柳 星 张 翼 轸 角 亢 氐 房 心 尾 箕 斗 女 虚 危 室 壁 奎 娄 胄 昂 毕 觜 参 井 鬼 柳 星".split("\\s+")));
		xingsu.put(7, ("张 翼 轸 角 亢 氐 房 心 尾 箕 斗 女 虚 危 室 壁 奎 娄 胄 昂 毕 觜 参 井 鬼 柳 星 张 翼 轸".split("\\s+")));
		xingsu.put(8, ("角 亢 氐 房 心 尾 箕 斗 女 虚 危 室 壁 奎 娄 胄 昂 毕 觜 参 井 鬼 柳 星 张 翼 轸 角 亢 氐".split("\\s+")));
		xingsu.put(9, ("氐 房 心 尾 箕 斗 女 虚 危 室 壁 奎 娄 胄 昂 毕 觜 参 井 鬼 柳 星 张 翼 轸 角 亢 氐 房 心".split("\\s+")));
		xingsu.put(10, ("心 尾 箕 斗 女 虚 危 室 壁 奎 娄 胄 昂 毕 觜 参 井 鬼 柳 星 张 翼 轸 角 亢 氐 房 心 尾 箕".split("\\s+")));
		xingsu.put(11, ("斗 女 虚 危 室 壁 奎 娄 胄 昂 毕 觜 参 井 鬼 柳 星 张 翼 轸 角 亢 氐 房 心 尾 箕 斗 女 虚".split("\\s+")));
		xingsu.put(12, ("虚 危 室 壁 奎 娄 胄 昂 毕 觜 参 井 鬼 柳 星 张 翼 轸 角 亢 氐 房 心 尾 箕 斗 女 虚 危 室".split("\\s+")));
	}
	/**
	 * 六十甲子
	 */
	public static final String[] jiazi = { "甲子", "乙丑", "丙寅", "丁卯", "戊辰", "己巳", "庚午", "辛未", "壬申", "癸酉", "甲戌", "乙亥", "丙子", "丁丑", "戊寅", "己卯", "庚辰", "辛巳", "壬午", "癸未", "甲申", "乙酉",
			"丙戌", "丁亥", "戊子", "己丑", "庚寅", "辛卯", "壬辰", "癸巳", "甲午", "乙未", "丙申", "丁酉", "戊戌", "己亥", "庚子", "辛丑", "壬寅", "癸卯", "甲辰", "乙巳", "丙午", "丁未", "戊申", "己酉", "庚戌", "辛亥", "壬子", "癸丑",
			"甲寅", "乙卯", "丙辰", "丁巳", "戊午", "己未", "庚申", "辛酉", "壬戌", "癸亥" };
	private Calendar calendar;
	Date baseDate = null;

	// ====== 传回农历 y年的总天数
	final private static int yearDays(int y) {
		int i, sum = 348;
		for (i = 0x8000; i > 0x8; i >>= 1) {
			if ((lunarInfo[y - 1900] & i) != 0)
				sum += 1;
		}
		return (sum + leapDays(y));
	}

	// ====== 传回农历 y年闰月的天数
	final private static int leapDays(int y) {
		if (leapMonth(y) != 0) {
			if ((lunarInfo[y - 1900] & 0x10000) != 0)
				return 30;
			else
				return 29;
		} else
			return 0;
	}

	// ====== 传回农历 y年闰哪个月 1-12 , 没闰传回 0
	final private static int leapMonth(int y) {
		return (int) (lunarInfo[y - 1900] & 0xf);
	}

	// ====== 传回农历 y年m月的总天数
	final private static int monthDays(int y, int m) {
		if ((lunarInfo[y - 1900] & (0x10000 >> m)) == 0)
			return 29;
		else
			return 30;
	}

	// ====== 传回农历 y年的生肖
	final public String animalsYear() {
		final String[] Animals = new String[] { "鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪" };
		return Animals[(year - 4) % 12];
	}

	// ====== 传入 月日的offset 传回干支, 0=甲子
	final private static String cyclicalm(int num) {
		final String[] Gan = new String[] { "甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸" };
		final String[] Zhi = new String[] { "子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥" };
		return (Gan[num % 10] + Zhi[num % 12]);
	}

	// ====== 传入 offset 传回干支, 0=甲子
	final public String cyclical() {
		int num = year - 1900 + 36;
		return (cyclicalm(num));
	}

	/**
	 * 传出y年m月d日对应的农历. yearCyl3:农历年与1864的相差数 ? monCyl4:从1900年1月31日以来,闰月数
	 * dayCyl5:与1900年1月31日相差的天数,再加40 ?
	 * 
	 * @param cal
	 * @return
	 */
	public Lunar(Calendar cal) {
		@SuppressWarnings("unused")
		int monCyl;
		int leapMonth = 0;
		baseDate = null;
		this.calendar = cal;
		try {
			baseDate = chineseDateFormat.parse("1900年1月31日");
		} catch (ParseException e) {
			e.printStackTrace();
			// To change body of catch statement use
			// Options | File Templates.
		}
		// 求出和1900年1月31日相差的天数
		int offset = (int) ((cal.getTime().getTime() - baseDate.getTime()) / 86400000L);
		monCyl = 14;

		// 用offset减去每农历年的天数
		// 计算当天是农历第几天
		// i最终结果是农历的年份
		// offset是当年的第几天
		int iYear, daysOfYear = 0;
		for (iYear = 1900; iYear < 2050 && offset > 0; iYear++) {
			daysOfYear = yearDays(iYear);
			offset -= daysOfYear;
			monCyl += 12;
		}
		if (offset < 0) {
			offset += daysOfYear;
			iYear--;
			monCyl -= 12;
		}
		// 农历年份
		year = iYear;
		leapMonth = leapMonth(iYear); // 闰哪个月,1-12
		leap = false;

		// 用当年的天数offset,逐个减去每月（农历）的天数，求出当天是本月的第几天
		int iMonth, daysOfMonth = 0;
		for (iMonth = 1; iMonth < 13 && offset > 0; iMonth++) {
			// 闰月
			if (leapMonth > 0 && iMonth == (leapMonth + 1) && !leap) {
				--iMonth;
				leap = true;
				daysOfMonth = leapDays(year);
			} else
				daysOfMonth = monthDays(year, iMonth);

			offset -= daysOfMonth;
			// 解除闰月
			if (leap && iMonth == (leapMonth + 1))
				leap = false;
			if (!leap)
				monCyl++;
		}
		// offset为0时，并且刚才计算的月份是闰月，要校正
		if (offset == 0 && leapMonth > 0 && iMonth == leapMonth + 1) {
			if (leap) {
				leap = false;
			} else {
				leap = true;
				--iMonth;
				--monCyl;
			}
		}
		// offset小于0时，也要校正
		if (offset < 0) {
			offset += daysOfMonth;
			--iMonth;
			--monCyl;
		}
		month = iMonth;
		day = offset + 1;
	}

	public static String getChinaDayString(int day) {
		String chineseTen[] = { "初", "十", "廿", "卅" };
		int n = day % 10 == 0 ? 9 : day % 10 - 1;
		if (day > 30)
			return "";
		if (day == 10)
			return "初十";
		if(day==20)
			return "廿";
		if(day==30)
			return "卅";
		else
			return chineseTen[day / 10] + chineseNumber1[n];
	}

	public String toString() {
		return (leap ? "闰" : "") + chineseNumber[month - 1] + "月" + getChinaDayString(day);
	}

	/**
	 * 获取星期
	 * 
	 * @param weekday
	 * @return
	 */
	public String getChinaWeekdayString(String weekday) {
		if (weekday.equals("Mon"))
			return "一";
		if (weekday.equals("Tue"))
			return "二";
		if (weekday.equals("Wed"))
			return "三";
		if (weekday.equals("Thu"))
			return "四";
		if (weekday.equals("Fri"))
			return "五";
		if (weekday.equals("Sat"))
			return "六";
		if (weekday.equals("Sun"))
			return "日";
		else
			return "";
	}

	/**
	 * 根据阳历月日算出星座
	 * 
	 * @return
	 */
	public String getXingZuo() {
		String date = dateformart.format(this.calendar.getTime());
		Integer month = Integer.valueOf(date.substring(5, 7));
		Integer day = Integer.valueOf(date.substring(8, 10));
		return getConstellation(month, day) + "座";
	}

	public static String getConstellation(Integer month, Integer day) {
		String s = "魔羯水瓶双鱼牡羊金牛双子巨蟹狮子处女天秤天蝎射手魔羯";
		Integer[] arr = { 20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22, 22 };
		Integer num = month * 2 - (day < arr[month - 1] ? 2 : 0);
		return s.substring(num, num + 2);
	}

	public String getXingsu() {
		return (String) xingsu.get(month)[day - 1] + "宿";
	}

	/**
	 * @param hour
	 *            这里的时间范围是1-12，具体几点到几点是子时、丑时请参考相关文档 具体的选择如下 "子：1", "丑：2", "寅：3",
	 *            "卯：4", "辰：5", "巳：6", "午：7", "未：8", "申：9", "酉：10", "戌：11",
	 *            "亥：12"
	 * @author
	 * */
	public String getYearGanZhi(int hour) {
		// 1864年是甲子年，每隔六十年一个甲子
		int idx = (year - 1864) % 60;
		// 没有过春节的话那么年还算上一年的，此处求的年份的干支
		String y = jiazi[idx];

		String m = "";
		String d = "";
		// String h = "";
		idx = idx % 5;
		int idxm = 0;
		/**
		 * 年上起月 甲己之年丙作首，乙庚之岁戊为头， 丙辛必定寻庚起，丁壬壬位顺行流， 更有戊癸何方觅，甲寅之上好追求。
		 */
		idxm = (idx + 1) * 2;
		if (idxm == 10)
			idxm = 0;
		// 求的月份的干支
		m = gan[(idxm + month - 1) % 10] + zhi[(month + 2 - 1) % 12];

		/*
		 * 求出和1900年1月31日甲辰日相差的天数 甲辰日是第四十天
		 */
		int offset = (int) ((calendar.getTime().getTime() - baseDate.getTime()) / 86400000L);
		offset = (offset + 40) % 60;
		// 求的日的干支
		d = jiazi[offset];

		/**
		 * 日上起时 甲己还生甲，乙庚丙作初， 丙辛从戊起，丁壬庚子居， 戊癸何方发，壬子是真途。
		 */

		offset = (offset % 5) * 2;
		// 求得时辰的干支
		// h = gan[(offset + hour - 1) % 10] + zhi[hour - 1];
		// 在此处输出我们的年月日时的天干地支
		return y + "," + m + "," + d;
	}

	public static void main(String[] args) throws ParseException {
		Calendar today = Calendar.getInstance();
		today.setTime(new java.util.Date());// 加载当前日期
		today.setTime(chineseDateFormat.parse("1985年07月17日"));// 加载自定义日期
		Lunar lunar = new Lunar(today);
		System.out.println(lunar.cyclical() + "年");// 计算输出阴历年份
		System.out.println(lunar.toString());// 计算输出阴历日期
		System.out.println(lunar.animalsYear());// 计算输出属相
		System.out.println(new java.sql.Date(today.getTime().getTime()));// 输出阳历日期
		System.out.println("星期" + lunar.getChinaWeekdayString(today.getTime().toString().substring(0, 3)));// 计算输出星期几
		System.out.println(lunar.getXingZuo());
		System.out.println(lunar.getYearGanZhi(3));
	}
}