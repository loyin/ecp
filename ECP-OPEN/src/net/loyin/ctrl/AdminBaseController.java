package net.loyin.ctrl;

import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.loyin.jfinal.TypeConverter;
import net.loyin.vo.JqFilters;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.gson.util.Tools;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.TableMapping;

/**
 * 后台管理基础Controller 只提供qryOp save del dataGrid方法
 * @author 刘声凤 2012-9-3 下午10:37:28
 */
public abstract class AdminBaseController<M extends Model<M>> extends BaseController<M> {
	
	protected int getPageNo(){
		return this.getParaToInt("page", 1);
	}
	protected int getPageSize(){
		return this.getParaToInt("rows",10);
	}
	/***
	 * 组合查询条件 支持 >,>= ,<,<=,!=,=,in,Nin,like, Llike,Rlike,Nlike,NLlike,NRlike
	 * @param where
	 * @param param
	 */
	protected void qryField(StringBuffer where,List<Object> param){
		String qryField=this.getPara("qryField");//查询字段 以逗号分隔
		if(StringUtils.isNotEmpty(qryField)){
			String[] k=qryField.split(",");
			for(String q:k){
				Object t=this.getPara(q);
				if(t!=null&&StringUtils.isNotEmpty(t.toString())){
					if(q.toLowerCase().contains("date")){
						try{
							t=dateFormat.parse(t.toString());
							t=new java.sql.Date(((java.util.Date)t).getTime());
						}catch(Exception e){}
					}
					if(q.toLowerCase().contains("time")){
						try{
							t=hmFormat.parse(t.toString());
							t=new java.sql.Date(((java.util.Date)t).getTime());
						}catch(Exception e){}
					}
					String[] pp=q.split("\\$");
					if(pp.length>2){
						where.append(" "+pp[1]+" ");
					}else{
						where.append(" and ");
					}
					where.append(pp[0]);
					/**生成对应字段 START*/
					if(q.endsWith("$ge")){
						where.append(" >=? ");
					}else if(q.endsWith("$gt")){
						where.append(" >? ");
					}else if(q.endsWith("$le")){
						where.append(" <=? ");
					}else if(q.endsWith("$lt")){
						where.append(" <? ");
					}else if(q.endsWith("$Nlike")||q.endsWith("$NLlike")||q.endsWith("$NRlike")){
						where.append("not like ? ");
					}else if(q.endsWith("$like")||q.endsWith("$Llike")||q.endsWith("$Rlike")){
						where.append(" like ? ");
					}else if(q.endsWith("$ne")){
						where.append(" !=? ");
					}else if(q.endsWith("$in")){
						String val=this.getPara(q);
						if(StringUtils.isNotEmpty(val)){
							where.append(" in ( ");
							String[] vs=val.split(",");
							for(int i=0;i<vs.length;i++){
								where.append("?");
								if(i<vs.length-1){
									where.append(",");
								}
								param.add(vs[i]);
							}
							where.append(" ) ");
						}
					}else if(q.endsWith("$Nin")){
						String val=this.getPara(q);
						if(StringUtils.isNotEmpty(val)){
							where.append(" not in ( '");
							String[] vs=val.split(",");
							for(int i=0;i<vs.length;i++){
								where.append("?");
								if(i<vs.length-1){
									where.append(",");
								}
								param.add(vs[i]);
							}
							where.append("' ) ");
						}
					}else{
						where.append(" =? ");
					}
					/**生成对应字段 END*/
					/**生成对应值 START*/
					if(q.endsWith("$like")){
						param.add("%"+t+"%");
					}else if(q.endsWith("$Llike")){
						param.add("%"+t);
					}else if(q.endsWith("$Rlike")){
						param.add(t+"%");
					}else  if(q.endsWith("$Nlike")){
						param.add("%"+t+"%");
					}else if(q.endsWith("$in")){
					}else if(q.endsWith("$Nin")){
					}else{
						param.add(t);
					}
					/** END*/
				}
			}
		}
	}

	/***
	 * <pre>
	 * jqGrid 查询
	 *  eq>等于 ne>不等 bw>开始于 bn>不开始于 ew>结束于 en>不结束于 cn>包含 nc>不包含 nu>不存在 nn>存在 in>属于 ni>不属于
	 *  rows:10
	 *  page:1
	 * sidx: //排序字段
	 * sord:asc
	 * filters:{"groupOp":"OR","rules":[{"field":"myac","op":"eq","data":""},{"field":"myac","op":"eq","data":""}]}
	 * searchField: 搜索字段
	 * searchString: 搜索内容
	 * searchOper:
	 * </pre>
	 * @param where
	 * @param parame
	 */
	protected void jqFilters(StringBuffer where, List<Object> parame) {
		String filtersStr = this.getPara("filters");
		Boolean _search=this.getParaToBoolean("_search");
		if(_search!=null&&_search==false){
			String searchField=this.getPara("searchField");
			String searchString=this.getPara("searchString");
			String searchOper=this.getPara("searchOper");
			if(StringUtils.isNotEmpty(searchField)&&StringUtils.isNotEmpty(searchOper)){
				where.append(" and ");
				where.append(searchField);
				where.append(" ");
				where.append(searchOper);
				where.append(" ? ");
				try{
					Date date=dateFormat.parse(searchString);
					parame.add(date);
				}catch(Exception e){
					parame.add(searchString);
				}
			}
			return;
		}
		if (StringUtils.isNotEmpty(filtersStr)) {
			JqFilters filters=(JqFilters)JSON.parseObject(filtersStr,JqFilters.class);
			filters.doFilter(filters,where,parame);
		}
	}
	/**
	 * 字段排序
	 * @param where
	 * @return boolean true:没有字段排序
	 */
	protected boolean sortField(StringBuffer where) {
		String sortName = this.getPara("sidx");
		String sortOrder = this.getPara("sort", "desc");
		if (StringUtils.isNotEmpty(sortName)) {
			where.append(" order by ");
			where.append(sortName);
			where.append(" ");
			where.append(sortOrder);
		}
		return sortName != null;
	}
	/**
	 * 给map传递 _sortField,_sort
	 * @param parame
	 */
	protected void sortField(Map<String,Object>parame){
		String sortField = this.getPara("sidx");
		String sort = this.getPara("sort", "desc");
		parame.put("_sortField", sortField);
		parame.put("_sort", sort);
	}
	public void del(){this.rendJson(false,null,"不可操作！");}
	public void save(){this.rendJson(false,null,"不可操作！");}
	/**
	 * 设置操作人员id及操作时间
	 * @param m
	 * @param uid
	 */
	protected void pullUser(Model<M> m,String uid) {
		Date date=new Date();
		this.getId();
		String now=dateTimeFormat.format(date);
		if (StringUtils.isNotEmpty(id)) {
				m.put("update_datetime",now);//修改时间
				m.put("updater_id",uid);//修改人
		} else {
			m.put("creater_id",uid);//创建人
			m.put("create_datetime",now);//创建时间
		}
	}
	protected Model<M> get(String id) {
		if(StringUtils.isEmpty(id))
			return null;
		try {
			Model<M>	model_ = modelClass.newInstance();
			return model_.findById(id);
		} catch (Exception e) {log.error("查询Model异常，id："+id,e);}
		return null;
	}

	/**查询Model返回json*/
	public void qryOp(){
		id=this.getPara(0);
		if(StringUtils.isEmpty(id)){
			id=this.getPara("id");
		}
		Model<M> m=get(id);
		if(m!=null)
			this.rendJson(true,null,"",m);
		else
			this.rendJson(false,null,"记录不存在！");
	}
	@SuppressWarnings("unchecked")
	protected M getModel() throws Exception {
		return (M)getModel2(modelClass);
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Model getModel2(Class clz) throws Exception  {
		HttpServletRequest request = this.getRequest();
		try{
			Model model_ = (Model) clz.newInstance();
			Table tableInfo = TableMapping.me().getTable(clz);
			model_.getAttrNames();
			Enumeration<String> attrNames = request.getParameterNames();
			while(attrNames.hasMoreElements()) {
				String attr = attrNames.nextElement();
				Class<?> colType = null;
				if (tableInfo.hasColumnLabel(attr.toLowerCase()))
					colType = tableInfo.getColumnType(attr.toLowerCase());
				if (tableInfo.hasColumnLabel(attr.toUpperCase())) {
					colType = tableInfo.getColumnType(attr.toUpperCase());
				}
				if (colType != null) {
					String value = request.getParameter(attr);
					model_.set(attr.toLowerCase(),
							value != null ? TypeConverter.convert(colType, value): null);
				}
			}
			/**从request流中取json*/
			if(model_.getAttrValues()==null||model_.getAttrValues().length==0){
				String json = Tools.inputStream2String(this.getRequest().getInputStream());
				model_=JSON.parseObject(json,model_.getClass());
			}
			return model_;
		}catch(Exception e){
			throw e;
		}
	}
	/**组织成树
	 * 
	 * @param id
	 * @param list
	 * @param list_tree
	 */
	protected void dotree(String id,List<Record> list,List<Record> list_tree,int level,boolean fill){
		for(Record r:list){
			String id_=r.getStr("id");
			String pid=r.getStr("pid");
			if(id==pid||(id!=null&&id.equals(pid))||(id==null&&"".equals(pid))){
				if(level>0){
					StringBuffer sb=new StringBuffer();
					if(fill){
						for(int i=1;i<level;i++){
							sb.append("&nbsp;&nbsp;");
						}
						if(level>0){
							sb.append("├--");
						}
					}
					r.set("name", sb+r.getStr("name"));
				}
				list_tree.add(r);
				dotree(id_,list,list_tree,level+1,fill);
			}
		}
	}
}
