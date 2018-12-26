package cn.listenerhe.handler;


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

     /**
      * 忽略参数
      *
      **/
     String[] lose() default "";

     //只解析特定的参数，其余参数则为 requestBody. + key
    // String analysisParam() default "";
}
