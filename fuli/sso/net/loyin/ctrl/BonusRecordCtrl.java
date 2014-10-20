package net.loyin.ctrl;

import java.util.Map;

import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.model.BonusRecord;

/**
 * 奖金记录明细
 * 
 * @author 龙影 2014年10月11日
 */
@RouteBind(path="bonusRecord")
public class BonusRecordCtrl extends AdminBaseController<BonusRecord> {
	public BonusRecordCtrl() {
		modelClass = BonusRecord.class;
	}
	public void dataGrid(){
		try{
			Map<String,Object> filter=this.getJsonAttrs();
			filter.put("user_id",this.getCurrentUserId());
			this.rendJson(true, null, "",BonusRecord.dao.page(this.getPageNo(),this.getPageSize(),filter));
		}catch(Exception e){
			log.error(e);
			this.rendJson(false, null, "查询数据异常");
		}
	}
}
