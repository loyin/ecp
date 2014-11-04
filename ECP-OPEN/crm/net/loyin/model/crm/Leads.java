package net.loyin.model.crm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.loyin.jfinal.anatation.TableBind;
import net.loyin.model.sso.Person;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
/**
 * 线索
 * @author 龙影
 */
@TableBind(name="crm_leads")
public class Leads extends Model<Leads> {
	private static final long serialVersionUID = 8699093530520166772L;
	public static final String tableName="crm_leads";
	public static Leads dao=new Leads();
	public Page<Leads> page(int pageNo, int pageSize, Map<String, Object> filter,Integer qryType) {
		String userView=Person.tableName;
		List<Object> parame=new ArrayList<Object>();
		StringBuffer sql=new StringBuffer(" from ");
		sql.append(tableName);
		sql.append(" t left join ");
		sql.append(userView);
		sql.append(" c on c.id=t.creater_id left join ");
		sql.append(userView);
		sql.append(" u on u.id=t.updater_id left join ");
		sql.append(userView);
		sql.append(" h on h.id=t.transform_id ");
		sql.append(" where t.company_id=? ");
		parame.add(filter.get("company_id"));
		String keyword=(String)filter.get("keyword");
		if(StringUtils.isNotEmpty(keyword)){
			sql.append(" and (t.name like ? or t.customer_name like ? )");
			keyword="%"+keyword+"%";
			parame.add(keyword);
			parame.add(keyword);
		}
		String start_date=(String) filter.get("start_date");
		if(StringUtils.isNotEmpty(start_date)){
			sql.append(" and t.create_datetime >= ?");
			parame.add(start_date+" 00:00:00");
		}
		String end_date=(String) filter.get("end_date");
		if(StringUtils.isNotEmpty(end_date)){
			sql.append(" and t.create_datetime <= ?");
			parame.add(end_date+" 23:59:59");
		}
		Integer is_transformed=(Integer) filter.get("is_transformed");//状态
		if(is_transformed!=null){
			sql.append(" and t.is_transformed = ?");
			parame.add(is_transformed);
		}
		Integer is_deleted=(Integer) filter.get("is_deleted");//状态
		if(is_deleted!=null){
			sql.append(" and t.is_deleted = ?");
			parame.add(is_deleted);
		}
		
		String uid=(String)filter.get("uid");//查询用户id
		if(StringUtils.isNotEmpty(uid)){
			sql.append(" and (t.creater_id=? or t.head_id=?)");
			parame.add(uid);
			parame.add(uid);
		}
		String user_id=(String)filter.get("user_id");//当前用户id
		String position_id=(String)filter.get("position_id");//当前用户岗位id
		
		String sql_ = "WITH RECURSIVE d AS (SELECT d1.id,d1.pid,d1.name,d1.sort_num,d1.type FROM v_user_position d1 where d1.id=?"
				+ "union ALL SELECT d2.id,d2.pid,d2.name,d2.sort_num,d2.type FROM v_user_position d2, d WHERE d2.pid = d.id) "
				+ "SELECT distinct id FROM d where type=10 and d.id not in (select id from sso_user where position_id =? and id!=?)";
		
		switch(qryType){
		case -1:
		case 0://我创建的的
			sql.append(" and t.creater_id=?");
			parame.add(user_id);
			break;
		case 1://下属创建的
			sql.append(" and t.creater_id in(");
			sql.append(sql_);
			sql.append(") and t.creater_id !=?");
			parame.add(position_id);
			parame.add(position_id);
			parame.add(user_id);
			parame.add(user_id);
			break;
		case 2://我负责的
			sql.append(" and t.head_id=?");
			parame.add(user_id);
			break;
		case 3://下属负责的
			sql.append(" and t.head_id in(");
			sql.append(sql_);
			sql.append(") and t.head_id !=?");
			parame.add(position_id);
			parame.add(position_id);
			parame.add(user_id);
			parame.add(user_id);
			break;
		case 4://未转换
			sql.append(" and t.creater_id in(");
			sql.append(sql_);
			sql.append(") and t.creater_id !=? and t.is_transformed=0");
			parame.add(position_id);
			parame.add(position_id);
			parame.add(user_id);
			parame.add(user_id);
			break;
		case 5://已转换
			sql.append(" and t.creater_id in(");
			sql.append(sql_);
			sql.append(") and t.creater_id !=? and t.is_transformed=1");
			parame.add(position_id);
			parame.add(position_id);
			parame.add(user_id);
			parame.add(user_id);
			break;
		}
		String sortField=(String)filter.get("_sortField");
		if(StringUtils.isNotEmpty(sortField)){
			sql.append(" order by ");
			sql.append(sortField);
			sql.append(" ");
			sql.append((String)filter.get("_sort"));
		}
		return dao.paginate(pageNo, pageSize, "select t.*,h.realname as transform_name,c.realname as creater_name,u.realname as updater_name ",sql.toString(),parame.toArray());
	}
	public Leads findById(String id,String company_id){
		String userView=Person.tableName;
		StringBuffer sql=new StringBuffer("select t.*,td.*,h.realname as transform_name,c.realname as creater_name,u.realname as updater_name from ");
		sql.append(tableName);
		sql.append(" t left join ");
		sql.append(LeadsData.tableName);
		sql.append(" td on td.id=t.id left join ");
		sql.append(userView);
		sql.append(" c on c.id=t.creater_id left join ");
		sql.append(userView);
		sql.append(" u on u.id=t.updater_id left join ");
		sql.append(userView);
		sql.append(" h on h.id=t.transform_id ");
		sql.append(" where t.company_id=? and t.id=? ");
		
		return dao.findFirst(sql.toString(),company_id,id);
	}
	/**直接删除*/
	@Before(Tx.class)
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
			Db.update("delete  from crm_leads_data where id in ("+ids_.toString()+")",parame.toArray());
			parame.add(company_id);
			Db.update("delete  from " + tableName + " where id in ("+ids_.toString()+") and company_id=? ",parame.toArray());
		}
	}
	public void trans(String id, String uid,String company_id) {
		
	}
	/***
	 * 转换商机
	 * @param id
	 * @param companyId
	 */
	@Before(Tx.class)
	public void exchange(String id, String company_id,String uid,String now) {
		//查询线索
		Leads leads=this.findById(id, company_id);
		/*String customer_id=leads.getStr("customer_id");
		String contacts_id=leads.getStr("contacts_id");
		if(StringUtils.isEmpty(customer_id)){//保存客户信息
			Customer customer=new Customer();
			customer.set("name",leads.get("customer_name"));
			customer.set("head_id",uid);
			customer.set("creater_id",uid);
			customer.set("create_datetime",now);
			customer.set("type",1);
			customer.set("sn",SnCreater.dao.create("CUSTOMER", company_id));
			customer.save();
			customer_id=customer.getStr("id");
		}
		if(StringUtils.isEmpty(contacts_id)){//联系人信息
			Contacts ct=new Contacts();
			ct.set("customer_id",customer_id);
			ct.set("creater_id",uid);
			ct.set("create_datetime",now);
			ct.save();
			contacts_id=ct.getStr("id");
		}*/
		//设置已经转换
		leads.set("is_transformed",1);
		leads.update();
	}
}
