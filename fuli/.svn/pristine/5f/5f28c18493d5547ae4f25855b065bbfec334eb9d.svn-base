/* 
 * jeasyPro
 * (c) 2012-2013 ____′↘夏悸 <wmails@126.cn>, MIT Licensed
 * http://www.jeasyuicn.com/
 * 2013-8-11 下午3:32:55
 */
package com.gson.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 输出图文消息
 * 
 * @author ____′↘夏悸
 * 
 */
public class NewsOutMessage extends OutMessage {

	private String			MsgType	= "news";
	private Integer			ArticleCount;
	private String			Title;
	private String			Description;
	private String			PicUrl;
	private String			Url;

	private List<Articles>	Articles;

	public String getMsgType() {
		return MsgType;
	}

	public int getArticleCount() {
		return ArticleCount;
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

	public String getPicUrl() {
		return PicUrl;
	}

	public void setPicUrl(String picUrl) {
		PicUrl = picUrl;
	}

	public String getUrl() {
		return Url;
	}

	public void setUrl(String url) {
		Url = url;
	}

	public List<Articles> getArticles() {
		return Articles;
	}

	public void setArticles(List<Articles> articles) {
		if (articles != null) {
			if (articles.size() > 10)
				articles = new ArrayList<Articles>(articles.subList(0, 10));

			ArticleCount = articles.size();
		}
		Articles = articles;
	}
}
