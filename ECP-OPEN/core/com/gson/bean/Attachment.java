/**
 * 微信公众平台开发模式(JAVA) SDK
 * (c) 2012-2013 ____′↘夏悸 <wmails@126.cn>, MIT Licensed
 * http://www.jeasyuicn.com/wechat
 */
package com.gson.bean;

import java.io.BufferedInputStream;

/**
 * 下载文件对象
 * @author ____′↘夏悸
 *
 */
public class Attachment {

	private String fileName;
	private String fullName;
	private String suffix;
	private String contentLength;
	private String contentType;
	private BufferedInputStream fileStream;
	private String error;
	
	
	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getContentLength() {
		return contentLength;
	}
	public void setContentLength(String contentLength) {
		this.contentLength = contentLength;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public BufferedInputStream getFileStream() {
		return fileStream;
	}
	public void setFileStream(BufferedInputStream fileStream) {
		this.fileStream = fileStream;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	public String getSuffix() {
		return suffix;
	}
}
