package net.loyin.ctrl.em;

import java.util.HashMap;
import java.util.Map;

import net.loyin.ctrl.AdminBaseController;
import net.loyin.jfinal.anatation.PowerBind;
import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.model.em.SaleGoal;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Page;

/**
 * 销售目标
 * 
 * @author 龙影 2014年10月23日
 */
@RouteBind(path = "saleGoal",sys="企业管理",model="销售目标")
public class SaleGoalCtrl extends AdminBaseController<SaleGoal> {
	public SaleGoalCtrl(){
		this.modelClass=SaleGoal.class;
	}
	public void dataGrid() {
		Map<String,Object> filter=new HashMap<String,Object>();
		String keyword=this.getPara("keyword");
		filter.put("keyword",keyword);
		filter.put("year",this.getPara("year"));
		filter.put("company_id",this.getCompanyId());
		filter.put("user_id",this.getCurrentUserId());
		filter.put("position_id",this.getPositionId());
		this.sortField(filter);
		Page<SaleGoal> page = SaleGoal.dao.pageGrid(getPageNo(), getPageSize(),filter);
		this.rendJson(true,null, "", page);
	}
	/**查询个人的目标*/
	public void qry(){
		String year=this.getPara("year");
		SaleGoal po=SaleGoal.dao.qry(year,this.getCurrentUserId());
		this.rendJson(true, null,null,po);
	}
	/**保存自己的目标*/
	public void saveMy(){
		try{
			SaleGoal po=this.getModel();
			String uid=this.getCurrentUserId();
			po.set("user_id",uid);
			po.set("company_id",this.getCompanyId());
			SaleGoal po_=SaleGoal.dao.qry(po.getStr("year"),po.getStr("user_id"));
			this.pullUser(po, uid);
			if(po_==null){
				po.save();
				id=po.getStr("id");
			}else{
				po.update();
			}
			this.rendJson(true, null, "保存目标成功！",id);
		}catch(Exception e){
			log.error(e);
			this.rendJson(false, null,"保存销售目标异常！"+e.getMessage());
		}
	}
	@PowerBind(code="A9_1_E",funcName="编辑")
	public void save(){
		try{
			this.getId();
			SaleGoal po=this.getModel();
			SaleGoal po_=SaleGoal.dao.qry(po.getStr("year"),po.getStr("user_id"));
			if(po_==null){
				po.save();
				id=po.getStr("id");
			}else{
				po.update();
			}
			this.rendJson(true, null, "保存目标成功！",id);
		}catch(Exception e){
			log.error(e);
			this.rendJson(false, null,"保存销售目标异常！"+e.getMessage());
		}
	}
	public void qryOp(){
		this.getId();
		if(StringUtils.isNotEmpty(id)){
			this.rendJson(true,null,"",SaleGoal.dao.findById(id,this.getCompanyId()));
		}else{
			
		}
	}
	@PowerBind(code="A9_1_E",funcName="删除")
	public void del(){
		try{
			this.getId();
			SaleGoal.dao.del(id,this.getCompanyId());
			this.rendJson(true, null, "删除成功！");
		}catch(Exception e){
			this.rendJson(false,null,"删除失败！");
		}
	}
}
