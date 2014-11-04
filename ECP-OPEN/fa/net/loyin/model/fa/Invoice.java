package net.loyin.model.fa;

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
 * 票据
 * @author 龙影
 */
@TableBind(name="fa_invoice")
public class Invoice extends Model<Invoice> {
	private static final long serialVersionUID = 147362941284332038L;
	public static final String tableName="fa_invoice";
	public static Invoice dao=new Invoice();
	/**
	 * 分页查询
	 * @param pageNo
	 * @param pageSize
	 * @param filter
	 * @return
	 */
	public Page<Invoice> pageGrid(Integer pageNo,Integer pageSize,Map<String,Object>filter){
		List<Object> parame=new ArrayList<Object>();
		StringBuffer sql=new StringBuffer(" from ");
		sql.append(tableName);
		sql.append(" t left join ");
		sql.append(Person.tableName);
		sql.append(" u on t.creater_id=u.id left join ");
		sql.append(Person.tableName);
		sql.append(" u1 on u1.id=t.head_id where t.company_id=? ");
		parame.add(filter.get("company_id"));
		String keyword=(String)filter.get("keyword");
		if(StringUtils.isNotEmpty(keyword)){
			sql.append(" and (t.order_billsn like ? or t.fpsn like ?)");
			keyword="%"+keyword+"%";
			parame.add(keyword);
			parame.add(keyword);
		}
		Integer type=(Integer)filter.get("type");
		if(type!=null){
			sql.append(" and t.type=? ");
			parame.add(type);
		}
		Integer pjlx=(Integer)filter.get("pjlx");
		if(pjlx!=null){
			sql.append(" and t.pjlx=? ");
			parame.add(pjlx);
		}
		String start_date=(String)filter.get("start_date");
		if(StringUtils.isNotEmpty(start_date)){
			sql.append(" and t.kprq >=? ");
			parame.add(start_date);
		}
		String end_date=(String)filter.get("end_date");
		if(StringUtils.isNotEmpty(end_date)){
			sql.append(" and t.kprq <=? ");
			parame.add(end_date);
		}
		String _sortField=(String)filter.get("_sortField");
		if(StringUtils.isNotEmpty(_sortField)){
			sql.append(" order by ");
			sql.append(_sortField);
			sql.append(" ");
			String _sort=(String)filter.get("_sort");
			sql.append(_sort);
		}
		return dao.paginate(pageNo,pageSize,"select t.id,t.order_billsn,t.kpnr,t.type,t.pjlx,t.fpsn,t.amt,t.kprq,t.create_datetime,t.creater_id,u.realname as creater_name,t.head_id,u1.realname as head_name ",
				sql.toString(), parame.toArray());
	}
	public void del(String id,String company_id){
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
			Db.update("delete  from " + tableName + " where id in ("+ids_.toString()+ ") and company_id=? ",parame.toArray());
		}
	}
	public Invoice findById(String id,String company_id){
		return dao.findFirst("select t.*,u.realname as creater_name,t.head_id,u1.realname as head_name from " 
	+ tableName + " t left join "+Person.tableName+" u on t.creater_id=u.id left join "+Person.tableName+" u1 on u1.id=t.head_id where t.id=? and t.company_id=?",id,company_id);
	}
}
