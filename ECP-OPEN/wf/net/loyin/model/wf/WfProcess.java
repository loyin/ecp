package net.loyin.model.wf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import net.loyin.jfinal.anatation.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

/**
 * 流程定义 
 * 增加company_id
 * @author 龙影
 */
@TableBind(name = "wf_process")
public class WfProcess extends Model<WfProcess> {
	private static final long serialVersionUID = -5081919728844431708L;
	public static final String tableName = "wf_process";
	public static WfProcess dao=new WfProcess();
	/**
	 * 分页查询部署的流程
	 * @param pageNumber
	 * @param pageSize
	 * @param filter
	 * @return
	 */
	public Page page(int pageNumber,int pageSize,Map<String,Object> filter){
		StringBuffer sql=new StringBuffer(" from ");
		sql.append(tableName);
		List<Object> parame=new ArrayList<Object>();
		if(filter.keySet().isEmpty()==false){
			sql.append(" where 1=1 ");
			if(StringUtils.isNotEmpty((String)filter.get("company_id"))){
				sql.append(" and company_id=?");
				parame.add(filter.get("company_id"));
			}
		}
		return dao.paginate(pageNumber, pageSize, "select * ",sql.toString(),parame.toArray());
	}
	/**
	 * 更新企业id
	 * @param id
	 * @param company_id
	 */
	public void upCompanyId(String id,String company_id){
		Db.update("update "+tableName+" set company_id=? where id=?",company_id,id);
	}
}
