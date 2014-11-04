package net.loyin.model.sso;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.loyin.jfinal.anatation.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;

/**
 * 岗位
 * 
 * @author 龙影
 */
@TableBind(name = "sso_position")
public class Position extends Model<Position> {
	private static final long serialVersionUID = -1316311332199833614L;
	public static final String tableName = "sso_position";
	public static Position dao = new Position();

	public void del(String id,String company_id) {
		if (StringUtils.isNotEmpty(id)){
			String[] ids=id.split(",");
			StringBuffer ids_=new StringBuffer();
			List<String> parame=new ArrayList<String>();
			for(String id_:ids){
				ids_.append("?,");
				parame.add(id_);
			}
			ids_.append("'-'");
			parame.add(company_id);
			Db.update("delete  from " + tableName + " where id in ("+ids_.toString()+") and company_id=?",parame.toArray());
		}
	}
	/**
	 * 提供tree数据 含 pid,id,name
	 * @param company_id
	 * @param type 0:部门 1：岗位
	 * @return
	 */
	public List<Record> treeList(String company_id,int type){
		List<Record> list=Db.find("select d.id,d.pid,d.type,d.name,d.sort_num from v_position as d where d.company_id=? and type=? order by d.sort_num asc",company_id,type);
		return list;
	}
	/**
	 * 提供 部门和岗位tree数据 含 pid(部门为parent_id 岗位为department_id),id,name,type 
	 * @param company_id
	 * @return
	 */
	public List<Record> treeAllList(String company_id){
		List<Record> list=Db.find("select d.id,d.pid,d.type,d.name,d.name nametext,d.department_id,d1.name department_name,d.sort_num from v_position as d left join v_position as d1 on d1.id=d.department_id where d.company_id=? order by d.sort_num asc",company_id);
		return list;
	}
	public Position findById(String id,String company_id){
		return dao.findFirst("select a.*,b.name as parent_name,c.name department_name from "+tableName+" as a left join "+tableName+" as b on b.id=a.parent_id left join "+tableName+" as c on c.id=a.department_id where a.id=? and a.company_id=? order by a.sort_num asc",id,company_id);
	}
	/**
	 * 查询权限
	 * @param id
	 * @param companyId
	 * @return
	 */
	public String qryPermission(String id, String companyId) {
		Position p= dao.findFirst("select permission from "+tableName+" where id=? and company_id=?",id,companyId);
		if(p!=null){
			return p.getStr("permission");
		}else{
			return null;
		}
	}
	/**
	 * 保存权限设置
	 * @param id
	 * @param permission
	 * @param companyId
	 */
	public void savePermission(String id, String permission, String companyId) {
		Position p=new Position();
		p.set("id",id);
		p.set("company_id",companyId);
		p.set("permission",permission);
		p.update();
		
	}
	/**缓存化权限*/
	public void cachPermission(String company_id){
		List<Object> param=new ArrayList<Object>();
		StringBuffer sql=new StringBuffer();
		sql.append("select * from ");
		sql.append(tableName);
		if(StringUtils.isNotEmpty(company_id)){
			sql.append(" where company_id=?  ");
			param.add(company_id);
		}
		List<Position> list=this.find(sql.toString(),param.toArray());
		if(list!=null&&list.isEmpty()==false){
			for(Position p:list){
				String pm=p.getStr("permission");
				String id=p.getStr("id");
				if(StringUtils.isNotEmpty(pm)){
					CacheKit.put("oneday", "power_"+id,pm);
				}
			}
		}
	}
}
