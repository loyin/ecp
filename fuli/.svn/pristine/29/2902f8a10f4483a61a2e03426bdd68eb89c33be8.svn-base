/**
 * 微信公众平台开发模式(JAVA) SDK
 * (c) 2012-2013 ____′↘夏悸 <wmails@126.cn>, MIT Licensed
 * http://www.jeasyuicn.com/wechat
 */
package com.gson;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.gson.util.ConfKit;
import com.gson.util.Tools;

/**
 * 请求拦截
 * @author GodSon
 */
public class WeChatFilter implements Filter {
    private final Logger LOGGER = Logger.getLogger(WeChatFilter.class);
    /** token */
	private static String TOKEN;
    @Override
    public void destroy() {
        LOGGER.info("WeChatFilter已经销毁");
    }
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        LOGGER.debug("访问方式:"+request.getMethod());
        Boolean isGet = request.getMethod().equals("GET");
        if (isGet) {
            doGet(request, response);
        } else {
            doPost(request, response);
        }
    }

    private void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/xml");
        ServletInputStream in = request.getInputStream();
        String xmlMsg = Tools.inputStream2String(in);
        LOGGER.debug("输入消息:[\n" + xmlMsg + "\n]");
        String xml = WeChat.processing(xmlMsg);
        LOGGER.debug("输出消息:[\n"+xml+"\n]");
        response.getWriter().write(xml);
    }

    private void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String outPut = "error";
            String signature = request.getParameter("signature");// 微信加密签名
            String timestamp = request.getParameter("timestamp");// 时间戳
            String nonce = request.getParameter("nonce");// 随机数
            String echostr = request.getParameter("echostr");//
            if (TOKEN == null)
    			TOKEN = ConfKit.get("WEIXIN_TOKEN_WT");
            // 验证
            if (WeChat.checkSignature(TOKEN, signature, timestamp, nonce)) {
                outPut = echostr;
            }
        response.getWriter().write(outPut);
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        LOGGER.info("WeChatFilter已经启动！");
        if (TOKEN == null)
			TOKEN = ConfKit.get("WEIXIN_TOKEN_WT");
    }

}
