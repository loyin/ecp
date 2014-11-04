package com.gson.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信接口全局返回码说明
 */
public class Status {
	
    private static Map<Integer, String> status = new HashMap<Integer, String>();

    static {
        status.put(-1, "系统繁忙");
        status.put(0, "请求成功");
        status.put(40001, "获取access_token时AppSecret错误，或者access_token无效");
        status.put(40002, "不合法的凭证类型");
        status.put(40003, "不合法的OpenID");
        status.put(40004, "不合法的媒体文件类型");
        status.put(40005, "不合法的文件类型");
        status.put(40006, "不合法的文件大小");
        status.put(40007, "不合法的媒体文件id");
        status.put(40008, "不合法的消息类型");
        status.put(40009, "不合法的图片文件大小");
        status.put(40010, "不合法的语音文件大小");
        status.put(40011, "不合法的视频文件大小");
        status.put(40012, "不合法的缩略图文件大小");
        status.put(40013, "不合法的APPID");
        status.put(40014, "不合法的access_token");
        status.put(40015, "不合法的菜单类型");
        status.put(40016, "不合法的按钮个数");
        status.put(40017, "不合法的按钮个数");
        status.put(40018, "不合法的按钮名字长度");
        status.put(40019, "不合法的按钮KEY长度");
        status.put(40020, "不合法的按钮URL长度");
        status.put(40021, "不合法的菜单版本号");
        status.put(40022, "不合法的子菜单级数");
        status.put(40023, "不合法的子菜单按钮个数");
        status.put(40024, "不合法的子菜单按钮类型");
        status.put(40025, "不合法的子菜单按钮名字长度");
        status.put(40026, "不合法的子菜单按钮KEY长度");
        status.put(40027, "不合法的子菜单按钮URL长度");
        status.put(40028, "不合法的自定义菜单使用用户");
        status.put(40029, "不合法的oauth_code");
        status.put(40030, "不合法的refresh_token");
        status.put(40031, "不合法的openid列表");
        status.put(40032, "不合法的openid列表长度");
        status.put(40033, "不合法的请求字符，不能包含\\uxxxx格式的字符");
        status.put(40035, "不合法的参数");
        status.put(40038, "不合法的请求格式");
        status.put(40039, "不合法的URL长度");
        status.put(40050, "不合法的分组id");
        status.put(40051, "分组名字不合法");
        status.put(41001, "缺少access_token参数");
        status.put(41002, "缺少appid参数");
        status.put(41003, "缺少refresh_token参数");
        status.put(41004, "缺少secret参数");
        status.put(41005, "缺少多媒体文件数据");
        status.put(41006, "缺少media_id参数");
        status.put(41007, "缺少子菜单数据");
        status.put(41008, "缺少oauth code");
        status.put(41009, "缺少openid");
        status.put(42001, "access_token超时");
        status.put(42002, "refresh_token超时");
        status.put(42003, "oauth_code超时");
        status.put(43001, "需要GET请求");
        status.put(43002, "需要POST请求");
        status.put(43003, "需要HTTPS请求");
        status.put(43004, "需要接收者关注");
        status.put(43005, "需要好友关系");
        status.put(44001, "多媒体文件为空");
        status.put(44002, "POST的数据包为空");
        status.put(44003, "图文消息内容为空");
        status.put(44004, "文本消息内容为空");
        status.put(45001, "多媒体文件大小超过限制");
        status.put(45002, "消息内容超过限制");
        status.put(45003, "标题字段超过限制");
        status.put(45004, "描述字段超过限制");
        status.put(45005, "链接字段超过限制");
        status.put(45006, "图片链接字段超过限制");
        status.put(45007, "语音播放时间超过限制");
        status.put(45008, "图文消息超过限制");
        status.put(45009, "接口调用超过限制");
        status.put(45010, "创建菜单个数超过限制");
        status.put(45015, "回复时间超过限制");
        status.put(45016, "系统分组，不允许修改");
        status.put(45017, "分组名字过长");
        status.put(45018, "分组数量超过上限");
        status.put(46001, "不存在媒体数据");
        status.put(46002, "不存在的菜单版本");
        status.put(46003, "不存在的菜单数据");
        status.put(46004, "不存在的用户");
        status.put(47001, "解析JSON/XML内容错误");
        status.put(48001, "api功能未授权");
        status.put(50001, "用户未授权该api");
    }

    /**
     * 错误信息
     * @param code
     * @return
     */
    public static String is(int code){
        if(status.containsKey(code)){
            return status.get(code);
        }
        return "【"+code+"】未知错误!";
    }
}
