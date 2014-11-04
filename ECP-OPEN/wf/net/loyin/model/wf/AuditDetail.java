package net.loyin.model.wf;

import java.util.List;

import net.loyin.jfinal.anatation.TableBind;
import net.loyin.model.sso.Person;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.tx.Tx;

/**
 * 审核记录
 * @author 龙影
 * 2014年10月10日
 */
@TableBind(name = "wf_audit_detail")
public class AuditDetail extends Model<AuditDetail> {
	private static final long serialVersionUID = -5081919728844431708L;
	public static final String tableName = "wf_audit_detail";
	public static AuditDetail dao=new AuditDetail();
	public List<AuditDetail> list(String id,String company_id){
		return this.find("select a.*,p.realname auditor_name from "+tableName+" a,"+Person.tableName+" p where p.id=a.auditor_id and p.company_id=? and a.id=?",company_id,id);
	}
	@Before(Tx.class)
	public void save(AuditDetail po, String tablename2) {
		po.save();
		Db.update("update "+tablename2+" set audit_status=?,auditor_id=?,audit_datetime=? where id=?",po.getInt("audit_status"),po.getStr("auditor_id"),po.getStr("audit_datetime"),po.getStr("id"));
	}
}
