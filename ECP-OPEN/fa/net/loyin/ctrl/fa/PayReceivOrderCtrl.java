package net.loyin.ctrl.fa;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.loyin.ctrl.AdminBaseController;
import net.loyin.jfinal.anatation.PowerBind;
import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.model.fa.PayReceivOrder;
import net.loyin.model.sso.SnCreater;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 * 收款付款
 * @author 龙影
 */
@RouteBind(path = "payReceivOrder", sys = "财务", model = "付款收款")
public class PayReceivOrderCtrl extends AdminBaseController<PayReceivOrder> {
	public PayReceivOrderCtrl() {
		this.modelClass = PayReceivOrder.class;
	}
	public void dataGrid() {
		Map<String,Object> filter=new HashMap<String,Object>();
		String start_date=this.getPara("start_date");
		filter.put("start_date",start_date);
		String end_date=this.getPara("end_date");
		filter.put("end_date",end_date);
		String keyword=this.getPara("keyword");
		filter.put("keyword",keyword);
		String uid=this.getPara("uid");
		filter.put("uid",uid);
		Integer type=this.getParaToInt("type");
		filter.put("type",type);
		Integer status=this.getParaToInt("status");
		filter.put("status",status);
		String company_id=this.getCompanyId();
		filter.put("company_id",company_id);
		String user_id=this.getCurrentUserId();
		filter.put("user_id",user_id);
		sortField(filter);
		Page<PayReceivOrder> page = PayReceivOrder.dao.pageGrid(getPageNo(), getPageSize(),filter);
		this.rendJson(true,null, "success", page);
	}

	public void qryOp() {
		getId();
		PayReceivOrder m = PayReceivOrder.dao.findById(id,this.getCompanyId());
		if (m != null)
			this.rendJson(true,null, "", m);
		else
			this.rendJson(false,null, "记录不存在！");
	}
	@PowerBind(code="A7_1_E",funcName="编辑")
	public void save(){
		try {
			PayReceivOrder po = (PayReceivOrder) getModel();
			if (po == null) {
				this.rendJson(false,null, "提交数据错误！");
				return;
			}
			this.getId();
			if(PayReceivOrder.dao.is_end(id)){
				this.rendJson(false,null, "此单据已经支付，不能修改！");
				return;
			}
			String uid=this.getCurrentUserId();
			pullUser(po, uid);
			String billsn=po.getStr("billsn");
			if (StringUtils.isEmpty(id)) {
				billsn=SnCreater.dao.create("FKSK"+po.getInt("type"),this.getCompanyId());
				po.set("company_id", this.getCompanyId());
				po.set("billsn",billsn);
				po.save();
				id=po.getStr("id");
			} else {
				po.update();
			}
			Integer status=this.getParaToInt("status");
			if(status==1){
				PayReceivOrder.dao.jiesuan(po.getBigDecimal("amt"),po.getStr("payables_id"),id);
			}
			Map<String,Object> data=new HashMap<String,Object>();
			data.put("id",id);
			data.put("billsn",billsn);
			this.rendJson(true,null, "操作成功！",data);
		} catch (Exception e) {
			PayReceivOrder.dao.upStatus(0, id);
			log.error("保存用户异常", e);
			this.rendJson(false,null, "保存数据异常！"+e.getMessage());
		}
	}
	/**查看应收应付的支付明细*/
	public void list(){
		this.rendJson(true, null, null,PayReceivOrder.dao.list(this.getId(),this.getCompanyId()));
	}
	@PowerBind(code="A7_1_E",funcName="删除")
	public void del() {
		try {
			getId();
			PayReceivOrder.dao.del(id,this.getCompanyId());
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
			PayReceivOrder.dao.trash(id,this.getCompanyId(),this.getCurrentUserId(),dateTimeFormat.format(new Date()));
			rendJson(true,null, ("删除成功！可以在回收站还原！"),id);
		} catch (Exception e) {
			log.error("删除异常", e);
			rendJson(false,null,("删除失败！"));
		}
	}
	public void rpt1List(){
		Map<String,Object> filter=new HashMap<String,Object>();
		filter.put("account_id",this.getPara("account_id"));
		filter.put("subject_id",this.getPara("subject_id"));
		filter.put("start_date",this.getPara("start_date"));
		filter.put("end_date",this.getPara("end_date"));
		filter.put("keyword",this.getPara("keyword"));
		filter.put("company_id",this.getCompanyId());
		List<Record> list=PayReceivOrder.dao.rpt1List(filter);
		this.rendJson(true, null, "",list);
	}
}
