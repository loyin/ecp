package net.loyin.validator;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.loyin.ctrl.BaseController;
import net.loyin.kit.I18N;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

/**
 * 验证
 * @author loyin 2013-6-2 下午3:14:22
 */
public abstract class BaseValidtor extends Validator {
	/** 用来装载错误系信息的key值 */
	protected String[] filedErrKeyList;
	/** true:转化成json错误信息 */
	protected boolean toJson = true;

	/***
	 * 生成json格式回显信息
	 */
	@Override
	protected void handleError(Controller c) {
		@SuppressWarnings("rawtypes")
		BaseController ctrl=(BaseController)c;
//		if (toJson == true) {
			Map<String, Object> json = new HashMap<String, Object>();
			StringBuffer msg = new StringBuffer();
			/**存储为json对象*/
			List<Map<String, String>> jsonList=new ArrayList<Map<String, String>>();
			for (String key : filedErrKeyList) {
				String msgstr = c.getAttrForStr(key);
				if (msgstr != null){
					msg.append(msgstr);
					Map<String, String> msgJson = new HashMap<String, String>();
					msgJson.put("key",key.replace("vld_",""));
					msgJson.put("msg", msgstr);
					jsonList.add(msgJson);
				}
			}
			json.put("data", jsonList);
			json.put("success", false);
			json.put("status",401);//访问不成功！
			json.put("msg", msg.toString());
			ctrl.renderJson(json);
//		}
	}
	/**获得国际化信息*/
	public String getText(String key,Controller c){
		return I18N.getText(key, c.getRequest().getLocale());
	}
	/**国际化*/
	public String fmtI18N(String key,Controller c,String...args){
		List<String> argList=new ArrayList<String>();
		if(args!=null){
			for(String a:args){
				argList.add(getText(a,c));
			}
		}
		return MessageFormat.format(getText(key,c), argList.toArray());
	}
}
