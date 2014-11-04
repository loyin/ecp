package net.loyin.model.scm;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.loyin.ctrl.BaseController;
import net.loyin.jfinal.anatation.TableBind;
import net.loyin.jfinal.model.IdGenerater;
import net.loyin.model.crm.Customer;
import net.loyin.model.fa.PayReceivAbles;
import net.loyin.model.sso.Person;
import net.loyin.model.sso.SnCreater;
import net.loyin.model.sso.User;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
/**
 * 订单合同
 * @author 龙影
 * 2014年9月23日
 */
@TableBind(name="scm_order")
public class Order extends Model<Order> {
	private static final long serialVersionUID = 8699093530520166772L;
	public static final String tableName="scm_order";
	public static Order dao=new Order();
	public Page<Order> page(int pageNo, int pageSize, Map<String, Object> filter,Integer qryType) {
		String userView=Person.tableName;
		List<Object> parame=new ArrayList<Object>();
		StringBuffer sql=new StringBuffer(" from ");
		sql.append(tableName);
		sql.append(" t left join ");
		sql.append(Customer.tableName);
		sql.append(" cust on cust.id=t.customer_id left join ");
		sql.append(userView);
		sql.append(" c on c.id=t.creater_id left join ");
		sql.append(userView);
		sql.append(" u on u.id=t.updater_id left join ");
		sql.append(userView);
		sql.append(" a on a.id=t.auditor_id left join ");
		sql.append(userView);
		sql.append(" h on h.id=t.head_id ");
		sql.append(" where t.company_id=? ");
		parame.add(filter.get("company_id"));
		String keyword=(String)filter.get("keyword");
		if(StringUtils.isNotEmpty(keyword)){
			sql.append(" and (t.name like ? or cust.name like ?)");
			keyword="%"+keyword+"%";
			parame.add(keyword);
			parame.add(keyword);
		}
		String start_date=(String) filter.get("start_date");
		if(StringUtils.isNotEmpty(start_date)){
			sql.append(" and (t.create_datetime >= ? or t.sign_date>=?)");
			parame.add(start_date+" 00:00:00");
			parame.add(start_date);
		}
		String end_date=(String) filter.get("end_date");
		if(StringUtils.isNotEmpty(end_date)){
			sql.append(" and (t.create_datetime <= ? or t.sign_date<=?)");
			parame.add(end_date+" 23:59:59");
			parame.add(end_date);
		}
		String status=(String) filter.get("status");//状态
		if(StringUtils.isNotEmpty(status)){
			sql.append(" and t.status = ?");
			parame.add(status);
		}
		Integer pay_status=(Integer) filter.get("pay_status");//支付状态
		if(pay_status!=null){
			sql.append(" and t.pay_status = ?");
			parame.add(pay_status);
		}
		Integer ordertype=(Integer) filter.get("ordertype");//订单类型
		if(ordertype!=null){
			sql.append(" and t.ordertype = ?");
			parame.add(ordertype);
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
		return dao.paginate(pageNo, pageSize, "select t.*,h.realname as head_name,c.realname as creater_name,a.realname as auditor_name,u.realname as updater_name,cust.name customer_name ",sql.toString(),parame.toArray());
	}
	public Order findById(String id,String company_id){
		String userView=Person.tableName;
		StringBuffer sql=new StringBuffer("select t.*,td.*,h.realname as head_name,c.realname as creater_name,u.realname as updater_name,cust.name customer_name from ");
		sql.append(tableName);
		sql.append(" t left join ");
		sql.append(OrderData.tableName);
		sql.append(" td on td.id=t.id left join ");
		sql.append(Customer.tableName);
		sql.append(" cust on cust.id=t.customer_id left join ");
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
			Db.update("delete  from scm_order_data where id in ("+ids_.toString()+")",parame.toArray());
			Db.update("delete  from scm_order_product where id in ("+ids_.toString()+")",parame.toArray());
			parame.add(company_id);
			Db.update("delete  from " + tableName + " where id in ("+ids_.toString()+") and company_id=? ",parame.toArray());
		}
	}
	/**判断是否已经支付*/
	public boolean isPay(String id){
		String[] ids=id.split(",");
		StringBuffer ids_=new StringBuffer();
		List<String> parame=new ArrayList<String>();
		for(String id_:ids){
			ids_.append("?,");
			parame.add(id_);
		}
		ids_.append("'-'");
		Record r=Db.findFirst("select count(1) ct from "+tableName+" where id in("+ids_.toString()+") and pay_status>0",parame.toArray());
		return (r!=null&&r.getLong("ct")>0);
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
	/**提交审核/取消审核*/
	public void subAudit(String id, String company_id, String uid,String now,int status) {
		if (StringUtils.isNotEmpty(id)) {
			//获取当前用户的上级用户
			User u=User.dao.findSupUser(uid);
			String auditor_id=u!=null?u.getStr("id"):"";
			if(status==1)
			Db.update("update " + tableName + " set audit_status=1,auditor_id=? where id=? and company_id=? and (head_id=? or creater_id=?) ",auditor_id,id,company_id,uid,uid);
			if(status==0)
				Db.update("update " + tableName + " set audit_status=0,auditor_id=null,audit_datetime=null where id=? and company_id=? and (head_id=? or creater_id=?) ",id,company_id,uid,uid);
		}
	}
	public BigDecimal qrySum(String uid,Integer ordertype) {
		Record r=Db.findFirst("select sum(order_amt) amt from "+tableName +" where (creater_id =? or head_id=? ) and ordertype=? and (submit_status=1 or audit_status=2)",uid,uid,ordertype);
		if(r!=null){
			Object amt=r.get("amt");
			if(amt!=null){
				return new BigDecimal(amt.toString());
			}
		}
		return new BigDecimal(0);
	}
	public BigDecimal qryNewAdd(String uid,String ytd,Integer ordertype) {
		Record r=Db.findFirst("select sum(case when ordertype=2 then order_amt else 0-order_amt end) amt from "+tableName +" where (creater_id =? or head_id=? ) and ordertype=?  and (submit_status=1 or audit_status=2) and sign_date>=?",uid,uid,ordertype,ytd);
		if(r!=null){
			Object amt=r.get("amt");
			if(amt!=null){
				return new BigDecimal(amt.toString());
			}
		}
		return new BigDecimal(0);
	}
	/***
	 * 对账表
	 * @param filter
	 * @return
	 */
	public List<Record> duiZhang(Map<String, Object> filter) {
		List<Object> parame=new ArrayList<Object>();
		StringBuffer sql=new StringBuffer("select t.order_amt,t.ordertype,t.rebate_amt,t.billsn,t.sign_date,p.pay_amt from ");
		sql.append(tableName);
		sql.append(" t left join ");
		sql.append(PayReceivAbles.tableName);
		sql.append(" p on p.order_id=t.id left join ");
		sql.append(Customer.tableName);
		sql.append(" c on c.id=t.customer_id where t.company_id=? and t.customer_id=? and t.sign_date between ? and ? ");
		parame.add(filter.get("company_id"));
		parame.add(filter.get("customer_id"));
		
		Integer type=(Integer)filter.get("type");
		if(type==1){//客户
			sql.append(" and c.csttype >0 ");
		}
		if(type==2){//供应商
			sql.append(" and c.csttype =0 ");
		}
		String start_date=(String)filter.get("start_date");
		parame.add(start_date);
		String end_date=(String)filter.get("end_date");
		parame.add(end_date);
		sql.append(" order by t.sign_date desc ");
		return Db.find(sql.toString(),parame.toArray());
	}
	/**
	 * 是否存在订单
	 * @param ordersn
	 * @param company_id
	 * @return
	 */
	public boolean exsit(String ordersn, String company_id, String customer_id,int ordertype) {
		Order order=this.findFirst("select * from "+tableName+" where billsn=? and company_id=? and customer_id=? and ordertype=?",ordersn,company_id,customer_id,ordertype);
		return order!=null;
	}
	/**
	 * 报价单转成订单
	 * @param id
	 */
	@Before(Tx.class)
	public void tansfer(String id,String company_id) {
		String id_=IdGenerater.me.diValFromPool();
		String billsn=SnCreater.dao.create("ORDER3", company_id);
		Db.update("INSERT INTO "+tableName+"(id, ordertype, customer_id, business_id, billsn, rebate, rebate_amt, order_amt, sign_date, head_id, audit_status, pay_status, start_date, end_date, create_datetime, creater_id, submit_status, is_deleted, deleter_id, delete_datetime, updater_id, update_datetime, company_id, delivery_date, auditor_id, audit_datetime, ordersn, pay_datetime) "
				+" select ?,2, customer_id, business_id, ?, rebate, rebate_amt, order_amt, sign_date, head_id, audit_status, pay_status, start_date, end_date, create_datetime, creater_id, submit_status, is_deleted, deleter_id, delete_datetime, updater_id, update_datetime, company_id, delivery_date, auditor_id, audit_datetime, billsn,pay_datetime from "+tableName+" where id=?",id_,billsn,id);
		
		Db.update("INSERT INTO "+OrderData.tableName+"(id, remark, pact, data)"
				+" select ?, remark, pact, data from "+OrderData.tableName+" where id=?",id_,id);
		
		Db.update("INSERT INTO "+OrderProduct.tableName+"(id,product_id, purchase_price, sale_price, amount, zkl, zhamt, amt, description, quoted_price, tax_rate, tax) "
				+" select ?,product_id, purchase_price, sale_price, amount, zkl, zhamt, amt, description, quoted_price, tax_rate, tax from "+OrderProduct.tableName+" where id=?",id_,id);
		Db.update("update "+tableName+" set is_transfer=1 where id=? and company_id=? ",id,company_id);
		String now=BaseController.dateTimeFormat.format(new Date());
		//生成应收应付单
		PayReceivAbles.dao.createFromOrder(this.findById(id_),now);
		//生成对应的出入库单
		StorageBill.dao.createFromOrder(this.findById(id), now);
	}
	/**产品销售或采购汇总统计*/
	public List<Record> rptList(Map<String, Object> filter) {
		Integer type=(Integer)filter.get("type");
		Integer f=(Integer)filter.get("f");//是否按 0:商品 1客户/供应商、2:销售员
		Integer m=(Integer)filter.get("m");// 1:计算毛利
		Integer t=(Integer)filter.get("t");// 1:统计退货
		Integer tax=(Integer)filter.get("tax");// 1:含税
		List<Object> parame=new ArrayList<Object>();
		StringBuffer sql=new StringBuffer();
		sql.append("select p.name product_name,p.billsn sn,p.model,p.unit,op.sale_price ");
		if(type==0){//销售-销售退货
			//销量
			sql.append(",(sum(case when o.ordertype=2 then op.amount else 0 end)");
			if(t==1)
			sql.append("-sum(case when o.ordertype=3 then op.amount else 0 end)");//退货
			sql.append(") amount");
			//销售收入
			sql.append(",(sum(case when o.ordertype=2 then op.amt else 0 end)");
			if(tax==1)
				sql.append("+sum(case when o.ordertype=2 then op.tax else 0 end)");
			if(t==1)
			sql.append("-sum(case when o.ordertype=3 then op.amt else 0 end)");//退货
			sql.append(") amt ");
			if(m==1){
				//毛利
				sql.append(",(sum(case when o.ordertype=2 then op.amt-op.amount*op.purchase_price else 0 end)");
			if(t==1)
				sql.append("-sum(case when o.ordertype=3 then op.amt-op.amount*op.purchase_price else 0 end)");//退货
			sql.append(") ml ");
			}
		}
		if(type==1){//采购-采购退货
			//采购量
			sql.append(",(sum(case when o.ordertype=0 then op.amount else 0 end)-sum(case when o.ordertype=1 then op.amount else 0 end)) amount");
			//采购支出
			sql.append(",(sum(case when o.ordertype=0 then op.amt else 0 end)");
			if(tax==1)
				sql.append("+sum(case when o.ordertype=0 then op.tax else 0 end)");//退货
			sql.append("-sum(case when o.ordertype=1 then op.amt else 0 end)) amt ");
		}
		if(f==1){//按客户 供应商
			sql.append(",c.name relation_name ");
		}
		if(f==2){//按销售员
			sql.append(",u.realname relation_name ");
		}
		sql.append(" from ");
		sql.append(tableName);
		sql.append(" o left join ");
		sql.append(OrderProduct.tableName);
		sql.append(" op on op.id=o.id left join ");
		if(f==1){
			sql.append(Customer.tableName);
			sql.append(" c on c.id=o.customer_id left join ");
		}
		if(f==2){
			sql.append(Person.tableName);
			sql.append(" u on u.id=o.head_id left join ");
		}
		sql.append(Product.tableName);
		sql.append(" p on op.product_id=p.id where o.company_id=? ");
		if(type==0){//销售及退货
			sql.append(" and (ordertype=2 or ordertype=3) and (submit_status=1 or audit_status=2) ");
		}
		if(type==1){//采购及退货
			sql.append(" and (ordertype=0 or ordertype=1) and (submit_status=1 or audit_status=2) ");
		}
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
		String customer_id=(String)filter.get("customer_id");
		if(StringUtils.isNotEmpty(customer_id)){//客户/供应商
			sql.append(" and o.customer_id=? ");
			parame.add(customer_id);
		}
		
		sql.append(" group by ");
		
		if(f==1){//按客户 供应商
			sql.append(" c.name, ");
		}
		if(f==2){//按销售员
			sql.append(" u.realname, ");
		}
		sql.append(" product_name,sn,model,p.billsn,unit,op.sale_price ");
		sql.append(" order by ");
		if(f==1){//按客户 供应商
			sql.append(" c.name asc, ");
		}
		if(f==2){//按销售员
			sql.append(" u.realname asc, ");
		}
		sql.append(" sn asc");
		return Db.find(sql.toString(),parame.toArray());
	}
	/**产品销售或采购明细表*/
	public List<Record> rpt1List(Map<String, Object> filter) {
		Integer type=(Integer)filter.get("type");
		Integer t=(Integer)filter.get("t");
		Integer m=(Integer)filter.get("m");// 1:计算毛利
		Integer tax=(Integer)filter.get("tax");// 1:含税
		List<Object> parame=new ArrayList<Object>();
		StringBuffer sql=new StringBuffer();
		sql.append("select o.sign_date,o.billsn,o.ordertype,c.name customer_name,u.realname head_name,p.name product_name,p.billsn sn,p.model,");
		sql.append("p.unit,(case when o.ordertype=0 or o.ordertype=2 then op.amount else 0-op.amount end) amount,op.purchase_price,op.sale_price,");
		if(m==1){
			sql.append("(case when o.ordertype=0 or o.ordertype=2 then op.amt-op.purchase_price*op.amount else op.purchase_price*op.amount-op.amt end)ml,");
		}
		if(tax==1){
			sql.append(" (case when o.ordertype=0 or o.ordertype=2 then op.amt+op.tax else 0-op.amt-op.tax end) amt, ");
		}else{
			sql.append(" (case when o.ordertype=0 or o.ordertype=2 then op.amt else 0-op.amt end) amt, ");
		}
		sql.append(" (case when o.ordertype=0 or o.ordertype=2 then op.purchase_price*op.amount else 0- op.purchase_price*op.amount end) \"cost\" from ");
		sql.append(tableName);
		sql.append(" o left join ");
		sql.append(OrderProduct.tableName);
		sql.append(" op on op.id=o.id left join ");
		sql.append(Customer.tableName);
		sql.append(" c on c.id=o.customer_id left join ");
		sql.append(Person.tableName);
		sql.append(" u on u.id=o.head_id left join ");
		sql.append(Product.tableName);
		sql.append(" p on op.product_id=p.id where o.company_id=? ");
		if(type==0){//销售及退货
			if(t==null||t==0){
				sql.append(" and (ordertype=2 or ordertype=3)");
			}else{
				sql.append(" and ordertype=2 ");
			}
		}
		if(type==1){//采购及退货
			if(t==null||t==0){
				sql.append(" and (ordertype=0 or ordertype=1)");
			}else{
				sql.append(" and ordertype=0 ");
			}
		}
		sql.append(" and (submit_status=1 or audit_status=2) ");
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
		String customer_id=(String)filter.get("customer_id");
		if(StringUtils.isNotEmpty(customer_id)){//客户/供应商
			sql.append(" and o.customer_id=? ");
			parame.add(customer_id);
		}
		sql.append(" order by o.sign_date asc,u.realname asc,c.name asc");
		return Db.find(sql.toString(),parame.toArray());
	}
	/**
	 * 成交金额
	 * @param uid
	 * @param start_date
	 * @param end_date
	 * @return
	 */
	public Object qryDealAmt(String uid, String start_date,String end_date) {
		List<Object> parame=new ArrayList<Object>();
		StringBuffer sql=new StringBuffer();
		sql.append("select sum(case when ordertype=2 then order_amt else 0-order_amt end)amt from ");
		sql.append(tableName);
		sql.append(" o where (o.submit_status=1 or o.audit_status=2) and o.head_id=? and o.ordertype in(2,3)");
		parame.add(uid);
		if(StringUtils.isNotEmpty(start_date)){
			sql.append(" and o.sign_date >=?");
			parame.add(start_date);
		}
		if(StringUtils.isNotEmpty(end_date)){
			sql.append(" and o.sign_date <=?");
			parame.add(end_date);
		}
		Record r=Db.findFirst(sql.toString(),parame.toArray());
		return r!=null?r.get("amt"):0;
	}
}
