package net.loyin.test;

import java.net.URLDecoder;

import org.apache.commons.lang3.StringUtils;

public class TestClz {

	public static void main(String[] args) {
		String s=null,s1="";
		System.out.println(
				StringUtils.isNoneEmpty(s)+
				"\t"+StringUtils.isNoneEmpty(s)+
				"\t"+StringUtils.isNotBlank(s)+
				"\t"+StringUtils.isNotEmpty(s)+
				
				"\n"+StringUtils.isNoneEmpty(s1)+
				"\t"+StringUtils.isNoneEmpty(s1)+
				"\t"+StringUtils.isNotBlank(s1)+
				"\t"+StringUtils.isNotEmpty(s1)+
				
				"\n"+StringUtils.isEmpty(s)+
				"\t"+StringUtils.isBlank(s)+
				"\n"+StringUtils.isEmpty(s1)+
				"\t"+StringUtils.isBlank(s1)
				);
		System.out.println(URLDecoder.decode("https://223.72.254.135/svn/CMMI/%E9%A1%B9%E7%9B%AE%E7%A0%94%E5%8F%91/NO_%E7%8E%B0%E5%AE%9E%E4%B8%80%E4%BD%93%E5%8C%96%E6%9C%8D%E5%8A%A1%E4%B8%8E%E7%AE%A1%E7%90%86%E5%B9%B3%E5%8F%B0/20_%E6%BA%90%E4%BB%A3%E7%A0%81/trunk"));
	}

}
