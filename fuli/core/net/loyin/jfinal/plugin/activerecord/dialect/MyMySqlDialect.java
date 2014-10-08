package net.loyin.jfinal.plugin.activerecord.dialect;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.loyin.jfinal.model.IdGenerater;

import org.apache.commons.lang.StringUtils;

import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
/**
 * 将ID设置为IdGenerater.timeTo62_fz()的方式来保存 从id_seq获取
 * @author 龙影
 */
public class MyMySqlDialect extends MysqlDialect {
	@Override
	public void forModelSave(Table table, Map<String, Object> attrs, StringBuilder sql, List<Object> paras) {
		sql.append("insert into ").append(table.getName()).append("(");
		String primaryKey=table.getPrimaryKey().toLowerCase();
		if(StringUtils.isEmpty(primaryKey)){
			primaryKey="id";
		}
		if(attrs.keySet().contains(primaryKey)==false){
			attrs.put(primaryKey,null);
		}
		StringBuilder temp = new StringBuilder(") values(");
		for (Entry<String, Object> e: attrs.entrySet()) {
			String colName = e.getKey();
			String idVal=null;
			if(primaryKey.equals(colName.toLowerCase())){//获取主键值
				if(StringUtils.isNotEmpty((String)e.getValue())){
					idVal=(String)e.getValue();
				}else{
					idVal=IdGenerater.me.timeTo62();
				}
			}
			if (table.hasColumnLabel(colName)) {
				if (paras.size() > 0) {
					sql.append(", ");
					temp.append(", ");
				}
				sql.append(colName);
				temp.append("?");
				if(idVal==null){
					paras.add(e.getValue());
				}else{
					paras.add(idVal);
				}
			}
		}
		sql.append(temp.toString()).append(")");
	}
}
