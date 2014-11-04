package net.loyin.model.em;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.loyin.jfinal.anatation.TableBind;
import net.loyin.model.sso.Person;
import net.loyin.model.sso.Position;
import net.loyin.model.sso.User;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

/**
 * 销售目标
 * @author 龙影
 * 2014年10月23日
 */
@TableBind(name="em_salegoal")
public class SaleGoal extends Model<SaleGoal> {
	private static final long serialVersionUID = 1246482917895101250L;
	public static final String tableName="em_salegoal";
	public static SaleGoal dao=new SaleGoal();
	/**
	 * 分页查询
	 * @param pageNo
	 * @param pageSize
	 * @param filter
	 * @return
	 */
	public Page<SaleGoal> pageGrid(Integer pageNo,Integer pageSize,Map<String,Object>filter){
		List<Object> parame=new ArrayList<Object>();
		StringBuffer sql=new StringBuffer(" from ");
		sql.append(tableName);
		sql.append(" t,");
		sql.append(Person.tableName);
		sql.append(" pe,");
		sql.append(Position.tableName);
		sql.append(" p,");
		sql.append(User.tableName);
		sql.append(" u,");
		sql.append(Position.tableName);
		sql.append(" dpt where t.user_id=pe.id and u.id=pe.id and u.position_id=p.id and p.department_id=dpt.id and t.company_id=? ");
		parame.add(filter.get("company_id"));
		String keyword=(String)filter.get("keyword");
		if(StringUtils.isNotEmpty(keyword)){
			sql.append(" and (pe.realname like ? )");
			keyword="%"+keyword+"%";
			parame.add(keyword);
		}
		String year=(String)filter.get("year");
		if(StringUtils.isNotEmpty(year)){
			sql.append(" and t.year=? ");
			parame.add(year);
		}
		String _sortField=(String)filter.get("_sortField");
		if(StringUtils.isNotEmpty(_sortField)){
			sql.append(" order by ");
			sql.append(_sortField);
			sql.append(" ");
			String _sort=(String)filter.get("_sort");
			sql.append(_sort);
		}
		return dao.paginate(pageNo,pageSize,"select t.*,pe.realname,dpt.name department_name ",
				sql.toString(), parame.toArray());
	}

	/**
	 * 查询个人目标 
	 * @param year
	 * @param uid
	 * @return
	 */
	public SaleGoal qry(String year, String uid){
		return this.findFirst("select * from "+tableName+" where year=? and user_id=?",year,uid);
	}
	public String save2() {
		String id=this.getStr("id");
		if(StringUtils.isEmpty(id)){
			SaleGoal po=this.qry(this.getStr("year"),this.getStr("user_id"));
			if(po==null){
				this.save();
				id=this.getStr("id");
			}else{
				this.update();
			}
		}else{
			this.update();
		}
		return id;
	}
	public SaleGoal findById(String id,String company_id){
		StringBuffer sql=new StringBuffer("select t.*,pe.realname,dpt.name department_name from ");
		sql.append(tableName);
		sql.append(" t,");
		sql.append(Person.tableName);
		sql.append(" pe,");
		sql.append(Position.tableName);
		sql.append(" p,");
		sql.append(User.tableName);
		sql.append(" u,");
		sql.append(Position.tableName);
		sql.append(" dpt where t.user_id=pe.id and u.id=pe.id and u.position_id=p.id and p.department_id=dpt.id and t.company_id=? and t.id=? ");
		return this.findFirst(sql.toString(),company_id,id);
	}
	/**获取月份目标值*/
	public Object qryMonth(String uid, String month,String year) {
		SaleGoal po=this.findFirst("select m"+month+" from "+tableName+" where user_id=? and year=?",uid,year);
		if(po!=null)
		return po.get("m"+month);
		return null;
	}
	/**
	 * 更新目标完成情况
	 * @param uid
	 * @param month
	 * @param year
	 * @param amt
	 */
	public void updateVal(String uid,Integer month,Integer year,BigDecimal amt){
		StringBuffer sql=new StringBuffer("update ");
		sql.append(tableName);
		sql.append(" set mv");
		sql.append(month);
		sql.append("=mv");
		sql.append(month);
		sql.append("+");
		sql.append(amt);
		sql.append(" where user_id=? and \"year\"=?");
		Db.update(sql.toString(),uid,year+"");
		sql=new StringBuffer("update ");
		sql.append(tableName);
		sql.append(" set ");
		if(month<4)
		sql.append("qv1=mv1+mv2+mv3");
		if(month>3&&month<7)
		sql.append("qv2=mv4+mv5+mv6");
		if(month>6&&month<10)
		sql.append("qv3=mv7+mv8+mv9");
		if(month>9)
		sql.append("qv4=mv10+mv11+mv12");
		sql.append(" where user_id=? and \"year\"=?");
		Db.update(sql.toString(),uid,year+"");
		Db.update("update "+tableName+" set yv=qv1+qv2+qv3+qv4 where user_id=? and \"year\"=?",uid,year+"");
	}
	/**删除*/
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
			Db.update("delete  from " + tableName + " where id in ("+ids_.toString()+") and company_id=? ",parame.toArray());
		}
	}
}
