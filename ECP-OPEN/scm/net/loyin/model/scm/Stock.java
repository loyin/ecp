package net.loyin.model.scm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.loyin.jfinal.anatation.TableBind;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
/**
 * 仓库库存
 * @author 龙影
 */
@TableBind(name="scm_stock")
public class Stock extends Model<Stock> {
	private static final long serialVersionUID = 6608818237278794192L;
	public static final String tableName="scm_stock";
	public static Stock dao=new Stock();
	public Page<Stock> pageGrid(int pageNo, int pageSize,Map<String,Object>filter){
		StringBuffer sql=new StringBuffer("from ");
		sql.append(tableName);
		sql.append(" t,");
		sql.append(Product.tableName);
		sql.append(" p,");
		sql.append(Depot.tableName);
		sql.append(" d where t.depot_id=d.id and p.id=t.product_id and p.company_id=?");
		List<Object> parame=new ArrayList<Object>();
		parame.add(filter.get("company_id"));
		String keyword=(String)filter.get("keyword");
		if(StringUtils.isNotEmpty(keyword)){
			sql.append(" and (p.name like ? or p.model like ? or d.name like ?)");
			keyword="%"+keyword+"%";
			parame.add(keyword);
			parame.add(keyword);
			parame.add(keyword);
		}
		String depot_id=(String)filter.get("depot_id");
		if(StringUtils.isNotEmpty(depot_id)&&!"-1".equals(depot_id)){
			sql.append(" and d.id=?");
			parame.add(depot_id);
		}
		String category_id=(String)filter.get("category_id");
		if(StringUtils.isNotEmpty(category_id)){
			sql.append(" and p.category=?");
			parame.add(category_id);
		}
		if(StringUtils.isEmpty(depot_id)){//按产品统计库存量
			sql.append(" group by product_id,p.name,p.category,p.unit,p.stock_warn,p.billsn,p.status,p.model,p.sale_price,p.purchase_price ");
		}else{
			sql.append(" group by product_id,p.name,d.name,p.category,p.unit,p.stock_warn,p.billsn,p.status,p.model,p.sale_price,p.purchase_price ");
		}
		String sortField=(String)filter.get("_sortField");
		if(StringUtils.isNotEmpty(sortField)){
			String sort=(String)filter.get("_sort");
			sql.append(" order by ");
			sql.append(sortField);
			sql.append(" ");
			sql.append(sort);
		}
		if(StringUtils.isEmpty(depot_id)){//按产品统计库存量
			return dao.paginate(pageNo,pageSize,"select t.product_id,sum(amount) as amount,p.name product_name,p.category,p.unit,p.stock_warn,p.billsn,p.status,p.model,p.sale_price,p.purchase_price",sql.toString(), parame.toArray());
		}else{
			return dao.paginate(pageNo,pageSize,"select d.name depot_name,t.product_id,sum(amount) as amount,p.name product_name,p.category,p.unit,p.stock_warn,p.billsn,p.status,p.model,p.sale_price,p.purchase_price",sql.toString(), parame.toArray());
		}
	}
	/**库存预警*/
	public Page<Stock> warnPageGrid(int pageNo, int pageSize,Map<String,Object>filter) {
		StringBuffer sql=new StringBuffer();
		sql.append("from (select nullif(sum(t.amount),0) amount,p.category,p.unit,p.name product_name,p.stock_warn,p.billsn,p.status,p.model,p.unit,p.sale_price,p.purchase_price from ");
		sql.append(Product.tableName);
		sql.append(" p left join ");
		sql.append(tableName);
		sql.append(" t on p.id=t.product_id left join ");
		sql.append(Depot.tableName);
		sql.append(" d on t.depot_id=d.id where p.company_id=? ");
		List<Object> parame=new ArrayList<Object>();
		parame.add(filter.get("company_id"));
		String keyword=(String)filter.get("keyword");
		if(StringUtils.isNotEmpty(keyword)){
			sql.append(" and (p.name like ? or p.model like ? or d.name like ?)");
			keyword="%"+keyword+"%";
			parame.add(keyword);
			parame.add(keyword);
			parame.add(keyword);
		}
		String depot_id=(String)filter.get("depot_id");
		if(StringUtils.isNotEmpty(depot_id)){
			sql.append(" and d.id=?");
			parame.add(depot_id);
		}
		String category_id=(String)filter.get("category_id");
		if(StringUtils.isNotEmpty(category_id)){
			sql.append(" and p.category=?");
			parame.add(category_id);
		}
		sql.append(" group by p.name,p.category,p.unit,p.stock_warn,p.billsn,p.status,p.model,p.unit,p.sale_price,p.purchase_price,p.category,p.id) a_");
		String sortField=(String)filter.get("_sortField");
		if(StringUtils.isNotEmpty(sortField)){
			String sort=(String)filter.get("_sort");
			sql.append(" order by ");
			sql.append(sortField);
			sql.append(" ");
			sql.append(sort);
		}
		return dao.paginate(pageNo,pageSize,"select a_.*",sql.toString(), parame.toArray());
	}
	public Double updateStock(String depot_id,String product_id,Double amount){
		List<Record> list=Db.find("select * from "+tableName+" where depot_id=? and product_id=? ",depot_id,product_id);
		if(list!=null&&list.isEmpty()==false){//修改
			Db.update("update scm_stock set amount=amount+? where depot_id=? and product_id=?",amount,depot_id,product_id);
		}else{//新增
			Db.update("insert into scm_stock (amount,depot_id,product_id) values(?,?,?)",amount,depot_id,product_id);	
		}
		Record r=Db.findFirst("select amount from "+tableName+" where depot_id=? and product_id=? ",depot_id,product_id);
		return r.getDouble("amount");
	}
	/**初始化库存
	 * @throws Exception */
	@Before(Tx.class)
	public void init(List<Map<String, Object>> dataList,String company_id) throws Exception {
		List<Depot> depotList=Depot.dao.find("select * from "+Depot.tableName+" where company_id=?",company_id);
		Map<String,String> depotMap=new HashMap<String,String>();
		for(Depot d:depotList){
			depotMap.put(d.getStr("name"),d.getStr("id"));
		}
		List<Product> productList=Product.dao.find("select * from "+Product.tableName+" where company_id=?",company_id);
		Map<String,String> productMap=new HashMap<String,String>();
		for(Product d:productList){
			productMap.put(d.getStr("billsn"),d.getStr("id"));
		}
		int len=dataList.size();
		Object[][]parame1=new Object[len][2];
		Object[][]parame2=new Object[len][3];
		int i=0;
		for(Map<String,Object> m:dataList){
			String product_sn=(String)m.get("product_sn");
			String product_id=productMap.get(product_sn);
			if(StringUtils.isEmpty(product_id)){
				throw new Exception("第"+(i+2)+"行的产品不存在！");
			}
			String depot_name=(String)m.get("depot_name");
			String depot_id=depotMap.get(depot_name);
			if(StringUtils.isEmpty(depot_id)){
				throw new Exception("第"+(i+2)+"行的仓库不存在！");
			}
			parame1[i][0]=depot_id;
			parame1[i][1]=product_id;
			
			parame1[i][0]=depot_id;
			parame1[i][1]=product_id;
			parame1[i][2]=(Double)m.get("amount");
			i++;
		}
		Db.batch("delete from "+tableName+" t where t.depot_id = ? and t.product_id=?",parame1,len);
		Db.batch("insert into "+tableName+"(depot_id,product_id,amount)values(?,?,?)",parame2,len);
	}
}
