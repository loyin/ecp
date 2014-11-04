package net.loyin.kit;
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
}
