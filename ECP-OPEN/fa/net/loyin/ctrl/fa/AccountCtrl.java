package net.loyin.ctrl.fa;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.loyin.ctrl.AdminBaseController;
import net.loyin.jfinal.anatation.PowerBind;
import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.model.fa.Account;
import net.loyin.model.fa.AccountDetail;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 * 账户
 * @author 龙影
 */
@RouteBind(path = "account",sys="财务",model="账户")
public class AccountCtrl extends AdminBaseController<Account> {
	public AccountCtrl() {
		this.modelClass = Account.class;
	}
	public void dataGrid(){
		List<Record> list=Account.dao.qryList(this.getCompanyId());
		List<Record> list_temp=new ArrayList<Record>();
		if(list!=null&&list.isEmpty()==false){
			dotree(null,list,list_temp,0,true);
		}
		Page<Record> page=new Page<Record>(list_temp,1,0,0,(list==null||list.isEmpty())?0:list.size());
		this.rendJson(true,null, "",page);
	}
	public void rptList(){
		Map<String,Object> filter=new HashMap<String,Object>();
		filter.put("account_id",this.getPara("account_id"));
		filter.put("start_date",this.getPara("start_date"));
		filter.put("end_date",this.getPara("end_date"));
		filter.put("company_id",this.getCompanyId());
		List<Record>list=new ArrayList<Record>();
		List<Record> dataList=Account.dao.rptList(filter);
		int i=0;
		String accountName="";
		BigDecimal amt_in_t=new BigDecimal(0),amt_out_t=new BigDecimal(0),balance=new BigDecimal(0);
		
		if(dataList!=null&&dataList.isEmpty()==false){
			for(Record r:dataList){
				String actname=r.getStr("name");
				BigDecimal amt_in=r.getBigDecimal("amt_in");
				BigDecimal amt_out=r.getBigDecimal("amt_out");
				BigDecimal balance_=r.getBigDecimal("balance");
				if(amt_in==null)
					amt_in=new BigDecimal(0);
				if(amt_out==null)
					amt_out=new BigDecimal(0);
				if(i==0){
					accountName=actname;
				}
				if(i>0&&accountName.equals(actname)==false){
					accountName=actname;
					Record r_=new Record();
					r_.set("account","");
					r_.set("remark","");
					r_.set("customser_name","");
					r_.set("create_date","");
					r_.set("ordersn","");
					r_.set("balance",balance);
					r_.set("amt_in",amt_in_t);
					r_.set("amt_out",amt_out_t);
					r_.set("name","小计");
					list.add(r_);
					amt_in_t=new BigDecimal(0);
					amt_out_t=new BigDecimal(0);
				}
				balance=balance_;
				amt_in_t=amt_in_t.add(amt_in);
				amt_out_t=amt_out_t.add(amt_out);
				list.add(r);
				i++;
			}
			Record r_=new Record();
			r_.set("account","");
			r_.set("remark","");
			r_.set("customser_name","");
			r_.set("create_date","");
			r_.set("ordersn","");
			r_.set("balance",balance);
			r_.set("amt_in",amt_in_t);
			r_.set("amt_out",amt_out_t);
			r_.set("name","小计");
			list.add(r_);
		}
		this.rendJson(true, null, "",list);
	}
	@PowerBind(code="A7_1_E",funcName="编辑")
	public void save() {
		try {
			Account po =  (Account) getModel();
			if (po == null) {
				this.rendJson(false,null, "提交数据错误！");
				return;
			}
			this.getId();
			if (StringUtils.isEmpty(id)) {
				po.set("company_id", this.getCompanyId());
				po.save();
				id=po.getStr("id");
			} else {
				po.update();
			}
			this.rendJson(true,null, "操作成功！",id);
		} catch (Exception e) {
			log.error("保存用户异常", e);
			this.rendJson(false,null, "保存数据异常！");
		}
	}
	@PowerBind(code="A7_1_E",funcName="删除")
	public void del() {
		try {
			getId();
			Account.dao.del(id,this.getCompanyId());
			rendJson(true,null, "删除成功！",id);
		} catch (Exception e) {
			log.error("删除异常", e);
			rendJson(false,null,"删除失败！");
		}
	}
	public void qryOp() {
		getId();
		Account m = Account.dao.findById(id,this.getCompanyId());
		if (m != null)
			this.rendJson(true,null, "", m);
		else
			this.rendJson(false,null, "记录不存在！");
	}
	/**帐号交易明细*/
	public void detailDg(){
		Map<String,Object> filter=new HashMap<String,Object>();
		filter.put("company_id",this.getCompanyId());
		filter.put("account_id",this.getPara("account_id"));
		this.rendJson(true, null, "",AccountDetail.dao.pageGrid(getPageNo(), getPageSize(), filter));
	}
}
