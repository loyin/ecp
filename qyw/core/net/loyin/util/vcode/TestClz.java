package net.loyin.util.vcode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TestClz {
	public static void main(String[] args) {
		try{
		 /*Captcha captcha = new SpecCaptcha(150,40,5);// png格式验证码
	        captcha.out(new FileOutputStream("E:/1.png"));
	        System.out.println(captcha.text());
	        captcha = new GifCaptcha(150,40,5);//   gif格式动画验证码
	        captcha.out(new FileOutputStream("E:/1.gif"));
	        System.out.println(captcha.text());*/
			Date date=new SimpleDateFormat("yyyy-MM-dd").parse("2014-09-01");
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.MONTH,cal.get(Calendar.MONTH)+1);
			cal.set(Calendar.DAY_OF_MONTH,1);
			cal.set(Calendar.DATE,cal.get(Calendar.DATE)-1);
			System.out.println("当月天数:"+cal.get(Calendar.DAY_OF_MONTH));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
