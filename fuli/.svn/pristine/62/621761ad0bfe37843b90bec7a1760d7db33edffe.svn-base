/**
 * 微信公众平台开发模式(JAVA) SDK
 * (c) 2012-2013 ____′↘夏悸 <wmails@126.cn>, MIT Licensed
 * http://www.jeasyuicn.com/wechat
 */
package com.gson.bean;

/**
 * 微信反馈 xml 转换，维权
 * @author L.cm
 * email: 596392912@qq.com
 * site:  http://www.dreamlu.net
 * @date 2014-4-23 上午9:46:30
 */
public class WeChatFeedBack {

	public static final String MSGTYPE_REQUEST = "request";
	public static final String MSGTYPE_CONFIRM = "confirm";
	public static final String MSGTYPE_REJECT  = "reject";
	
	private String OpenId;  // 用户openid
	private String AppId;   // appid
	private long   TimeStamp; // 时间戳
	private String MsgType;   // 通知类型：  request  用户提交投诉 ， confirm  用户确认消除 投诉， reject  用户拒绝消除投诉
	private String Phone;        // 电话
	private String FeedBackId;   // 投诉单号
	private String TransId;      // 交易订单号
	private String Reason;       // 用户投诉原因
	private String Solution;     // 用户希望解决方案
	private String ExtInfo;      // 备注信息
	private String AppSignature; // 签名；字段来源：对前面的其他字段与 appKey按照字典序排序后，使用 SHA1 算法得到的结果。由商户生成后传入。
	private String SignMethod;   // sha1

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
	public long getTimeStamp() {
		return TimeStamp;
	}
	public void setTimeStamp(long timeStamp) {
		TimeStamp = timeStamp;
	}
	public String getMsgType() {
		return MsgType;
	}
	public void setMsgType(String msgType) {
		MsgType = msgType;
	}
	public String getPhone() {
		return Phone;
	}
	public void setPhone(String phone) {
		Phone = phone;
	}
	public String getFeedBackId() {
		return FeedBackId;
	}
	public void setFeedBackId(String feedBackId) {
		FeedBackId = feedBackId;
	}
	public String getTransId() {
		return TransId;
	}
	public void setTransId(String transId) {
		TransId = transId;
	}
	public String getReason() {
		return Reason;
	}
	public void setReason(String reason) {
		Reason = reason;
	}
	public String getSolution() {
		return Solution;
	}
	public void setSolution(String solution) {
		Solution = solution;
	}
	public String getExtInfo() {
		return ExtInfo;
	}
	public void setExtInfo(String extInfo) {
		ExtInfo = extInfo;
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
		return "WeChatFeedBackDto [OpenId=" + OpenId + ", AppId=" + AppId
				+ ", TimeStamp=" + TimeStamp + ", MsgType=" + MsgType
				+ ", Phone=" + Phone + ", FeedBackId=" + FeedBackId
				+ ", TransId=" + TransId + ", Reason=" + Reason + ", Solution="
				+ Solution + ", ExtInfo=" + ExtInfo + ", AppSignature="
				+ AppSignature + ", SignMethod=" + SignMethod + "]";
	}
}