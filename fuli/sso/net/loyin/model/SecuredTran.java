package net.loyin.model;

import net.loyin.jfinal.anatation.TableBind;

import com.jfinal.plugin.activerecord.Model;

/**
 * 担保交易
 * @author 龙影 2014年10月6日
 */
@TableBind(name = "secured_tran")
public class SecuredTran extends Model<SecuredTran> {
	private static final long serialVersionUID = -7482047704137777115L;
	public static final String tableName = "secured_tran";
	public static SecuredTran dao=new SecuredTran();
}
