package net.loyin.model.fa;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.loyin.ctrl.BaseController;
import net.loyin.jfinal.anatation.TableBind;
import net.loyin.model.sso.Person;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
/**
 * 账号交易明细
 * @author 龙影
 * 2014年10月9日
 */
@TableBind(name="fa_account_detail")
public class AccountDetail extends Model<AccountDetail> {
	private static final long serialVersionUID = -3019567867083004213L;
	public static final String tableName="fa_account_detail";
	public static AccountDetail dao=new AccountDetail();
	public Page<AccountDetail> pageGrid(int pageNo, int pageSize,Map<String, Object> filter){
		List<Object> parame=new ArrayList<Object>();
		StringBuffer sql=new StringBuffer();
		sql.append(" from ");
		sql.append(tableName);
		sql.append(" t left join ");
		sql.append(Person.tableName);
		sql.append(" c on c.id=t.creater_id where c.company_id=? and t.account_id=? ");
		parame.add(filter.get("company_id"));
		parame.add(filter.get("account_id"));
		sql.append(" order by t.create_datetime desc ");
		return this.paginate(pageNo, pageSize, "select t.*,c.realname creater_name ",sql.toString(),parame.toArray());
	}
	
	/**
	 * @param amt_in 转入金额
	 * @param amt_out 转出金额
	 * @param account_id 账号
	 * @param relation_id 关联来往单位
	 * @param creater_id 操作员
	 * @param remark 备注
	 * @throws Exception 
	 */
	public void install(BigDecimal amt_in,BigDecimal amt_out,String account_id,String relation_id ,String creater_id,String remark) throws Exception{
		if(amt_in==null&&amt_out==null){
			throw new Exception("转入及转出金额不能都为空值！");
		}
		AccountDetail po=new AccountDetail();
		BigDecimal zero=new BigDecimal(0);
		if(amt_in!=null){
			if(amt_in.compareTo(zero)>=0){
				po.set("amt_in",amt_in);
			}else{
				po.set("amt_out",amt_in.abs());//转换成对应的正数
			}
		}
		if(amt_out!=null){
			if(amt_out.compareTo(zero)>=0){
				po.set("amt_out",amt_out);
			}else{
				po.set("amt_in",amt_out.abs());//转换成对应的正数
			}
		}
		po.set("account_id",account_id);
		po.set("creater_id",creater_id);
		po.set("relation_id",relation_id);
		po.set("remark",remark);
		po.set("create_datetime",BaseController.dateTimeFormat.format(new Date()));
		po.save();
		String id=po.getStr("id");
		//更新明细的余额信息
		Db.update("update "+tableName+" set balance="+Account.tableName+".amt from "+Account.tableName+" where "+Account.tableName+".id=? and "+Account.tableName+".id=account_id and "+tableName+".id=?",account_id,id);
	}
}
