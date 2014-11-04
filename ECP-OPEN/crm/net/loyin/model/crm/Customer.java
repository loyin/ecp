package net.loyin.model.crm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.loyin.ctrl.BaseController;
import net.loyin.jfinal.anatation.TableBind;
import net.loyin.jfinal.model.IdGenerater;
import net.loyin.model.sso.Person;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
/**
 * 客户/供应商
 * @author 龙影
 */
@TableBind(name="crm_customer")
public class Customer extends Model<Customer> {
	private static final long serialVersionUID = -4221825254783835788L;
	public static final String tableName="crm_customer";
	public static Customer dao=new Customer();
	/**
	 * 分页条件查询
	 * @param pageNo
	 * @param pageSize
	 * @param filter 参数 为:k,v==>字段,值
	 * @param qryType 查询类型
	 * @return
	 */
	public Page<Customer> pageGrid(int pageNo, int pageSize,Map<String, Object> filter,Integer qryType) {
		Date now=new Date();
		Calendar cal=Calendar.getInstance();
		String userView=Person.tableName;
		String sql_ = "WITH RECURSIVE d AS (SELECT d1.id,d1.pid,d1.name,d1.sort_num,d1.type FROM v_user_position d1 where d1.id=?"
				+ "union ALL SELECT d2.id,d2.pid,d2.name,d2.sort_num,d2.type FROM v_user_position d2, d WHERE d2.pid = d.id) "
				+ "SELECT distinct id FROM d where type=10 and d.id not in (select id from sso_user where position_id =? and id!=?)";
		List<Object> parame=new ArrayList<Object>();
		StringBuffer sql=new StringBuffer(" from ");
		sql.append(tableName);
		sql.append(" t left join ");
		sql.append(userView);
		sql.append(" c on c.id=t.creater_id left join ");
		sql.append(userView);
		sql.append(" u on u.id=t.updater_id left join ");
		sql.append(userView);
		sql.append(" h on h.id=t.head_id ");
		sql.append(" where t.company_id=? ");
		parame.add(filter.get("company_id"));
		String user_id=(String)filter.get("user_id");//当前用户id
		String position_id=(String)filter.get("position_id");//当前用户岗位id
		String keyword=(String)filter.get("keyword");
		if(StringUtils.isNotEmpty(keyword)){
			sql.append(" and (t.name like ? or t.sn like ?)");
			keyword="%"+keyword+"%";
			parame.add(keyword);
			parame.add(keyword);
		}
		String uid=(String)filter.get("uid");//当前用户查询用户id
		if(StringUtils.isNotEmpty(uid)){
			sql.append(" and (t.creater_id=? or t.head_id=?)");
			parame.add(uid);
			parame.add(uid);
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
		Integer status=(Integer) filter.get("status");//状态
		if(status!=null){
			sql.append(" and t.status = ?");
			parame.add(status);
		}
		Integer is_deleted=(Integer) filter.get("is_deleted");//是否删除
		if(is_deleted!=null){
			sql.append(" and t.is_deleted = ?");
			parame.add(is_deleted);
		}
		Integer type=(Integer) filter.get("type");//状态
		if(type>=0){
			sql.append(" and t.type = ?");
			parame.add(type);
		}else{//-1默认查询客户
			sql.append(" and t.type >0 ");
		}
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
		case 5://一周未跟进  下属及本人的负责的
			sql.append(" and t.head_id in(");
			sql.append(sql_);
			sql.append(")");
			parame.add(position_id);
			parame.add(position_id);
			parame.add(user_id);
			sql.append(" and t.id not in(select customer_id from crm_contact_record c where c.create_datetime >=?and c.create_datetime<=? )");
			sql.append(" and t.id in(select customer_id from crm_contact_record c)");
			cal.add(Calendar.DATE,-7);
			parame.add(BaseController.dateFormat.format(cal.getTime())+" 00:00:00");
			parame.add(BaseController.dateFormat.format(now)+" 23:59:59");
			break;
		case 6://半月未跟进  下属及本人的负责的
			sql.append(" and t.head_id in(");
			sql.append(sql_);
			sql.append(")");
			parame.add(position_id);
			parame.add(position_id);
			parame.add(user_id);
			sql.append(" and t.id not in(select customer_id from crm_contact_record c where c.create_datetime >=?and c.create_datetime<=? )");
			sql.append(" and t.id in(select customer_id from crm_contact_record c)");
			cal.add(Calendar.DATE,-15);
			parame.add(BaseController.dateFormat.format(cal.getTime())+" 00:00:00");
			parame.add(BaseController.dateFormat.format(now)+" 23:59:59");
			break;
		case 7://一直未跟进  下属及本人的负责的
			sql.append(" and t.head_id in(");
			sql.append(sql_);
			sql.append(")");
			parame.add(position_id);
			parame.add(position_id);
			parame.add(user_id);
			sql.append(" and t.id not in(select customer_id from crm_contact_record c)");
			break;
		case 9://已购买 下属及本人的负责的
			sql.append(" and t.head_id in(");
			sql.append(sql_);
			sql.append(")");
			parame.add(position_id);
			parame.add(position_id);
			parame.add(user_id);
			sql.append(" and t.id in(select customer_id from scm_order c )");
			break;
		case 10://未购买下属及本人负责的
			sql.append(" and t.head_id in(");
			sql.append(sql_);
			sql.append(")");
			parame.add(position_id);
			parame.add(position_id);
			parame.add(user_id);
			sql.append(" and t.id not in(select customer_id from scm_order c )");
			break;
		}
		String sortField=(String)filter.get("_sortField");
		if(StringUtils.isNotEmpty(sortField)){
			String sort=(String)filter.get("_sort");
			sql.append(" order by ");
			sql.append(sortField);
			sql.append(" ");
			sql.append(sort);
		}
		return dao.paginate(pageNo, pageSize, "select t.*,h.realname as head_name,c.realname as creator_name,u.realname as updater_name ",sql.toString(),parame.toArray());
	}
	/**
	 * 查询客户信息及联系人信息
	 * @param filter
	 * @param qryType
	 * @return
	 */
	public List<Record> findCustAndContactList(Map<String, Object> filter,Integer qryType) {
		Date now=new Date();
		Calendar cal=Calendar.getInstance();
		String userView=Person.tableName;
		String sql_ = "WITH RECURSIVE d AS (SELECT d1.id,d1.pid,d1.name,d1.sort_num,d1.type FROM v_user_position d1 where d1.id=?"
				+ "union ALL SELECT d2.id,d2.pid,d2.name,d2.sort_num,d2.type FROM v_user_position d2, d WHERE d2.pid = d.id) "
				+ "SELECT distinct id FROM d where type=10 and d.id not in (select id from sso_user where position_id =? and id!=?)";
		List<Object> parame=new ArrayList<Object>();
		StringBuffer sql=new StringBuffer("select distinct t.*,h.realname as head_name,");
		sql.append("ct.name ctname,ct.mobile,ct.post,ct.department,ct.sex,ct.saltname,ct.telephone cttelephone,ct.email ctemail,ct.qq,ct.zip_code ctzip_code,ct.type cttype,ct.idcard ");
		sql.append(" from ");
		sql.append(tableName);
		sql.append(" t left join ");
		sql.append(Contacts.tableName);
		sql.append(" ct on ct.customer_id=t.id left join ");
		sql.append(userView);
		sql.append(" h on h.id=t.head_id ");
		sql.append(" where t.company_id=? ");
		parame.add(filter.get("company_id"));
		String user_id=(String)filter.get("user_id");//当前用户id
		String position_id=(String)filter.get("position_id");//当前用户岗位id
		String keyword=(String)filter.get("keyword");
		if(StringUtils.isNotEmpty(keyword)){
			sql.append(" and (t.name like ? or t.sn like ?)");
			keyword="%"+keyword+"%";
			parame.add(keyword);
			parame.add(keyword);
		}
		String uid=(String)filter.get("uid");//当前用户查询用户id
		if(StringUtils.isNotEmpty(uid)){
			sql.append(" and (t.creater_id=? or t.head_id=?)");
			parame.add(uid);
			parame.add(uid);
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
		Integer status=(Integer) filter.get("status");//状态
		if(status!=null){
			sql.append(" and t.status = ?");
			parame.add(status);
		}
		Integer is_deleted=(Integer) filter.get("is_deleted");//是否删除
		if(is_deleted!=null){
			sql.append(" and t.is_deleted = ?");
			parame.add(is_deleted);
		}
		Integer type=(Integer) filter.get("type");//状态
		if(type>=0){
			sql.append(" and t.type = ?");
			parame.add(type);
		}else{//-1默认查询客户
			sql.append(" and t.type >0 ");
		}
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
		case 5://一周未跟进  下属及本人的负责的
			sql.append(" and t.head_id in(");
			sql.append(sql_);
			sql.append(")");
			parame.add(position_id);
			parame.add(position_id);
			parame.add(user_id);
			sql.append(" and t.id not in(select customer_id from crm_contact_record c where c.create_datetime >=?and c.create_datetime<=? )");
			sql.append(" and t.id in(select customer_id from crm_contact_record c)");
			cal.add(Calendar.DATE,-7);
			parame.add(BaseController.dateFormat.format(cal.getTime())+" 00:00:00");
			parame.add(BaseController.dateFormat.format(now)+" 23:59:59");
			break;
		case 6://半月未跟进  下属及本人的负责的
			sql.append(" and t.head_id in(");
			sql.append(sql_);
			sql.append(")");
			parame.add(position_id);
			parame.add(position_id);
			parame.add(user_id);
			sql.append(" and t.id not in(select customer_id from crm_contact_record c where c.create_datetime >=?and c.create_datetime<=? )");
			sql.append(" and t.id in(select customer_id from crm_contact_record c)");
			cal.add(Calendar.DATE,-15);
			parame.add(BaseController.dateFormat.format(cal.getTime())+" 00:00:00");
			parame.add(BaseController.dateFormat.format(now)+" 23:59:59");
			break;
		case 7://一直未跟进  下属及本人的负责的
			sql.append(" and t.head_id in(");
			sql.append(sql_);
			sql.append(")");
			parame.add(position_id);
			parame.add(position_id);
			parame.add(user_id);
			sql.append(" and t.id not in(select customer_id from crm_contact_record c)");
			break;
		case 9://已购买 下属及本人的负责的
			sql.append(" and t.head_id in(");
			sql.append(sql_);
			sql.append(")");
			parame.add(position_id);
			parame.add(position_id);
			parame.add(user_id);
			sql.append(" and t.id in(select customer_id from scm_order c )");
			break;
		case 10://未购买下属及本人负责的
			sql.append(" and t.head_id in(");
			sql.append(sql_);
			sql.append(")");
			parame.add(position_id);
			parame.add(position_id);
			parame.add(user_id);
			sql.append(" and t.id not in(select customer_id from scm_order c )");
			break;
		}
		    sql.append(" order by t.sn desc");
		return Db.find(sql.toString(),parame.toArray());
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
			Db.update("delete  from " + CustomerData.tableName + " where id in ("+ids_.toString()+")",parame.toArray());
			parame.add(company_id);
			Db.update("delete  from " + Contacts.tableName + " where customer_id in ("+ids_.toString()+") and company_id=? ",parame.toArray());
			Db.update("delete  from " + tableName + " where id in ("+ids_.toString()+") and company_id=? ",parame.toArray());
		}
	}
	/**回收站*/
	public void trash(String id, String uid, String company_id,String delete_datatime) {
		if (StringUtils.isNotEmpty(id)) {
			String[] ids=id.split(",");
			StringBuffer ids_=new StringBuffer();
			List<String> parame=new ArrayList<String>();
			parame.add(uid);
			parame.add(delete_datatime);
			for(String id_:ids){
				ids_.append("?,");
				parame.add(id_);
			}
			ids_.append("'-'");
			parame.add(company_id);
			Db.update("update " + tableName + " set is_deleted=1,deleter_id=?,delete_datetime=? where id in ("+ids_.toString()+") and company_id=? ",parame.toArray());
		}
	}
	public boolean existCust(String name,String id,int type,String company_id){
		Customer c=null;
		if(StringUtils.isEmpty(id)){
			c=dao.findFirst("select * from "+tableName+" where type=? and name=? and company_id=?",type,name,company_id);
		}else{
			c=dao.findFirst("select * from "+tableName+" where id!=? and type=? and name=? and company_id=?",id,type,name,company_id);
		}
		return c!=null;
	}
	public Customer findById(String id,String company_id){
		String userView=Person.tableName;
		StringBuffer sql=new StringBuffer();
		sql.append(tableName);
		sql.append(" t left join ");
		sql.append(userView);
		sql.append(" c on c.id=t.creater_id left join ");
		sql.append(userView);
		sql.append(" u on u.id=t.updater_id left join ");
		sql.append(userView);
		sql.append(" h on h.id=t.head_id ");
		return dao.findFirst("select t.*,d.remark,d.data,h.realname as head_name,c.realname as creater_name,u.realname as updater_name from "+sql.toString()+" left join "+CustomerData.tableName+" d on d.id=t.id where t.company_id=? and t.id=?",company_id,id);
	}
	/**恢复*/
	public void reply(String id, String company_id) {
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
			Db.update("update " + tableName + " set is_deleted=0,deleter_id=null,delete_datetime=null where id in ("+ids_.toString()+") and company_id=? ",parame.toArray());
		}
	}
	/**放入客户池*/
	public void toPool(String id, String company_id) {
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
			Db.update("update " + tableName + " set status=1,head_id=null where id in ("+ids_.toString()+") and company_id=? ",parame.toArray());
		}
	}
	/**分配2或领取1*/
	@Before(Tx.class)
	public void allot(String id, String uid,int type,String now) {
		if (StringUtils.isNotEmpty(id)) {
			String[] ids=id.split(",");
			StringBuffer ids_=new StringBuffer();
			List<String> parame=new ArrayList<String>();
			parame.add(uid);
			for(String id_:ids){
				ids_.append("?,");
				parame.add(id_);
			}
			ids_.append("'-'");
			Db.update("update " + tableName + " set head_id=?,status=0 where id in ("+ids_.toString()+")",parame.toArray());
			//记录客户流转记录
			for(String id_:ids){
				Db.update("INSERT INTO crm_customer_record(id, customer_id, user_id, create_datetime, type) VALUES (?, ?, ?, ?, ?);",IdGenerater.me.getIdSeq(),id_,uid,now,type);
			}
		}
	}
	public int qryCount(String uid) {
		Record r=Db.findFirst("select count(id) ct from "+tableName +" where (creater_id =? or head_id=? ) ",uid,uid);
		if(r!=null){
			Object ct=r.get("ct");
			if(ct!=null){
				return Integer.parseInt(ct.toString());
			}
		}
		return 0;	
		
	}
	public Integer qryNewAddCount(String uid, String ytd) {
		Record r=Db.findFirst("select count(id) ct from "+tableName +" where (creater_id =? or head_id=? ) and create_datetime>=?",uid,uid,ytd+" 00:00:00");
		if(r!=null){
			Object ct=r.get("ct");
			if(ct!=null){
				return Integer.parseInt(ct.toString());
			}
		}
		return 0;
	}
	/**员工绩效*/
	public List<Record> performance(Map<String, Object> filter) {
		String start_date = (String) filter.get("start_date");
		start_date += " 00:00:00";
		String end_date = (String) filter.get("end_date");
		end_date += " 23:59:59";

		List<Object> parame = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("select u.id,p.realname,dpt.name department_name, ");
		sql.append("(select count(c.id) from crm_customer c where c.creater_id=u.id and c.create_datetime<=? )cust_ct, ");
		parame.add(start_date);
		sql.append("(select count(c.id) from crm_customer c where c.creater_id=u.id and c.create_datetime>=? and  c.create_datetime<=? )newcust_ct, ");
		parame.add(start_date);
		parame.add(end_date);
		sql.append("(select count(c.id) from crm_customer c where c.creater_id=u.id and c.create_datetime>=? and  c.create_datetime<=? )contact_ct, ");
		parame.add(start_date);
		parame.add(end_date);
		sql.append("(select count(l.id) from crm_leads l where l.creater_id=u.id and create_datetime>=? and  create_datetime<=? )leads_ct, ");
		parame.add(start_date);
		parame.add(end_date);
		sql.append("(select count(l.id) from crm_business l where l.creater_id=u.id and create_datetime>=? and create_datetime<=? )biz_ct, ");
		parame.add(start_date);
		parame.add(end_date);
		sql.append("(select sum(l.estimate_price) from crm_business l where l.creater_id=u.id and create_datetime>=? and  create_datetime<=? )biz_amt, ");
		parame.add(start_date);
		parame.add(end_date);
		sql.append("(select count(l.id) from scm_order l where l.creater_id=u.id and l.ordertype=4 and create_datetime>=? and create_datetime<=?)quoted_ct, ");
		parame.add(start_date);
		parame.add(end_date);
		sql.append("(select sum(l.order_amt) from scm_order l where l.creater_id=u.id and l.ordertype=4 and create_datetime>=? and create_datetime<=?)quoted_amt, ");
		parame.add(start_date);
		parame.add(end_date);
		sql.append("(select count(l.id) from scm_order l where l.creater_id=u.id and l.ordertype=2 and create_datetime>=? and create_datetime<=?)order_ct, ");
		parame.add(start_date);
		parame.add(end_date);
		sql.append("(select sum(l.order_amt) from scm_order l where l.creater_id=u.id and l.ordertype=2 and create_datetime>=? and create_datetime<=?)order_amt, ");
		parame.add(start_date);
		parame.add(end_date);
		sql.append("(select sum(l.order_amt) from scm_order l where l.creater_id=u.id and l.ordertype=2  and create_datetime>=? and create_datetime<=?)order_amt, ");
		parame.add(start_date);
		parame.add(end_date);
		sql.append("(select sum(l.pay_amt) from fa_pay_receiv_ables l where l.head_id=u.id and l.type=1  and create_datetime>=? and create_datetime<=?)pay_amt, ");
		parame.add(start_date);
		parame.add(end_date);
		sql.append("(select sum(l.amt-l.pay_amt) from fa_pay_receiv_ables l where l.head_id=u.id and l.type=1  and create_datetime>=? and create_datetime<=?)nopay_amt, ");
		parame.add(start_date);
		parame.add(end_date);
		sql.append("(select sum(l.order_amt) from scm_order l where l.head_id=u.id and l.ordertype=3 and create_datetime>=? and create_datetime<=?)tui_amt ");
		parame.add(start_date);
		parame.add(end_date);
		sql.append("from sso_user u ,sso_person p ,sso_position po,sso_position dpt where p.id=u.id and po.id=u.position_id and dpt.id=po.department_id ");
		sql.append(" and u.company_id=? and u.status=1 ");
		parame.add(filter.get("company_id"));
		String uid = (String) filter.get("uid");
		if (StringUtils.isNotEmpty(uid)) {
			sql.append(" and u.id=? ");
			parame.add(uid);
		}
		String department_id = (String) filter.get("department_id");
		if (StringUtils.isNotEmpty(department_id)) {
			sql.append(" and dpt.id=? ");
			parame.add(department_id);
		}
		sql.append(" order by department_name asc,p.realname asc ");
		return Db.find(sql.toString(), parame.toArray());
	}
	/**
	 * 导入客户及联系人
	 * @param dataList
	 * @param company_id
	 * @param userid
	 * @param clist
	 */
	@Before(Tx.class)
	public void impl(List<Map<String, Object>> dataList, String company_id,String userid,List<String>clist,String now){
		for(Map<String,Object> map:dataList){
			Customer cust=new Customer();
			for(int i=0;i<23;i++){
				String cl=clist.get(i);
				cust.set(cl,map.get(cl));
			}
			cust.set("head_id",userid);
			cust.set("creater_id",userid);
			cust.set("create_datetime",now);
			cust.set("company_id",company_id);
			cust.save();
			String custid=cust.getStr("id");
			Contacts ct=new Contacts();
			ct.set("customer_id",custid);
			for(int i=4;i<clist.size();i++){
				String cl=clist.get(i);
				ct.set(cl.replace("ct",""),map.get(cl));
			}
			ct.set("head_id",userid);
			ct.set("creater_id",userid);
			ct.set("create_datetime",now);
			ct.save();
		}
	}
}
