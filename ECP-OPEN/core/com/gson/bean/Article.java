package com.gson.bean;

import java.io.Serializable;

/**
 * 群发图文消息对象
 * @author ____′↘夏悸
 *
 */
public class Article implements Serializable{
	private static final long serialVersionUID = 1L;

	private String thumb_media_id;
	private String author;
	private String title;
	private String content_source_url;
	private String content;
	private String digest;
	
	public String getThumb_media_id() {
		return thumb_media_id;
	}
	public void setThumb_media_id(String thumb_media_id) {
		this.thumb_media_id = thumb_media_id;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent_source_url() {
		return content_source_url;
	}
	public void setContent_source_url(String content_source_url) {
		this.content_source_url = content_source_url;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getDigest() {
		return digest;
	}
	public void setDigest(String digest) {
		this.digest = digest;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
