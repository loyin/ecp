package net.loyin.ctrl.scm;

import java.util.HashMap;
import java.util.Map;

import net.loyin.ctrl.AdminBaseController;
import net.loyin.jfinal.anatation.PowerBind;
import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.model.scm.Competitor;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Page;
/**
 * 仓库
 * @author 龙影
 */
@RouteBind(path="competitor",sys="进销存",model="竞争对手")
public class CompetitorCtrl extends AdminBaseController<Competitor> {
	public CompetitorCtrl(){
		this.modelClass=Competitor.class;
	}
	public void dataGrid() {
		String company_id=this.getCompanyId();
		Map<String,Object> filter=new HashMap<String,Object>();
		filter.put("keyword",this.getPara("keyword"));
		filter.put("start_date",this.getPara("start_date"));
		filter.put("end_date",this.getPara("end_date"));
		filter.put("company_id",company_id);
		this.sortField(filter);
		Page<Competitor> page = Competitor.dao.pageGrid(getPageNo(), getPageSize(),filter);
		this.rendJson(true,null, "success", page);
	}
	public void qryOp() {
		getId();
		Competitor m = Competitor.dao.findById(id, this.getCompanyId());
		if (m != null)
			this.rendJson(true,null, "", m);
		else
			this.rendJson(false,null, "记录不存在！");
	}
	@PowerBind(code="A2_1_E",funcName="编辑")
	public void save() {
		try {
			Competitor po = (Competitor) getModel();
			if (po == null) {
				this.rendJson(false,null, "提交数据错误！");
				return;
			}
			getId();
			String uid=this.getCurrentUserId();
			this.pullUser(po, uid);
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
	@PowerBind(code="A2_1_E",funcName="删除")
	public void del() {
		try {
			getId();
			Competitor.dao.del(id,this.getCompanyId());
			rendJson(true,null,"删除成功！",id);
		} catch (Exception e) {
			log.error("删除异常", e);
			rendJson(false,null,"删除失败！");
		}
	}
}
