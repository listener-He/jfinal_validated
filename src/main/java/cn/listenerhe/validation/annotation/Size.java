package cn.listenerhe.validation.annotation;

import java.lang.annotation.*;

/**
 * @Auther: hehh
 * @Date: 2018/12/24 17:25
 * @Description: 被注释的元素的大小必须在指定的范围内
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Size {
    int min()default 0;
    int max() default Integer.MAX_VALUE;
    String msg() default "";

}
