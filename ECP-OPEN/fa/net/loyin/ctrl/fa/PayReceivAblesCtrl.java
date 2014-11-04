package net.loyin.ctrl.fa;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.loyin.ctrl.AdminBaseController;
import net.loyin.jfinal.anatation.PowerBind;
import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.model.fa.PayReceivAbles;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 * 应收应付
 * @author 龙影
 */
@RouteBind(path = "payReceivAbles", sys = "财务", model= "应收应付")
public class PayReceivAblesCtrl extends AdminBaseController<PayReceivAbles> {
	public PayReceivAblesCtrl() {
		this.modelClass = PayReceivAbles.class;
	}
	public void dataGrid() {
		Map<String,Object> filter=new HashMap<String,Object>();
		filter.put("keyword", this.getPara("keyword"));
		filter.put("start_date", this.getPara("start_date"));
		filter.put("end_date", this.getPara("end_date"));
		filter.put("status", this.getParaToInt("status"));
		filter.put("type", this.getParaToInt("type"));
		filter.put("is_deleted", this.getParaToInt("is_deleted"));
		filter.put("company_id",this.getCompanyId());
		this.sortField(filter);
		Page<PayReceivAbles> page = PayReceivAbles.dao.pageGrid(getPageNo(), getPageSize(),filter,this.getParaToInt("qryType",-1));
		this.rendJson(true,null, "success", page);
	}
	public void rptList(){
		Map<String,Object> filter=new HashMap<String,Object>();
		filter.put("start_date", this.getPara("start_date"));
		filter.put("end_date", this.getPara("end_date"));
		filter.put("type", this.getParaToInt("type"));
		filter.put("company_id",this.getCompanyId());
		filter.put("customer_id",this.getPara("customer_id"));
		filter.put("uid",this.getPara("uid"));
		Map<String,Object> data=new HashMap<String,Object>();
		List<Record> list=new ArrayList<Record>();
		List<Record> dataList=PayReceivAbles.dao.rptList(filter);
		//组织数据
		BigDecimal amt_temp=new BigDecimal(0);//计算余额累计(按来往单位)
		BigDecimal amt_0=new BigDecimal(0);//计算余额总计
		BigDecimal order_amt_t=new BigDecimal(0);//计算订单金额总计
		String customer_name="";
		int type_=0;
		int i=0;
		if (dataList != null && dataList.isEmpty() == false) {
			for (Record r : dataList) {
				String cstname = r.getStr("customer_name");
				BigDecimal amt = r.getBigDecimal("amt");
				BigDecimal order_amt = r.getBigDecimal("order_amt");
				if (amt == null)
					amt = new BigDecimal(0);
				if (order_amt == null)
					order_amt = new BigDecimal(0);
				type_ = r.getInt("type");
				if (i == 0) {
					customer_name = cstname;
				}
				if (i > 0 && customer_name.equals(cstname) == false) {
					customer_name = cstname;
					Record r_ = new Record();
					r_.set("csttype", "");
					r_.set("billsn", "");
					r_.set("amt", "");
					r_.set("sign_date", "");
					r_.set("customer_name", "小计");
					r_.set("type", type_);
					r_.set("ordertype", "");
					r_.set("order_amt", order_amt_t);
					r_.set("amt0", amt_temp);
					list.add(r_);
					amt_temp = new BigDecimal(0);
					order_amt_t = new BigDecimal(0);
					amt_temp = amt;
//					order_amt_t = order_amt;
				} else {
					amt_temp = amt_temp.add(amt);
//					order_amt_t = order_amt_t.add(order_amt);
				}
				r.set("amt0", amt_temp);
				list.add(r);
				amt_0 = amt_0.add(amt);
				i++;
			}
			Record r_ = new Record();
			r_.set("csttype", "");
			r_.set("billsn", "");
			r_.set("amt", "");
			r_.set("sign_date", "");
			r_.set("customer_name", "小计");
			r_.set("type", type_);
			r_.set("ordertype", "");
			r_.set("order_amt", order_amt_t);
			r_.set("amt0", amt_temp);
			list.add(r_);
		}
		data.put("list",list);
		Map<String,Object> userData=new HashMap<String,Object>();
		userData.put("amt0_total",amt_0);
		data.put("userData",userData);
		this.rendJson(true,null, "success",data);
	}
	/**应收应付汇总表*/
	public void rptSumList() {
		Map<String,Object> filter=new HashMap<String,Object>();
		filter.put("start_date", this.getPara("start_date"));
		filter.put("end_date", this.getPara("end_date"));
		filter.put("type", this.getParaToInt("type"));
		filter.put("company_id",this.getCompanyId());
		filter.put("customer_id",this.getPara("customer_id"));
		filter.put("uid",this.getPara("uid"));
		List<Record> dataList=PayReceivAbles.dao.rptSumList(filter);
		this.rendJson(true,null, "success",dataList);
	}
	/**往来单位欠款表*/
	public void wlqkList() {
		Map<String,Object> filter=new HashMap<String,Object>();
		filter.put("csttype", this.getParaToInt("csttype"));
		filter.put("company_id",this.getCompanyId());
		filter.put("customer_id",this.getPara("customer_id"));
		filter.put("uid",this.getPara("uid"));
		List<Record> dataList=PayReceivAbles.dao.wlqkList(filter);
		this.rendJson(true,null, "success",dataList);
	}

	public void qryOp() {
		getId();
		PayReceivAbles m = PayReceivAbles.dao.findById(id,this.getCompanyId());
		if (m != null)
			this.rendJson(true,null, "", m);
		else
			this.rendJson(false,null, "记录不存在！");
	}
	@PowerBind(code="A7_1_E",funcName="编辑")
	public void save() {
		try {
			PayReceivAbles po = (PayReceivAbles) getModel();
			if (po == null) {
				this.rendJson(false,null, "提交数据错误！");
				return;
			}
			id=po.getStr("id");
			String uid=this.getCurrentUserId();
			pullUser(po, uid);
			if (StringUtils.isEmpty(id)) {
				po.set("company_id", this.getCompanyId());
				po.save();
				id=po.getStr("id");
			} else {
				po.update();
			}
			this.rendJson(true,null, "操作成功！",id);
		} catch (Exception e) {
			log.error("保存用户异常", e);
			this.rendJson(false,null, "保存数据异常！");
		}
	}
	@PowerBind(code="A7_1_E",funcName="删除")
	public void del() {
		try {
			getId();
			PayReceivAbles.dao.del(id,this.getCompanyId());
			rendJson(true,null, ("删除成功！"),id);
		} catch (Exception e) {
			log.error("删除异常", e);
			rendJson(false,null,("删除失败！"));
		}
	}
	/**回收站*/
	@PowerBind(code="A7_1_E",funcName="回收站")
	public void trash() {
		try {
			getId();
			PayReceivAbles.dao.trash(id,this.getCompanyId(),this.getCurrentUserId(),dateTimeFormat.format(new Date()));
			rendJson(true,null, ("删除成功！"),id);
		} catch (Exception e) {
			log.error("删除异常", e);
			rendJson(false,null,("删除失败！"));
		}
	}
}
