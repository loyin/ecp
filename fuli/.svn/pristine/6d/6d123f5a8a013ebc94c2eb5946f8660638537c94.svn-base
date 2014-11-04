/**
 * 微信公众平台开发模式(JAVA) SDK
 * (c) 2012-2013 ____′↘夏悸 <wmails@126.cn>, MIT Licensed
 * http://www.jeasyuicn.com/wechat
 */
package com.gson.bean;

/**
 * 微信post过来的xml转换成bean
 * @author L.cm
 * email: 596392912@qq.com
 * site:  http://www.dreamlu.net
 * @date 2014-4-23 上午9:48:47
 */
public class WeChatBuyPost {

	private String OpenId;			// 支付该笔订单的用户 ID
	private String AppId;			// 公众号 id
	private int IsSubscribe;		// 用户是否关注了公众号。1 为关注，0 为未关注
	private long TimeStamp;			// 时间戳
	private String NonceStr;		// 随机字符串；字段来源：商户生成的随机字符
	private String AppSignature;	// 字段名称：签名
	private String SignMethod;		// SHA1

	public String getOpenId() {
		return OpenId;
	}
	public void setOpenId(String openId) {
		OpenId = openId;
	}
	public String getAppId() {
		return AppId;
	}
	public void setAppId(String appId) {
		AppId = appId;
	}
	public int getIsSubscribe() {
		return IsSubscribe;
	}
	public void setIsSubscribe(int isSubscribe) {
		IsSubscribe = isSubscribe;
	}
	public long getTimeStamp() {
		return TimeStamp;
	}
	public void setTimeStamp(long timeStamp) {
		TimeStamp = timeStamp;
	}
	public String getNonceStr() {
		return NonceStr;
	}
	public void setNonceStr(String nonceStr) {
		NonceStr = nonceStr;
	}
	public String getAppSignature() {
		return AppSignature;
	}
	public void setAppSignature(String appSignature) {
		AppSignature = appSignature;
	}
	public String getSignMethod() {
		return SignMethod;
	}
	public void setSignMethod(String signMethod) {
		SignMethod = signMethod;
	}

	@Override
	public String toString() {
		return "WeChatBuyPost [OpenId=" + OpenId + ", AppId=" + AppId
				+ ", IsSubscribe=" + IsSubscribe + ", TimeStamp=" + TimeStamp
				+ ", NonceStr=" + NonceStr + ", AppSignature=" + AppSignature
				+ ", SignMethod=" + SignMethod + "]";
	}
}