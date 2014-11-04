package net.loyin.model.oa;

import java.util.ArrayList;
import java.util.List;

import net.loyin.jfinal.anatation.TableBind;
import net.loyin.model.sso.Person;
import net.loyin.model.sso.User;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

/**
 * 评论
 * 
 * @author 龙影 2014年9月19日
 */
@TableBind(name = "oa_comment")
public class Comment extends Model<Comment> {
	private static final long serialVersionUID = -98977900492538706L;
	public static final String tableName = "oa_comment";
	public static Comment dao = new Comment();

	public void del(String id, String relation_id) {
		if (StringUtils.isNotEmpty(id)) {
			String[] ids=id.split(",");
			StringBuffer ids_=new StringBuffer();
			List<String> parame=new ArrayList<String>();
			for(String id_:ids){
				ids_.append("?,");
				parame.add(id_);
			}
			ids_.append("'-'");
			parame.add(relation_id);
			StringBuffer sql = new StringBuffer("delete  from ");
			sql.append(tableName);
			sql.append(" where id in ('");
			sql.append(ids_);
			sql.append("') and relation_id=? ");
			Db.update(sql.toString(),parame.toArray());
		}
	}

	public Page<Comment> pageGrid(int pageNo, int pageSize, String relation_id) {
		StringBuffer sql = new StringBuffer(" from ");
		sql.append(tableName);
		sql.append(" t,");
		sql.append(User.tableName);
		sql.append(" u,");
		sql.append(Person.tableName);
		sql.append(" p where p.id=u.id and u.id=t.creater_id and t.relation_id=? order by t.create_datetime desc");
		return dao.paginate(pageNo, pageSize,
				"select t.*,p.realname,u.uname,u.head_img ", sql.toString(),
				relation_id);
	}
}
