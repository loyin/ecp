package net.loyin.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.loyin.jfinal.anatation.TableBind;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
/**
 * 用户
 * @author 龙影
 */
@TableBind(name="users")
public class User extends Model<User> {
	private static final long serialVersionUID = 3983654365673819205L;
	public static final String tableName="users";
	public static User dao=new User();
	/**
	 * 分页查询
	 * @param pageNo
	 * @param pageSize
	 * @param where 查询条件
	 * @param param 参数
	 * @return
	 */
	public Page<User> pageGrid(Integer pageNo,Integer pageSize,Map<String,Object> filter){
		StringBuffer sql=new StringBuffer();
		List<Object> parame=new ArrayList<Object>();
		return dao.paginate(pageNo,pageSize,"select  u.id,u.uname,ps.realname,ps.email,ps.mobile,ps.sex,ps.head_pic,u.status,u.reg_date,u.last_login_time,u.login_ip,p.name as position_name,d.name as department_name",
				sql.toString(), parame.toArray());
	}
	public User qryLoginUser(String id) {
		List<User>list= this.findByCache("user",id, "select * from "+tableName+" where id=?",id);
		if(list!=null&&list.isEmpty()==false)
		return list.get(0);
		return null;
	}
	public User login(String userno,String password){
		return this.findFirst("select u.* from "+tableName+" u where  sn=? and pwd1=?",userno,password);
	}
	/**更新登录信息*/
	public void upLogin(String id, String ip,String nowStr) {
		Db.update("update "+tableName +" set last_login_time=login_time,last_login_ip=login_ip,login_count=login_count+1,login_time=?,login_ip=? where id=? ",nowStr, ip,id);
	}
	@Before(Tx.class)
	public void del(String id){
		if (StringUtils.isNotEmpty(id)) {
			Db.update("delete  from " + tableName + " where id in ("+ id+ ")");
		}
	}
}
