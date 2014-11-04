package net.loyin.jfinal.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * id生成器
 * @author loyin 龙影
 *2013-6-15
 */
public class IdGenerater {
	/**必须以0开始 62位无序*/
	public static String s="0L1OCGypHdVm34hFeRr9KqoZcP7iw85znjgAkU62SJuDIYtbfBxWQMvEslaTNX";
	/**必须以0开始 62位有序*/
	public static String s1="0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	/**必须以0开始 16进制*/
	public static String s_16="01234567890ABCDEF";
	public static IdGenerater me=new IdGenerater();
	/**id池*/
	private static List<String> idPool=new ArrayList<String>();
	public void initIdPool(){
		if(idPool.isEmpty())
		for(int i=0;i<3;i++){
			idPool.add(this.idValTo62());
		}
	}
	public String timeTo62(){
		return this.to62(new Date().getTime());
	}
	/**翻转time生成*/
	public String timeTo62_fz(){
		return this.to62(Long.parseLong(new StringBuffer(new Date().getTime()+"").reverse().toString()));
	}
	/**
	 * 转换62位字符
	 * @param val
	 * @return
	 */
	public String to62(Long val){
		return toX(val,s.length(),s);
	}
	/***
	 * 62进制转换10进制
	 * @param val
	 * @return
	 */
	public Long to10(String val){
		return to10_X(val,s);
	}
	/***
	 * X进制转换10进制
	 * @param val
	 * @return
	 */
	public Long to10_X(String val,String s_){
		if(StringUtils.isEmpty(val)){
			return null;
		}
		Double sum=0D;
		char[] clist=val.toCharArray();
		int i=0;
		int len=clist.length;
		for(char c:clist){
			int cat=0;
			cat=s_.indexOf(c);
			sum=sum+cat*Math.pow(62, (len-i-1));
			i++;
		}
		return sum.longValue();
	}
	/** 
     * 将数转为任意进制 
     * @param num 
     * @param base 进制
     * @param s_运算字符串
     * @return 
     */  
    public String toX(Long num,int base,String s_){
        StringBuffer str = new StringBuffer();  
        Stack<Character> st = new Stack<Character>();  
        while(num != 0){
            st.push(s_.charAt((int) (num%base)));  
            num/=base;  
        }
        while(!st.isEmpty()){
            str.append(st.pop());  
        }  
        String s= str.toString();
        if(StringUtils.isEmpty(s)){
        	return "0";
        }
        return s;
    }
    /**从id_seq获取值*/
    public Long getIdSeq(){
    	Record r=Db.findFirst("select nextval('id_seq') idval");
    	return Long.parseLong(r.get("idval").toString());
    }
    public String idValTo62(){
    	return to62(getIdSeq());
    }
    /**从id池获取*/
    public String diValFromPool(){
    	if(idPool.isEmpty())
    	initIdPool();
    	String v=idPool.get(0);
    	idPool.remove(0);
    	return v;
    }
    @Test
    public void test(){
    	Long time1=System.nanoTime();
    	System.out.println(IdGenerater.me.to62(9223372036854775807L)+"\n"+IdGenerater.me.timeTo62_fz()+"\n"+(System.nanoTime()-time1));
    }
}
