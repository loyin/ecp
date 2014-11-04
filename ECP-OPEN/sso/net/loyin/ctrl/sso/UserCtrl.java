package net.loyin.ctrl.sso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.loyin.ctrl.AdminBaseController;
import net.loyin.jfinal.anatation.PowerBind;
import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.model.crm.Customer;
import net.loyin.model.em.SaleGoal;
import net.loyin.model.fa.PayReceivAbles;
import net.loyin.model.scm.Order;
import net.loyin.model.sso.Company;
import net.loyin.model.sso.Person;
import net.loyin.model.sso.Position;
import net.loyin.model.sso.User;
import net.loyin.util.safe.MD5;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 * 企业用户
 * @author 龙影
 */
@RouteBind(sys = "设置", path = "user",model= "用户")
public class UserCtrl extends AdminBaseController<User> {
	public UserCtrl() {
		this.modelClass = User.class;
	}
	public void dataGrid() {
		String keyword=this.getPara("keyword");
		List<Object> param = new ArrayList<Object>();
		StringBuffer where = new StringBuffer();
		/** 添加查询字段条件 */
		this.qryField(where, param);
		if(StringUtils.isNotEmpty(keyword)){
			keyword= "%"+keyword+"%";
			where.append(" and ( u.uname like ? or  ps.realname like ?)");
			param.add(keyword);
			param.add(keyword);
		}
		where.append(" and u.company_id=?");
		param.add(this.getCompanyId());
		this.jqFilters(where, param);
		sortField(where);
		Page<User> page = User.dao.pageGrid(getPageNo(), getPageSize(), where,param);
		this.rendJson(true,null, "success", page);
	}

	public void qryOp() {
		getId();
		User m = User.dao.findById(id,this.getCompanyId());
		if (m != null){
			m.set("password","");
			this.rendJson(true,null, "", m);
		}
		else
			rendJson(false,null, "记录不存在！");
	}
	@PowerBind(code="A11_1_E",funcName="编辑")
	public void save() {
		try {
			User po = (User) getModel();
			if (po == null) {
				this.rendJson(false,null, "提交数据错误！");
				return;
			}
			String password = po.getStr("password");
			if (StringUtils.isNotEmpty(password)) {
				po.set("password", MD5.getMD5ofStr(password));
			} else {
				po.remove("password");
			}
			id=po.getStr("id");
			Person person=(Person)this.getModel2(Person.class);
			if (StringUtils.isEmpty(id)) {
				po.set("reg_date", dateFormat.format(new Date()));
				String company_id=this.getCompanyId();
				po.set("company_id",company_id);
				po.save();
				id=po.getStr("id");
				person.set("id",id);
				person.set("company_id",company_id);
				person.save();
			} else {
				po.update();
				person.set("id",id);
				person.update();
			}
			this.rendJson(true,null, "操作成功！",id);
		} catch (Exception e) {
			log.error("保存用户异常", e);
			this.rendJson(false,null, "保存数据异常！");
		}
	}
	public void person(){
		this.setAttr("uid",this.getCurrentUserId());
	}
	/**保存个人设置*/
	public void savePersonSet(){
		try {
			User po = (User) getModel();
			if (po == null) {
				this.rendJson(false,null, "提交数据错误！");
				return;
			}
			String password = po.getStr("password");
			if (StringUtils.isNotEmpty(password)) {
				po.set("password", MD5.getMD5ofStr(password));
			} else {
				po.remove("password");
			}
			id=po.getStr("id");
			Person person=(Person)this.getModel2(Person.class);
			po.update();
			person.set("id",id);
			person.update();
			this.rendJson(true,null, "操作成功！",id);
		} catch (Exception e){
			log.error("保存个人设置异常", e);
			this.rendJson(false,null, "保存数据异常！");
		}
	}
	@PowerBind(code="A11_1_E",funcName="删除")
	public void del() {
		try {
			getId();
			User.dao.del(id,this.getCompanyId());
			rendJson(true,null,"删除成功！",id);
		} catch (Exception e) {
			log.error("删除异常", e);
			rendJson(false,null,"删除失败！");
		}
	}
	/** 获取登录信息 */
	public void loginInfo() {
		String uid = this.getCurrentUserId();
		String company_id = this.getCompanyId();
		try {
			User user = User.dao.findById(uid,company_id);
			if (user == null) {
				this.rendJson(false,null, "用户记录不存在！");
				return;
			}else if(user.getInt("status")==0){
				this.rendJson(false,null, "用户已经禁用了！");
				return;
			}
			Date now=new Date();
			Company company = Company.dao.qryById(company_id);
			if(company==null){
				this.rendJson(false,null, "企业记录不存在！");
				return;
			}else if(company.getInt("status")==0){
				this.rendJson(false,null, "企业已经禁用了！");
				return;
			}
			company.put("isExpired",now.compareTo(dateFormat.parse(company.getStr("expiry_date")))>0);//是否过期
			Map<String, Object> data = new HashMap<String, Object>();
//			user.remove("id");
//			user.remove("company_id");
			user.remove("password");
			user.remove("status");
//			company.remove("id");
			company.remove("code");
			company.remove("status");
			String config=company.getStr("config");//获取企业自定义配置项
			if(StringUtils.isNotEmpty(config)){
				Map<String,Object> cf=(Map<String,Object>)JSON.parse(config);
				company.set("config",cf);
			}
			data.put("user", user);
			data.put("company", company);
			data.put("date",dateFormat.format(new Date()));
			//查询岗位权限
			Position position=Position.dao.findById(user.getStr("position_id"));
			String permission_=position.getStr("permission");
			data.put("permission",permission_);
			if(StringUtils.isNotEmpty(permission_)){
				Map<String,Boolean> p=new HashMap<String,Boolean>();
				String[]ss=permission_.split(",");
				for(String s:ss){
					p.put(s, true);
				}
				data.put("rights",p);
			}else
			data.put("rights",null);
			
			Calendar cal=Calendar.getInstance();
			cal.setTime(now);
			cal.add(Calendar.DATE,-6);
			data.put("beginDate",dateFormat.format(cal.getTime()));
			data.put("endDate",dateFormat.format(now));
			this.rendJson(true,null, "", data);
		} catch (Exception e) {
			log.error("获取登录用户信息异常",e);
			this.rendJson(false,null, "获取用户登录信息异常！");
		}
	}
	/**
	 * 获取用户树  type 为10 表示人员
	 */
	public void userTree(){
		String company_id=this.getCompanyId();
		int type=this.getParaToInt("type",0);
		String position_id=getPositionId();
		List<Record> list=User.dao.list4tree(position_id,company_id,type);
		this.rendJson(true,null,"",list);
	}
	/**只含用户*/
	public void list(){
		String company_id=this.getCompanyId();
		int type=this.getParaToInt("type",0);
		String position_id=getPositionId();
		List<Record> list=User.dao.list(this.getCurrentUserId(),position_id,company_id,type);
		this.rendJson(true,null,"",list);
	}
	@PowerBind(code="A11_1_E",funcName="禁用")
	public void disable(){
		getId();
		try{
		User.dao.disable(id,this.getCompanyId());
			this.rendJson(true,null, "操作成功！", id);
		}catch(Exception e){
			this.rendJson(false,null, "操作失败！");
		}
	}
	public void mainInfo(){
		String uid=this.getCurrentUserId();
		Date now=new Date();
		Calendar cal=Calendar.getInstance();
		cal.setTime(now);
		cal.add(Calendar.DATE,-1);
		String ytd=dateFormat.format(cal.getTime());//昨天日期
		Map<String,Object> data=new HashMap<String,Object>();
		//总客户数量 （负责及创建的）
		data.put("cust_count",Customer.dao.qryCount(uid));
		//（昨天至今天）新增客户数量
		data.put("cust_newaddcount",Customer.dao.qryNewAddCount(uid,ytd));
		//应收款 （负责及创建的）
		data.put("payab_amt1",PayReceivAbles.dao.qrySumNoPay(uid,1));
		//（昨天至今天）新增应收款
		data.put("payab_newamt1",PayReceivAbles.dao.qryNewAddNoPay(uid,ytd,1));
		//应付款 （负责及创建的）
		data.put("payab_amt",PayReceivAbles.dao.qrySumNoPay(uid,0));
		//（昨天至今天）新增应付款
		data.put("payab_newamt",PayReceivAbles.dao.qryNewAddNoPay(uid,ytd,0));
		//销售额（负责及创建的）累计
		data.put("sale_amt",Order.dao.qryDealAmt(uid,null,null));
		//（昨天至今天）新增销售额
		data.put("sale_newamt",Order.dao.qryDealAmt(uid,ytd,null));
		//消息
		cal=Calendar.getInstance();
		cal.setTime(now);
		cal.set(Calendar.DATE,0);
		//本月起始日期
		String mthday=dateFormat.format(cal.getTime());
		String month=new SimpleDateFormat("M").format(now);
		String year=new SimpleDateFormat("yyyy").format(now);
		//本月成交金额
		data.put("deal_amt",Order.dao.qryDealAmt(uid,mthday,null));
		//本月回款金额
		data.put("back_amt",PayReceivAbles.dao.qryBackAmt(uid,mthday,null));
		//本月目标金额
		data.put("goal_amt",SaleGoal.dao.qryMonth(uid,month,year));
		
		this.rendJson(true, null, "", data);
	}
	/**修改密码*/
	public void savePwd(){
		String pwd=this.getPara("pwd");
		if(StringUtils.isEmpty(pwd)){
			this.rendJson(false,null,"原密码不能为空！");
			return;
		}
		String pwd1=this.getPara("pwd1");
		if(StringUtils.isEmpty(pwd1)){
			this.rendJson(false,null,"新密码不能为空！");
			return;
		}
		String pwd2=this.getPara("pwd2");
		if(StringUtils.isEmpty(pwd2)){
			this.rendJson(false,null,"确认新密码不能为空！");
			return;
		}
		if(pwd1.equals(pwd2)==false){
			this.rendJson(false,null,"确认新密码与新密码不一致！");
			return;
		}
		String uid=this.getCurrentUserId();
		pwd=MD5.getMD5ofStr(pwd);
		if(User.dao.chckPwd(pwd,uid)){
			this.rendJson(false,null,"原密码不正确！");
			return;
		}
		pwd1=MD5.getMD5ofStr(pwd1);
		User.dao.upPwd(pwd1,uid);
		this.rendJson(true, null, "修改新密码成功！请牢记新密码！");
	}
}
