package net.loyin.ctrl.fa;

import java.util.HashMap;
import java.util.Map;

import net.loyin.ctrl.AdminBaseController;
import net.loyin.jfinal.anatation.PowerBind;
import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.model.fa.Invoice;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Page;

/**
 * 票据
 * @author 龙影
 * 2014年9月24日
 */
@RouteBind(path = "invoice", sys = "财务", model = "票据")
public class InvoiceCtrl extends AdminBaseController<Invoice> {
	public InvoiceCtrl() {
		this.modelClass = Invoice.class;
	}
	public void dataGrid() {
		Map<String,Object> filter=new HashMap<String,Object>();
		String keyword=this.getPara("keyword");
		filter.put("keyword",keyword);
		Integer type=this.getParaToInt("type");
		filter.put("type",type);
		Integer pjlx=this.getParaToInt("pjlx");
		filter.put("pjlx",pjlx);
		String start_date=this.getPara("start_date");
		filter.put("start_date",start_date);
		String end_date=this.getPara("end_date");
		filter.put("end_date",end_date);
		filter.put("uid",this.getPara("uid"));
		filter.put("company_id",this.getCompanyId());
		this.sortField(filter);
		Page<Invoice> page = Invoice.dao.pageGrid(getPageNo(), getPageSize(),filter);
		this.rendJson(true,null, "success", page);
	}

	public void qryOp() {
		getId();
		Invoice m = Invoice.dao.findById(id,this.getCompanyId());
		if (m != null)
			this.rendJson(true,null, "", m);
		else
			this.rendJson(false,null, "记录不存在！");
	}
	@PowerBind(code="A7_1_E",funcName="编辑")
	public void save() {
		try {
			Invoice po = (Invoice) getModel();
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
			log.error("保存票据异常", e);
			this.rendJson(false,null, "保存数据异常！");
		}
	}
	@PowerBind(code="A7_1_E",funcName="删除")
	public void del() {
		try {
			getId();
			Invoice.dao.del(id,this.getCompanyId());
			rendJson(true,null,"删除成功！",id);
		} catch (Exception e) {
			log.error("删除异常", e);
			rendJson(false,null,"删除失败！");
		}
	}
}
