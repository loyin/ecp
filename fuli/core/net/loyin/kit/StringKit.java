package net.loyin.kit;

import org.apache.commons.lang3.StringUtils;

/**
 * 字符串处理
 * @author 刘声凤
 * 2014年2月27日
 */
@SuppressWarnings("deprecation")
public class StringKit extends com.jfinal.kit.StringKit {
	public static final StringKit kit=new StringKit();
	/**blob转stiring*/
	public String fromBlob(Object con){
		if(con==null){
			return "";
		}
		if(con instanceof String ==false)
			return new String((byte[])con);
		else{
			return (String) con;
		}
	}
	/**
	 * 将字符串转换为UTF-8格式
	 * @param str
	 * @return String
	 */
	public static String toUTF8(String str){
		try {
			str = new String(str.getBytes("ISO-8859-1"), "UTF-8");
		} catch (Exception e) {}
		return str;
	}
	public static Boolean toBool(String str,boolean... def){
		if(StringUtils.isNotEmpty(str)){
			return Boolean.parseBoolean(str);
		}
		if(def!=null)
		return def[0];
		return null;
	}
	public static Integer toInt(String str,Integer...def){
		if(StringUtils.isNotEmpty(str)){
			return Integer.parseInt(str);
		}
		if(def!=null)
			return def[0];
		return null;
	}
	public static Long toLong(String str,Long...def){
		if(StringUtils.isNotEmpty(str)){
			return Long.parseLong(str);
		}
		if(def!=null)
			return def[0];
		return null;
	}
}
