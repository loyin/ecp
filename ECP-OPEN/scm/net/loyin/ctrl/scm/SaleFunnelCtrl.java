package net.loyin.ctrl.scm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.loyin.ctrl.AdminBaseController;
import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.model.crm.Business;

/**
 * 销售漏斗
 * 
 * @author 龙影 2014年10月30日
 */
@SuppressWarnings("rawtypes")
@RouteBind(path = "salefunnel")
public class SaleFunnelCtrl extends AdminBaseController {
	public void my() {
		Date now=new Date();
		Calendar cal=Calendar.getInstance();
		cal.setTime(now);
		cal.set(Calendar.DATE,0);
		//本月起始日期
		String start_date=dateFormat.format(cal.getTime());
		String end_date=dateFormat.format(now);
		List data=new ArrayList();
		data.add(Business.dao.myFunnel(this.getCurrentUserId(),start_date,end_date,0));
		data.add(Business.dao.myFunnel(this.getCurrentUserId(),start_date,end_date,1));
		this.rendJson(true, null, "", data);
	}
	public void qry(){
		String start_date=this.getPara("start_date");
		String end_date=this.getPara("end_date");
		String head_id=this.getPara("head_id");
		String company_id=this.getCompanyId();
		List data=new ArrayList();
		data.add(Business.dao.funnel(company_id,head_id,start_date,end_date,0));
		data.add(Business.dao.funnel(company_id,head_id,start_date,end_date,1));
		this.rendJson(true, null, "", data);
	}
}
