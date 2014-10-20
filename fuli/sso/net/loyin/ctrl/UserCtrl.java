package net.loyin.ctrl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.jfinal.model.IdGenerater;
import net.loyin.model.User;

/**
 * 用户
 * 
 * @author 龙影 2014年10月8日
 */
@RouteBind(path = "user")
public class UserCtrl extends AdminBaseController<User> {
	public UserCtrl(){
		this.modelClass=User.class;
	}
	/**保存密码*/
	public void savePwd() {
		Map<String,Object> attr=this.getJsonAttrs();
		if(attr==null||attr.keySet().isEmpty()){
			this.rendJson(false,null,"未提交数据！且原一级密码不能为空！");
			return;
		}
		String pwd1=(String)attr.get("pwd1");
		String pwd2=(String)attr.get("pwd2");
		String pwd3=(String)attr.get("pwd3");
		String pwd4=(String)attr.get("pwd4");
		String pwd5=(String)attr.get("pwd5");
		String pwd6=(String)attr.get("pwd6");
		if(StringUtils.isNotEmpty(pwd1)||StringUtils.isNotEmpty(pwd2)||StringUtils.isNotEmpty(pwd3)){
			if(StringUtils.isEmpty(pwd1)){
				this.rendJson(false,null,"原一级密码不能为空！");
				return;
			}
			if(StringUtils.isEmpty(pwd2)){
				this.rendJson(false,null,"新一级密码不能为空！");
				return;
			}
			if(StringUtils.isEmpty(pwd3)){
				this.rendJson(false,null,"确认一级密码不能为空！");
				return;
			}
			if(StringUtils.isNotEmpty(pwd3)&&pwd3.equals(pwd2)==false){
				this.rendJson(false,null,"新一级密码与确认一级密码不一致！");
				return;
			}
		}
		if(StringUtils.isNotEmpty(pwd4)||StringUtils.isNotEmpty(pwd5)||StringUtils.isNotEmpty(pwd6)){
			if(StringUtils.isEmpty(pwd4)){
				this.rendJson(false,null,"原二级密码不能为空！");
				return;
			}
			if(StringUtils.isEmpty(pwd5)){
				this.rendJson(false,null,"新二级密码不能为空！");
				return;
			}
			if(StringUtils.isEmpty(pwd6)){
				this.rendJson(false,null,"确认二级密码不能为空！");
				return;
			}
			if(StringUtils.isNotEmpty(pwd6)&&pwd6.equals(pwd5)==false){
				this.rendJson(false,null,"新二级密码与确认二级密码不一致！");
				return;
			}
		}
		String uid=this.getCurrentUserId();
		User u=User.dao.findById(uid);
		String pwd1_=u.getStr("pwd1");
//		String pwd2_=u.getStr("pwd2");
		if(StringUtils.isNotEmpty(pwd1)&&pwd1_.equals(pwd1)==false){
			this.rendJson(false, null,"原一级密码不正确！");
			return;
		}
		/*if(StringUtils.isNotEmpty(pwd4)&&pwd2_.equals(pwd4)==false){
			this.rendJson(false, null,"原二级密码不正确！");
			return;
		}*/
		if(StringUtils.isNotEmpty(pwd6)&&pwd6.equals(pwd3)){
			this.rendJson(false, null,"两级密码不能相同！");
			return;
		}
		if(StringUtils.isNotEmpty(pwd3)){
			u.set("pwd1",pwd3);
		}
		if(StringUtils.isNotEmpty(pwd6)){
			u.set("pwd2",pwd6);
		}
		try{
			u.update();
			this.rendJson(true, null, "密码修改成功！请牢记密码！");
		}catch(Exception e){
			this.rendJson(false, null, "密码修改失败！");
		}
	}
	/**保存注册信息*/
	public void reg(){
		try {
			Map<String,Object> attr=this.getJsonAttrs();
			if(attr==null||attr.keySet().isEmpty()){
				this.rendJson(false, null, "为提交数据！");
				return;
			}
			//校验密码
			String pwd1=(String)attr.get("pwd1");
			String pwd1_=(String)attr.get("pwd1_");
			String pwd2=(String)attr.get("pwd2");
			String pwd2_=(String)attr.get("pwd2_");
				if(StringUtils.isEmpty(pwd1)){
					this.rendJson(false,null,"登录密码不能为空！");
					return;
				}
				if(StringUtils.isEmpty(pwd1_)){
					this.rendJson(false,null,"验证登录密码不能为空！");
					return;
				}
				if(pwd1.equals(pwd1_)==false){
					this.rendJson(false,null,"登录密码与验证登录密码不一致！");
					return;
				}
				if(StringUtils.isEmpty(pwd2)){
					this.rendJson(false,null,"二级密码不能为空！");
					return;
				}
				if(StringUtils.isEmpty(pwd2_)){
					this.rendJson(false,null,"验证二级密码不能为空！");
					return;
				}
				if(pwd2.equals(pwd2_)==false){
					this.rendJson(false,null,"二级密码与验证二级密码不一致！");
					return;
				}
			String referee_sn=(String)attr.get("referee_sn");
			if(User.dao.checkSn(referee_sn,1)){
				this.rendJson(false,null,"推介编号不存在！");
				return;
			}
			User u=new User();
			u.setAttrs(attr);
			u.set("creater_id",this.getCurrentUserId());
			u.save();
			this.rendJson(true,null,"成功注册帐号，请牢记用玩家编号和密码！",u.getStr("id"));
		} catch (Exception e) {
			log.error(e);
			this.rendJson(false,null,"保存注册信息异常！");
		}
		
	}
	/**获取会员编号*/
	public void getSN(){
		this.rendJson(true, null, IdGenerater.me.timeTo62());
	}
}
