package net.loyin.ctrl.sso;

import java.util.ArrayList;
import java.util.List;

import net.loyin.ctrl.AdminBaseController;
import net.loyin.jfinal.anatation.PowerBind;
import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.model.sso.DailyPhrase;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Page;

/**
 * 每日一语
 * @author 龙影
 */
@RouteBind(model="日精进",path="dailyPhrase",sys="设置")
public class DailyPhraseCtrl extends AdminBaseController<DailyPhrase> {
	public DailyPhraseCtrl() {
		this.modelClass = DailyPhrase.class;
	}
	public void dataGrid() {
		List<Object> param = new ArrayList<Object>();
		StringBuffer where = new StringBuffer();
		/** 添加查询字段条件 */
		this.qryField(where, param);
		where.append(" and company_id=?");
		param.add(this.getCompanyId());
		this.jqFilters(where, param);
		sortField(where);
		Page<DailyPhrase> page = DailyPhrase.dao.pageGrid(getPageNo(), getPageSize(), where,param);
		this.rendJson(true,null, "success", page);
	}

	public void qryOp() {
		getId();
		DailyPhrase m = DailyPhrase.dao.findById(id);
		if (m != null)
			this.rendJson(true,null, "", m);
		else
			this.rendJson(false,null, "记录不存在！");
	}
	@PowerBind(code="A11_1_E",funcName="编辑")
	public void save() {
		try {
			DailyPhrase po =  (DailyPhrase) getModel();
			if (po == null) {
				this.rendJson(false,null, "提交数据错误！");
				return;
			}
			id=po.getStr("id");
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
	@PowerBind(code="A11_1_E",funcName="删除")
	public void del() {
		try {
			getId();
			DailyPhrase.dao.del(id,this.getCompanyId());
			rendJson(true,null, "删除成功！",id);
		} catch (Exception e) {
			log.error("删除异常", e);
			rendJson(false,null,"删除失败！");
		}
	}
	/**获取随机一条*/
	public void random(){
		this.rendJson(true, null, "",DailyPhrase.dao.random(this.getCompanyId()));
	}
}
