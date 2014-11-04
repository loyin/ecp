package net.loyin.model.scm;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import net.loyin.jfinal.anatation.TableBind;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.tx.Tx;
/**
 * 订单产品表
 * 适用与商机、订单
 * @author 龙影
 * 2014年9月23日
 */
@TableBind(name="scm_order_product")
public class OrderProduct extends Model<OrderProduct> {
	private static final long serialVersionUID = 2862475552937245134L;
	public static final String tableName="scm_order_product";
	public static OrderProduct dao=new OrderProduct();
	@Before(Tx.class)
	public void insert(List<Map<String,Object>> list,String id){
		Db.update("delete from "+tableName+" where id=?",id);
		Object[][]paras=new Object[list.size()][12];
		int i=0;
		for (Map<String, Object> a : list) {
			paras[i][0] = id;
			paras[i][1] = a.get("product_id");
			if (StringUtils.isNotEmpty((String) a.get("purchase_price")))
				paras[i][2] = new BigDecimal((String) a.get("purchase_price"));
			if (StringUtils.isNotEmpty((String) a.get("sale_price")))
				paras[i][3] = new BigDecimal((String) a.get("sale_price"));
			if (StringUtils.isNotEmpty((String) a.get("amount")))
				paras[i][4] = Float.parseFloat((String) a.get("amount"));
			if (StringUtils.isNotEmpty((String) a.get("zkl")))
				paras[i][5] = Float.parseFloat((String) a.get("zkl"));
			if (StringUtils.isNotEmpty((String) a.get("zhamt")))
				paras[i][6] = new BigDecimal((String) a.get("zhamt"));
			if (StringUtils.isNotEmpty((String) a.get("amt")))
				paras[i][7] = new BigDecimal((String) a.get("amt"));
			paras[i][8] = a.get("description");
			if(StringUtils.isNotEmpty((String) a.get("quoted_price")))
				paras[i][9] = new BigDecimal((String) a.get("quoted_price"));
			if (StringUtils.isNotEmpty((String) a.get("tax_rate")))
				paras[i][10] = Float.parseFloat((String) a.get("tax_rate"));
			if (StringUtils.isNotEmpty((String) a.get("tax")))
				paras[i][11] = new BigDecimal((String) a.get("tax"));
			i++;
		}
		Db.batch("INSERT INTO scm_order_product(id,product_id,purchase_price,sale_price,amount,zkl,zhamt,amt,description,quoted_price,tax_rate,tax)VALUES (?,?,?,?,?,?,?,?,?,?,?,?);",
				paras,list.size());
	}
	public List<OrderProduct> list(String id){
		return this.find("select t.*,p.name product_name,p.unit from "+tableName+" t,"+Product.tableName+" p where p.id=t.product_id and t.id=? order by p.name ",id);
	}
}
