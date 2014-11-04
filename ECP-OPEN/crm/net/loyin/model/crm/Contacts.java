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
 * 客户/供应商 联系人
 * @author 龙影
 * 2014年9月17日
 */
@TableBind(name="crm_contacts")
public class Contacts extends Model<Contacts> {
	private static final long serialVersionUID = 8904351654149927778L;
	public static final String tableName="crm_contacts";
	public static Contacts dao=new Contacts();
	public List<Contacts> findByCustomerId(String customer_id){
		return dao.find("select * from "+tableName+" where customer_id=? order by is_main desc",customer_id);
	}
	public Contacts findById(String id,String company_id){
		String userView=Person.tableName;
		StringBuffer sql=new StringBuffer("select t.*,cust.type as custType,cust.name as customer_name,c.realname creater_name,u.realname updater_name,h.realname head_name from ");
		sql.append(tableName);
		sql.append(" t left join ");
		sql.append(Customer.tableName);
		sql.append(" cust on cust.id=t.customer_id ");
		sql.append(" left join ");
		sql.append(userView);
		sql.append(" c on c.id=t.creater_id left join ");
		sql.append(userView);
		sql.append(" u on u.id=t.updater_id left join ");
		sql.append(userView);
		sql.append(" h on h.id=cust.head_id ");
		sql.append(" where t.id=? and cust.company_id=? ");
		return dao.findFirst(sql.toString(),id,company_id);
	}
	/***
	 * 查询首要联系人
	 * @param id
	 * @return
	 */
	public Contacts findMainByCustomerId(String customer_id) {
		return dao.findFirst("select * from "+tableName+" where customer_id=? and is_main=1 ",customer_id);
	}
	@Before(Tx.class)
	public void del(String id,String company_id){
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
			Db.update("delete  from " + tableName + " where id in ("+ids_.toString()+ ") and company_id=? ",parame.toArray());
		}
	}
	public Page<Contacts> pageGrid(int pageNo, int pageSize,Map<String, Object> filter) {
		String userView=Person.tableName;
		List<Object> parame=new ArrayList<Object>();
		StringBuffer sql=new StringBuffer(" from ");
		sql.append(tableName);
		sql.append(" t left join ");
		sql.append(Customer.tableName);
		sql.append(" cust on cust.id=t.customer_id ");
		sql.append(" left join ");
		sql.append(userView);
		sql.append(" c on c.id=t.creater_id left join ");
		sql.append(userView);
		sql.append(" u on u.id=t.updater_id left join ");
		sql.append(userView);
		sql.append(" h on h.id=cust.head_id ");
		sql.append(" where 1=1 ");
		String keyword=(String)filter.get("keyword");
		if(StringUtils.isNotEmpty(keyword)){
			sql.append(" and (t.name like ? or cust.sn like ? or cust.name like ? or t.mobile like ? or t.telephone like ?)");
			keyword="%"+keyword+"%";
			parame.add(keyword);
			parame.add(keyword);
			parame.add(keyword);
			parame.add(keyword);
			parame.add(keyword);
		}
		String customer_id=(String)filter.get("customer_id");
		if(StringUtils.isNotEmpty(customer_id)){
			sql.append(" and t.customer_id=? ");
			parame.add(customer_id);
		}
		String sortField=(String)filter.get("_sortField");
		if(StringUtils.isNotEmpty(sortField)){
			String sort=(String)filter.get("_sort");
			sql.append(" order by ");
			sql.append(sortField);
			sql.append(" ");
			sql.append(sort);
		}
		return this.paginate(pageNo, pageSize, "select t.*,cust.type as custType,cust.name as customer_name,c.realname creater_name,u.realname updater_name,h.realname head_name ",sql.toString(),parame.toArray());
	}
	public List<Contacts> list(String companyId, String customer_id) {
		return dao.find("select * from "+tableName+" where customer_id=? and company_id=? order by is_main desc",customer_id,companyId);
	}
	
}
