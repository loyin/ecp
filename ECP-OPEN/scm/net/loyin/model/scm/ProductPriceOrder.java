package net.loyin.model.scm;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.loyin.jfinal.anatation.TableBind;
import net.loyin.model.sso.Person;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
/**
 * 产品价目表
 * @author 龙影
 */
@TableBind(name="scm_product_price_order")
public class ProductPriceOrder extends Model<ProductPriceOrder> {
	private static final long serialVersionUID = 1531167184359663729L;
	public static final String tableName="scm_product_price_order";
	public static ProductPriceOrder dao=new ProductPriceOrder();
	/**
	 * 分页查询
	 * @param pageNo
	 * @param pageSize
	 * @param where 查询条件
	 * @param param 参数
	 * @return
	 */
	public Page<ProductPriceOrder> pageGrid(Integer pageNo,Integer pageSize,StringBuffer where,List<Object> param){
		return dao.paginate(pageNo,pageSize,"select t.id,t.subject,t.start_date,t.end_date,t.creater_id,t.create_datetime,t.remark,p.realname as creater_realname",
				"from " + tableName + " t,"+Person.tableName+" p where p.id=t.creater_id "+ where.toString(), param.toArray());
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
			Db.update("delete  from scm_product_price where id in ("+ ids_.toString()+ ")",parame.toArray());
			parame.add(company_id);
			Db.update("delete  from " + tableName + " where id in ("+ ids_.toString()+ ") and company_id=? ",parame.toArray());
		}
	}
	public ProductPriceOrder findById(String id,String company_id){
		return dao.findFirst("select t.*,p.realname creater_realname from "	+ tableName + " t,"+Person.tableName+" p where p.id=t.creater_id and t.company_id=? and t.id=? ",company_id,id);
	}
	/**
	 * 保存产品列表
	 * @param list 产品信息列表
	 * @param id
	 */
	@Before(Tx.class)
	public void insertProductList(List<Map<String,Object>> list,String id){
		Db.update("delete from scm_product_price where id=?",id);
		Object[][]paras=new Object[list.size()][7];
		int i=0;
		for (Map<String, Object> a : list) {
			paras[i][0] = id;
			paras[i][1] = a.get("product_id");
			if (StringUtils.isNotEmpty((String) a.get("cost")))
				paras[i][2] = new BigDecimal((String)a.get("cost"));
			if (StringUtils.isNotEmpty((String)a.get("price")))
				paras[i][3] = new BigDecimal((String)a.get("price"));
			if (StringUtils.isNotEmpty((String) a.get("amount")))
				paras[i][4] = Float.parseFloat((String)a.get("amount"));
			paras[i][5] = (String) a.get("start_date");
			paras[i][6] =(String) a.get("end_date");
			i++;
		}
		Db.batch("INSERT INTO scm_product_price(id,product_id,cost,amount,price,start_date,end_date)VALUES (?,?,?,?,?,?,?)",paras,list.size());
	}
	public List<Record> productlist(String id) {
		return Db.find("select pp.*,p.name product_name from scm_product_price pp,"+Product.tableName+" p where pp.product_id=p.id and pp.id=? order by product_name ",id);
	}
}
