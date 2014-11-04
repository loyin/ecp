/**
 * 微信公众平台开发模式(JAVA) SDK
 * (c) 2012-2014 ____′↘夏悸 <wmails@126.cn>, MIT Licensed
 * http://www.jeasyuicn.com/wechat
 */
package com.gson.plugin;

/**
 * 插件接口
 * @author ____′↘夏悸
 *
 */
public interface Plugin {
	public void run(Object... obj);

    public Boolean install();

    public Boolean uninstall();

    public Boolean upgrade();
}
