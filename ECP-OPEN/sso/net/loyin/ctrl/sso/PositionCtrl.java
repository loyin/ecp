package net.loyin.ctrl.sso;

import java.util.ArrayList;
import java.util.List;

import net.loyin.ctrl.AdminBaseController;
import net.loyin.jfinal.anatation.PowerBind;
import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.model.sso.Position;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 *岗位/部门
 * @author 龙影
 */
@RouteBind(model="岗位",path="position",sys="设置")
public class PositionCtrl extends AdminBaseController<Position> {
	public PositionCtrl() {
		this.modelClass = Position.class;
	}
	public void dataGrid(){
		List<Record> list=Position.dao.treeAllList(this.getCompanyId());
		List<Record> list_temp=new ArrayList<Record>();
		if(list!=null&&list.isEmpty()==false){
			dotree(null,list,list_temp,0,true);
		}
		Page<Record> page=new Page<Record>(list_temp,1,0,0,(list==null||list.isEmpty())?0:list.size());
		this.rendJson(true,null, "",page);
	}
	/**维护树*/
	public void tree(){
		this.rendJson(true,null, "",Position.dao.treeAllList(this.getCompanyId()));
	}
	/**岗位树*/
	public void postionTree(){
		this.rendJson(true,null, "",Position.dao.treeList(this.getCompanyId(),1));
	}
	/**部门树*/
	public void departMentTree(){
		this.rendJson(true,null, "",Position.dao.treeList(this.getCompanyId(),0));
	}
	public void qryOp() {
		getId();
		Position m = Position.dao.findById(id,this.getCompanyId());
		if (m != null)
			this.rendJson(true,null, "", m);
		else
			this.rendJson(false,null, "记录不存在！");
	}
	/***
	 * 查询权限
	 */
	public void qryPermission(){
		getId();
		String permission=Position.dao.qryPermission(id,this.getCompanyId());
		this.rendJson(true,null, "",permission);
	}
	@PowerBind(code="A11_1_E",funcName="设置权限")
	public void savePermission(){
		getId();
		String[] code=this.getParaValues("code");
		String permission=ArrayUtils.toString(code,"");
		try{
			permission=permission.replace("{","").replace("}","");
			Position.dao.savePermission(id,permission,this.getCompanyId());
			this.rendJson(true,null, "保存权限设置成功！");
		}catch(Exception e){
			log.error("保存权限设置异常",e);
			this.rendJson(false,null, "保存权限设置异常！");
		}
	}
	@PowerBind(code="A11_1_E",funcName="编辑")
	public void save() {
		try {
			Position po =  (Position) getModel();
			if (po == null) {
				this.rendJson(false,null, "提交数据错误！");
				return;
			}
			id=po.getStr("id");
			if (StringUtils.isEmpty(id)) {
				po.set("company_id", this.getCompanyId());
				po.save();
				id=po.getStr("id");
			} else {
				po.update();
			}
			this.rendJson(true,null, "操作成功！",id);
		} catch (Exception e) {
			log.error("保存用户异常", e);
			this.rendJson(false,null, "保存数据异常！");
		}
	}
	@PowerBind(code="A11_1_E",funcName="删除")
	public void del() {
		try {
			getId();
			Position.dao.del(id,this.getCompanyId());
			rendJson(true,null,"删除成功！",id);
		} catch (Exception e) {
			log.error("删除异常", e);
			rendJson(false,null,"删除失败！");
		}
	}
}
