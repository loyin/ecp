package net.loyin.ctrl;

import java.util.Map;

import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.model.Trans;

/**
 * 金币交易明细
 * @author 龙影 2014年10月11日
 */
@RouteBind(path="trans")
public class TransCtrl extends AdminBaseController<Trans> {
	public TransCtrl() {
		modelClass = Trans.class;
	}
	public void dataGrid(){
		try{
			Map<String,Object> filter=this.getJsonAttrs();
			filter.put("user_id",this.getCurrentUserId());
			this.rendJson(true, null, "",Trans.dao.page(this.getPageNo(),this.getPageSize(),filter));
		}catch(Exception e){
			log.error(e);
			this.rendJson(false, null, "查询数据异常");
		}
	}
	/**抢购金币 列表*/
	public void buyList(){
		try{
			Map<String,Object> filter=this.getJsonAttrs();
			filter.put("usid",this.getCurrentUserId());
			this.rendJson(true, null, "",Trans.dao.buyPage(this.getPageNo(),this.getPageSize(),filter));
		}catch(Exception e){
			log.error(e);
			this.rendJson(false, null, "查询数据异常");
		}
	}
	/**购买金币*/
	public void buy(){
		try{
		}catch(Exception e){
			log.error(e);
			this.rendJson(false, null, "查询数据异常");
		}
	}
}
