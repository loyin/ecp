package net.loyin.kit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

/**
 * SQL工具
 * @author 刘声凤 2014年1月1日
 */
public class SqlKit {
	/** 生成的sql */
	public String sql;
	/** 组合的参数 */
	public Object[] paras;
	public static SqlKit me=new SqlKit();
	/***
	 * 
	 * @param sql_ 如: select 1 from ta where name=#name# 
	 * @param paraMap 参数map
	 * @return
	 */
	public static SqlKit parameSql(String sql_, Map<String, Object> paraMap) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sqlSb = new StringBuffer();
		String[] sts = sql_.split("#");
		for (String s : sts) {
			Object t = paraMap.get(s);
			if (t != null) {
				list.add(t);
				sqlSb.append("?");
			} else {
				sqlSb.append(s);
			}
		}
		me.sql = sqlSb.toString();
		me.paras = list.toArray();
		return me;
	}
	public static void main(String[] args) {
		String sql="select * from table1 t where t.name=#name# and t.cc=#cc# or t.cc=#name#";
		SqlKit sk=new SqlKit();
	   Map<String,Object>	paraMap=new HashMap<String,Object>();
	   paraMap.put("name","loyin");
	   paraMap.put("cc","====");
		sk.parameSql(sql, paraMap);
		System.out.println(sk.sql+"\t"+ArrayUtils.toString(sk.paras));
	}
}
