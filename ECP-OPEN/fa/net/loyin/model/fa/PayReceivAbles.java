package net.loyin.model.fa;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.loyin.jfinal.anatation.TableBind;
import net.loyin.model.crm.Customer;
import net.loyin.model.scm.Order;
import net.loyin.model.sso.Person;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
/**
 * 应收应付
 * @author 龙影
 */
@TableBind(name="fa_pay_receiv_ables")
public class PayReceivAbles extends Model<PayReceivAbles> {
	private static final long serialVersionUID = -227368641266014593L;
	public static final String tableName="fa_pay_receiv_ables";
	public static PayReceivAbles dao=new PayReceivAbles();
	/**
	 * 分页查询
	 * @param pageNo
	 * @param pageSize
	 * @param where 查询条件
	 * @param param 参数
	 * @return
	 */
	public Page<PayReceivAbles> pageGrid(Integer pageNo,Integer pageSize,Map<String,Object> filter,Integer qryType){
		StringBuffer sql=new StringBuffer(" from ");
		List<Object> parame=new ArrayList<Object>();
		sql.append(tableName);
		sql.append(" t left join ");
		sql.append(Order.tableName);
		sql.append(" o on o.id=t.order_id left join ");
		sql.append(Customer.tableName);
		sql.append(" cst on cst.id=t.customer_id left join ");
		sql.append(Person.tableName);
		sql.append(" u on t.creater_id=u.id left join ");
		sql.append(Person.tableName);
		sql.append(" u1 on u1.id=t.head_id where o.is_deleted=0 and t.company_id=? ");
		parame.add((String)filter.get("company_id"));
		Integer is_deleted=(Integer)filter.get("is_deleted");
		if(is_deleted!=null){
			sql.append(" and t.is_deleted=? ");
			parame.add(is_deleted);
		}
		Integer type = (Integer) filter.get("type");
		if(type!=null){
			sql.append(" and t.type=?");
			parame.add(type);
		}
		Integer status=(Integer)filter.get("status");
		if(status!=null){
			sql.append(" and t.status=? ");
			parame.add(status);
		}
		String start_date=(String)filter.get("start_date");
		if(StringUtils.isNotEmpty(start_date)){
			sql.append(" and t.create_datetime>=?");
			parame.add(start_date+" 00:00:00");
		}
		String end_date=(String)filter.get("end_date");
		if(StringUtils.isNotEmpty(end_date)){
			sql.append(" and t.create_datetime<=?");
			parame.add(end_date+" 23:59:59");
		}
		String keyword=(String)filter.get("keyword");
		if(StringUtils.isNotEmpty(keyword)){
			sql.append(" and (cst.name like ? or cst.sn like ? or o.billsn like ?)");
			keyword="%"+keyword+"%";
			parame.add(keyword);
			parame.add(keyword);
			parame.add(keyword);
		}
		String sortField=(String)filter.get("_sortField");
		if(StringUtils.isNotEmpty(sortField)){
			sql.append(" order by ");
			sql.append(sortField);
			sql.append(" ");
			sql.append((String)filter.get("_sort"));
		}
		return paginate(pageNo,pageSize,"select t.*,o.billsn,cst.name customer_name,cst.type csttype,u.realname as creater_name,t.head_id,u1.realname as head_name ",sql.toString(), parame.toArray());
	}
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
			Db.update("delete  from " + tableName + " where id in ("+ ids_.toString()+ ") and company_id=? ",parame.toArray());
		}
	}
	public PayReceivAbles findById(String id,String company_id){
		StringBuffer sql=new StringBuffer("select t.*,o.billsn,cst.name customer_name,u.realname as creater_name,t.head_id,u1.realname as head_name  from ");
		sql.append(tableName);
		sql.append(" t left join ");
		sql.append(Order.tableName);
		sql.append(" o on o.id=t.order_id left join ");
		sql.append(Customer.tableName);
		sql.append(" cst on cst.id=t.customer_id left join ");
		sql.append(Person.tableName);
		sql.append(" u on t.creater_id=u.id left join ");
		sql.append(Person.tableName);
		sql.append(" u1 on u1.id=t.head_id where o.is_deleted=0 and t.id=? and t.company_id=? ");
		return dao.findFirst(sql.toString(),id,company_id);
	}
	/**回收站
	 * @param id
	 * @param company_id 
	 * @param uid 操作用户
	 * @param delete_datetime 删除时间
	 */
	public void trash(String id,String company_id,String uid,String delete_datetime){
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
			Db.update("update " + tableName + " set is_delete=1,deleter_id=?,delete_datetime=? where id in ("+ ids_.toString()+ ") and company_id=? ",parame.toArray());
		}
	}
	/**
	 * 通过订单创建应收应付(订单审核通过后创建)
	 * @param order
	 */
	@Before(Tx.class)
	public String createFromOrder(Order order,String now){
		Integer ordertype=order.getInt("ordertype");//订单类型
		Integer type=0;
		if(ordertype<2){//采购订单、采购退货
			type=0;//付款
		}
		if(ordertype>1){//销售订单 销售退货
			type=1;//收款
		}
		String order_id=order.getStr("id");
		String customer_id=order.getStr("customer_id");
		String company_id=order.getStr("company_id");
		BigDecimal amt=order.getBigDecimal("order_amt");
		if(ordertype==1||ordertype==3){//退货 生成负数应收应付款
			amt=new BigDecimal(0-amt.doubleValue());
		}
		String head_id=order.getStr("head_id");
		String creater_id=order.getStr("creater_id");
		//删除已有的应收应付
		Db.update("delete from "+PayReceivOrder.tableName+" where payables_id in (select id from "+tableName+" where order_id=?)",order_id);
		Db.update("delete from "+tableName+" where order_id=?",order_id);
		PayReceivAbles po=new PayReceivAbles();
		po.set("order_id",order_id);
		po.set("name","订单:"+order.get("billsn")+"-"+(type==0?"应付款":"应收款"));
		po.set("company_id",company_id);
		po.set("customer_id",customer_id);
		po.set("amt",amt);
		po.set("head_id",head_id);
		po.set("creater_id",creater_id);
		po.set("create_datetime",now);
		po.set("type",type);
		po.save();
		return po.getStr("id");
	}
	/***
	 * 应收应付报表明细 含期初余额
	 * @param filter
	 * @return
	 */
	public List<Record> rptList(Map<String, Object> filter) {
		StringBuffer sql=new StringBuffer("select t.type,t.name,(t.amt-t.pay_amt) amt,o.billsn,o.sign_date,o.ordertype,o.order_amt,cst.name customer_name,cst.type csttype from ");
		List<Object> parame=new ArrayList<Object>();
		sql.append(tableName);
		sql.append(" t left join ");
		sql.append(Order.tableName);
		sql.append(" o on o.id=t.order_id left join ");
		sql.append(Customer.tableName);
		sql.append(" cst on cst.id=t.customer_id where o.is_deleted=0 and t.company_id=? ");
		parame.add((String)filter.get("company_id"));
		String uid=(String)filter.get("uid");
		if(StringUtils.isNotEmpty(uid)){
			sql.append(" and t.head_id =?");
			parame.add(uid);
		}
		Integer is_deleted=(Integer)filter.get("is_deleted");
		if(is_deleted!=null){
			sql.append(" and t.is_deleted=? ");
			parame.add(is_deleted);
		}
		Integer type=(Integer)filter.get("type");
		if(type!=null){
			sql.append(" and t.type=? ");
			parame.add(type);
		}
		String customer_id=(String)filter.get("customer_id");
		if(StringUtils.isNotEmpty(customer_id)){
			sql.append(" and t.customer_id =?");
			parame.add(customer_id);
		}
		String start_date=(String)filter.get("start_date");
		if(StringUtils.isNotEmpty(start_date)){
			sql.append(" and t.create_datetime>=?");
			parame.add(start_date+" 00:00:00");
		}
		String end_date=(String)filter.get("end_date");
		if(StringUtils.isNotEmpty(end_date)){
			sql.append(" and t.create_datetime<=?");
			parame.add(end_date+" 23:59:59");
		}
		sql.append(" order by customer_name asc,qc asc,sign_date desc");//客户名称、是否期初余额、订单日期
		return Db.find(sql.toString(), parame.toArray());
	}
	/***
	 * 应收应付汇总报表(未结清)排除期初余额
	 * @param filter
	 * @return
	 */
	public List<Record> rptSumList(Map<String, Object> filter) {
		String customer_id=(String)filter.get("customer_id");
//		if(StringUtils.isEmpty(customer_id)){
//			return null;
//		}
		StringBuffer sql=new StringBuffer("select sum(t.amt) amt,sum(t.pay_amt) pay_amt,t.customer_id,cst.name customer_name,cst.type csttype from ");
		List<Object> parame=new ArrayList<Object>();
		sql.append(tableName);
		sql.append(" t left join ");
		sql.append(Order.tableName);
		sql.append(" o on o.id=t.order_id left join ");
		sql.append(Customer.tableName);
		sql.append(" cst on cst.id=t.customer_id where o.is_deleted=0 and t.company_id=? and t.status<2 and t.qc=0 ");
		parame.add((String)filter.get("company_id"));
		if(StringUtils.isNotEmpty(customer_id)){
			sql.append(" and t.customer_id =? ");
			parame.add(customer_id);
		}
		String start_date=(String)filter.get("start_date");
		if(StringUtils.isNotEmpty(start_date)){
			sql.append(" and (t.create_datetime>=? or t.qc_date>=?)");
			parame.add(start_date+" 00:00:00");
			parame.add(start_date);
		}
		String end_date=(String)filter.get("end_date");
		if(StringUtils.isNotEmpty(end_date)){
			sql.append(" and (t.create_datetime<=? or t.qc_date<=?)");
			parame.add(end_date+" 23:59:59");
			parame.add(end_date);
		}
		Integer type=(Integer)filter.get("type");
		if(type!=null){
			sql.append(" and t.type=? ");
			parame.add(type);
		}
		String uid=(String)filter.get("uid");
		if(StringUtils.isNotEmpty(uid)){
			sql.append(" and t.head_id =?");
			parame.add(uid);
		}
		
		sql.append(" group by customer_name,t.customer_id,csttype ");
		String sortField=(String)filter.get("_sortField");
		if(StringUtils.isNotEmpty(sortField)){
			sql.append(" order by ");
			sql.append(sortField);
			sql.append(" ");
			sql.append((String)filter.get("_sort"));
		}else{
			sql.append(" order by customer_name asc");
		}
		return Db.find(sql.toString(), parame.toArray());
	}
	public BigDecimal qrySumNoPay(String uid,int type) {
		Record r=Db.findFirst("select sum(amt-pay_amt) amt from "+tableName +" where (creater_id =? or head_id=? ) and type=? and status<2",uid,uid,type);
		if(r!=null){
			Object amt=r.get("amt");
			if(amt!=null){
				return new BigDecimal(amt.toString());
			}
		}
		return new BigDecimal(0);	
		
	}
	public BigDecimal qryNewAddNoPay(String uid, String ytd,int type) {
		Record r=Db.findFirst("select sum(amt-pay_amt) amt from "+tableName +" where (creater_id =? or head_id=? )and create_datetime>=? and type=? and status<2",uid,uid,ytd+" 00:00:00",type);
		if(r!=null){
			Object amt=r.get("amt");
			if(amt!=null){
				return new BigDecimal(amt.toString());
			}
		}
		return new BigDecimal(0);	
	}
	/**
	 * 创建期初应收应付余额
	 * @param nowDate 日期
	 * @param now 日期时间
	 */
	public void initial(String nowDate,String now){
		Db.update("insert into fa_pay_receiv_ables (id,company_id,type,customer_id,name,amt,creater_id,head_id,create_datetime,qcdate,qc)"+
		"select concat(customer_id,'"+now.replaceAll("\\-","")+"'),company_id,type,customer_id,'期初余额', sum(amt-pay_amt),'0','0','"+now+"','"+nowDate+"',1 from fa_pay_receiv_ables where create_datetime between ? and ? group by customer_id,type,company_id",nowDate+" 00:00:00",nowDate+" 23:59:59");
	}
	public List<Record> wlqkList(Map<String, Object> filter) {
		String customer_id=(String)filter.get("customer_id");
//		if(StringUtils.isEmpty(customer_id)){
//			return null;
//		}
		StringBuffer sql=new StringBuffer("select cst.sn,sum(case when t.type=0 then t.amt-t.pay_amt else 0 end) amt0,sum(case when t.type=1 then t.amt-t.pay_amt else 0 end) amt1,t.customer_id,cst.name customer_name,cst.type csttype from ");
		List<Object> parame=new ArrayList<Object>();
		sql.append(tableName);
		sql.append(" t left join ");
		sql.append(Customer.tableName);
		sql.append(" cst on cst.id=t.customer_id where t.company_id=? and t.status<2 and t.qc=0 ");
		parame.add((String)filter.get("company_id"));
		if(StringUtils.isNotEmpty(customer_id)){
			sql.append(" and t.customer_id =? ");
			parame.add(customer_id);
		}
		Integer csttype=(Integer)filter.get("csttype");
		if(csttype!=null){
			sql.append(" and cst.type=? ");
			parame.add(csttype);
		}
		String uid=(String)filter.get("uid");
		if(StringUtils.isNotEmpty(uid)){
			sql.append(" and t.head_id =?");
			parame.add(uid);
		}
		sql.append(" group by customer_name,t.customer_id,csttype,cst.sn ");
		String sortField=(String)filter.get("_sortField");
		if(StringUtils.isNotEmpty(sortField)){
			sql.append(" order by ");
			sql.append(sortField);
			sql.append(" ");
			sql.append((String)filter.get("_sort"));
		}else{
			sql.append(" order by customer_name asc");
		}
		return Db.find(sql.toString(), parame.toArray());
	}
	/***
	 * 回款金额
	 * @param uid
	 * @param start_date 起始日期
	 * @param end_date 起始日期
	 * @return
	 */
	public Object qryBackAmt(String uid, String start_date,String end_date) {
		List<Object> parame=new ArrayList<Object>();
		StringBuffer sql=new StringBuffer();
		sql.append("select sum(o.amt)amt from ");
		sql.append(PayReceivOrder.tableName);
		sql.append(" o,");
		sql.append(tableName);
		sql.append(" p where o.payables_id=p.id and o.status=1 and p.head_id=? ");
		parame.add(uid);
		if(StringUtils.isNotEmpty(start_date)){
			sql.append(" and o.pay_datetime >=?");
			parame.add(start_date);
		}
		if(StringUtils.isNotEmpty(end_date)){
			sql.append(" and o.pay_datetime <=?");
			parame.add(end_date);
		}
		Record r=Db.findFirst(sql.toString(),parame.toArray());
		return r!=null?r.get("amt"):0;
	}
}
