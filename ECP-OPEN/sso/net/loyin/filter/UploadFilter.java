package net.loyin.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.loyin.util.PropertiesContent;
import net.loyin.util.safe.CipherUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.jfinal.render.RenderException;

/**
 * ueditor及其他上传过滤
 * @author 龙影
 */
public class UploadFilter implements Filter {
	private static final Logger log=Logger.getLogger(UploadFilter.class);
	private static final String contentType = "application/json;charset=utf-8";
	private static final String contentTypeForIE = "text/html;charset=utf-8";
	private static final String msg="您没有权限上传文件或登录超时！";
    public UploadFilter() {
    }
	public void destroy() {
	}
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request=(HttpServletRequest)req;
		HttpServletResponse response=(HttpServletResponse)resp;
		String cookieVal=getCookieValue(PropertiesContent.get("cookie_field_key"),request);
		boolean can=false;
		if(StringUtils.isEmpty(cookieVal)){
		}else{
			Map<String, String> userMap =new HashMap<String,String>();
			if (StringUtils.isNotEmpty(cookieVal)){
				cookieVal = CipherUtil.decryptData(cookieVal);
				userMap = (Map<String, String>) JSON.parse(cookieVal);
			}
			if(StringUtils.isNotEmpty(userMap.get("uid"))){
				//做权限判断
				can=true;
			}
		}
		if(can){
			chain.doFilter(request, response);
		}else{
			Map<String,Object>jsonMap=new HashMap<String,Object>();
			jsonMap.put("status",401);
			jsonMap.put("state",msg);
			jsonMap.put("msg",msg);
			render(jsonMap,request,response);
		}
	}
	public void init(FilterConfig fConfig) throws ServletException {}
	
	public void render(Map<String,Object>jsonMap,HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer = null;
		boolean forIE=false;//是否为IE浏览器
		try {
			String agent = request.getHeader("User-Agent");
			forIE=agent.contains("MSIE");
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
			
			response.setContentType(forIE ? contentTypeForIE : contentType);
			writer = response.getWriter();
	        writer.write(JSON.toJSONString(jsonMap));
	        writer.flush();
		} catch (IOException e) {
			throw new RenderException(e);
		}
		finally {
			if (writer != null)
				writer.close();
		}
	}
	public String getCookieValue(String name,HttpServletRequest request) {
		Cookie cookie=this.getCookieObject(name, request);
		if(cookie!=null){
			return cookie.getValue();
		}
		return null;
	}
	/**
	 * Get cookie object by cookie name.
	 */
	public Cookie getCookieObject(String name,HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null)
			for (Cookie cookie : cookies)
				if (cookie.getName().equals(name))
					return cookie;
		return null;
	}
}
