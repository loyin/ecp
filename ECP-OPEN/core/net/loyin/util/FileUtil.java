package net.loyin.util;

import org.junit.Test;

public class FileUtil {
	/**获得classes物理路径*/
	public static String classPath() {
		return ("*"+FileUtil.class.getResource("/").getFile()).replace("*/","").replace("*","");
	}
	@Test
	public void test(){
		System.out.println(FileUtil.classPath());
	}
}
