package net.loyin.interceptor;

import java.util.Date;

import net.loyin.ctrl.BaseController;
import net.loyin.jfinal.model.IdGenerater;
import net.loyin.util.PropertiesContent;
import net.loyin.util.TextUtil;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;

/**
 * 基础拦截器父类
 * @author 刘声凤 2014年1月25日
 */
public abstract class BaseInterceptor implements Interceptor {
	protected static IdGenerater idGenerater = new IdGenerater();
	/**权限检测*/
	protected static Boolean power_check;
	/**登录状态验证排除url*/
	protected static String[] power_url_exclude;
	@Override
	public void intercept(ActionInvocation ai) {
		if(power_check==null){
			power_check=PropertiesContent.getToBool("power_check",false);
		}
		if(power_url_exclude==null)
			power_url_exclude=PropertiesContent.get("power_url_exclude","").split(",");
		
		BaseController ctrl=(BaseController)ai.getController();
		String uid=ctrl.getCurrentUserId();
		String uri=ctrl.getRequest().getRequestURI();
		//登录超时控制
		if(StringUtils.isEmpty(uid)&&(!ArrayUtils.contains(power_url_exclude,uri))){
			if((uri.contains(".json")))
			{
				ctrl.renderJson("{\"msg\":\"登录超时，请重新登录！\",\"status\":300}");
				
			}else{
				ctrl.redirect("/");
			}
			return;
		}
		ctrl.keepPara();
		doIntercept(ai);
		ctrl.setAttr("now",new Date());
		ctrl.setAttr("IdGenerater",IdGenerater.me);
		ctrl.setAttr("TextUtil",TextUtil.me);
		ctrl.setAttr("PropertiesContent",PropertiesContent.me);
//		ai.invoke();//注意 不能执行此方法 否则拦截器失效 主要是验证拦截
	}
	public abstract void doIntercept(ActionInvocation ai);
}
