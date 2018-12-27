package cn.listenerhe.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *   解决跨域注解
 * @author hehh
 *
 */
@Target({ ElementType.TYPE,ElementType.METHOD})  
@Retention(RetentionPolicy.RUNTIME)
public @interface CrossOrigin {
	// ip地址指定,默认所有，可使用域名。
	String[] origins()default {"*"};
	//给指定的请求头授权
	String[] Headers()default {"*"};
	
	String[] method() default {"GET,POST,PUT,OPTIONS"};
	//是否允许携带证书（如：cookie）
	boolean credentials() default false;
	
	//该预检请求 可以被缓存多少秒？（-1表示禁用 每一次请求都需要提供预检请求，即用OPTIONS请求进行检测）
	int maxAge() default 1800;
	
	
}
