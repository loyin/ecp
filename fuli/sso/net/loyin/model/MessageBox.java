package net.loyin.model;

import net.loyin.jfinal.anatation.TableBind;

import com.jfinal.plugin.activerecord.Model;

/**
 * 留言、私人邮箱
 * 
 * @author 龙影 2014年10月6日
 */
@TableBind(name = "message_box")
public class MessageBox extends Model<MessageBox> {
	private static final long serialVersionUID = -7482047704137777115L;
	public static final String tableName = "message_box";
	public static MessageBox dao=new MessageBox();
}
