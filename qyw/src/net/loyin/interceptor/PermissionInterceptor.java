package net.loyin.interceptor;

import java.util.List;

import net.loyin.ctrl.BaseController;
import net.loyin.jfinal.anatation.PowerBind;
import net.loyin.jfinal.model.IdGenerater;

import com.jfinal.core.ActionInvocation;
import com.jfinal.log.Logger;
import com.jfinal.plugin.ehcache.CacheKit;
/**
 * 管理用户后台权限验证拦截器
 * @author 刘声凤
 *  2012-9-6 下午8:32:53
 */
public class PermissionInterceptor extends BaseInterceptor {
	public Logger log=Logger.getLogger(getClass());
	protected static IdGenerater idGenerater=new IdGenerater();
	/** 获取当前系统操作人 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void doIntercept(ActionInvocation ai){
		BaseController ctrl=(BaseController)ai.getController();
		ctrl.setAttr("port",ctrl.getRequest().getServerPort());
		PowerBind p=ai.getMethod().getAnnotation(PowerBind.class);
		log.debug("操作权限控制");
		if(p==null){
			p=ai.getController().getClass().getAnnotation(PowerBind.class);
		}
		if(p==null){
			ai.invoke();
		}else if(power_check){
			boolean v=true;
			String code=null;
				v=p.v();
				code=p.code();
			boolean f=false;
			if(v==true){
				//菜单权限判断
				List<String> powerCodeList=(List<String>)CacheKit.get("oneday", "power_"+ctrl.getCurrentUserId());
				if(powerCodeList!=null&&powerCodeList.isEmpty()==false){
					if(powerCodeList.contains(code)){//安全码匹配
						ctrl.setAttr("powersafecodelist",powerCodeList);
						ai.invoke();//注意 一定要执行此方法
					}else{
						f=true;
					}
				}else{
					f=true;
				}
			}
			if(f)
			ctrl.renderText("{\"success\":false,\"msg\":\"您未有此操作权限！请勿越权操作！<br>请重新登录获得最新权限设置！\",\"status\":401}");
		}
	}
}