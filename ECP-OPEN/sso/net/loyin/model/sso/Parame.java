package net.loyin.model.sso;

import java.util.ArrayList;
import java.util.List;

import net.loyin.jfinal.anatation.TableBind;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;
/**
 * 参数
 * @author 龙影
 */
@TableBind(name="sso_parame")
public class Parame extends Model<Parame> {
	private static final long serialVersionUID = -3460285118690352227L;
	public static final String tableName="sso_parame";
	public static Parame dao=new Parame();
	/**查找参数查询*/
	public List<Record> qryList(String company_id,Integer type){
		List<Object> parame=new ArrayList<Object>();
		StringBuffer where=new StringBuffer("where d.company_id=? ");
		parame.add(company_id);
		if(type!=null){
			where.append(" and d.type= ?");
			parame.add(type);
		}
		where.append(" order by d.sort_num asc");
		return Db.find("select d.id,d.name,d.name as textname,d.parent_id as pid,d.sort_num,d.type,d.is_end from " + tableName + " d "+where.toString() ,parame.toArray());
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
	public Parame findById(String id,String company_id){
		return dao.findFirst("select d.*,p.name as parent_name from "+tableName+" d left join "+tableName+" p on p.id=d.parent_id where d.id=? and d.company_id=?",id,company_id);
	}
	/**查询参数的类别*/
	public List<Integer> qryTypeList(String companyId){
		List<Record>list=Db.find("select distinct type from "+tableName+" where company_id=? order by type asc",companyId);
		if(list!=null&&list.isEmpty()==false){
			List<Integer> list_=new ArrayList<Integer>();
			for(Record r:list){
				list_.add(r.getInt("type"));
			}
			return list_;
		}
		return null;
	}
	/**查看地区数据*/
	public List<Record> qryAreaList() {
		return Db.find("select * from base_area order by sort_num asc");
	}
	public List<Parame> list(String company_id) {
		return this.find("select id,name from "+tableName+" where company_id=?",company_id);
	}
}
