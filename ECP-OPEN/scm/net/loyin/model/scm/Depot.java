package net.loyin.model.scm;

import java.util.ArrayList;
import java.util.List;

import net.loyin.jfinal.anatation.TableBind;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
/**
 * 仓库
 * @author 龙影
 */
@TableBind(name="scm_depot")
public class Depot extends Model<Depot> {
	private static final long serialVersionUID = 8739554734071036012L;
	public static final String tableName="scm_depot";
	public static Depot dao=new Depot();
	/**
	 * 分页查询
	 * @param pageNo
	 * @param pageSize
	 * @param company_id
	 * @return
	 */
	public Page<Depot> pageGrid(Integer pageNo,Integer pageSize,String company_id){
		return dao.paginate(pageNo,pageSize,"select t.* ","from " + tableName + " t where company_id=? ",company_id);
	}
	@Before(Tx.class)
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
			Db.update("delete  from " + tableName + " where id in ("+ ids_.toString()+ ") and company_id=? ",parame.toArray());
		}
	}
	public Depot findById(String id,String company_id){
		return dao.findFirst("select t.* from "+ tableName + " t where t.company_id=? and t.id=? ",company_id,id);
	}
	public List<Depot> list(String companyId) {
		return dao.find("select id,name from "+tableName+" where company_id=? order by name asc",companyId);
	}
}
