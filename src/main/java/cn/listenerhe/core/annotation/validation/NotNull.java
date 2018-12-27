package cn.listenerhe.core.annotation.validation;

import java.lang.annotation.*;

/**
 * @Auther: hehh
 * @Date: 2018/12/24 17:06
 * @Description: 验证null
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface NotNull {
    /** 是否不能为null */
    boolean value() default true;
    /**消息提示*/
    String msg() default "";


}
