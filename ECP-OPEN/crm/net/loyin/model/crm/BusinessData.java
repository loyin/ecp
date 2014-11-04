package net.loyin.model.crm;

import net.loyin.jfinal.anatation.TableBind;

import com.jfinal.plugin.activerecord.Model;
/**
 * 商机数据
 * @author 龙影
 */
@TableBind(name="crm_business_data")
public class BusinessData extends Model<BusinessData> {
	private static final long serialVersionUID = 8699093530520166772L;
	public static final String tableName="crm_business_data";
	public static BusinessData dao=new BusinessData();
	
}
