package net.loyin.util.sn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.loyin.util.FileUtil;
import net.loyin.util.safe.CipherUtil;

import org.junit.Test;

import com.sun.org.apache.xml.internal.security.utils.Base64;

/**
 * 序列号验证
 * 
 * @author 刘声凤 2014年5月8日
 */
public class SnChecker{
	public static boolean check() {
		/** 读取WEB-INF/SN */
		try {
			String path=FileUtil.classPath().replace("classes/","");
			File file=new File(path+"SN");
			if(new File(path+"PRODUCTID").exists()==false){
				FileWriter fr=new FileWriter(path+"PRODUCTID");
				String sequence=WindowsSequenceService.me.getSequence();
				fr.write(sequence);
				fr.flush();
				return false;
			}
			BufferedReader br=new BufferedReader(new FileReader(file));
			String sn=br.readLine();
			br.close();
			sn=new String(Base64.decode(sn));//64位解码
			sn=CipherUtil.decryptData(sn);//加密解密
			String[]s=sn.split("@");//sn@sn加密
			String t=CipherUtil.decryptData(s[1], s[0]+"loyin");
			if(!s[0].equals(t)){
				return false;
			}else{
				String sequence=WindowsSequenceService.me.getSequence();
				return s[0].equals(sequence);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	public static String createSn(){
		String sn="0727-c5a6-29a4-1b81-6512-e5ad-1905-30f8";
		StringBuffer sb=new StringBuffer(sn);
		sb.append("@");
		sb.append(CipherUtil.encryptData(sn,sn+"loyin"));
		sb.append("@");
		sb.append((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()));
		return Base64.encode(CipherUtil.encryptData(sb.toString()).getBytes());
	}
	@Test
	public void test(){
		System.out.println(SnChecker.createSn());
	}
}
