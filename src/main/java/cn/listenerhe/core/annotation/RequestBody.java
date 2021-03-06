package cn.listenerhe.core.annotation;


import cn.listenerhe.core.utils.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  json参数绑定
 * @Auther: hehh
 * @Date: 2018/9/15 18:03
 * @Description:
 */
@Target({ ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestBody {
     RequestMethod[] mtrhod() default {RequestMethod.POST,RequestMethod.GET}; //请求方法


     //只解析特定的参数，其余参数则为 requestBody. + key
    // String analysisParam() default "";
}
