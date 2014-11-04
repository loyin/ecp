package net.loyin.model.sso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.loyin.jfinal.anatation.TableBind;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
/**
 * 每日一语
 * @author 龙影
 */
@TableBind(name="sso_daily_phrase")
public class DailyPhrase extends Model<DailyPhrase> {
	private static final long serialVersionUID = 3217131008155761762L;
	public static final String tableName="sso_daily_phrase";
	public static DailyPhrase dao=new DailyPhrase();
	public Page<DailyPhrase> pageGrid(int pageNo, int pageSize,
			StringBuffer where, List<Object> param) {
		return dao.paginate(pageNo,pageSize,"select  * ","from " + tableName + " d where 1=1 "+ where.toString(), param.toArray());
	}
	public void del(String id,String company_id) {
		if (StringUtils.isNotEmpty(id)) {
			String[] ids=id.split(",");
			StringBuffer ids_=new StringBuffer();
			List<String> parame=new ArrayList<String>();
			for(String id_:ids){
				ids_.append("?,");
				parame.add(id_);
			}
			ids_.append("'-'");
			parame.add(company_id);
			Db.update("delete  from " + tableName + " where id in ("+ids_.toString()+") and company_id=?",parame.toArray());
		}
	}
	/***
	 * 获取随机一条
	 * @param companyId
	 * @return
	 */
	public DailyPhrase random(String company_id) {
		List<DailyPhrase> list=this.find("select * from "+tableName+" where company_id=? order by sort_num asc",company_id);
		if(list==null||list.isEmpty()){
			return null;
		}
		Random random=new Random();
		return list.get(random.nextInt(list.size()));
	}
}
