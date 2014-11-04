package net.loyin.model.fa;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import net.loyin.jfinal.anatation.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;
/**
 * 财务账户
 * @author 龙影
 */
@TableBind(name="fa_account")
public class Account extends Model<Account> {
	private static final long serialVersionUID = 8641164088961189346L;
	public static final String tableName="fa_account";
	public static Account dao=new Account();
	public List<Record> qryList(String company_id) {
		return Db.find("select d.* from "+tableName+" d where d.company_id=?",company_id);
	}
	public void del(String id,String company_id) {
		if (StringUtils.isNotEmpty(id)) {
			String[] ids=id.split(",");
			StringBuffer ids_=new StringBuffer();
			List<String> parame=new ArrayList<String>();
			for(String id_:ids){
				ids_.append("?,");
				parame.add(id_);
			}
			ids_.append("'-'");
			parame.add(company_id);
			Db.update("delete  from " + tableName + " where id in ("+ids_.toString()+") and company_id=? ",parame.toArray());
		}
	}
	public Account findById(String id,String company_id){
		return dao.findFirst("select d.* from "+tableName+" d where d.company_id=? and d.id=?",company_id,id);
	}
	/**现金银行报表*/
	public List<Record> rptList(Map<String, Object> filter) {
		List<Object> parame=new ArrayList<Object>();
		
		StringBuffer sql=new StringBuffer("select t.name,t.account,d.amt_in,d.remark,d.amt_out,to_date(d.create_datetime,'yyyy-MM-dd') create_date,d.balance,c.name customer_name from fa_account t,fa_account_detail d ");
		sql.append(" left join crm_customer c on c.id=d.relation_id where t.id=d.account_id and t.company_id=?");
		parame.add(filter.get("company_id"));
		String start_date=(String)filter.get("start_date");
		if(StringUtils.isNotEmpty(start_date)){
			sql.append(" and d.create_datetime >=?");
			parame.add(start_date+" 00:00:00");
		}
		String end_date=(String)filter.get("end_date");
		if(StringUtils.isNotEmpty(end_date)){
			sql.append(" and d.create_datetime <=?");
			parame.add(end_date+" 23:59:59");
		}
		String account_id=(String)filter.get("account_id");
		if(StringUtils.isNotEmpty(account_id)){
			sql.append(" and d.account_id =?");
			parame.add(account_id);
		}
		sql.append(" order by t.name asc,d.create_datetime asc ");
		return Db.find(sql.toString(),parame.toArray());
	}
	
	
}
