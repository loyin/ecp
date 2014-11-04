package net.loyin.model.sso;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import net.loyin.jfinal.anatation.TableBind;

import com.alibaba.fastjson.JSON;
import com.jfinal.plugin.activerecord.Model;
/**
 * 用户属性，包含系统用户及联系人
 * @author 龙影
 */
@TableBind(name="sso_person")
public class Person extends Model<Person> {
	private static final long serialVersionUID = -7717028702408247594L;
	public static final String tableName="sso_person";
	public static Person dao=new Person();
	public Map<String,Object> qryById(String id) {
		Person p= dao.findFirst("select p.*,pd.data as data"+tableName+" p,sso_ where p.id=pd.id and p.id=?",id);
		if(p==null)
			return null;
		//将data转换成json
		Map<String,Object> map=p.getAttrs();
		String data=p.getStr("data");
		if(StringUtils.isNotEmpty(data)){
			map.put("data",JSON.parse(data));
		}
		return map;
	}
}