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
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
/**
 * 商机
 * @author 龙影
 */
@TableBind(name="crm_business")
public class Business extends Model<Business> {
	private static final long serialVersionUID = 8699093530520166772L;
	public static final String tableName="crm_business";
	public static Business dao=new Business();
	public Page<Business> page(int pageNo, int pageSize, Map<String, Object> filter,Integer qryType) {
		String userView=Person.tableName;
		List<Object> parame=new ArrayList<Object>();
		StringBuffer sql=new StringBuffer(" from ");
		sql.append(tableName);
		sql.append(" t left join ");
		sql.append(Customer.tableName);
		sql.append(" cust on cust.id=t.customer_id left join ");
		sql.append(Contacts.tableName);
		sql.append(" ct on ct.id=t.contacts_id left join ");
		sql.append(userView);
		sql.append(" c on c.id=t.creater_id left join ");
		sql.append(userView);
		sql.append(" u on u.id=t.updater_id left join ");
		sql.append(userView);
		sql.append(" h on h.id=t.head_id ");
		sql.append(" where t.company_id=? ");
		parame.add(filter.get("company_id"));
		String keyword=(String)filter.get("keyword");
		if(StringUtils.isNotEmpty(keyword)){
			sql.append(" and (t.name like ? or cust.name like ? or cust.sn like ?)");
			keyword="%"+keyword+"%";
			parame.add(keyword);
			parame.add(keyword);
			parame.add(keyword);
		}
		String start_date=(String) filter.get("start_date");
		if(StringUtils.isNotEmpty(start_date)){
			sql.append(" and (t.create_datetime >= ? or t.due_date>=?)");
			parame.add(start_date+" 00:00:00");
			parame.add(start_date);
		}
		String end_date=(String) filter.get("end_date");
		if(StringUtils.isNotEmpty(end_date)){
			sql.append(" and (t.create_datetime <= ? or t.due_date<=?)");
			parame.add(end_date+" 23:59:59");
			parame.add(end_date);
		}
		String status=(String) filter.get("status");//状态
		if(StringUtils.isNotEmpty(status)){
			sql.append(" and t.status = ?");
			parame.add(status);
		}
		String origin=(String) filter.get("origin");//来源
		if(StringUtils.isNotEmpty(origin)){
			sql.append(" and t.origin = ?");
			parame.add(origin);
		}
		Integer is_end=(Integer) filter.get("is_end");//是否结束
		if(is_end!=null){
			sql.append(" and t.is_end = ?");
			parame.add(is_end);
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
		case -1://我创建的及我负责的
			sql.append(" and (t.creater_id=? or t.head_id=?)");
			parame.add(user_id);
			parame.add(user_id);
			break;
		case 0://我创建的
			sql.append(" and t.creater_id=?");
			parame.add(user_id);
			break;
		case 1://我负责的
			sql.append(" and t.head_id=?");
			parame.add(user_id);
			break;
		case 2://下属创建的
			sql.append(" and t.creater_id in(");
			sql.append(sql_);
			sql.append(") and t.creater_id !=?");
			parame.add(position_id);
			parame.add(position_id);
			parame.add(user_id);
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
		}
		
		String sortField=(String)filter.get("_sortField");
		if(StringUtils.isNotEmpty(sortField)){
			sql.append(" order by ");
			sql.append(sortField);
			sql.append(" ");
			sql.append((String)filter.get("_sort"));
		}
		return dao.paginate(pageNo, pageSize, "select t.*,ct.name contacts_name,h.realname as head_name,c.realname as creater_name,u.realname as updater_name,cust.name customer_name ",sql.toString(),parame.toArray());
	}
	public Business findById(String id,String company_id){
		String userView=Person.tableName;
		StringBuffer sql=new StringBuffer("select t.*,ct.name contacts_name,td.*,h.realname as head_name,c.realname as creater_name,u.realname as updater_name,cust.name customer_name from ");
		sql.append(tableName);
		sql.append(" t left join ");
		sql.append(BusinessData.tableName);
		sql.append(" td on td.id=t.id left join ");
		sql.append(Customer.tableName);
		sql.append(" cust on cust.id=t.customer_id left join ");
		sql.append(Contacts.tableName);
		sql.append(" ct on ct.id=t.contacts_id left join ");
		sql.append(userView);
		sql.append(" c on c.id=t.creater_id left join ");
		sql.append(userView);
		sql.append(" u on u.id=t.updater_id left join ");
		sql.append(userView);
		sql.append(" h on h.id=t.head_id ");
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
			Db.update("delete  from crm_business_data where id in ("+ ids_.toString()+ ")",parame.toArray());
			Db.update("delete  from scm_order_product where id in ("+ ids_.toString()+ ")",parame.toArray());
			parame.add(company_id);
			Db.update("delete  from " + tableName + " where id in ("+ ids_.toString()+ ") and company_id=? ",parame.toArray());
		}
	}
	/**
	 * 我的销售漏斗
	 * @param user_id
	 * @param start_date
	 * @param end_date
	 * @param type 0:数量 1:金额
	 * @return
	 */
	public List<Record> myFunnel(String user_id,String start_date,String end_date,Integer type) {
		return Db.find("select "+(type==0?"count(t.id)":"sum(t.estimate_price)/10000")+" as \"value\",p.name from "+tableName+" t left join sso_parame p on t.status=p.id where t.head_id=? and t.is_end=0  and t.create_datetime>=? and t.create_datetime <=? group by p.name",user_id,start_date+" 00:00:00",end_date+" 23:59:59");
	}
	public Object funnel(String company_id, String head_id, String start_date,String end_date, int type) {
		List<Object> parame=new ArrayList<Object>();
		StringBuffer sql=new StringBuffer("select p.name,");
		sql.append((type==0?"count(t.id)":"sum(t.estimate_price)/10000"));
		sql.append(" as \"value\" from ");
		sql.append(tableName);
		sql.append(" t left join sso_parame p on t.status=p.id where t.company_id=? and t.is_end=0 ");
		parame.add(company_id);
		if(StringUtils.isNotEmpty(head_id)){
			sql.append(" and head_id=? ");
			parame.add(head_id);
		}
		if(StringUtils.isNotEmpty(start_date)){
			sql.append(" and t.create_datetime>=? ");
			parame.add(start_date+" 00:00:00");
		}
		if(StringUtils.isNotEmpty(end_date)){
			sql.append("  and t.create_datetime <=? ");
			parame.add(end_date+" 23:59:59");
		}
		sql.append(" group by p.name");
		return Db.find(sql.toString(),parame.toArray());
	}
}
