package net.loyin.util.vcode;

import java.io.FileOutputStream;

public class TestClz {
	public static void main(String[] args) {
		try{
		 Captcha captcha = new SpecCaptcha(150,40,5);// png格式验证码
	        captcha.out(new FileOutputStream("E:/1.png"));
	        System.out.println(captcha.text());
	        captcha = new GifCaptcha(150,40,5);//   gif格式动画验证码
	        captcha.out(new FileOutputStream("E:/1.gif"));
	        System.out.println(captcha.text());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
