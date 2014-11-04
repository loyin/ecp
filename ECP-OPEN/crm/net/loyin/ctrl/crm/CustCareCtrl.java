package net.loyin.ctrl.crm;

import java.util.HashMap;
import java.util.Map;

import net.loyin.ctrl.AdminBaseController;
import net.loyin.jfinal.anatation.PowerBind;
import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.model.crm.CustCare;

import org.apache.commons.lang3.StringUtils;
/**
 * 客户关怀
 * @author 龙影
 */
@RouteBind(path="custCare")
public class CustCareCtrl extends AdminBaseController<CustCare> {
	public CustCareCtrl(){
		this.modelClass=CustCare.class;
	}
	public void dataGrid(){
		Map<String,Object> filter=new HashMap<String,Object>();
		filter.put("company_id",this.getCompanyId());
		filter.put("keyword",this.getPara("keyword"));
		filter.put("start_date",this.getPara("start_date"));
		filter.put("end_date",this.getPara("end_date"));
		filter.put("uid",this.getPara("uid"));
		filter.put("user_id",this.getCurrentUserId());
		filter.put("position_id",this.getPositionId());
		this.sortField(filter);
		this.rendJson(true, null, "",CustCare.dao.page(this.getPageNo(),this.getPageSize(),filter,this.getParaToInt("qryType",-1)));
	}
	@PowerBind(code="A1_1_E",funcName="删除")
	@RouteBind(code="000DEL", model = "", name = "")
	public void del() {
		try {
			getId();
			CustCare.dao.del(id,this.getCompanyId());
			rendJson(true,null,"删除成功！",id);
		} catch (Exception e) {
			log.error("删除异常", e);
			rendJson(false,null,"删除失败！请检查是否被使用！");
		}
	}
	@PowerBind(code="A1_1_V",funcName="查看")
	public void qryOp() {
		getId();
		CustCare m = CustCare.dao.findById(id, this.getCompanyId());
		if (m == null)
			this.rendJson(false,null, "记录不存在！");
		else{
			this.rendJson(true, null, "",m);
		}
	}
	@PowerBind(code="A1_1_E",funcName="编辑")
	public void save() {
		try {
			CustCare po = (CustCare) getModel();
			if (po == null) {
				this.rendJson(false,null, "提交数据错误！");
				return;
			}
			getId();
			this.pullUser(po, this.getCurrentUserId());
			if (StringUtils.isEmpty(id)) {
				po.set("company_id", this.getCompanyId());
				po.save();
				id=po.getStr("id");
			} else {
				po.update();
			}
			this.rendJson(true,null, "操作成功！",id);
		} catch (Exception e) {
			log.error("保存异常", e);
			this.rendJson(false,null, "保存数据异常！");
		}
	}


}
