package net.loyin.jfinal.anatation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Route 绑定Controller注解<br>
 * 在controller上使用
 * @author 刘声凤
 *  2012-9-4 上午11:48:26
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface RouteBind {
	/**对应的路径名 已/开头*/
	String path() default"/";
	/**视图所在目录*/
	String viewPath() default "";
	/**名称*/
	String name()default "";
	/**系统名称*/
	String sys() default "";
	/**模块*/
	String model()default "";
	/**编码 5位编码 可用于绑定权限*/
	String code()default "";
}
