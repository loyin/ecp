package net.loyin.ctrl.sso;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import net.loyin.ctrl.AdminBaseController;
import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.model.sso.Company;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.jfinal.plugin.ehcache.CacheKit;

/**
 * 企业设置
 * 
 * @author 龙影 2014年10月9日
 */
@RouteBind(path = "company")
public class CompanyCtrl extends AdminBaseController<Company> {
	public CompanyCtrl() {
		this.modelClass=Company.class;
	}
	/**查询当前用户企业*/
	public void qry(){
		String company_id=this.getCompanyId();
		Company company=Company.dao.findById(company_id);
		Map<String,Object>map=new HashMap<String,Object>();
		String config=company.getStr("config");
		if(StringUtils.isNotEmpty(config)){
			map=(Map<String,Object>)JSON.parse(config);
		}
		company.set("config",map);
		this.rendJson(true,null,"",company);
	}
	/**查询企业设置 即系统设置*/
	public void qryConfig(){
		String company_id=this.getCompanyId();
		Company company=Company.dao.findById(company_id);
		Map<String,Object>map=new HashMap<String,Object>();
		String config=company.getStr("config");
		if(StringUtils.isNotEmpty(config)){
			map=(Map<String,Object>)JSON.parse(config);
		}
		this.rendJson(true, null, "",map);
	}
	/**保存系统设置*/
	public void saveConfig(){
		try{
			Map<String,Object> map=new HashMap<String,Object>();
			Enumeration<String> attrs= this.getAttrNames();
			while (attrs.hasMoreElements()){
				String name = (String) attrs.nextElement();
				if(name.startsWith("config[p_")){
					map.put(name.replace("config[","").replace("]",""),this.getAttr(name));
				}
			}
			if(map.keySet().isEmpty()){
				this.rendJson(false, null, "未提交数据");
				return;
			}
			String company_id=this.getCompanyId();
			Company company=Company.dao.qryCacheById(company_id);
			company.set("name",this.getPara("name"));
			company.set("short_name",this.getPara("short_name"));
			company.set("industry",this.getPara("industry"));
			company.set("province",this.getPara("province"));
			company.set("city",this.getPara("city"));
			company.set("address",this.getPara("address"));
			company.set("fax",this.getPara("fax"));
			company.set("telephone",this.getPara("telephone"));
			
			company.set("config",JSON.toJSONString(map));
			company.update();
			this.rendJson(true, null, "保存企业设置成功！");
			CacheKit.remove("oneday","company"+company_id);
		}catch(Exception e){
			log.error(e);
			this.rendJson(false, null, "保存异常！");
		}
	}
}
