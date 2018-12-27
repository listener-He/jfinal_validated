package cn.listenerhe.core.annotation.validation;

import java.lang.annotation.*;

/**
 * @Auther: hehh
 * @Date: 2018/12/24 17:13
 * @Description: 被注释的元素必须为true
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface AssertTrue{
    /**消息提示*/
    String msg() default "";
}
