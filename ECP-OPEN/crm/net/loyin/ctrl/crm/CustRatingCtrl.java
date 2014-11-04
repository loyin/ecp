package net.loyin.ctrl.crm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.loyin.ctrl.AdminBaseController;
import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.model.crm.CustRating;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Record;

/**
 * 客户会员等级
 * @author 龙影
 */
@RouteBind(path = "custRating")
public class CustRatingCtrl extends AdminBaseController<CustRating> {
	public CustRatingCtrl(){
		modelClass=CustRating.class;
	}
	public void dataGrid(){
		Map<String,Object> m=new HashMap<String,Object>();
		List<CustRating> list=CustRating.dao.list(getCompanyId());
		m.put("list",list);
		rendJson(true, null,"",m);
	}
	public void list(){
		Map<String,Object> m=new HashMap<String,Object>();
		List<CustRating> list=CustRating.dao.list(getCompanyId());
		m.put("list",list);
		for(CustRating r:list){
			m.put(r.getStr("id"),r);
		}
		rendJson(true, null,"",m);
	}
	public void save(){
		try{
			CustRating po=(CustRating)getModel();
			getId();
			if(StringUtils.isEmpty(id)){
				po.set("company_id", this.getCompanyId());
				po.save();
				id=po.getStr("id");
			}else{
				po.update();
			}
			rendJson(true, null,"保存成功！", id);
		}catch(Exception e){
			log.error("保存异常",e);
			rendJson(false, null, "保存异常！");
		}
	}
	public void qryOp() {
		getId();
		CustRating m = CustRating.dao.findById(id, getCompanyId());
		if (m != null)
			rendJson(true,null, "", m);
		else
			rendJson(false,null, "记录不存在！");
	}
	public void del(){
		try {
			getId();
			CustRating.dao.del(id,getCompanyId());
			rendJson(true,null,"删除成功！",id);
		} catch (Exception e) {
			log.error("删除异常", e);
			rendJson(false,null,"删除失败！");
		}
	}
}
