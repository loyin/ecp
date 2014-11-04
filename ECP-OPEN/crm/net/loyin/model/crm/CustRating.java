package net.loyin.model.crm;

import java.util.ArrayList;
import java.util.List;

import net.loyin.jfinal.anatation.TableBind;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
/**
 * 客户会员等级
 * @author 龙影
 * 2014年9月15日
 */
@TableBind(name="crm_cust_rating")
public class CustRating extends Model<CustRating> {
	private static final long serialVersionUID = 7252922814948623888L;
	public static final String tableName="crm_cust_rating";
	public static CustRating dao=new CustRating();
	public List<CustRating> list(String company_id) {
		return dao.find("select * from "+tableName+" where company_id=? ",company_id);
	}
	public void del(String id, String company_id) {
		if (StringUtils.isNotEmpty(id)) {
			String[] ids=id.split(",");
			StringBuffer ids_=new StringBuffer();
			List<String> parame=new ArrayList<String>();
			for(String id_:ids){
				ids_.append("?,");
				parame.add(id_);
			}
			ids_.append("'-'");
			parame.add(company_id);
			Db.update("delete from " + tableName + " where id in ("+ids_.toString()+") and company_id=? ",parame.toArray());
		}
	}
	public CustRating findById(String id,String company_id){
		return dao.findFirst("select t.* from "+ tableName + " t where t.company_id=? and t.id=? ",company_id,id);
	}
}
