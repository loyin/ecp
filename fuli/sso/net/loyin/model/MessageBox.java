package net.loyin.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.loyin.ctrl.BaseController;
import net.loyin.jfinal.anatation.TableBind;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

/**
 * 留言、私人邮箱
 * 
 * @author 龙影 2014年10月6日
 */
@TableBind(name = "message_box")
public class MessageBox extends Model<MessageBox> {
	private static final long serialVersionUID = -7482047704137777115L;
	public static final String tableName = "message_box";
	public static MessageBox dao=new MessageBox();
	public Page<MessageBox> page(int pageNo, int pageSize, Map<String, Object> filter) throws Exception {
		List<Object> parame=new ArrayList<Object>();
		StringBuffer sql=new StringBuffer(" from ");
		sql.append(tableName);
		sql.append(" where touser_id =?");
		parame.add(filter.get("user_id"));
		String date=(String)filter.get("date");
		if(StringUtils.isNotEmpty(date)){
			sql.append(" and create_datetime between ? and ?");
			String[] as=date.split("/");
			if(as[1].equals("1")){//上半月
				parame.add(as[0]+"-01 00:00:00");
				parame.add(as[0]+"-15 23:59:59");
			}else{//下半月
				Date date_=BaseController.dateFormat.parse(as[0]+"-01");
				Calendar cal=Calendar.getInstance();
				cal.setTime(date_);
				cal.set(Calendar.MONTH,cal.get(Calendar.MONTH)+1);
				cal.set(Calendar.DAY_OF_MONTH,1);
				cal.set(Calendar.DATE,cal.get(Calendar.DATE)-1);
				parame.add(as[0]+"-16 00:00:00");
				parame.add(as[0]+"-"+cal.get(Calendar.DAY_OF_MONTH)+" 23:59:59");
			}
		}
		sql.append(" order by create_datetime desc");
		return this.paginate(pageNo, pageSize, "select *",sql.toString(),parame.toArray());
	}
}
