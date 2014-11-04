/* 
 * jeasyPro
 * (c) 2012-2013 ____′↘夏悸 <wmails@126.cn>, MIT Licensed
 * http://www.jeasyuicn.com/
 * 2013-8-11 下午3:32:28
 */
package com.gson.bean;

/**
 * 输出音乐消息
 * 
 * @author ____′↘夏悸
 * 
 */
public class MusicOutMessage extends OutMessage {
	private String	MsgType	= "music";
	private String	MusicUrl;
	private String	HQMusicUrl;

	public String getMsgType() {
		return MsgType;
	}

	public String getMusicUrl() {
		return MusicUrl;
	}

	public void setMusicUrl(String musicUrl) {
		MusicUrl = musicUrl;
	}

	public String getHQMusicUrl() {
		return HQMusicUrl;
	}

	public void setHQMusicUrl(String hQMusicUrl) {
		HQMusicUrl = hQMusicUrl;
	}
}
