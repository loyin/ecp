package net.loyin.model.scm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.loyin.jfinal.anatation.TableBind;
import net.loyin.model.sso.Person;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
/**
 * 竞争对手
 * @author 龙影
 */
@TableBind(name="scm_competitor")
public class Competitor extends Model<Competitor> {
	private static final long serialVersionUID = -3544681998668610255L;
	public static final String tableName="scm_competitor";
	public static Competitor dao=new Competitor();
	public Page<Competitor> pageGrid(int pageNo, int pageSize,Map<String, Object> filter){
		String userView=Person.tableName;
		List<Object> parame=new ArrayList<Object>();
		StringBuffer sql=new StringBuffer("from ");
		sql.append(tableName);
		sql.append(" t left join ");
		sql.append(userView);
		sql.append(" c on c.id=t.creater_id left join ");
		sql.append(userView);
		sql.append(" u on u.id=t.updater_id ");
		sql.append(" where t.company_id=? ");
		parame.add(filter.get("company_id"));
		String keyword=(String)filter.get("keyword");
		if(StringUtils.isNotEmpty(keyword)){
			sql.append(" and t.name like ? ");
			parame.add("%"+keyword+"%");
		}
		String start_date=(String)filter.get("start_date");
		if(StringUtils.isNotEmpty(start_date)){
			sql.append(" and create_datetime >= ? ");
			parame.add(start_date+" 00:00:00");
		}
		String end_date=(String)filter.get("end_date");
		if(StringUtils.isNotEmpty(end_date)){
			sql.append(" and create_datetime <= ? ");
			parame.add(end_date+" 23:59:59");
		}
		return dao.paginate(pageNo, pageSize,"select t.id,t.name,t.qyxz,t.ppzmd,t.sczy,t.ppxz,t.jzwx,c.realname as creater_name,u.realname as updater_name ",sql.toString(),parame.toArray());
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
			Db.update("delete  from SCM_BIZCOMPETITOR where competitor_id in ("+ids_.toString()+")",parame.toArray());
			parame.add(company_id);
			Db.update("delete  from " + tableName + " where id in ("+ids_.toString()+") and company_id=? ",parame.toArray());
		}
	}
	public Competitor findById(String id,String company_id){
		String userView=Person.tableName;
		StringBuffer sql=new StringBuffer();
		sql.append(tableName);
		sql.append(" t left join ");
		sql.append(userView);
		sql.append(" c on c.id=t.creater_id left join ");
		sql.append(userView);
		sql.append(" u on u.id=t.updater_id ");
		return dao.findFirst("select t.*,c.realname as creator_name,u.realname as updater_name from "+sql.toString()+" where t.company_id=? and t.id=?",company_id,id);
	}
}
