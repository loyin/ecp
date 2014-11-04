package net.loyin.model.scm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.loyin.jfinal.anatation.TableBind;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
/**
 * 产品
 * @author 龙影
 */
@TableBind(name="scm_product")
public class Product extends Model<Product> {
	private static final long serialVersionUID = -5465375747846490039L;
	public static final String tableName="scm_product";
	public static Product dao=new Product();
	/**
	 * 分页查询
	 * @param pageNo
	 * @param pageSize
	 * @param filter 参数
	 * @return
	 */
	public Page<Product> pageGrid(Integer pageNo,Integer pageSize,Map<String,Object> filter){
		List<Object> parame=new ArrayList<Object>();
		StringBuffer sql=new StringBuffer(" from ");
		sql.append(tableName);
		sql.append(" t where t.company_id=? ");
		parame.add(filter.get("company_id"));
		String keyword=(String)filter.get("keyword");
		if(StringUtils.isNotEmpty(keyword)){
			sql.append(" and (t.billsn like ? or t.name like ?)");
			keyword="%"+keyword+"%";
			parame.add(keyword);
			parame.add(keyword);
		}
		String sortField=(String)filter.get("_sortField");
		if(StringUtils.isNotEmpty(sortField)){
			sql.append(" order by ");
			sql.append(sortField);
			sql.append(" ");
			sql.append((String)filter.get("_sort"));
		}
		return dao.paginate(pageNo,pageSize,"select t.id,t.id product_id,t.name,t.name product_name,t.unit,t.stock_warn,t.billsn,t.status,t.model,t.sale_price,t.purchase_price,t.category",
				sql.toString(), parame.toArray());
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
	public Product findById(String id,String company_id){
		return dao.findFirst("select t.*,t.sale_price,t.purchase_price from "
				+ tableName + " t where t.company_id=? and t.id=? ",company_id,id);
	}
	/**禁用或激活*/
	public void disable(String id,String company_id) {
		Db.update("update "+tableName+" set status=case when status=0 then 1 else 0 end where id=? and company_id=?",id,company_id);
	}
}
