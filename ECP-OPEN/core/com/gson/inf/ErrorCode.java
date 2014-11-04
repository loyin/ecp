package com.gson.inf;

import java.util.HashMap;
import java.util.Map;
/***
 * 微信错误码
 * @author 龙影
 */
public class ErrorCode {
	public static Map<Integer, String> me = new HashMap<Integer, String>();
	static{
		me.put(-1,"系统繁忙");
		me.put(0,"请求成功");
		me.put(40001,"获取access_token时AppSecret错误，或者access_token无效");
		me.put(40002,"不合法的凭证类型");
		me.put(40003,"不合法的OpenID");
		me.put(40004,"不合法的媒体文件类型");
		me.put(40005,"不合法的文件类型");
		me.put(40006,"不合法的文件大小");
		me.put(40007,"不合法的媒体文件id");
		me.put(40008,"不合法的消息类型");
		me.put(40009,"不合法的图片文件大小");
		me.put(40010,"不合法的语音文件大小");
		me.put(40011,"不合法的视频文件大小");
		me.put(40012,"不合法的缩略图文件大小");
		me.put(40013,"不合法的APPID");
		me.put(40014,"不合法的access_token");
		me.put(40015,"不合法的菜单类型");
		me.put(40016,"不合法的按钮个数");
		me.put(40017,"不合法的按钮个数");
		me.put(40018,"不合法的按钮名字长度");
		me.put(40019,"不合法的按钮KEY长度");
		me.put(40020,"不合法的按钮URL长度");
		me.put(40021,"不合法的菜单版本号");
		me.put(40022,"不合法的子菜单级数");
		me.put(40023,"不合法的子菜单按钮个数");
		me.put(40024,"不合法的子菜单按钮类型");
		me.put(40025,"不合法的子菜单按钮名字长度");
		me.put(40026,"不合法的子菜单按钮KEY长度");
		me.put(40027,"不合法的子菜单按钮URL长度");
		me.put(40028,"不合法的自定义菜单使用用户");
		me.put(40029,"不合法的oauth_code");
		me.put(40030,"不合法的refresh_token");
		me.put(40031,"不合法的openid列表");
		me.put(40032,"不合法的openid列表长度");
		me.put(40033,"不合法的请求字符，不能包含\\uxxxx格式的字符");
		me.put(40035,"不合法的参数");
		me.put(40038,"不合法的请求格式");
		me.put(40039,"不合法的URL长度");
		me.put(40050,"不合法的分组id");
		me.put(40051,"分组名字不合法");
		me.put(41001,"缺少access_token参数");
		me.put(41002,"缺少appid参数");
		me.put(41003,"缺少refresh_token参数");
		me.put(41004,"缺少secret参数");
		me.put(41005,"缺少多媒体文件数据");
		me.put(41006,"缺少media_id参数");
		me.put(41007,"缺少子菜单数据");
		me.put(41008,"缺少oauth code");
		me.put(41009,"缺少openid");
		me.put(42001,"access_token超时");
		me.put(42002,"refresh_token超时");
		me.put(42003,"oauth_code超时");
		me.put(43001,"需要GET请求");
		me.put(43002,"需要POST请求");
		me.put(43003,"需要HTTPS请求");
		me.put(43004,"需要接收者关注");
		me.put(43005,"需要好友关系");
		me.put(44001,"多媒体文件为空");
		me.put(44002,"POST的数据包为空");
		me.put(44003,"图文消息内容为空");
		me.put(44004,"文本消息内容为空");
		me.put(45001,"多媒体文件大小超过限制");
		me.put(45002,"消息内容超过限制");
		me.put(45003,"标题字段超过限制");
		me.put(45004,"描述字段超过限制");
		me.put(45005,"链接字段超过限制");
		me.put(45006,"图片链接字段超过限制");
		me.put(45007,"语音播放时间超过限制");
		me.put(45008,"图文消息超过限制");
		me.put(45009,"接口调用超过限制");
		me.put(45010,"创建菜单个数超过限制");
		me.put(45015,"回复时间超过限制");
		me.put(45016,"系统分组，不允许修改");
		me.put(45017,"分组名字过长");
		me.put(45018,"分组数量超过上限");
		me.put(46001,"不存在媒体数据");
		me.put(46002,"不存在的菜单版本");
		me.put(46003,"不存在的菜单数据");
		me.put(46004,"不存在的用户");
		me.put(47001,"解析JSON/XML内容错误");
		me.put(48001,"api功能未授权");
		me.put(50001,"用户未授权该api");
	}
	public static String get(Integer key){
		return me.get(key);
	}
	public static String get(Map<String,Object> result){
		return me.get(result.get("errcode"));
	}
}
