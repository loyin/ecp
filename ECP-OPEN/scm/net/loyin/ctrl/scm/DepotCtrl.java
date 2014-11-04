package net.loyin.ctrl.scm;

import net.loyin.ctrl.AdminBaseController;
import net.loyin.jfinal.anatation.PowerBind;
import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.model.scm.Depot;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Page;
/**
 * 仓库
 * @author 龙影
 */
@RouteBind(path="depot",sys="进销存",model="仓库")
public class DepotCtrl extends AdminBaseController<Depot> {
	public DepotCtrl(){
		this.modelClass=Depot.class;
	}
	public void dataGrid() {
		Page<Depot> page = Depot.dao.pageGrid(getPageNo(), getPageSize(),this.getCompanyId());
		this.rendJson(true,null, "success", page);
	}
	public void list(){
		this.rendJson(true, null, "",Depot.dao.list(this.getCompanyId()));
	}
	public void qryOp() {
		getId();
		Depot m = Depot.dao.findById(id, this.getCompanyId());
		if (m != null)
			this.rendJson(true,null, "", m);
		else
			this.rendJson(false,null, "记录不存在！");
	}
	@PowerBind(code="A4_1_E",funcName="编辑")
	public void save() {
		try {
			Depot po = (Depot) getModel();
			if (po == null) {
				this.rendJson(false,null, "提交数据错误！");
				return;
			}
			getId();
			if (StringUtils.isEmpty(id)) {
				po.set("company_id", this.getCompanyId());
				po.save();
				id=po.getStr("id");
			} else {
				po.update();
			}
			this.rendJson(true,null, "操作成功！",id);
		} catch (Exception e) {
			log.error("保存产品异常", e);
			this.rendJson(false,null, "保存数据异常！");
		}
	}
	@PowerBind(code="A4_1_E",funcName="删除")
	public void del() {
		try {
			getId();
			Depot.dao.del(id,this.getCompanyId());
			rendJson(true,null,"删除成功！",id);
		} catch (Exception e) {
			log.error("删除异常", e);
			rendJson(false,null,"删除失败！");
		}
	}
}
