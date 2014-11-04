package net.loyin.model.sso;

import java.util.ArrayList;
import java.util.List;

import net.loyin.jfinal.anatation.TableBind;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
/**
 * 用户
 * @author 龙影
 */
@TableBind(name="sso_user")
public class User extends Model<User> {
	private static final long serialVersionUID = -5301851381511273243L;
	public static final String tableName="sso_user";
	public static User dao=new User();
	
	/**
	 * 分页查询
	 * @param pageNo
	 * @param pageSize
	 * @param where 查询条件
	 * @param param 参数
	 * @return
	 */
	public Page<User> pageGrid(Integer pageNo,Integer pageSize,StringBuffer where,List<Object> param){
		return dao.paginate(pageNo,pageSize,"select  u.id,u.uname,ps.realname,ps.email,ps.mobile,ps.sex,ps.head_pic,u.status,u.reg_date,u.last_login_time,u.login_ip,p.name as position_name,d.name as department_name",
				"from " + tableName + " u, "+Position.tableName+" p ,"+Position.tableName+" d ,"+Person.tableName+" ps where  u.id=ps.id and d.id=p.department_id and u.position_id=p.id  "+ where.toString(), param.toArray());
	}
	public User qryLoginUser(String id) {
		List<User>list= this.findByCache("user",id, "select u.*,p.* from "+tableName+" u,"+Person.tableName+" p where u.id=p.id and u.id=?",id);
		if(list!=null&&list.isEmpty()==false)
		return list.get(0);
		return null;
	}
	public User login(String userno,String password,String companyId){
		return this.findFirst("select u.*,per.*,p.name position_name,p.department_id,de.name department_name from "+tableName+" u,"+Position.tableName+" p,"+Position.tableName+" de,"+Person.tableName+" per where per.id=u.id and de.id=p.department_id and p.id=u.position_id and uname=? and password=? and u.company_id=? ",userno,password,companyId);
	}
	public User findById(String id,String company_id){
		return User.dao.findFirst("select  u.*,ps.realname,ps.email,ps.mobile,ps.sex,ps.head_pic,ps.telephone,ps.address,p.name as position_name,d.name as department_name from " + tableName + " u, "+Position.tableName+" p ,"+Position.tableName+" d ,"+Person.tableName+" ps where  u.id=ps.id and d.id=p.department_id and u.position_id=p.id and u.id=? and u.company_id=?",id,company_id);
	}
	/**更新登录信息*/
	public void upLogin(String id, String ip,String nowStr) {
		Db.update("update "+tableName +" set last_login_time=?,login_ip=? where id=? ",nowStr, ip,id);
	}
	/**禁用或激活用户*/
	public void disable(String id,String company_id) {
		Db.update("update "+tableName+" set status=case when status=0 then 1 else 0 end where id=? and company_id=?",id,company_id);
	}
	@Before(Tx.class)
	public void del(String id,String company_id){
		if (StringUtils.isNotEmpty(id)) {
			String[] ids=id.split(",");
			StringBuffer ids_=new StringBuffer();
			List<String> parame=new ArrayList<String>();
			for(String id_:ids){
				ids_.append("?,");
				parame.add(id_);
			}
			ids_.append("'-'");
			Db.update("delete  from " + Person.tableName + " where id in ("+ ids_.toString()+ ")",parame.toArray());
			parame.add(company_id);
			Db.update("delete  from " + tableName + " where id in ("+ ids_.toString()+ ") and company_id=? ",parame.toArray());
		}
	}
	/**
	 * 用于用户树组织
	 * @param position_id 岗位id
	 * @param company_id 企业 
	 * @param type 0:全部  1：上级 2：下级 
	 * @return
	 */
	public List<Record> list4tree(String position_id,String company_id,int type) {
		List<Record> list=null;
		switch(type){
		case 0://全部
			String sql = "WITH RECURSIVE d AS (SELECT d1.id,d1.pid,d1.name,d1.sort_num,d1.type FROM v_user_position d1 where d1.company_id=?"
					+ "union ALL SELECT d2.id,d2.pid,d2.name,d2.sort_num,d2.type FROM v_user_position d2, d WHERE d2.id = d.pid) "
					+ "SELECT distinct * FROM d";
			list=Db.find(sql,company_id);
			break;
		case 1://上级
			sql = "WITH RECURSIVE d AS (SELECT d1.id,d1.pid,d1.name,d1.sort_num,d1.type FROM v_user_position d1 where d1.id=?"
					+ "union ALL SELECT d2.id,d2.pid,d2.name,d2.sort_num,d2.type FROM v_user_position d2, d WHERE d2.id = d.pid) "
					+ "SELECT distinct * FROM d";
			list=Db.find(sql,position_id);
			break;
		case 2://下级
			sql = "WITH RECURSIVE d AS (SELECT d1.id,d1.pid,d1.name,d1.sort_num,d1.type FROM v_user_position d1 where d1.id=?"
					+ "union ALL SELECT d2.id,d2.pid,d2.name,d2.sort_num,d2.type FROM v_user_position d2, d WHERE d2.pid = d.id) "
					+ "SELECT distinct * FROM d";
			list=Db.find(sql,position_id);
			break;
		}
		return list;
	}
	/**
	 * 上下级用户
	 * @param u_id 当前用户id
	 * @param position_id 岗位id
	 * @param company_id 企业 
	 * @param type 0:全部  1：上级 2：下级 
	 * @return
	 */
	public List<Record> list(String uid,String position_id,String company_id,int type) {
		List<Record> list=null;
		switch(type){
		case 0://全部
			String sql = "WITH RECURSIVE d AS (SELECT d1.id,d1.pid,d1.name,d1.sort_num,d1.type FROM v_user_position d1 where d1.company_id=?"
					+ "union ALL SELECT d2.id,d2.pid,d2.name,d2.sort_num,d2.type FROM v_user_position d2, d WHERE d2.id = d.pid) "
					+ "SELECT distinct * FROM d where type=10";
			list=Db.find(sql,company_id);
			break;
		case 1://上级
			sql = "WITH RECURSIVE d AS (SELECT d1.id,d1.pid,d1.name,d1.sort_num,d1.type FROM v_user_position d1 where d1.id=?"
					+ "union ALL SELECT d2.id,d2.pid,d2.name,d2.sort_num,d2.type FROM v_user_position d2, d WHERE d2.id = d.pid) "
					+ "SELECT distinct * FROM d where type=10";
			list=Db.find(sql,position_id);
			break;
		case 2://下级
			sql = "WITH RECURSIVE d AS (SELECT d1.id,d1.pid,d1.name,d1.sort_num,d1.type FROM v_user_position d1 where d1.id=?"
					+ "union ALL SELECT d2.id,d2.pid,d2.name,d2.sort_num,d2.type FROM v_user_position d2, d WHERE d2.pid = d.id) "
					+ "SELECT distinct id FROM d where type=10 and d.id not in (select id from sso_user where position_id =? and id!=?)";
			list=Db.find(sql,position_id,position_id,uid);
			break;
		}
		return list;
	}
	/**获取用户的上级*/
	public User findSupUser(String uid) {
		return findFirst("select u.* from "+tableName+" u,"+Position.tableName+" p,"+tableName+" u1 where p.id=u1.position_id and u1.id=? and u.position_id=p.parent_id ",uid);
	}
	/**检查密码*/
	public boolean chckPwd(String pwd,String uid) {
		User u=this.findFirst("select * from "+tableName+" where id=? and password=?",uid,pwd);
		return u==null;
	}
	/**修改密码*/
	public void upPwd(String pwd, String uid) {
		Db.update("update "+tableName+" set password=? where id=?",pwd,uid);
	}
}
