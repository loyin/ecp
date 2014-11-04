package net.loyin.model.scm;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import net.loyin.jfinal.anatation.TableBind;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.tx.Tx;
/**
 * 库存盘点产品列表
 * @author 龙影
 */
@TableBind(name="scm_stock_check_list")
public class StockCheckList extends Model<StockCheckList> {
	private static final long serialVersionUID = 8119136709277162100L;
	public static final String tableName="scm_stock_check_list";
	public static StockCheckList dao=new StockCheckList();
	@Before(Tx.class)
	public void insert(List<Map<String,Object>> list,String id){
		Db.update("delete from "+tableName+" where id=?",id);
		Object[][]paras=new Object[list.size()][4];
		int i=0;
		for (Map<String, Object> a : list) {
			paras[i][0] = id;
			paras[i][1] = a.get("product_id");
			if (StringUtils.isNotEmpty((String) a.get("amount")))
				paras[i][2] = Float.parseFloat((String) a.get("amount"));
			if (StringUtils.isNotEmpty((String) a.get("amount2")))
				paras[i][3] = Float.parseFloat((String) a.get("amount2"));
			i++;
		}
		Db.batch("INSERT INTO "+tableName+"(id,product_id,amount,amount2)VALUES (?,?,?,?)",paras,list.size());
	}
	public List<StockCheckList> list(String id){
		return this.find("select t.*,p.name product_name,p.unit from "+tableName+" t,"+Product.tableName+" p where p.id=t.product_id and t.id=? order by p.name ",id);
	}
}
