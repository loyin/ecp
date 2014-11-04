package net.loyin.util.safe;

import java.util.ResourceBundle;


public class CipherUtil {
	public static String key;
	static{
		ResourceBundle rb = ResourceBundle.getBundle("config");
		key=rb.getString("webkey");
	}
	/**
	 * @param decryptdata
	 *            要解密的暗码
	 * @param decryptkey
	 *            解密的密钥
	 * @return 解密后的明码
	 * @throws Exception
	 */
	public static String decryptData(String decryptdata, String decryptkey){
		try{
			DESPlus desPlus = new DESPlus(decryptkey);
			return desPlus.decrypt(decryptdata);
		}catch(Exception e){}
		return null;
	}
	public static String decryptData(String str) {
		return decryptData(str,key);
	}
	/**
	 * @param encryptdata
	 *            要解密的暗码
	 * @param decryptkey
	 *            解密的密钥
	 * @return 解密后的明码
	 * @throws Exception
	 */
	public static String encryptData(String encryptdata, String decryptkey) {
		try{
		DESPlus desPlus = new DESPlus(decryptkey);
		return desPlus.encrypt(encryptdata);
		}catch(Exception e){}
		return null;
	}
	public static String encryptData(String encryptdata) {
		return encryptData(encryptdata,key);
	}
}
