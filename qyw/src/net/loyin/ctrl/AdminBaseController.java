package net.loyin.ctrl;

import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.loyin.jfinal.TypeConverter;
import net.loyin.util.Tools;

import com.alibaba.fastjson.JSON;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.TableMapping;

/**
 * 后台管理基础Controller 只提供qryOp save del dataGrid方法
 * @author 刘声凤 2012-9-3 下午10:37:28
 */
public abstract class AdminBaseController<M extends Model<M>> extends BaseController<M> {
	
	public void del(){this.rendJson(false,null,"不可操作！");}
	/**
	 * 设置操作人员id及操作时间
	 * @param m
	 * @param uid
	 * @param type 0： 修改 1 ：删除
	 */
	protected void pullUser(Model<M> m,String uid,int type) {
		Date date=new Date();
		String now=dateTimeFormat.format(date);
		if (id!=null) {
			if(type==0){
				m.put("update_datetime",now);//修改时间
				m.put("updater_id",uid);//修改人
			}else{
				m.put("delete_datetime",now);//删除时间
				m.put("delete_id",uid);//删除人
			}
		} else {
			m.put("creater_id",uid);//创建人
			m.put("create_datetime",now);//创建时间
		}
	}
	protected Model<M> get(Long id) {
		try {
			Model<M>	model_ = modelClass.newInstance();
			return model_.findById(id);
		} catch (Exception e) {log.error("查询Model异常，id："+id,e);}
		return null;
	}

	/**查询Model返回json*/
	public void qryOp(){
		getId();
		Model<M> m=get(id);
		if(m!=null)
			this.rendJson(true,null,"",m);
		else
			this.rendJson(false,null,"记录不存在！");
	}
	@SuppressWarnings("unchecked")
	protected Model<M> getModel() throws Exception {
		return getModel2(modelClass);
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
			if(id==r.getStr("pid")||(id!=null&&id.equals(pid))){
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
