package net.loyin.model.sso;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import net.loyin.jfinal.anatation.TableBind;

import com.alibaba.fastjson.JSON;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
/**
 *企业 
 * @author 龙影
 */
@TableBind(name="sso_company")
public class Company extends Model<Company> {
	private static final long serialVersionUID = -584471063844941313L;
	public static final String tableName="sso_company";
	public static Company dao=new Company();
	/**
	 * 判断是否存在公司
	 * @param companyName
	 * @return boolean
	 */
	public boolean extisCompany(String companyName){
		return qryCompanyByName(companyName)!=null;
	}
	public Company qryCacheById(String id){
		List<Company> list=this.findByCache("oneday","company"+id,"select * from "+tableName+" where id=?",id);
		if(list==null||list.isEmpty()){
			return null;
		}
		return list.get(0);
	}
	/**
	 * 查询公司
	 * @param companyName
	 * @return boolean
	 */
	public Company qryCompanyByName(String companyName){
		return this.findFirst("select * from "+tableName+" where name=? ",companyName);
	}
	public Company qryById(String company_id) {
		return this.findFirst("select * from "+tableName+" where id=? ",company_id);
	}
	/**企业系统设置*/
	public void updateConfig(String config, String companyId) {
		Db.update("update "+tableName+" set config=? where id=?",config,companyId);
	}
	public Map<String,Object> getConfig(){
		String config=this.getStr("config");
		if(StringUtils.isEmpty(config))
		return null;
		Map<String,Object> m=(Map<String,Object>)JSON.parse(config);
		return m;
	}
}
