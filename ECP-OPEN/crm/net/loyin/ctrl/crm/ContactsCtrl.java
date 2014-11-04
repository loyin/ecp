package net.loyin.ctrl.crm;

import java.util.HashMap;
import java.util.Map;

import net.loyin.ctrl.AdminBaseController;
import net.loyin.jfinal.anatation.PowerBind;
import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.model.crm.Contacts;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Page;
/**
 * 联系人管理
 * @author 龙影
 * 2014年9月17日
 */
@RouteBind(path="contacts",sys="客户",model="联系人")
public class ContactsCtrl extends AdminBaseController<Contacts> {
	public ContactsCtrl(){
		this.modelClass=Contacts.class;
	}
	public void dataGrid() {
		Map<String,Object> filter=new HashMap<String,Object>();
		filter.put("company_id",this.getCompanyId());
		filter.put("keyword",this.getPara("keyword"));
		filter.put("type",this.getPara("type"));
		filter.put("start_date",this.getPara("start_date"));
		filter.put("end_date",this.getPara("end_date"));
		filter.put("uid",this.getPara("uid"));
		filter.put("customer_id",this.getPara("customer_id"));
		this.sortField(filter);
		Page<Contacts> page = Contacts.dao.pageGrid(getPageNo(), getPageSize(),filter);
		this.rendJson(true,null, "success", page);
	}
	public void list(){
		String customer_id=this.getPara("customer_id");
		this.rendJson(true, null, "",Contacts.dao.list(this.getCompanyId(),customer_id));
	}
	public void mainContacts(){//获取主要联系人
		String customer_id=this.getPara("customer_id");
		this.rendJson(true, null, "",Contacts.dao.findMainByCustomerId(customer_id));
	}
	public void qryOp() {
		getId();
		Contacts m = Contacts.dao.findById(id, this.getCompanyId());
		if (m != null)
			this.rendJson(true,null, "", m);
		else
			this.rendJson(false,null, "记录不存在！");
	}
	@PowerBind(code="A1_1_E",funcName="编辑")
	public void save() {
		try {
			Contacts po = (Contacts) getModel();
			if (po == null) {
				this.rendJson(false,null, "提交数据错误！");
				return;
			}
			getId();
			String uid=this.getCurrentUserId();
			this.pullUser(po, uid);
			if (StringUtils.isEmpty(id)) {
				po.set("company_id", this.getCompanyId());
				po.save();
				id=po.getStr("id");
			} else {
				po.update();
			}
			this.rendJson(true,null, "操作成功！",id);
		} catch (Exception e) {
			log.error("保存产品异常", e);
			this.rendJson(false,null, "保存数据异常！");
		}
	}
	@PowerBind(code="A1_1_E",funcName="删除")
	public void del() {
		try {
			getId();
			Contacts.dao.del(id,this.getCompanyId());
			rendJson(true,null,"删除成功！",id);
		} catch (Exception e) {
			log.error("删除异常", e);
			rendJson(false,null,"删除失败！");
		}
	}
}
