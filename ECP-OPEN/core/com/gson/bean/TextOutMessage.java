/* 
 * jeasyPro
 * (c) 2012-2013 ____′↘夏悸 <wmails@126.cn>, MIT Licensed
 * http://www.jeasyuicn.com/
 * 2013-8-11 下午3:31:50
 */
package com.gson.bean;

/**
 * 输出文字消息
 * 支持转移到多客服系统 转msgtype为：transfer_customer_service
 * @author ____′↘夏悸
 */
public class TextOutMessage extends OutMessage {

	private String	MsgType	= "text";
	// 文本消息
	private String	Content;
	
	public TextOutMessage() {
	}
	
	public TextOutMessage(String content) {
		Content = content;
	}

	public String getMsgType() {
		return MsgType;
	}

	public void setMsgType(String msgType) {
		MsgType = msgType;
	}

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}
}
