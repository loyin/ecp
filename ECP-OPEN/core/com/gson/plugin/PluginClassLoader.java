/**
 * 微信公众平台开发模式(JAVA) SDK
 * (c) 2012-2014 ____′↘夏悸 <wmails@126.cn>, MIT Licensed
 * http://www.jeasyuicn.com/wechat
 */
package com.gson.plugin;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义类加载器
 * @author ____′↘夏悸
 *
 */
public class PluginClassLoader extends URLClassLoader {

	private List<JarURLConnection> cachedJarFiles = new ArrayList<JarURLConnection>();

	public PluginClassLoader() {
		super(new URL[] {}, findParentClassLoader());
	}

	/**
	 * 将指定的文件url添加到类加载器的classpath中去，并缓存jar connection，方便以后卸载jar
	 * 
	 * @param 一个可想类加载器的classpath中添加的文件url
	 * @throws IOException
	 */
	public void addURLFile(URL file) throws IOException {
		// 打开并缓存文件url连接
		URLConnection uc = file.openConnection();
		if (uc instanceof JarURLConnection) {
			uc.setUseCaches(true);
			((JarURLConnection) uc).getManifest();
			cachedJarFiles.add((JarURLConnection) uc);
		}
		addURL(file);
	}

	/**
	 * 卸载jar包
	 * @throws IOException 
	 */
	public void unloadJarFiles() throws IOException {
		for (JarURLConnection url : cachedJarFiles) {
				url.getJarFile().close();
				url = null;
		}
	}

	/**
	 * 定位基于当前上下文的父类加载器
	 * 
	 * @return 返回可用的父类加载器.
	 */
	private static ClassLoader findParentClassLoader() {
		ClassLoader parent = PluginManager.class.getClassLoader();
		if (parent == null) {
			parent = PluginClassLoader.class.getClassLoader();
		}
		if (parent == null) {
			parent = ClassLoader.getSystemClassLoader();
		}
		return parent;
	}
}
