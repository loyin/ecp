package net.loyin.ctrl.crm;

import java.util.HashMap;
import java.util.Map;

import net.loyin.ctrl.AdminBaseController;
import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.model.crm.IntegralHistory;

import com.jfinal.plugin.activerecord.Page;

/**
 * 会员积分记录查询
 * @author 龙影
 * 2014年9月15日
 */
@RouteBind(path = "integralHistory")
public class IntegralHistoryCtrl extends AdminBaseController<IntegralHistory> {
	public void dataGrid(){
		Map<String,Object> filter=new HashMap<String,Object>();
		String cust_id=this.getPara("customer_id");
		filter.put("customer_id",cust_id);
		String keyword=this.getPara("keyword");
		filter.put("keyword",keyword);
		String rating=this.getPara("rating");
		filter.put("rating",rating);
		filter.put("company_id",this.getCompanyId());
		Page<IntegralHistory> page = IntegralHistory.dao.pageGrid(getPageNo(), getPageSize(),filter);
		this.rendJson(true,null, "success", page);
	}
}
