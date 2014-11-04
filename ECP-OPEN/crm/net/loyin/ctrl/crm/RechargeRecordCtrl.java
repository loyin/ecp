package net.loyin.ctrl.crm;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.loyin.ctrl.AdminBaseController;
import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.model.crm.RechargeRecord;

/**
 * 会员充值
 * 
 * @author 龙影 2014年9月28日
 */
@RouteBind(path = "rechargeRecord")
public class RechargeRecordCtrl extends AdminBaseController<RechargeRecord> {
	public RechargeRecordCtrl(){
		this.modelClass=RechargeRecord.class;
	}
	/**充值*/
	public void recharge() {
		getId();
		try{
			Map<String,String> userMap=this.getUserMap();
			String user_id=userMap.get("uid");
			Map<String,Object> attr=new HashMap<String,Object>();
			attr.put("creater_id",user_id);
			attr.put("customer_id",id);
			attr.put("create_datetime",dateTimeFormat.format(new Date()));
			BigDecimal amt=this.getAttr("amt");
			attr.put("amt",amt);
			RechargeRecord.dao.recharge(attr,userMap.get("company_id"));
			this.rendJson(true, null, "充值成功！");
		}catch(Exception e){
			log.error("充值异常:",e);
			this.rendJson(false, null, "充值异常");
		}
	}
}
