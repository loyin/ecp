package net.loyin.ctrl.crm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.loyin.ctrl.AdminBaseController;
import net.loyin.jfinal.anatation.PowerBind;
import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.model.crm.Business;
import net.loyin.model.crm.BusinessData;
import net.loyin.model.scm.OrderProduct;

import org.apache.commons.lang3.StringUtils;
/**
 * 商机
 * @author 龙影
 * 2014年9月22日
 */
@RouteBind(path="business",sys="销售",model="商机")
public class BusinessCtrl extends AdminBaseController<Business> {
	public BusinessCtrl(){
		this.modelClass=Business.class;
	}
	public void dataGrid(){
		Map<String,Object> filter=new HashMap<String,Object>();
		filter.put("company_id",this.getCompanyId());
		filter.put("keyword",this.getPara("keyword"));
		filter.put("start_date",this.getPara("start_date"));
		filter.put("end_date",this.getPara("end_date"));
		filter.put("is_end",this.getParaToInt("is_end",0));
		filter.put("status",this.getPara("status"));
		filter.put("orgigin",this.getPara("origin"));
		filter.put("uid",this.getPara("uid"));
		filter.put("user_id",this.getCurrentUserId());
		filter.put("position_id",this.getPositionId());
		this.sortField(filter);
		this.rendJson(true, null, "",Business.dao.page(this.getPageNo(),this.getPageSize(),filter,this.getParaToInt("qryType",-1)));
	}
	@PowerBind(code="A3_1_E",funcName="删除")
	@RouteBind(code="000DEL", model = "", name = "")
	public void del() {
		try {
			getId();
			Business.dao.del(id,this.getCompanyId());
			rendJson(true,null,"删除成功！",id);
		} catch (Exception e) {
			log.error("删除异常", e);
			rendJson(false,null,"删除失败！请检查是否被使用！");
		}
	}
	@PowerBind(code="A3_1_V",funcName="查看")
	public void qryOp() {
		getId();
		Business m = Business.dao.findById(id, this.getCompanyId());
		if (m != null){
			List<OrderProduct>productlist=OrderProduct.dao.list(id);
			m.put("productlist",productlist);
			m.put("productlistlength",productlist.size());
			this.rendJson(true,null, "", m);
		}
		else
			this.rendJson(false,null, "记录不存在！");
	}
	@PowerBind(code="A3_1_E",funcName="编辑")
	public void save() {
		try {
			Business po = (Business) getModel();
			if (po == null) {
				this.rendJson(false,null, "提交数据错误！");
				return;
			}
			getId();
			String pname="productlist";
			Integer productlistlength=this.getParaToInt("productlistlength");
			List<Map<String,Object>> productlist=new ArrayList<Map<String,Object>>();
			for(int i=0;i<productlistlength;i++){
				Map<String,Object> attr=new HashMap<String,Object>();
				attr.put("product_id",this.getAttr(pname+"["+i+"][product_id]"));
				attr.put("quoted_price",this.getAttr(pname+"["+i+"][quoted_price]"));
				attr.put("sale_price",this.getAttr(pname+"["+i+"][sale_price]"));
				attr.put("amount",this.getAttr(pname+"["+i+"][amount]"));
				attr.put("description",this.getAttr(pname+"["+i+"][description]"));
				productlist.add(attr);
			}
			BusinessData ldata=(BusinessData)this.getModel2(BusinessData.class);
			this.pullUser(po, this.getCurrentUserId());
			if (StringUtils.isEmpty(id)) {
				po.set("company_id", this.getCompanyId());
				po.save();
				id=po.getStr("id");
				ldata.set("id",id);
				ldata.save();
			} else {
				po.update();
				ldata.update();
			}
			OrderProduct.dao.insert(productlist, id);
			this.rendJson(true,null, "操作成功！",id);
		} catch (Exception e) {
			log.error("保存商机异常", e);
			this.rendJson(false,null, "保存数据异常！");
		}
	}
}
