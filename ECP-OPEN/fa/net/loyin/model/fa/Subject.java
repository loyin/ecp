package net.loyin.model.fa;

import java.util.ArrayList;
import java.util.List;

import net.loyin.jfinal.anatation.TableBind;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;
/**
 * 财务科目
 * @author 龙影
 */
@TableBind(name="fa_subject")
public class Subject extends Model<Subject> {
	private static final long serialVersionUID = 2769332685967322949L;
	public static final String tableName="fa_subject";
	public static Subject dao=new Subject();
	public List<Record> treeAllList(String company_id,Integer type) {
		List<Object> parame=new ArrayList<Object>();
		StringBuffer where=new StringBuffer(" where d.company_id=? ");
		parame.add(company_id);
		if(type!=null){
			where.append(" and d.type=?");
			parame.add(type);
		}
		where.append(" order by d.code asc ");
		return Db.find("select d.*,d.parent_id as pid,d.name as textname,p.name as parent_name  from "+tableName+" d left join "+tableName+" p on p.id=d.parent_id "+where.toString(),parame.toArray());
	}
	public List<Record> list(String company_id){
		return Db.find("select id,concat(code,'-',name)as name from "+tableName+" where company_id=? order by code asc",company_id);
	}
	public void del(String id,String company_id) {
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
			Db.update("delete  from " + tableName + " where id in ("+ids_.toString()+") and company_id=? ",parame.toArray());
		}
	}
	public Subject findById(String id,String company_id){
		return dao.findFirst("select d.*,p.name as parent_name from "+tableName+" d left join "+tableName+" p on p.id=d.parent_id where d.company_id=? and d.id=?",company_id,id);
	}
	
}
