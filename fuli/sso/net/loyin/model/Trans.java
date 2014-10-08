package net.loyin.model;

import net.loyin.jfinal.anatation.TableBind;

import com.jfinal.plugin.activerecord.Model;

/**
 * 交易 包括转账、卖出、抢购
 * @author 龙影 2014年10月6日
 */
@TableBind(name = "trans")
public class Trans extends Model<Trans> {
	private static final long serialVersionUID = -7482047704137777115L;
	public static final String tableName = "trans";
	public static Trans dao=new Trans();
}