/**
 * 微信公众平台开发模式(JAVA) SDK
 * (c) 2012-2013 ____′↘夏悸 <wmails@126.cn>, MIT Licensed
 * http://www.jeasyuicn.com/wechat
 */
package com.gson.bean;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.gson.inf.MsgTypes;

public class InMessage {

	private String ToUserName;
	private String FromUserName;
	private Long CreateTime;
	private String MsgType = "text";
	private Long MsgId;
	// 文本消息
	private String Content;
	// 图片消息
	private String PicUrl;
	// 位置消息
	private String Location_X;
	private String Location_Y;
	private Long Scale;
	private String Label;
	// 链接消息
	private String Title;
	private String Description;
	private String Url;
	// 语音信息
	private String MediaId;
	private String Format;
	private String Recognition;
	// 事件
	private String Event;
	private String EventKey;
	private String Ticket;
	// 上报地理位置事件
	private String Precision;
	private String Latitude;
	private String Longitude;
	//群发事件
	private String Status;
	private String TotalCount;
	private String FilterCount;
	private String SentCount;
	private String ErrorCount;
	
	private String SceneValue;

	public String getToUserName() {
		return ToUserName;
	}

	public void setToUserName(String toUserName) {
		ToUserName = toUserName;
	}

	public String getFromUserName() {
		return FromUserName;
	}

	public void setFromUserName(String fromUserName) {
		FromUserName = fromUserName;
	}

	public Long getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Long createTime) {
		CreateTime = createTime;
	}

	public String getMsgType() {
		return MsgType;
	}

	public void setMsgType(String msgType) {
		MsgType = msgType;
	}

	public Long getMsgId() {
		return MsgId;
	}

	public void setMsgId(Long msgId) {
		MsgId = msgId;
	}

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}

	public String getPicUrl() {
		return PicUrl;
	}

	public void setPicUrl(String picUrl) {
		PicUrl = picUrl;
	}

	public String getLocation_X() {
		return Location_X;
	}

	public void setLocation_X(String location_X) {
		Location_X = location_X;
	}

	public String getLocation_Y() {
		return Location_Y;
	}

	public void setLocation_Y(String location_Y) {
		Location_Y = location_Y;
	}

	public Long getScale() {
		return Scale;
	}

	public void setScale(Long scale) {
		Scale = scale;
	}

	public String getLabel() {
		return Label;
	}

	public void setLabel(String label) {
		Label = label;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getUrl() {
		return Url;
	}

	public void setUrl(String url) {
		Url = url;
	}

	public String getEvent() {
		return Event;
	}

	public void setEvent(String event) {
		Event = event;
	}

	public String getEventKey() {
		return EventKey;
	}

	public void setEventKey(String eventKey) {
		EventKey = eventKey;
	}

	public String getMediaId() {
		return MediaId;
	}

	public void setMediaId(String mediaId) {
		MediaId = mediaId;
	}

	public String getFormat() {
		return Format;
	}

	public void setFormat(String format) {
		Format = format;
	}

	public String getRecognition() {
		return Recognition;
	}

	public void setRecognition(String recognition) {
		Recognition = recognition;
	}

	public String getTicket() {
		return Ticket;
	}

	public void setTicket(String ticket) {
		Ticket = ticket;
	}
	
	public String getPrecision() {
		return Precision;
	}

	public void setPrecision(String precision) {
		Precision = precision;
	}

	public String getLatitude() {
		return Latitude;
	}

	public void setLatitude(String latitude) {
		Latitude = latitude;
	}

	public String getLongitude() {
		return Longitude;
	}

	public void setLongitude(String longitude) {
		Longitude = longitude;
	}
	

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public String getTotalCount() {
		return TotalCount;
	}

	public void setTotalCount(String totalCount) {
		TotalCount = totalCount;
	}

	public String getFilterCount() {
		return FilterCount;
	}

	public void setFilterCount(String filterCount) {
		FilterCount = filterCount;
	}

	public String getSentCount() {
		return SentCount;
	}

	public void setSentCount(String sentCount) {
		SentCount = sentCount;
	}

	public String getErrorCount() {
		return ErrorCount;
	}

	public void setErrorCount(String errorCount) {
		ErrorCount = errorCount;
	}
	
	public void setSceneValue(String sceneValue) {
		SceneValue = sceneValue;
	}
	
	public String getSceneValue() {
		return SceneValue;
	}

	public Map<String,Object> toMap(){
		Map<String,Object> map = new HashMap<String, Object>();
		Field[] fields = InMessage.class.getDeclaredFields();
		for (Field field : fields) {
			Object obj;
			try {
				obj = field.get(this);
				if(obj!=null){
					map.put(field.getName(), obj);
				}
				map.remove("ToUserName");
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return map;
	}
	
	public Boolean isEvent(){
		return this.MsgType.equals(MsgTypes.EVENT.getType());
	}
	
	public Boolean isText(){
		return this.MsgType.equals(MsgTypes.TEXT.getType());
	}
	
	public Boolean isImage(){
		return this.MsgType.equals(MsgTypes.IMAGE.getType());
	}
	
	public Boolean isVoice(){
		return this.MsgType.equals(MsgTypes.VOICE.getType());
	}
	
	public Boolean isVideo(){
		return this.MsgType.equals(MsgTypes.VIDEO.getType());
	}
	
	public Boolean isLocation(){
		return this.MsgType.equals(MsgTypes.LOCATION.getType());
	}
	
	public Boolean isLink(){
		return this.MsgType.equals(MsgTypes.LINK.getType());
	}
	
	public Boolean isVerify(){
		return this.MsgType.equals(MsgTypes.VERIFY.getType());
	}
}
