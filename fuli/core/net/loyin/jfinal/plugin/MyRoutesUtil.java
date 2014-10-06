package net.loyin.jfinal.plugin;

import java.io.File;
import java.util.List;

import net.loyin.jfinal.anatation.RouteBind;

import com.jfinal.config.Routes;

/**
 * Routes 工具类 自动绑定Controller
 * @author 刘声凤
 *  2012-9-4 下午12:35:55
 */
public class MyRoutesUtil{
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void add(Routes me){
		List<Class> list= ClassSearcher.findClasses();
		if(list!=null&&list.isEmpty()==false){
			for(Class clz:list){
				RouteBind rb=(RouteBind)clz.getAnnotation(RouteBind.class);
				if(rb!=null){
					String clzDir=(new File(clz.getResource("").getPath())).getName();
					if("ctrl".equals(clzDir)){
						clzDir="";
					}else{
						clzDir+="/";
					}
					String route=rb.path().startsWith("/")?rb.path():(clzDir+rb.path());
					System.out.println("Add Route: "+route);
					me.add(route,clz,rb.viewPath());
				}
			}
		}
	}
}
