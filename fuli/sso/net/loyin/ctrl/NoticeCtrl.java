package net.loyin.ctrl;

import java.util.Map;

import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.model.Notice;

/**
 * 公告
 * @author 龙影 2014年10月11日
 */
@RouteBind(path="notice")
public class NoticeCtrl extends AdminBaseController<Notice> {
	public NoticeCtrl() {
		modelClass = Notice.class;
	}
	public void dataGrid(){
		try{
			Map<String,Object> filter=this.getJsonAttrs();
			this.rendJson(true, null, "",Notice.dao.page(this.getPageNo(),this.getPageSize(),filter));
		}catch(Exception e){
			log.error(e);
			this.rendJson(false, null, "查询数据异常");
		}
	}
	public void qryOp(){
		Map<String,Object> filter=this.getJsonAttrs();
		this.rendJson(true,null,"",Notice.dao.findById(filter.get("id")));
	}
}
