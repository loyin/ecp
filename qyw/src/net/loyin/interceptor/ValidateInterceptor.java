package net.loyin.interceptor;

import com.jfinal.core.ActionInvocation;
/**
 * 验证拦截器
 * 按action的code获取验证定义文件(json)，然后解析执行相关的验证动作。
 * @author 龙影 2014-08-25
 */
public class ValidateInterceptor extends BaseInterceptor {
	@Override
	public void doIntercept(ActionInvocation ai) {
		
	}
}
