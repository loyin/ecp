package net.loyin.model;

import net.loyin.jfinal.anatation.TableBind;

import com.jfinal.plugin.activerecord.Model;

/**
 * 奖金记录
 * 
 * @author 龙影 2014年10月6日
 */
@TableBind(name = "bonus_record")
public class BonusRecord extends Model<BonusRecord> {
	private static final long serialVersionUID = -7482047704137777115L;
	public static final String tableName = "bonus_record";
	public static BonusRecord dao=new BonusRecord();
}
