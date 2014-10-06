package net.loyin.ctrl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.kit.StringKit;
import net.loyin.model.User;
import net.loyin.util.PropertiesContent;
import net.loyin.util.safe.CipherUtil;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.ClearInterceptor;
import com.jfinal.aop.ClearLayer;
/**
 *首页调用及登录退出 
 * @author 龙影
 */
@SuppressWarnings("rawtypes")
@RouteBind(path="/",name="",sys="",model="",code="")
@ClearInterceptor
public class IndexCtrl extends BaseController {

	public void login(){
		Map<String,Object> attrs=getJsonAttrs();
		String userno=(String)attrs.get("username");
		String pwd=(String)attrs.get("password");
		String vcode=(String)attrs.get("vcode");
		if(StringUtils.isEmpty(userno)||StringUtils.isEmpty(pwd)||StringUtils.isEmpty(vcode)){
			this.rendJson(false, null, "帐号、密码、验证码未填写！");
			return;
		}
		String code=this.getCookie("ValidCode");
		if(StringUtils.isEmpty(code)){
			this.rendJson(false, null, "验证码已失效，请刷新验证码图片！");
			return;
		}
		this.setCookie("ValidCode","",1);
		code=CipherUtil.decryptData(code);
		if(vcode.equals(code)==false){
//			this.rendJson(false, null, "验证码不匹配！");
//			return;
		}
			User m=User.dao.login(userno, pwd);
			if(m!=null){
				String uid=m.getStr("id");
				String ip = this.getRequest().getHeader("X-Real-IP");
				if(StringUtils.isEmpty(ip)){
					ip=this.getRequest().getRemoteAddr();
				}
				String nowStr=dateTimeFormat.format(new Date());
				m.upLogin(uid,ip,nowStr);
				int maxTime=1800;
				boolean autoLogin=StringKit.toBool((String)attrs.get("autoLogin"),false);
				this.setCookie("autoLogin", autoLogin+"", 604800);
				if(autoLogin){
					maxTime=604800;//保持一星期
				}
				Map<String,Object> userMap=new HashMap<String,Object>();
				userMap.put("loginTime",nowStr);
				userMap.put("ip",ip);
				userMap.put("uid",uid);
				this.setCookie(PropertiesContent.get("cookie_field_key"),CipherUtil.encryptData(JSON.toJSONString(userMap)),maxTime);
				this.rendJson(true,null, "登录成功");
				return;
			}else{
				this.rendJson(false,null,"用户名或密码错误！");
				return;
			}
	}
	@ClearInterceptor(ClearLayer.ALL)
	public void logout(){
		this.removeCookie(PropertiesContent.get("cookie_field_key"));
		this.removeCookie("autoLogin");
		this.redirect("/");
	}
	public void qryLoginInfo(){
		User u=this.getCurrentUser();
		this.rendJson(u!=null, null, "",u);
	}
}
