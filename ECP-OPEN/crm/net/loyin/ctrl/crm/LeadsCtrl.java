package net.loyin.ctrl.crm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.loyin.ctrl.AdminBaseController;
import net.loyin.jfinal.anatation.PowerBind;
import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.model.crm.Leads;
import net.loyin.model.crm.LeadsData;

import org.apache.commons.lang3.StringUtils;
/**
 * 线索
 * @author 龙影
 * 2014年9月22日
 */
@RouteBind(path="leads",sys="销售",model="线索")
public class LeadsCtrl extends AdminBaseController<Leads> {
	public LeadsCtrl() {
		this.modelClass=Leads.class;
	}
	public void dataGrid(){
		Map<String,Object> filter=new HashMap<String,Object>();
		filter.put("company_id",this.getCompanyId());
		filter.put("keyword",this.getPara("keyword"));
		filter.put("start_date",this.getPara("start_date"));
		filter.put("end_date",this.getPara("end_date"));
		filter.put("is_transformed",this.getParaToInt("is_transformed",0));
		filter.put("is_deleted",this.getParaToInt("is_deleted",0));
		filter.put("uid",this.getPara("uid"));
		filter.put("user_id",this.getCurrentUserId());
		filter.put("position_id",this.getPositionId());
		this.sortField(filter);
		this.rendJson(true, null, "",Leads.dao.page(this.getPageNo(),this.getPageSize(),filter,this.getParaToInt("qryType",-1)));
	}
	@PowerBind(code="A2_1_E",funcName="删除")
	public void del() {
		try {
			getId();
			Leads.dao.del(id,this.getCompanyId());
			rendJson(true,null,"删除成功！",id);
		} catch (Exception e) {
			log.error("删除异常", e);
			rendJson(false,null,"删除失败！请检查是否被使用！");
		}
	}
	/**转换商机*/
	@PowerBind(code="A2_1_E",funcName="转换为商机")
	public void exchange() {
		try {
			getId();
			Leads.dao.exchange(id,this.getCompanyId(),this.getCurrentUserId(),dateTimeFormat.format(new Date()));
			rendJson(true,null,"转换商机成功！",id);
		} catch (Exception e) {
			log.error("转换商机异常", e);
			rendJson(false,null,"转换商机失败！");
		}
	}
	@PowerBind(code="A2_1_V",funcName="查看线索")
	public void qryOp() {
		getId();
		Leads m = Leads.dao.findById(id, this.getCompanyId());
		if (m != null)
			this.rendJson(true,null, "", m);
		else
			this.rendJson(false,null, "记录不存在！");
	}
	@PowerBind(code="A2_1_E",funcName="编辑")
	public void save() {
		try {
			Leads po = (Leads) getModel();
			if (po == null) {
				this.rendJson(false,null, "提交数据错误！");
				return;
			}
			getId();
			LeadsData ldata=(LeadsData)this.getModel2(LeadsData.class);
			this.pullUser(po, this.getCurrentUserId());
			if (StringUtils.isEmpty(id)) {
				po.set("company_id", this.getCompanyId());
				po.save();
				id=po.getStr("id");
				ldata.set("id",id);
				ldata.save();
			} else {
				po.update();
				ldata.update();
			}
			this.rendJson(true,null, "操作成功！",id);
		} catch (Exception e) {
			log.error("保存线索异常", e);
			this.rendJson(false,null, "保存数据异常！");
		}
	}
	/**转换 到商机*/
	@PowerBind(code="A2_1_E",funcName="转换为商机")
	public void trans(){
		this.getId();
		try{
			Leads.dao.trans(id,this.getCurrentUserId(),this.getCompanyId());
		}catch(Exception e){
			log.error("线索转换错误！",e);
		}
	}
}
