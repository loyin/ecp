package net.loyin.ctrl.fa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.loyin.ctrl.AdminBaseController;
import net.loyin.jfinal.anatation.PowerBind;
import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.model.fa.Subject;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
/**
 * 财务科目
 * @author 龙影
 */
@RouteBind(sys="财务",path="subject",model="科目")
public class SubjectCtrl extends AdminBaseController<Subject> {
	public SubjectCtrl() {
		this.modelClass = Subject.class;
	}
	public void dataGrid(){
		List<Record> list=Subject.dao.treeAllList(this.getCompanyId(),this.getParaToInt("type"));
		List<Record> list_temp=new ArrayList<Record>();
		if(list!=null&&list.isEmpty()==false){
			dotree(null,list,list_temp,0,true);
		}
		Page<Record> page=new Page<Record>(list_temp,1,0,0,(list==null||list.isEmpty())?0:list.size());
		this.rendJson(true,null, "",page);
	}
	/**维护树*/
	public void tree(){
		this.rendJson(true,null, "",Subject.dao.treeAllList(this.getCompanyId(),this.getParaToInt("type")));
	}
	public void list(){
		this.rendJson(true,null, "",Subject.dao.list(this.getCompanyId()));
	}
	public void list1(){
		Map<String,Object> data=new HashMap<String,Object>();
		List<Record> list=Subject.dao.list(this.getCompanyId());
		for(Record r:list){
			data.put(r.getStr("id"),r.getStr("name"));
		}
		this.rendJson(true,null, "",data);
	}
	public void qryOp() {
		getId();
		Subject m = Subject.dao.findById(id,this.getCompanyId());
		if (m != null)
			this.rendJson(true,null, "", m);
		else
			this.rendJson(false,null, "记录不存在！");
	}
	@PowerBind(code="A7_1_E",funcName="编辑")
	public void save() {
		try {
			Subject po =  (Subject) getModel();
			if (po == null) {
				this.rendJson(false,null, "提交数据错误！");
				return;
			}
			this.getId();
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
	@PowerBind(code="A7_1_E",funcName="删除")
	public void del() {
		try {
			getId();
			Subject.dao.del(id,this.getCompanyId());
			rendJson(true,null, "删除成功！",id);
		} catch (Exception e) {
			log.error("删除异常", e);
			rendJson(false,null,"删除失败！");
		}
	}
}
