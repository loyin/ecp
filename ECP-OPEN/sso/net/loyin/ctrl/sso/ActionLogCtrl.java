package net.loyin.ctrl.sso;

import java.util.HashMap;
import java.util.Map;

import net.loyin.ctrl.AdminBaseController;
import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.model.sso.ActionLog;
/**
 * 
 * @author 龙影
 * 2014年10月30日
 */
@RouteBind(sys="设置",path="operlog",model="操作日志")
public class ActionLogCtrl extends AdminBaseController<ActionLog> {

	public ActionLogCtrl(){
		this.modelClass = ActionLog.class;
	}
	
	public void dataGrid(){
		Map<String,String>userMap=this.getUserMap();
		Map<String,Object> filter=new HashMap<String,Object>();
		filter.put("company_id",userMap.get("company_id"));
		filter.put("keyword",this.getPara("keyword"));
		filter.put("start_date",this.getPara("start_date"));
		filter.put("end_date",this.getPara("end_date"));
		filter.put("uid",this.getPara("uid"));
		filter.put("user_id",userMap.get("uid"));
		filter.put("position_id",userMap.get("position_id"));
		this.sortField(filter);
		this.rendJson(true, null, "",ActionLog.dao.page(this.getPageNo(),this.getPageSize(),filter,this.getParaToInt("qryType",0)));
	}
}
