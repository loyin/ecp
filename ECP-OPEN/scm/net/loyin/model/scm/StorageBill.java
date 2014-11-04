package net.loyin.model.scm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import net.loyin.jfinal.anatation.TableBind;
import net.loyin.jfinal.model.IdGenerater;
import net.loyin.model.sso.Person;
import net.loyin.model.sso.SnCreater;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
/**
 * 出入库单
 * @author 龙影
 */
@TableBind(name="scm_storage_bill")
public class StorageBill extends Model<StorageBill> {
	private static final long serialVersionUID = 5708102282757729482L;
	public static final String tableName="scm_storage_bill";
	public static StorageBill dao=new StorageBill();
	public Page<StorageBill> page(int pageNo, int pageSize, Map<String, Object> filter,Integer qryType) {
		String userView=Person.tableName;
		List<Object> parame=new ArrayList<Object>();
		StringBuffer sql=new StringBuffer(" from ");
		sql.append(tableName);
		sql.append(" t left join ");
		sql.append(Depot.tableName);
		sql.append(" dout on dout.id=t.depot_id left join ");
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
			sql.append(" and t.billsn like ?");
			keyword="%"+keyword+"%";
			parame.add(keyword);
		}
		String start_date=(String) filter.get("start_date");
		if(StringUtils.isNotEmpty(start_date)){
			sql.append(" and (t.create_datetime >= ? or t.bill_date>=?)");
			parame.add(start_date+" 00:00:00");
			parame.add(start_date);
		}
		String end_date=(String) filter.get("end_date");
		if(StringUtils.isNotEmpty(end_date)){
			sql.append(" and (t.create_datetime <= ? or t.bill_date<=?)");
			parame.add(end_date+" 23:59:59");
			parame.add(end_date);
		}
		Integer status=(Integer) filter.get("status");//提交状态
		if(status!=null){
			sql.append(" and t.status = ?");
			parame.add(status);
		}
		Integer type=(Integer) filter.get("type");//提交状态
		if(type!=null){
			sql.append(" and t.type = ?");
			parame.add(type);
		}
		Integer is_deleted=(Integer) filter.get("is_deleted");//是否删除
		if(is_deleted!=null){
			sql.append(" and t.is_deleted = ?");
			parame.add(is_deleted);
		}
		sql.append(" and (t.creater_id='0' or(");
		String user_id=(String)filter.get("user_id");//当前用户id
		String position_id=(String)filter.get("position_id");//当前用户岗位id
		
		String sql_ = "WITH RECURSIVE d AS (SELECT d1.id,d1.pid,d1.name,d1.sort_num,d1.type FROM v_user_position d1 where d1.id=?"
				+ "union ALL SELECT d2.id,d2.pid,d2.name,d2.sort_num,d2.type FROM v_user_position d2, d WHERE d2.pid = d.id) "
				+ "SELECT distinct id FROM d where type=10 and d.id not in (select id from sso_user where position_id =? and id!=?)";
		switch(qryType){
		case -1://我创建的及我负责的
			sql.append(" (t.creater_id=? or t.head_id=?)");
			parame.add(user_id);
			parame.add(user_id);
			break;
		case 0://我创建的
			sql.append(" t.creater_id=?");
			parame.add(user_id);
			break;
		case 1://我负责的
			sql.append(" t.head_id=?");
			parame.add(user_id);
			break;
		case 2://下属创建的
			sql.append(" t.creater_id in(");
			sql.append(sql_);
			sql.append(") and t.creater_id !=?");
			parame.add(position_id);
			parame.add(position_id);
			parame.add(user_id);
			parame.add(user_id);
			break;
		case 3://下属负责的
			sql.append(" t.head_id in(");
			sql.append(sql_);
			sql.append(") and t.head_id !=?");
			parame.add(position_id);
			parame.add(position_id);
			parame.add(user_id);
			parame.add(user_id);
			break;
		case 5://我及下属的
			sql.append(" (t.head_id in(");
			sql.append(sql_);
			sql.append(")or t.creater_id in(");
			sql.append(sql_);
			sql.append(")or t.head_id =? or t.creater_id=?)");
			parame.add(position_id);
			parame.add(position_id);
			parame.add(user_id);
			parame.add(position_id);
			parame.add(position_id);
			parame.add(user_id);
			parame.add(user_id);
			parame.add(user_id);
			break;
		}
		sql.append("))");
		String sortField=(String)filter.get("_sortField");
		if(StringUtils.isNotEmpty(sortField)){
			sql.append(" order by ");
			sql.append(sortField);
			sql.append(" ");
			sql.append((String)filter.get("_sort"));
		}
		return dao.paginate(pageNo, pageSize, "select t.*,h.realname as head_name,c.realname as creater_name,u.realname as updater_name,dout.name depot_name ",sql.toString(),parame.toArray());
	}
	public StorageBill findById(String id,String company_id){
		String userView=Person.tableName;
		StringBuffer sql=new StringBuffer("select t.*,h.realname as head_name,c.realname as creater_name,u.realname as updater_name,dout.name depot_name from ");
		sql.append(tableName);
		sql.append(" t left join ");
		sql.append(Depot.tableName);
		sql.append(" dout on dout.id=t.depot_id left join ");
		sql.append(userView);
		sql.append(" c on c.id=t.creater_id left join ");
		sql.append(userView);
		sql.append(" u on u.id=t.updater_id left join ");
		sql.append(userView);
		sql.append(" h on h.id=t.head_id ");
		sql.append(" where t.company_id=? and t.id=? ");
		return dao.findFirst(sql.toString(),company_id,id);
	}
	/**判断是否已经提交*/
	public boolean isSubmit(String id){
		String[] ids=id.split(",");
		StringBuffer ids_=new StringBuffer();
		List<String> parame=new ArrayList<String>();
		for(String id_:ids){
			ids_.append("?,");
			parame.add(id_);
		}
		ids_.append("'-'");
		Record r=Db.findFirst("select count(1) ct from "+tableName+" where id in("+ids_.toString()+") and status>0",parame.toArray());
		return (r!=null&&r.getLong("ct")>0);
	}
	/**直接删除 未提交的时间*/
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
			Db.update("delete from scm_stock_allot_list where id in ("+ids_.toString()+")",parame.toArray());
			parame.add(company_id);
			Db.update("delete from " + tableName + " where id in ("+ids_.toString()+") and company_id=? ",parame.toArray());
		}
	}
	/**回收站 未提交的数据*/
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
			Db.update("update " + tableName + " set is_deleted=1,deleter_id=?,delete_datetime=? where id in ("+ids_.toString()+") and status=0 and company_id=? ",parame.toArray());
		}
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
	/**提交*/
	@Before(Tx.class)
	public void submit(String id, String company_id, String uid,String now,String nowtime) {
		Db.update("update " + tableName + " set status=1 where id=? and company_id=? ",id,company_id);
		StorageBill po=this.findById(id, company_id);
		String depot_id=po.getStr("depot_id");
		//获取产品信息
		List<Record> list=Db.find("select * from "+StorageBillList.tableName+" where id=?",id);
		Integer type=po.getInt("type");
		if(list!=null&&list.isEmpty()==false){
			for(Record r:list){
				Double amount=r.getDouble("amount");
				String product_id=r.getStr("product_id");
				Double balance=Stock.dao.updateStock(depot_id, product_id,type>4?amount:(0-amount));//更改差异额
				//更新结余库存
				Db.update("update "+StorageBillList.tableName+" set balance=? where id=? and product_id=? ",balance,id,product_id);
			}
		}
	}
	/**仓库流水帐*/
	public Page<StorageBill> flowPage(int pageNo, int pageSize, Map<String, Object> filter) {
		String userView=Person.tableName;
		List<Object> parame=new ArrayList<Object>();
		StringBuffer sql=new StringBuffer(" from ");
		sql.append(tableName);
		sql.append(" t left join ");
		sql.append(StorageBillList.tableName);
		sql.append(" sbl on sbl.id=t.id left join ");
		sql.append(Product.tableName);
		sql.append(" p on sbl.product_id=p.id left join ");
		sql.append(Depot.tableName);
		sql.append(" dout on dout.id=t.depot_id left join ");
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
			sql.append(" and (t.billsn like ? or p.billsn like ? or p.name like ? or p.model like ?)");
			keyword="%"+keyword+"%";
			parame.add(keyword);
			parame.add(keyword);
			parame.add(keyword);
			parame.add(keyword);
		}
		String start_date=(String) filter.get("start_date");
		if(StringUtils.isNotEmpty(start_date)){
			sql.append(" and (t.create_datetime >= ? or t.bill_date>=?)");
			parame.add(start_date+" 00:00:00");
			parame.add(start_date);
		}
		String end_date=(String) filter.get("end_date");
		if(StringUtils.isNotEmpty(end_date)){
			sql.append(" and (t.create_datetime <= ? or t.bill_date<=?)");
			parame.add(end_date+" 23:59:59");
			parame.add(end_date);
		}
		sql.append(" and t.status = 1");
		Integer type=(Integer) filter.get("type");//类型
		if(type!=null){
			sql.append(" and t.type = ?");
			parame.add(type);
		}
		String sortField=(String)filter.get("_sortField");
		if(StringUtils.isNotEmpty(sortField)){
			sql.append(" order by ");
			sql.append(sortField);
			sql.append(" ");
			sql.append((String)filter.get("_sort"));
		}
		return dao.paginate(pageNo, pageSize, "select t.*,sbl.amount,p.billsn product_sn,p.name product_name,p.unit,p.category,h.realname as head_name,c.realname as creater_name,u.realname as updater_name,dout.name depot_name ",sql.toString(),parame.toArray());
	}
	/**从订单转换出入库单*/
	@Before(Tx.class)
	public void createFromOrder(Order order,String now){
		Integer ordertype=order.getInt("ordertype");
		String order_id=order.getStr("id");
		String company_id=order.getStr("company_id");
		String id_=IdGenerater.me.diValFromPool();
		Integer type=0;
		switch(ordertype){
		case 0://采购
			type=0;
			break;
		case 1://采购退货   采购退货出库
			type=5;
			break;
		case 2://销售
			type=4;
			break;
		case 3://销售退货
			type=1;
			break;
		}
		String sn=SnCreater.dao.create(type>3?"OUTSTOCK":"INSTOCK", company_id);
		Db.update("INSERT INTO "+tableName+" (id, company_id,billsn,bill_date,order_id,ordersn,type,creater_id, create_datetime) "
				+" select ?,?,?,?,id,billsn,?,'0',? from "+Order.tableName+" where id=?",id_,company_id,sn,now.split(" ")[0],type,now,order_id);
		Db.update("INSERT INTO "+StorageBillList.tableName+" (id, product_id, amount)"+
				" select ?,product_id,amount from "+OrderProduct.tableName+" where id=?",id_,order_id);
		
	}
	/**收发明细*/
	public List<Record> rptList(Map<String, Object> filter) {
		List<Object> parame=new ArrayList<Object>();
		StringBuffer sql=new StringBuffer();
		sql.append("select o.bill_date,o.billsn,d1.name depot_name,o.type,p.billsn sn,p.name product_name,p.model,(case when type<4 then op.amount else 0 end)amount_in,(case when type>3 then op.balance else 0 end)balance_in,(case when type>3 then op.amount else 0 end)amount_out,(case when type>3 then op.balance else 0 end)balance_out,p.unit,u.realname head_name from ");
		sql.append(tableName);
		sql.append(" o left join ");
		sql.append(StorageBillList.tableName);
		sql.append(" op on op.id=o.id left join ");
		sql.append(Depot.tableName);
		sql.append(" d1 on d1.id=o.depot_id left join ");
		sql.append(Person.tableName);
		sql.append(" u on u.id=o.head_id left join ");
		sql.append(Product.tableName);
		sql.append(" p on op.product_id=p.id where o.company_id=? ");
		sql.append(" and o.status=1 ");
		parame.add(filter.get("company_id"));
		String uid=(String)filter.get("head_id");
		if(StringUtils.isNotEmpty(uid)){//负责人
			sql.append(" and o.head_id=? ");
			parame.add(uid);
		}
		String product_id=(String)filter.get("product_id");
		if(StringUtils.isNotEmpty(product_id)){//产品
			sql.append(" and op.product_id=? ");
			parame.add(product_id);
		}
		String depot_id=(String)filter.get("depot_id");
		if(StringUtils.isNotEmpty(depot_id)){//仓库
			sql.append(" and o.depot_id=? ");
			parame.add(depot_id);
		}
		sql.append(" order by o.bill_date asc,o.billsn asc");
		return Db.find(sql.toString(),parame.toArray());
	}
}
