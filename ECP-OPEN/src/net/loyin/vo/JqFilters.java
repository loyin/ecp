package net.loyin.vo;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * jqGrid搜索过滤器
 * @author 龙影
 */
public class JqFilters {
	public String groupOp;
	public List<JqFiled> rules;
	public List<JqFilters> groups;
	/**
	 * 构建条件语句
	 * @param filters
	 * @param where
	 * @param parame
	 */
	public void doFilter(JqFilters filters,StringBuffer where ,List<Object> parame){
		String groupOp=filters.groupOp;
		where.append(" "+groupOp+" ( ");
		List<JqFiled> rules=filters.rules;
		if (rules != null && rules.isEmpty() == false) {
			int i=0;
			for (JqFiled r : rules) {
				if(StringUtils.isEmpty(r.field)){
					continue;
				}
				if(i!=0)
				where.append(" " + groupOp + " ");
				i++;
				where.append(r.field + " ");
				String op = (String) r.op;
				String data = (String) r.data;
				if("eq".equals(op)||"true".equals(op)){// eq>等于
					where.append("=?");
				}else if ("ne".equals(op)) {// ne>不等
					where.append("!=?");
				} else if ("bw".equals(op)) {// bw>开始于
					where.append(" like ");
					data="%"+data;
				} else if ("bn".equals(op)) {// bn>不开始于
					where.append("not like ");
					data="%"+data;
				} else if ("ew".equals(op)) { // ew>结束 于
					where.append(" like ");
					data=data+"%";
				} else if ("en".equals(op)) { // en>不结束于
					where.append(" not like ");
					data=data+"%";
				} else if ("nu".equals(op)) { // nu>不存在
					where.append(" is null ");
					data=null;
				} else if ("nn".equals(op)) { // nn>存在
					where.append(" is not null ");
					data=null;
				/// 待完善
				} else if ("cn".equals(op)) { // cn>包含
					where.append(" in ");
				} else if ("nc".equals(op)) { // nc>不包含
					where.append(" not in ");
				} else if ("in".equals(op)) { // in>属于
					where.append(" in ");
					data=null;
				} else if ("ni".equals(op)) { // ni>不属于
					where.append(" not in ");
					data=null;
				}
				if(data!=null)
				parame.add(data);
			}
			if(filters.groups!=null&&filters.groups.isEmpty()==false){
				for(JqFilters fls:filters.groups){
					doFilter(fls,where ,parame);
				}
			}
			where.append(")");
		}
	}
	public String getGroupOp() {
		return groupOp;
	}

	public void setGroupOp(String groupOn) {
		this.groupOp = groupOn;
	}

	public List<JqFiled> getRules() {
		return rules;
	}

	public void setRules(List<JqFiled> rules) {
		this.rules = rules;
	}

	public List<JqFilters> getGroups() {
		return groups;
	}

	public void setGroups(List<JqFilters> groups) {
		this.groups = groups;
	}

}
