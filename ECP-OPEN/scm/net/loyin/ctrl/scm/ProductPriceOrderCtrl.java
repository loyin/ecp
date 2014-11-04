package net.loyin.ctrl.scm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.loyin.ctrl.AdminBaseController;
import net.loyin.jfinal.anatation.PowerBind;
import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.model.scm.ProductPriceOrder;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
/**
 * 产品价目表
 * @author 龙影
 */
@RouteBind(path="productPriceOrder",sys="进销存",model="产品价目表")
public class ProductPriceOrderCtrl extends AdminBaseController<ProductPriceOrder> {
	public ProductPriceOrderCtrl(){
		this.modelClass=ProductPriceOrder.class;
	}
	public void dataGrid() {
		String keyword=this.getPara("keyword");
		List<Object> param = new ArrayList<Object>();
		StringBuffer where = new StringBuffer();
		/** 添加查询字段条件 */
		this.qryField(where, param);
		if(StringUtils.isNotEmpty(keyword)){
			keyword= "%"+keyword+"%";
			where.append(" and ( t.subject like ?)");
			param.add(keyword);
		}
		where.append(" and t.company_id=?");
		param.add(this.getCompanyId());
		this.jqFilters(where, param);
		sortField(where);
		Page<ProductPriceOrder> page = ProductPriceOrder.dao.pageGrid(getPageNo(), getPageSize(), where,param);
		this.rendJson(true,null, "success", page);
	}

	public void qryOp() {
		getId();
		ProductPriceOrder m = ProductPriceOrder.dao.findById(id, this.getCompanyId());
		if (m != null){
			List<Record>productlist=ProductPriceOrder.dao.productlist(id);
			m.put("productlist",productlist);
			m.put("productlistlength",productlist.size());
			this.rendJson(true,null, "", m);
		}
		else
			this.rendJson(false,null, "记录不存在！");
	}
	@PowerBind(code={"A2_1_E","A3_1_E"},funcName="编辑")
	public void save() {
		try {
			ProductPriceOrder po = (ProductPriceOrder) getModel();
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
				attr.put("cost",this.getAttr(pname+"["+i+"][cost]"));
				attr.put("price",this.getAttr(pname+"["+i+"][price]"));
				attr.put("amount",this.getAttr(pname+"["+i+"][amount]"));
				attr.put("start_date",this.getAttr("start_date"));
				attr.put("end_date",this.getAttr("end_date"));
				productlist.add(attr);
			}
			this.pullUser(po, this.getCurrentUserId());
			if (StringUtils.isEmpty(id)) {
				po.set("company_id", this.getCompanyId());
				po.save();
			} else {
				po.update();
			}
			id=po.getStr("id");
			po.insertProductList(productlist, id);
			this.rendJson(true,null, "操作成功！",id);
		} catch (Exception e) {
			log.error("保存产品异常", e);
			this.rendJson(false,null, "保存数据异常！");
		}
	}
	@PowerBind(code={"A2_1_E","A3_1_E"},funcName="删除")
	public void del() {
		try {
			getId();
			ProductPriceOrder.dao.del(id,this.getCompanyId());
			rendJson(true,null,"删除成功！",id);
		} catch (Exception e) {
			log.error("删除异常", e);
			rendJson(false,null,"删除失败！");
		}
	}
}
