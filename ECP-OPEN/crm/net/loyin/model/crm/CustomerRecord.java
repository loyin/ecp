package net.loyin.model.crm;

import net.loyin.jfinal.anatation.TableBind;

import com.jfinal.plugin.activerecord.Model;
/**
 * 客户流转记录
 * @author 龙影
 */
@TableBind(name="crm_customer_record")
public class CustomerRecord extends Model<CustomerRecord> {
	private static final long serialVersionUID = -1974122280599644721L;
	public static final String tableName="CustomerData.java";
	public static CustomerRecord dao=new CustomerRecord();
	
}
