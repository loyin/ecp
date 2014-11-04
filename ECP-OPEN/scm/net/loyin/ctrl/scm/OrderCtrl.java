package net.loyin.ctrl.scm;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.loyin.ctrl.AdminBaseController;
import net.loyin.jfinal.anatation.PowerBind;
import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.model.em.SaleGoal;
import net.loyin.model.fa.PayReceivAbles;
import net.loyin.model.scm.Order;
import net.loyin.model.scm.OrderData;
import net.loyin.model.scm.OrderProduct;
import net.loyin.model.scm.StorageBill;
import net.loyin.model.sso.Company;
import net.loyin.model.sso.SnCreater;
import net.loyin.model.wf.AuditDetail;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Record;
/**
 * 订单
 * @author 龙影
 * 2014年9月23日
 */
@RouteBind(path="order",sys="进销存",model="订单")
public class OrderCtrl extends AdminBaseController<Order> {
	public OrderCtrl() {
		this.modelClass=Order.class;
	}
	public void dataGrid(){
		Map<String,String>userMap=this.getUserMap();
		Map<String,Object> filter=new HashMap<String,Object>();
		filter.put("company_id",userMap.get("company_id"));
		filter.put("keyword",this.getPara("keyword"));
		filter.put("start_date",this.getPara("start_date"));
		filter.put("end_date",this.getPara("end_date"));
		filter.put("ordertype",this.getParaToInt("ordertype"));
		filter.put("pay_status",this.getParaToInt("pay_status"));
		filter.put("uid",this.getPara("uid"));
		filter.put("is_deleted",this.getParaToInt("is_deleted",0));
		filter.put("user_id",userMap.get("uid"));
		filter.put("position_id",userMap.get("position_id"));
		this.sortField(filter);
		this.rendJson(true, null, "",Order.dao.page(this.getPageNo(),this.getPageSize(),filter,this.getParaToInt("qryType")));
	}
	@PowerBind(code={"A2_1_E","A3_1_E"},funcName="删除")
	public void del(){
		try {
			getId();
			if(Order.dao.isPay(id)){
				this.rendJson(false, null, "已经存在支付，请重新选择需要删除的数据！");
				return;
			}
			Order.dao.del(id,this.getCompanyId());
			rendJson(true,null,"删除成功！",id);
		} catch (Exception e) {
			log.error("删除异常", e);
			rendJson(false,null,"删除失败！请检查是否被使用！");
		}
	}
	public void qryOp() {
		getId();
		Order m = Order.dao.findById(id, this.getCompanyId());
		if (m != null){
			List<OrderProduct>productlist=OrderProduct.dao.list(id);
			m.put("productlist",productlist);
			m.put("productlistlength",productlist.size());
			this.rendJson(true,null, "", m);
		}
		else
			this.rendJson(false,null, "记录不存在！");
	}
	@PowerBind(code={"A2_1_E","A3_1_E"},funcName="回收站")
	public void trash() {
		try {
			getId();
			if(Order.dao.isPay(id)){
				this.rendJson(false, null, "已经存在支付，请重新选择需要删除的数据！");
				return;
			}
			Order.dao.trash(id,this.getCurrentUserId(),this.getCompanyId(),dateTimeFormat.format(new Date()));
			rendJson(true,null,"移动到回收站成功！",id);
		} catch (Exception e) {
			log.error("移动到回收站异常", e);
			rendJson(false,null,"移动到回收站失败！");
		}
	}
	/**恢复*/
	@PowerBind(code={"A2_1_E","A3_1_E"},funcName="恢复")
	public void reply() {
		try {
			getId();
			Order.dao.reply(id,this.getCompanyId());
			rendJson(true,null,"恢复成功！",id);
		} catch (Exception e) {
			log.error("恢复异常", e);
			rendJson(false,null,"恢复失败！");
		}
	}
	/**提交审核/取消提交*/
	@PowerBind(code={"A2_1_E","A3_1_E"},funcName="提交审核")
	public void subAudit() {
		int status=this.getParaToInt("status",1);
		try {
			getId();
			Order.dao.subAudit(id,this.getCompanyId(),this.getCurrentUserId(),dateTimeFormat.format(new Date()),status);
			rendJson(true,null,(status==1?"提交审核":"取消审核申请")+"成功！",id);
		} catch (Exception e) {
			log.error("提交审核异常", e);
			rendJson(false,null,(status==1?"提交审核":"取消审核申请")+"失败！");
		}
	}
	@PowerBind(code={"A2_1_E","A3_1_E"},funcName="编辑")
	public void save() {
		try {
			Order po = (Order) getModel();
			if (po == null) {
				this.rendJson(false,null, "提交数据错误！");
				return;
			}
			getId();
			String company_id=this.getCompanyId();
			String ordersn=this.getPara("ordersn");
			String customer_id=this.getPara("customer_id");
			Integer ordertype=this.getParaToInt("ordertype");
			if(ordertype==1||ordertype==3){
				if(StringUtils.isEmpty(ordersn)){
					this.rendJson(false, null, "原订单编号不能为空！");
					return;
				}else{
					if(Order.dao.exsit(ordersn,company_id,customer_id,ordertype-1)==false){
						this.rendJson(false, null, "原订单编号不存在！");
						return;
					}
				}
			}
			String pname="productlist";
			Integer productlistlength=this.getParaToInt("productlistlength");
			List<Map<String,Object>> productlist=new ArrayList<Map<String,Object>>();
			for(int i=0;i<productlistlength;i++){
				Map<String,Object> attr=new HashMap<String,Object>();
				attr.put("product_id",this.getAttr(pname+"["+i+"][product_id]"));
				attr.put("purchase_price",this.getAttr(pname+"["+i+"][purchase_price]"));
				attr.put("sale_price",this.getAttr(pname+"["+i+"][sale_price]"));
				attr.put("quoted_price",this.getAttr(pname+"["+i+"][quoted_price]"));//报价
				attr.put("amount",this.getAttr(pname+"["+i+"][amount]"));
				attr.put("zkl",this.getAttr(pname+"["+i+"][zkl]"));
				attr.put("zhamt",this.getAttr(pname+"["+i+"][zhamt]"));
				attr.put("tax_rate",this.getAttr(pname+"["+i+"][tax_rate]"));
				attr.put("tax",this.getAttr(pname+"["+i+"][tax]"));
				attr.put("amt",this.getAttr(pname+"["+i+"][amt]"));
				attr.put("description",this.getAttr(pname+"["+i+"][description]"));
				productlist.add(attr);
			}
			OrderData ldata=(OrderData)this.getModel2(OrderData.class);
			po.set("audit_status",0);//设置为提交状态
			this.pullUser(po, this.getCurrentUserId());
			String sn="";
			if (StringUtils.isEmpty(id)) {
				sn=SnCreater.dao.create("ORDER"+ordertype, company_id);
				po.set("billsn",sn);
				po.set("company_id",company_id);
				po.save();
				id=po.getStr("id");
				ldata.set("id",id);
				ldata.save();
			} else {
				po.update();
				ldata.update();
			}
			Map<String,String> data=new HashMap<String,String>();
			data.put("id",id);
			data.put("sn",po.getStr("billsn"));
			OrderProduct.dao.insert(productlist, id);
			this.rendJson(true,null, "操作成功！",data);
		} catch (Exception e) {
			log.error("保存订单异常", e);
			this.rendJson(false,null, "保存数据异常！");
		}
	}
	/**提交*/
	@PowerBind(code={"A2_1_E","A3_1_E"},funcName="提交")
	public void submit(){
		try{
			this.getId();
			String company_id=this.getCompanyId();
			Order order=Order.dao.findById(id, company_id);
			if(order==null){
				this.rendJson(false,null,"数据不存在！");
				return;
			}
			Company company= Company.dao.qryCacheById(company_id);
			Map<String,Object> config=company.getConfig();
			Integer ordertype=order.getInt("ordertype");
			if((ordertype<2)//如果是采购订单 采购退货 直接生成应收应付单
				||(ordertype==2&&"false".equals(config.get("p_sale_audit")))//销售订单审核 false
				||(ordertype==3&&"false".equals(config.get("p_saletui_audit")))	){//销售退货审核 false
				String now=dateTimeFormat.format(new Date());
				PayReceivAbles.dao.createFromOrder(order,now);
				//生成对应的出入库单
				StorageBill.dao.createFromOrder(order, now);
				//更新销售目标
				String[] s=now.split("-");
				if(ordertype==2||ordertype==3){//销售或退货
					BigDecimal zero=new BigDecimal(0);
					BigDecimal amt=order.getBigDecimal("order_amt");
					if(ordertype==3)
					amt=zero.subtract(amt);
					SaleGoal.dao.updateVal(order.getStr("head_id"),Integer.parseInt(s[1]),Integer.parseInt(s[0]),amt);
				}
				order.set("audit_status",2);//设置为审核通过
				order.set("submit_status",1);//设置为提交状态
			}else{
				order.set("audit_status",1);//设置为提交状态
			}
			order.update();
			this.rendJson(true, null, "订单提交成功！");
		}catch(Exception e){
			log.error("提交订单异常", e);
			this.rendJson(false,null, "提交数据异常！");
		}
	}
	@PowerBind(code={"A2_1_E","A3_1_E"},funcName="审核")
	public void saveAudit(){
		try{
			getId();
			AuditDetail po=(AuditDetail)this.getModel2(AuditDetail.class);
			po.set("auditor_id",this.getCurrentUserId());
			String now=dateTimeFormat.format(new Date());
			po.set("audit_datetime",now);
			String tablename=this.getPara("table");
			AuditDetail.dao.save(po,tablename);
			String company_id=this.getCompanyId();
			this.getId();
			Order order=Order.dao.findById(id, company_id);
			if(order.getInt("audit_status")>1){
				this.rendJson(false, null, "已经审核过，请勿重复审核！");
				return;
			}
			int ordertype=order.getInt("ordertype");
			if(po.getInt("audit_status")==2&&ordertype!=4){//不为报价单
				//生成应收应付单
				PayReceivAbles.dao.createFromOrder(order,now);
				//生成对应的出入库单
				StorageBill.dao.createFromOrder(order, now);
				//更新销售目标
				String[] s=now.split("-");
				if(ordertype==2||ordertype==3){//销售或退货
					BigDecimal zero=new BigDecimal(0);
					BigDecimal amt=order.getBigDecimal("order_amt");
					if(ordertype==3)
					amt=zero.subtract(amt);
					SaleGoal.dao.updateVal(order.getStr("head_id"),Integer.parseInt(s[1]),Integer.parseInt(s[0]),amt);
				}
			}
			if(po.getInt("audit_status")==2&&order.getInt("ordertype")==4){//报价单
				//转成订单
				Order.dao.tansfer(id,company_id);
			}
			this.rendJson(true, null, "保存审核结果成功！");
		}catch(Exception e){
			log.error(e);
			this.rendJson(false, null, "保存审核结果异常！");
		}
	}
	
	/**对账单详情*/
	public void dzData(){
		Map<String,Object> filter=new HashMap<String,Object>();
		filter.put("type",this.getParaToInt("type",1));//默认客户
		filter.put("company_id",this.getCompanyId());
		filter.put("customer_id",this.getPara("customer_id"));
		filter.put("start_date",this.getPara("start_date"));
		filter.put("end_date",this.getPara("end_date"));
		this.rendJson(true, null, "",Order.dao.duiZhang(filter));
	}
	/**产品销售或采购汇总统计*/
	public void rptList(){
		Map<String,Object> filter=new HashMap<String,Object>();
		filter.put("product_id",this.getPara("product_id"));
		filter.put("head_id",this.getPara("head_id"));
		filter.put("customer_id",this.getPara("customer_id"));
		filter.put("start_date",this.getPara("start_date"));
		filter.put("end_date",this.getPara("end_date"));
		filter.put("type",this.getParaToInt("type"));
		filter.put("tax",this.getParaToInt("tax",0));//含税统计
		Integer f=this.getParaToInt("f",0);//分 0:产品 1客户供应商、2销售员
		filter.put("f",f);
		filter.put("m",this.getParaToInt("m",0));//显示毛利  只针对客户、销售员
		filter.put("t",this.getParaToInt("t",0));//统计退货
		filter.put("company_id",this.getCompanyId());
		List<Record>list=new ArrayList<Record>();
		List<Record> dataList=Order.dao.rptList(filter);
		int i=0;
		String tjName="";
		BigDecimal amt_t=new BigDecimal(0);//销售金额、采购金额统计
		double cost_t=0;//销售成本 即采购价X数量
		double ml_t=0;//毛利统计
		double amount_t=0;
		if(dataList!=null&&dataList.isEmpty()==false){
			for(Record r:dataList){
				String actname="";
					if(f==0){
						actname=r.getStr("product_name");
						r.set("relation_name",actname);
					}else{
						actname=r.getStr("relation_name");
					}
				Double amount=r.getDouble("amount");
				if(amount==null){
					amount=0d;
				}
				BigDecimal amt=r.getBigDecimal("amt");
				Double ml=r.getDouble("ml");
				if(amt==null)
					amt=new BigDecimal(0);
				if(ml==null)
					ml=new Double(0);
				if(i==0){
					tjName=actname;
				}
				if(i>0&&tjName.equals(actname)==false){
					tjName=actname;
					Record r_=new Record();
					r_.set("amt",amt_t);
					r_.set("ml",ml_t);
					r_.set("amount",amount_t);
					r_.set("cost",cost_t);
					r_.set("name","小计");
					list.add(r_);
					amt_t=new BigDecimal(0);
					ml_t=0;
					amount_t=0;
					cost_t=0;
				}
				amt_t=amt_t.add(amt);
				amount_t+=amount;
				ml_t+=ml;
				BigDecimal purchase_price=r.getBigDecimal("purchase_price");
				if(purchase_price==null){
					purchase_price=new BigDecimal(0);
				}
				double cost=amount*purchase_price.doubleValue();
				r.set("cost",cost);
				cost_t+=cost;
				list.add(r);
				i++;
			}
			Record r_=new Record();
			r_.set("amt",amt_t);
			r_.set("ml",ml_t);
			r_.set("cost",cost_t);
			r_.set("amount",amount_t);
			r_.set("name","小计");
			list.add(r_);
		}
		this.rendJson(true, null, "",list);
	}
	/**产品销售或采购明细*/
	public void rpt1List(){
		Map<String,Object> filter=new HashMap<String,Object>();
		filter.put("product_id",this.getPara("product_id"));
		filter.put("head_id",this.getPara("head_id"));
		filter.put("customer_id",this.getPara("customer_id"));
		filter.put("start_date",this.getPara("start_date"));
		filter.put("end_date",this.getPara("end_date"));
		filter.put("type",this.getParaToInt("type"));
		filter.put("t",this.getParaToInt("t"));//显示退货
		filter.put("tax",this.getParaToInt("tax",0));//含税统计
		filter.put("m",this.getParaToInt("m",0));//显示毛利  只针对客户、销售员
		filter.put("company_id",this.getCompanyId());
		List<Record> list=Order.dao.rpt1List(filter);
		this.rendJson(true, null, "",list);
	}
}
