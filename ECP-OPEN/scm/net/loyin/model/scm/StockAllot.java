package net.loyin.model.scm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.loyin.jfinal.anatation.TableBind;
import net.loyin.model.sso.Person;
import net.loyin.model.sso.SnCreater;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
/**
 * 调拨
 * @author 龙影
 */
@TableBind(name="scm_stock_allot")
public class StockAllot extends Model<StockAllot> {
	private static final long serialVersionUID = -1447533853973326468L;
	public static final String tableName="scm_stock_allot";
	public static StockAllot dao=new StockAllot();
	public Page<StockAllot> page(int pageNo, int pageSize, Map<String, Object> filter,Integer qryType) {
		String userView=Person.tableName;
		List<Object> parame=new ArrayList<Object>();
		StringBuffer sql=new StringBuffer(" from ");
		sql.append(tableName);
		sql.append(" t left join ");
		sql.append(Depot.tableName);
		sql.append(" dout on dout.id=t.out_depot_id left join ");
		sql.append(Depot.tableName);
		sql.append(" dto on dto.id=t.to_depot_id left join ");
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
		Integer submit_status=(Integer) filter.get("submit_status");//提交状态
		if(submit_status!=null){
			sql.append(" and t.submit_status = ?");
			parame.add(submit_status);
		}
		Integer is_deleted=(Integer) filter.get("is_deleted");//是否删除
		if(is_deleted!=null){
			sql.append(" and t.is_deleted = ?");
			parame.add(is_deleted);
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
		case 5://我及下属的
			sql.append(" and (t.head_id in(");
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
		
		String sortField=(String)filter.get("_sortField");
		if(StringUtils.isNotEmpty(sortField)){
			sql.append(" order by ");
			sql.append(sortField);
			sql.append(" ");
			sql.append((String)filter.get("_sort"));
		}
		return dao.paginate(pageNo, pageSize, "select t.*,h.realname as head_name,c.realname as creater_name,u.realname as updater_name,dout.name out_depot_name,dto.name to_depot_name ",sql.toString(),parame.toArray());
	}
	public StockAllot findById(String id,String company_id){
		String userView=Person.tableName;
		StringBuffer sql=new StringBuffer("select t.*,h.realname as head_name,c.realname as creater_name,u.realname as updater_name,dout.name out_depot_name,dto.name to_depot_name from ");
		sql.append(tableName);
		sql.append(" t left join ");
		sql.append(Depot.tableName);
		sql.append(" dout on dout.id=t.out_depot_id left join ");
		sql.append(Depot.tableName);
		sql.append(" dto on dto.id=t.to_depot_id left join ");
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
		Record r=Db.findFirst("select count(1) ct from "+tableName+" where id in("+ids_.toString()+") and submit_status>0",parame.toArray());
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
			Db.update("update " + tableName + " set is_deleted=1,deleter_id=?,delete_datetime=? where id in ("+ids_.toString()+") and submit_status=0 and company_id=? ",parame.toArray());
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
	public String[] submit(String id, String company_id, String uid,String now,String nowtime) {
		String[] billsnList=new String[2];
		Db.update("update " + tableName + " set submit_status=1 where id=? and company_id=? and (head_id=? or creater_id=?) ",id,company_id,uid,uid);
		StockAllot po=this.findById(id, company_id);
		String to_depot_id=po.getStr("to_depot_id");
		String out_depot_id=po.getStr("out_depot_id");
			billsnList[0]=SnCreater.dao.create("OUTSTOCK", company_id);
			billsnList[1]=SnCreater.dao.create("INSTOCK", company_id);
			//生成出入库
			StorageBill sb=new StorageBill();
			//出库单
			sb.set("billsn",billsnList[0]);
			sb.set("type",2);//2调拨出库 6调拨入库
			sb.set("depot_id",out_depot_id);
			sb.set("head_id",po.getStr("head_id"));
			sb.set("creater_id",uid);
			sb.set("create_datetime",nowtime);
			sb.set("bill_date",now);
			sb.set("company_id",company_id);
			sb.save();
			String bill_id=sb.getStr("id");
			Db.update("insert into "+StorageBillList.tableName+" (id,product_id,amount) select '"+bill_id+"',product_id,amount from "+StockAllotList.tableName+" a where a.id=?",id);
			//入库单
			sb.set("id",null);
			sb.set("type",6);//6调拨入库
			sb.set("depot_id",to_depot_id);
			sb.set("billsn",billsnList[1]);
			sb.save();
			bill_id=sb.getStr("id");
			Db.update("insert into "+StorageBillList.tableName+" (id,product_id,amount) select '"+bill_id+"',product_id,amount from "+StockAllotList.tableName+" a where a.id=?",id);
		return billsnList;
	}
	public List<Record> rptList(Map<String, Object> filter) {
		List<Object> parame=new ArrayList<Object>();
		StringBuffer sql=new StringBuffer();
		sql.append("select o.bill_date,o.billsn,d1.name out_depot_name,d2.name to_depot_name,p.billsn sn,p.name product_name,p.model,op.amount,p.unit,u.realname head_name from ");
		sql.append(tableName);
		sql.append(" o left join ");
		sql.append(StockAllotList.tableName);
		sql.append(" op on op.id=o.id left join ");
		sql.append(Depot.tableName);
		sql.append(" d1 on d1.id=o.out_depot_id left join ");
		sql.append(Depot.tableName);
		sql.append(" d2 on d2.id=o.to_depot_id left join ");
		sql.append(Person.tableName);
		sql.append(" u on u.id=o.head_id left join ");
		sql.append(Product.tableName);
		sql.append(" p on op.product_id=p.id where o.company_id=? ");
		sql.append(" and o.submit_status=1 ");
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
		String out_depot_id=(String)filter.get("out_depot_id");
		if(StringUtils.isNotEmpty(out_depot_id)){//调出仓库
			sql.append(" and o.out_depot_id=? ");
			parame.add(out_depot_id);
		}
		String to_depot_id=(String)filter.get("to_depot_id");
		if(StringUtils.isNotEmpty(to_depot_id)){//调入仓库
			sql.append(" and o.to_depot_id=? ");
			parame.add(to_depot_id);
		}
		sql.append(" order by o.bill_date asc");
		return Db.find(sql.toString(),parame.toArray());
	}
}
