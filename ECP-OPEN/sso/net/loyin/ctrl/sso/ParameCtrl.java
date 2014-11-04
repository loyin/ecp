package net.loyin.ctrl.sso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.loyin.ctrl.AdminBaseController;
import net.loyin.jfinal.anatation.PowerBind;
import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.model.sso.Parame;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 * 系统自定义参数
 * 
 * @author 龙影
 */
@RouteBind(path = "parame",sys="设置",model="自定义参数")
public class ParameCtrl extends AdminBaseController<Parame> {
	public ParameCtrl() {
		this.modelClass = Parame.class;
	}
	public void dataGrid() {
		List<Record> list=Parame.dao.qryList(this.getCompanyId(),this.getParaToInt("type"));
		List<Record> list_temp=new ArrayList<Record>();
		if(list!=null&&list.isEmpty()==false){
			dotree(null,list,list_temp,0,true);
		}
		Page<Record> page=new Page<Record>(list_temp,1,0,0,(list==null||list.isEmpty())?0:list.size());
		this.rendJson(true,null, "",page);
	}
	public void tree(){
		this.rendJson(true, null,"",Parame.dao.qryList(this.getCompanyId(),this.getParaToInt("type",0)));
	}
	public void qryOp() {
		getId();
		Parame m = Parame.dao.findById(id,this.getCompanyId());
		if (m != null)
			this.rendJson(true,null, "", m);
		else
			this.rendJson(false,null, "记录不存在！");
	}
	/**以id值索引将list转换成json{Lood:{id:"Lood"}}*/
	public void list(){
		List<Record>list=Parame.dao.qryList(this.getCompanyId(),this.getParaToInt("type"));
		Map<String,Object>data=new HashMap<String,Object>();
//		List<Record> allList=new ArrayList<Record>();//所有参数列表
		List<List<Record>> typeList=new ArrayList<List<Record>>();//按类别列表
		for(int i=0;i<999;i++){
			typeList.add(new ArrayList<Record>());
		}
		if(list!=null&&list.isEmpty()==false){
			for(Record r:list){
//				allList.add(r);
				data.put(r.getStr("id"), r);
				int type=r.getInt("type");
				typeList.get(type).add(r);
			}
//			data.put("list",allList);
			data.put("typeList",typeList);
		}
		this.rendJson(data);
	}
	/**提供按id组织的地区数组*/
	public void areaList(){
		List<Record>list=Parame.dao.qryAreaList();
		Map<String,Object>data=new HashMap<String,Object>();
		List<Record> provinceList=new ArrayList<Record>();//省份列表
		Map<String,Record> allList=new HashMap<String,Record>();//所有地区列表
		List<Map<Integer,List<Record>>> zoneList=new ArrayList<Map<Integer,List<Record>>>(9);//按区域省份列表
		for(int i=1;i<=9;i++){
			Map<Integer,List<Record>> d=new HashMap<Integer,List<Record>>();
			d.put(i,new ArrayList<Record>());
			zoneList.add(d);
		}
		if(list!=null&&list.isEmpty()==false){
			for(Record r:list){
				String id_=r.getStr("id");
				String pid_=r.getStr("pid");
				allList.put(id_,r);
				if(StringUtils.isNotEmpty(pid_)){
					continue;
				}
				provinceList.add(r);
				int zone_type=r.getInt("zone_type");
				zoneList.get(zone_type-1).get(zone_type).add(r);
				List<Record> child=new ArrayList<Record>();
				for(Record r1:list){
					if(id_.equals(r1.getStr("pid"))){
						child.add(r1);
					}
				}
				data.put(id_,child);
			}
			data.put("provinceList",provinceList);
			data.put("list",allList);
			data.put("zoneList",zoneList);
		}
		this.rendJson(data);
	}
	@PowerBind(code="A11_1_E",funcName="编辑")
	public void save() {
		try {
			Parame po =  (Parame) getModel();
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
	@PowerBind(code="A11_1_E",funcName="删除")
	public void del() {
		try {
			getId();
			Parame.dao.del(id,this.getCompanyId());
			rendJson(true,null, "删除成功！",id);
		} catch (Exception e) {
			log.error("删除异常", e);
			rendJson(false,null,"删除失败！");
		}
	}
}
