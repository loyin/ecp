package net.loyin.model.crm;

import java.math.BigDecimal;
import java.util.Map;

import net.loyin.jfinal.anatation.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

/**
 * 充值记录
 * 
 * @author 龙影 2014年9月28日
 */
@TableBind(name = "crm_recharge_record")
public class RechargeRecord extends Model<RechargeRecord> {
	private static final long serialVersionUID = -967917912189365368L;
	public static final String tableName = "crm_recharge_record";
	public static RechargeRecord dao = new RechargeRecord();
	/**
	 * 充值
	 * @param attr
	 * @param company_id
	 */
	public void recharge(Map<String, Object> attr, String company_id) {
		Db.update(
				"update crm_customer c set c.amt=? where id=? and company_id=?",
				(BigDecimal) attr.get("amt"), (String) attr.get("customer_id"),
				company_id);
		RechargeRecord po = new RechargeRecord();
		po.setAttrs(attr);
		po.save();
	}

}
