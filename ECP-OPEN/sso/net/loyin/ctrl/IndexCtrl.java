package net.loyin.ctrl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.model.sso.Company;
import net.loyin.model.sso.User;
import net.loyin.util.PropertiesContent;
import net.loyin.util.safe.CipherUtil;
import net.loyin.util.safe.MD5;
import net.loyin.validator.LoginValid;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Before;
import com.jfinal.aop.ClearInterceptor;
import com.jfinal.aop.ClearLayer;
/**
 *首页调用及登录退出 
 * @author 龙影
 */
@SuppressWarnings("rawtypes")
@RouteBind(path="/",name="",sys="",model="",code="")
public class IndexCtrl extends BaseController {
	public void index() {
		String uid= this.getCurrentUserId();
		if(StringUtils.isEmpty(uid)){
			this.renderHTML("login");
			return;
		}
		super.index();
	}

	@Before(LoginValid.class)//不生效 需要优化
	public void login(){
		String userno=this.getPara("userno");
		String pwd=this.getPara("pwd");
		String companyName=this.getPara("company");
		Company company=Company.dao.qryCompanyByName(companyName);
		if(company==null){
			rendJson(false,null, "公司不存在！");
			return;
		}
			pwd=MD5.getMD5ofStr(pwd);
			User m=User.dao.login(userno, pwd,company.getStr("id"));
			if(m!=null){
				String uid=m.getStr("id");
				String ip = this.getRequest().getHeader("X-Real-IP");
				if(StringUtils.isEmpty(ip)){
					ip=this.getRequest().getRemoteAddr();
				}
				int status=m.getInt("status");
				if(status==0){
					this.rendJson(false,null,"此用户被禁用，请联系公司管理员！");
					return;
				}
				String nowStr=dateTimeFormat.format(new Date());
				m.upLogin(m.getStr("id"),ip,nowStr);
				int maxTime=1800;
				boolean autoLogin=this.getParaToBoolean("autoLogin",false);
				this.setCookie("autoLogin", autoLogin+"", 604800);
				if(autoLogin){
					maxTime=604800;//保持一星期
				}
				Map<String,Object> userMap=new HashMap<String,Object>();
				userMap.put("loginTime",nowStr);
				userMap.put("ip",ip);
				userMap.put("uid",uid);
				userMap.put("company_id",company.getStr("id"));
				userMap.put("company_name",company.getStr("name"));
				userMap.put("position_id",m.get("position_id"));
				userMap.put("position_name",m.get("position_name"));
				userMap.put("department_id",m.get("department_id"));
				userMap.put("department_name",m.get("department_name"));
				this.setCookie(PropertiesContent.get("cookie_field_key"),CipherUtil.encryptData(JSON.toJSONString(userMap)),maxTime);
				this.rendJson(true,null, "登录成功");
				return;
			}else{
				this.rendJson(false,null, "用户名或密码错误！");
				return;
			}
	}
	@ClearInterceptor(ClearLayer.ALL)
	public void logout(){
		this.removeCookie(PropertiesContent.get("cookie_field_key"));
		this.removeCookie("autoLogin");
//		this.renderHTML("login");
		this.redirect("/");
	}
}
