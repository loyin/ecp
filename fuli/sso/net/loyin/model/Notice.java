package net.loyin.model;

import net.loyin.jfinal.anatation.TableBind;

import com.jfinal.plugin.activerecord.Model;

/**
 * 公告
 * @author 龙影 2014年10月6日
 */
@TableBind(name = "notice")
public class Notice extends Model<Notice> {
	private static final long serialVersionUID = -7482047704137777115L;
	public static final String tableName = "notice";
	public static Notice dao=new Notice();
}
