package cn.listenerhe.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
* @ClassName: ControllerKey 
* @Description: TODO(name代表类的路径) 
* @author hehh
* @date 2018年5月30日 下午4:04:11 
*
 */
@Target({ElementType.TYPE })  
@Retention(RetentionPolicy.RUNTIME)
public @interface ControllerKey {
	public String value();
}
