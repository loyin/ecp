package net.loyin.ctrl.scm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.loyin.ctrl.AdminBaseController;
import net.loyin.jfinal.anatation.PowerBind;
import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.model.scm.Product;
import net.loyin.model.sso.SnCreater;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Page;
/**
 * 
 * @author 龙影
 */
@RouteBind(path="product",sys="进销存",model="产品")
public class ProductCtrl extends AdminBaseController<Product> {
	public ProductCtrl(){
		this.modelClass=Product.class;
	}
	public void dataGrid() {
		Map<String,String>userMap=this.getUserMap();
		Map<String,Object> filter=new HashMap<String,Object>();
		filter.put("company_id",userMap.get("company_id"));
		filter.put("keyword",this.getPara("keyword"));
		filter.put("category",this.getPara("category"));
		this.sortField(filter);
		Page<Product> page = Product.dao.pageGrid(getPageNo(), getPageSize(),filter);
		this.rendJson(true,null, "success", page);
	}

	public void qryOp() {
		getId();
		Product m = Product.dao.findById(id, this.getCompanyId());
		if (m != null)
			this.rendJson(true,null, "", m);
		else
			this.rendJson(false,null, "记录不存在！");
	}
	@PowerBind(code={"A2_1_E","A3_1_E"},funcName="编辑")
	public void save() {
		try {
			Product po = (Product) getModel();
			if (po == null) {
				this.rendJson(false,null, "提交数据错误！");
				return;
			}
			getId();
			String sn=po.getStr("billsn");
			String company_id=this.getCompanyId();
			if (StringUtils.isEmpty(id)) {
				if(StringUtils.isEmpty(sn))
				{
					sn=SnCreater.dao.create("PRODUCT", company_id);
					po.set("billsn",sn);
				}
				po.set("company_id",company_id);
				po.save();
			} else {
				po.update();
			}
			Map<String,Object> data=new HashMap<String,Object>();
			data.put("id",id);
			data.put("sn",sn);
			this.rendJson(true,null, "操作成功！",data);
		} catch (Exception e) {
			log.error("保存产品异常", e);
			this.rendJson(false,null, "保存数据异常！");
		}
	}
	@PowerBind(code={"A2_1_E","A3_1_E"},funcName="删除")
	public void del() {
		try {
			getId();
			Product.dao.del(id,this.getCompanyId());
			rendJson(true,null,"删除成功！",id);
		} catch (Exception e) {
			log.error("删除异常", e);
			rendJson(false,null,"删除失败！",id);
		}
	}
	@PowerBind(code={"A2_1_E","A3_1_E"},funcName="编辑")
	public void disable(){
		getId();
		try{
			Product.dao.disable(id,this.getCompanyId());
			this.rendJson(true,null, "操作成功！", id);
		}catch(Exception e){
			this.rendJson(false,null, "操作失败！");
		}
	}
}
