package cn.listenerhe.core.annotation.validation;

import java.lang.annotation.*;

/**
 * @Auther: hehh
 * @Date: 2018/12/24 17:34
 * @Description: 正则匹配
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Pattern {
    /**正则*/
    String regular();
    String msg() default "";
}
