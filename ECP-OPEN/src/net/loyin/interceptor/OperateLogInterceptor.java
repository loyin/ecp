package net.loyin.interceptor;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import net.loyin.ctrl.BaseController;
import net.loyin.jfinal.anatation.RouteBind;
import net.loyin.model.sso.ActionLog;
import net.loyin.util.PropertiesContent;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.jfinal.core.ActionInvocation;
/**
 * 操作记录日志拦截器
 * @author 刘声凤
 * 2014年1月25日
 */
public class OperateLogInterceptor extends BaseInterceptor {
	private static Logger log=Logger.getLogger(OperateLogInterceptor.class);
	public void doIntercept(ActionInvocation ai) {
		log.debug("操作日志记录");
		BaseController ctrl=(BaseController)ai.getController();
		RouteBind p=ai.getMethod().getAnnotation(RouteBind.class);
		Class ctrlClz=ai.getController().getClass();
		if(p==null){
			p=(RouteBind) ctrlClz.getAnnotation(RouteBind.class);
		}
		if(p!=null){
			String clzName=ctrlClz.getName();
			log.debug("类名："+clzName);
			String detail=null;
			String logkey=PropertiesContent.get(clzName.replace("net.loyin.ctrl.",""));
			String id=ctrl.getPara("id");
			if(StringUtils.isEmpty(id)){
				id=ctrl.getPara(0);
			}
			if(logkey!=null&&!"".equals(logkey)){
				try{
					logkey=new String(logkey.getBytes("ISO-8859-1"),"utf-8");
					}catch(Exception e){
						log.error(e);
					}
				List<Object> parames=new ArrayList<Object>();
				parames.add(id);
				String[] logstr=logkey.split("\\|");
				if(logstr.length>1){
					String[] pk=logstr[1].split(",");
					if(pk!=null&&pk.length>0){
						for(String pp:pk){
							parames.add(ctrl.getPara(pp,""));
						}
					}
				}
				detail=MessageFormat.format(logstr[0],parames.toArray());
			}
			int exct=-1;
			String method= ai.getMethodName();
			if("index".equals(method)){
				return;
			}else if("save".equals(method)){
				if(StringUtils.isEmpty(id)){
					exct=ActionLog.ADD_;
				}else{
					exct=ActionLog.EDIT_;
				}
			}else if("del".equals(method)||"delete".equals(method)){//删除
				exct=ActionLog.DEL_;
			}else if("saveAudit".equals(method)){//审核
				exct=ActionLog.PIHENPI_;
			}else if("submit".equals(method)){//提交
				exct=ActionLog.SUBMIT_;
			}else if("impl".equals(method)){//导入
				exct=ActionLog.IMPL_;
			}else{
				return;
			}
			if(exct<0)
				return;
			try{
				String ip = ctrl.getRequest().getHeader("X-Real-IP");
				if(StringUtils.isEmpty(ip)){
					ip=ctrl.getRequest().getRemoteAddr();
				}
				log.debug("操作日志记录入库");
				ActionLog.dao.addLog(p.sys(), p.name(), ctrl.getCurrentUserId(),ip, exct, detail); //记录到数据库表中
			}catch(Exception e){
				log.error("记录操作日志异常",e);
			}
		}
	}
}
