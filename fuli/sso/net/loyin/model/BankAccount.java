package net.loyin.model;

import net.loyin.jfinal.anatation.TableBind;

import com.jfinal.plugin.activerecord.Model;

/**
 * 收款银行账号
 * 
 * @author 龙影 2014年10月6日
 */
@TableBind(name = "bank_account")
public class BankAccount extends Model<BankAccount> {
	private static final long serialVersionUID = -7482047704137777115L;
	public static final String tableName = "bank_account";
	public static BankAccount dao=new BankAccount();
}
