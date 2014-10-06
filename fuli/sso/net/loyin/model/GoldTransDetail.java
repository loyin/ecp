package net.loyin.model;

import net.loyin.jfinal.anatation.TableBind;

import com.jfinal.plugin.activerecord.Model;

/**
 * 金币交易明细
 * 
 * @author 龙影 2014年10月6日
 */
@TableBind(name = "gold_trans_detail")
public class GoldTransDetail extends Model<GoldTransDetail> {
	private static final long serialVersionUID = -7482047704137777115L;
	public static final String tableName = "gold_trans_detail";
	public static GoldTransDetail dao=new GoldTransDetail();
}
