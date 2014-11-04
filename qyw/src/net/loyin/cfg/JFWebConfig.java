package net.loyin.cfg;

import net.loyin.handler.ServletHandler;
import net.loyin.interceptor.PermissionInterceptor;
import net.loyin.jfinal.plugin.MyRoutesUtil;
import net.loyin.util.PropertiesContent;

import org.apache.log4j.Logger;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.ext.handler.FakeStaticHandler;
import com.jfinal.plugin.ehcache.EhCachePlugin;
/**
 * API引导式配置
 * @author loyin 龙影
 *2013-6-5
 */
public class JFWebConfig extends JFinalConfig {
	private Logger log=Logger.getLogger(getClass());
	/**
	 * 配置常量O
	 */
	public void configConstant(Constants me){
		me.setDevMode(PropertiesContent.getToBool("jfinal.devmode",false));
		me.setMaxPostSize(PropertiesContent.getToInteger("maxpostsize",104857600));//100mb
	}
	
	/**
	 * 配置路由
	 */
	public void configRoute(Routes me) {
		MyRoutesUtil.add(me);
		log.debug("配置路由");
	}
	/**
	 * 配置插件
	 */
	public void configPlugin(Plugins me) {
		me.add(new EhCachePlugin());
	}
	/**
	 * 配置全局拦截器
	 */
	public void configInterceptor(Interceptors me) {
		me.add(new PermissionInterceptor());//权限控制
//		me.add(new OperateLogInterceptor());//日志记录
	}
	/**
	 * 配置处理器
	 */
	public void configHandler(Handlers me) {
		me.add(new ServletHandler());//添加处理Servlet的handler
	  //该处理器将request.getContextPath()存储在root中，可以解决路径问题
	  	ContextPathHandler path = new ContextPathHandler("root");
	  	me.add(path);
	  	me.add(new FakeStaticHandler(".json"));//通过.json后缀访问后台链接
	}
}