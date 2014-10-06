package net.loyin.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.handler.Handler;
/***
 * Servlet 处理
 * @author 刘声凤
 * 2014年3月22日
 */
public class ServletHandler extends Handler {
	@Override
	public void handle(String target, HttpServletRequest request,
			HttpServletResponse response, boolean[] isHandled) {
		int index=target.indexOf(".srv");
		if(index!=-1){
			return;
		}else{
			nextHandler.handle(target, request, response, isHandled);
		}
	}

}
