package net.loyin.ctrl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.aop.ClearInterceptor;
import com.jfinal.aop.ClearLayer;

import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.util.safe.CipherUtil;
/**
 * 验证码
 * @author loyin
 *2013-1-12 下午6:34:28
 */
@SuppressWarnings("rawtypes")
@RouteBind(path = "/ValidCode",name="验证码",sys="",model="",code="")
public class ValidCodeCtrl extends BaseController {
	private static final String chars = "vwxBtuFpqEJKLM6eGZPH3AdU7NYDmnQ2rsfghRS8VCjkT5bcW49Xayz";
	private static final int WIDTH = 150;
	private static final int HEIGHT = 50;
	@ClearInterceptor(ClearLayer.ALL)
	public void jpg() {
		HttpServletResponse response = this.getResponse();
		response.setContentType("image/jpeg");

		// 防止浏览器缓冲
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		
		BufferedImage image = new BufferedImage(WIDTH, HEIGHT,BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		char[] rands = getCode(4);
		drawBackground(g);
		drawRands(g, rands);
		g.dispose();
		try {
			this.setCookie("ValidCode", CipherUtil.encryptData(new String(rands).toLowerCase()),6000);//60秒
			ServletOutputStream out = response.getOutputStream();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ImageIO.write(image, "PNG", bos);
			byte[] buf = bos.toByteArray();
			response.setContentLength(buf.length);
			out.write(buf);
			bos.close();
			out.close();
//			session.setAttribute("check", new String(rands).toLowerCase());
//			this.setCookie("ValidCode", CipherUtil.encryptData(new String(rands).toLowerCase()),6000);//60秒
		} catch (Exception e) {log.error("cookie存储异常",e);}
		renderNull();
	}

	/**
	 * 产生随机数
	 * @return
	 */
	private char[] getCode(int length) {
		char[] rands = new char[length];
		for (int i = 0; i < length; i++) {
			int rand = (int) (Math.random() * chars.length());
			rands[i] = chars.charAt(rand);
		}
		return rands;
	}

	/**
	 * 绘制背景
	 * @param g
	 */
	private void drawBackground(Graphics g) {
		g.setColor(new Color(255,255,255));
		g.fillRect(0, 0, WIDTH, HEIGHT);
		Random random = new Random();
		int len = 0;
		while (len <= 5) {
			len = random.nextInt(15);
		}
		for (int i = 0; i < len; i++) {
			int x = (int) (random.nextInt(WIDTH));
			int y = (int) (random.nextInt(HEIGHT));
			int red = (int) (255 - random.nextInt(200));
			int green = (int) (255 - random.nextInt(200));
			int blue = (int) (255 - random.nextInt(200));
			g.setColor(new Color(red, green, blue));
//			 g.drawLine(x, y, random.nextInt(WIDTH)-x, random.nextInt(HEIGHT)-y);
			g.drawArc(x, y, random.nextInt(WIDTH)-x, random.nextInt(HEIGHT)-y, random.nextInt(WIDTH)-x, random.nextInt(HEIGHT)-y);
			g.drawOval(x, y, 2, 2);
		}
	}

	/**
	 * 绘制验证码
	 * @param g
	 * @param rands
	 */
	private void drawRands(Graphics g, char[] rands) {
		Random random = new Random();
		g.setFont(new Font("黑体", Font.ITALIC | Font.BOLD, 45));
		for (int i = 0; i < rands.length; i++) {
			int red = (int) (random.nextInt(255));
			int green = (int) (random.nextInt(255));
			int blue = (int) (random.nextInt(255));
			g.setColor(new Color(red, green, blue));
			g.drawString("" + rands[i], i *( random.nextInt(4)*5+25), 40);
		}
	}
	@ClearInterceptor(ClearLayer.ALL)
	@Override
	public void index() {
		jpg();
	}
}
