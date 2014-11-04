package net.loyin.model.scm;

import java.util.List;
import java.util.Map;

import net.loyin.jfinal.anatation.TableBind;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.tx.Tx;
/**
 * 调拨产品明细
 * @author 龙影
 */
@TableBind(name="scm_stock_allot_list")
public class StockAllotList extends Model<StockAllotList> {
	private static final long serialVersionUID = -1447533853973326468L;
	public static final String tableName="scm_stock_allot_list";
	public static StockAllotList dao=new StockAllotList();
	@Before(Tx.class)
	public void insert(List<Map<String,Object>> list,String id){
		Db.update("delete from "+tableName+" where id=?",id);
		Object[][]paras=new Object[list.size()][3];
		int i=0;
		for (Map<String, Object> a : list) {
			paras[i][0] = id;
			paras[i][1] = a.get("product_id");
			if (StringUtils.isNotEmpty((String) a.get("amount")))
				paras[i][2] = Float.parseFloat((String) a.get("amount"));
			i++;
		}
		Db.batch("INSERT INTO "+tableName+"(id,product_id,amount)VALUES (?,?,?)",paras,list.size());
	}
	public List<StockAllotList> list(String id){
		return this.find("select t.*,p.name product_name,p.unit from "+tableName+" t,"+Product.tableName+" p where p.id=t.product_id and t.id=? order by p.name ",id);
	}
}