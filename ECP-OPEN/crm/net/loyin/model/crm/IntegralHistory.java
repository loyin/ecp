package net.loyin.model.crm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import net.loyin.jfinal.anatation.TableBind;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
/**
 * 客户积分记录
 * @author 龙影
 */
@TableBind(name="crm_integral_history")
public class IntegralHistory extends Model<IntegralHistory> {
	private static final long serialVersionUID = 5432220733870597907L;
	public static final String tableName="crm_integral_history";
	public static IntegralHistory dao=new IntegralHistory();
	public Page<IntegralHistory> pageGrid(int pageNo, int pageSize,	Map<String, Object> filter) {
		List<Object> parame=new ArrayList<Object>();
		StringBuffer sql=new StringBuffer("from ");
		sql.append(tableName);
		sql.append(" t,");
		sql.append(Customer.tableName);
		sql.append(" c where c.id=t.customer_id and c.company_id=?");
		parame.add(filter.get("company_id"));
		String keyword=(String)filter.get("keyword");
		if(StringUtils.isNotEmpty(keyword)){
			sql.append(" and c.sn like ? or c.name like ?");
			keyword="%"+keyword+"%";
			parame.add(keyword);
			parame.add(keyword);
		}
		String rating=(String)filter.get("rating");
		if(StringUtils.isNotEmpty(rating)){
			sql.append(" and rating=? ");
			parame.add(rating);
		}
		String customer_id=(String)filter.get("customer_id");
		if(StringUtils.isNotEmpty(customer_id)){
			sql.append(" and t.customer_id=? ");
			parame.add(customer_id);
		}
		sql.append(" order by t.create_time desc");
		return dao.paginate(pageNo, pageNo, "",sql.toString(),parame.toArray());
	}
	
}
