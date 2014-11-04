package net.loyin.model.fa;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.loyin.ctrl.BaseController;
import net.loyin.jfinal.anatation.TableBind;
import net.loyin.model.crm.Customer;
import net.loyin.model.scm.Order;
import net.loyin.model.sso.Person;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

/**
 * 收款付款
 * 
 * @author 龙影
 */
@TableBind(name = "fa_pay_receiv_order")
public class PayReceivOrder extends Model<PayReceivOrder> {
	private static final Logger log=Logger.getLogger(PayReceivOrder.class);
	private static final long serialVersionUID = 7401737015374782267L;
	public static final String tableName = "fa_pay_receiv_order";
	public static PayReceivOrder dao = new PayReceivOrder();

	/**
	 * 分页查询
	 * @param pageNo
	 * @param pageSize
	 * @param filer 参数
	 * @return
	 */
	public Page<PayReceivOrder> pageGrid(Integer pageNo, Integer pageSize,
			Map<String, Object> filter) {
		List<Object> parame = new ArrayList<Object>();
		String userView=Person.tableName;
		StringBuffer sql=new StringBuffer(" from ");
		sql.append(tableName);
		sql.append(" t left join ");
		sql.append(userView);
		sql.append(" c on c.id=t.creater_id left join ");
		sql.append(Customer.tableName);
		sql.append(" c_ on c_.id=t.customer_id left join ");
		sql.append(userView);
		sql.append(" u on u.id=t.head_id ");
		sql.append(" where t.company_id=? ");
		parame.add(filter.get("company_id"));
		String keyword=(String)filter.get("keyword");
		if(StringUtils.isNotEmpty(keyword)){
			sql.append(" and (t.name like ? or c_.name like ?)");
			keyword="%"+keyword+"%";
			parame.add(keyword);
			parame.add(keyword);
		}
		String start_date=(String) filter.get("start_date");
		if(StringUtils.isNotEmpty(start_date)){
			sql.append(" and (t.create_datetime >= ?  or t.pay_datetime>=?)");
			parame.add(start_date+" 00:00:00");
			parame.add(start_date+" 00:00:00");
		}
		String end_date=(String) filter.get("end_date");
		if(StringUtils.isNotEmpty(end_date)){
			sql.append(" and (t.create_datetime <= ? or t.pay_datetime<=?)");
			parame.add(end_date+" 23:59:59");
			parame.add(end_date+" 23:59:59");
		}
		String uid=(String)filter.get("uid");//查询用户id
		if(StringUtils.isNotEmpty(uid)){
			sql.append(" and (t.creater_id=? or t.head_id=?)");
			parame.add(uid);
			parame.add(uid);
		}
		Integer type = (Integer) filter.get("type");
		if(type!=null){
			sql.append(" and t.type=?");
			parame.add(type);
		}
		Integer status = (Integer) filter.get("status");
		if(status!=null){
			sql.append(" and t.status=?");
			parame.add(status);
		}
//		String user_id = (String) filter.get("user_id");
		return dao.paginate(pageNo,pageSize,"select t.*,c.realname as creater_name,t.head_id,u.realname as head_name,c_.name customer_name ",sql.toString(), parame.toArray());
	}

	public void del(String id, String company_id) {
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

	/**
	 * 回收站
	 * @param id
	 * @param company_id
	 * @param uid 操作用户
	 * @param delete_datetime 删除时间
	 */
	public void trash(String id, String company_id, String uid,String delete_datetime) {
		if (StringUtils.isNotEmpty(id)) {
			String[] ids=id.split(",");
			StringBuffer ids_=new StringBuffer();
			List<String> parame=new ArrayList<String>();
			parame.add(uid);
			parame.add(delete_datetime);
			for(String id_:ids){
				ids_.append("?,");
				parame.add(id_);
			}
			ids_.append("'-'");
			parame.add(company_id);
			Db.update("update "+ tableName+ " set is_delete=1,deleter_id=?,delete_datetime=? where id in ("+ids_.toString()+") and company_id=? ",parame.toArray());
		}
	}

	public PayReceivOrder findById(String id, String company_id) {
		return dao
				.findFirst(
						"select t.*,u.realname as creater_name,t.head_id,u1.realname as head_name,a.name account_name,s.name subject_name,c.name customer_name from "
								+ tableName
								+ " t left join "
								+ Person.tableName
								+ " u on t.creater_id=u.id left join "
								+ Subject.tableName
								+ " s on t.subject_id=s.id left join "
								+ Account.tableName
								+ " a on t.account_id=a.id left join "
								+ Customer.tableName
								+ " c on t.customer_id=c.id left join "
								+ Person.tableName
								+ " u1 on u1.id=t.head_id where t.id=? and t.company_id=?",
						id, company_id);
	}
	public boolean is_end(String id){
		PayReceivOrder po=this.findById(id);
		Integer is_end=0;
		if(po!=null){
			is_end=po.getInt("is_end");
		}
		return is_end==1;
	}
	/**
	 * 应收应付结算
	 * @param amt 付款金额
	 * @param ables_id 应收应付id
	 * @param order_id 收款付款id
	 * @throws Exception 
	 */
	@Before(Tx.class)
	public void jiesuan(BigDecimal amt, String ables_id,String order_id) throws Exception{
		PayReceivOrder order=PayReceivOrder.dao.findById(order_id);
		if(order.getInt("is_end")==1){
			throw new Exception("已经支付了，不能重复支付！");
		}
		String creater_id=order.getStr("creater_id");
		PayReceivAbles po=PayReceivAbles.dao.findById(ables_id);
		BigDecimal amt1=po.getBigDecimal("amt");//应支付金额
		BigDecimal pay_amt=po.getBigDecimal("pay_amt");//已支付金额
		if(pay_amt==null){
			pay_amt=new BigDecimal(0);
		}
		BigDecimal amt2=pay_amt.add(amt);
		String account_id=order.getStr("account_id");
		String customer_id=order.getStr("customer_id");
		Account a=Account.dao.findById(account_id);
		Integer type=po.getInt("type");
		if(type==0){//付款
			BigDecimal ye=a.getBigDecimal("amt");
			if(ye.compareTo(amt)<0||(ye.subtract(amt).compareTo(new BigDecimal(0))<=0)){
				throw new Exception("支付金额大于账号的余额！");
			}
			Db.update("update "+Account.tableName+" set amt=amt-? where id=?",amt,account_id);
			AccountDetail.dao.install(null, amt, account_id,customer_id, creater_id,order.getStr("name"));
		}else{//收款
			Db.update("update "+Account.tableName+" set amt=amt+? where id=?",amt,account_id);
			AccountDetail.dao.install(amt,null,account_id,customer_id,creater_id,order.getStr("name"));
		}
		Db.update("update "+tableName+" set is_end=1,status=1 where id=?",order_id);//更新为已结束状态。
		String now=BaseController.dateTimeFormat.format(new Date());
		Integer status=1;
		log.debug((amt1.compareTo(new BigDecimal(0))<0&&amt1.compareTo(amt2)<0)//负数
			||(amt1.compareTo(new BigDecimal(0))>0&&amt1.compareTo(amt2)>0)//正数
			);
		if((amt1.compareTo(new BigDecimal(0))<0&&amt1.compareTo(amt2)<0)//负数
			||(amt1.compareTo(new BigDecimal(0))>0&&amt1.compareTo(amt2)>0)//正数
			){
			status=1;//部分支付
		}else{
			status=2;//已结算
		}
		Db.update("update "+PayReceivAbles.tableName+" set pay_amt=?,status=?,pay_datetime=? where id=?",amt2,status,now,ables_id);
		Db.update("update "+Order.tableName+" set pay_status=?,pay_datetime=? from "+PayReceivAbles.tableName+" p where p.id=? and p.order_id=scm_order.id ",status,now,ables_id);
//		等同于 Db.update("update "+Order.tableName+" set pay_status=? where id in (select order_id from "+PayReceivAbles.tableName+" p  where p.id=?)",(amt1.compareTo(amt2)>0?1:(amt2.compareTo(amt1)>=0?2:0)),ables_id);
	}
	/**
	 * 修改支付状态
	 * @param i
	 * @param id
	 */
	public void upStatus(int i, String id) {
		if(StringUtils.isNotEmpty(id))
		Db.update("update "+tableName+" set status=? where id=?",i,id);
	}
	/**应收应付支付明细
	 * @param id 应收应付id
	 * @param company_id
	 * @return
	 */
	public List<PayReceivOrder> list(String id,String company_id) {
		return this.find("select t.*,u.realname as creater_name from "
				+ tableName
				+ " t left join "
				+ Person.tableName
				+ " u on t.creater_id=u.id where t.payables_id=? and t.company_id=? order by t.create_datetime",
		id, company_id);
	}
	/**科目统计*/
	public List<Record> rpt1List(Map<String, Object> filter) {
		List<Object> parame=new ArrayList<Object>();
		StringBuffer sql=new StringBuffer("select t.subject_id,t.billsn,t.pay_datetime,(case when t.type=1 then t.amt else 0 end)amt_in,(case when t.type=0 then t.amt else 0 end)amt_out,c.name customer_name from ");
		sql.append(tableName);
		sql.append(" t left join crm_customer c on c.id=t.customer_id where t.company_id=? and t.status=1 ");
		parame.add(filter.get("company_id"));
		String start_date=(String)filter.get("start_date");
		if(StringUtils.isNotEmpty(start_date)){
			sql.append(" and t.pay_datetime >=?");
			parame.add(start_date);
		}
		String end_date=(String)filter.get("end_date");
		if(StringUtils.isNotEmpty(end_date)){
			sql.append(" and t.pay_datetime <=?");
			parame.add(end_date);
		}
		String keyword=(String)filter.get("keyword");
		if(StringUtils.isNotEmpty(keyword)){
			sql.append(" and (t.billsn like ? or c.name like ? or c.sn like ?)");
			keyword="%"+keyword+"%";
			parame.add(keyword);
			parame.add(keyword);
			parame.add(keyword);
		}
		sql.append(" order by t.subject_id asc,t.pay_datetime asc ");
		return Db.find(sql.toString(),parame.toArray());
	}
}
