package net.loyin.ctrl;

import java.util.Map;

import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.model.GoldTransDetail;

/**
 * 金币明细
 * @author 龙影 2014年10月11日
 */
@RouteBind(path="goldTransDetail")
public class GoldTransDetailCtrl extends AdminBaseController<GoldTransDetail> {
	public GoldTransDetailCtrl() {
		modelClass = GoldTransDetail.class;
	}
	public void dataGrid(){
		try{
			Map<String,Object> filter=this.getJsonAttrs();
			filter.put("user_id",this.getCurrentUserId());
			this.rendJson(true, null, "",GoldTransDetail.dao.page(this.getPageNo(),this.getPageSize(),filter));
		}catch(Exception e){
			log.error(e);
			this.rendJson(false, null, "查询数据异常");
		}
	}
}
