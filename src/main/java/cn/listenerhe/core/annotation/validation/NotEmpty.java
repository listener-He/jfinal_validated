package cn.listenerhe.core.annotation.validation;

import java.lang.annotation.*;

/**
 * @Auther: hehh
 * @Date: 2018/12/24 17:11
 * @Description: 检查约束元素是否为NULL或者是EMPTY.
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface NotEmpty{
    /**消息提示*/
    String msg() default "";
}
