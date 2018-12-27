package cn.listenerhe.core.annotation.validation;

import java.lang.annotation.*;

/**
 * @Auther: hehh
 * @Date: 2018/12/24 17:28
 * @Description: 被注释的元素必须是一个数字，其值必须在可接受的范围内　
 * 且整数部分的位数不能超过integer，小数部分的位数不能超过fraction
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Digits {
    /**整数部分*/
    int integer();
    /**小数部分*/
    int fraction();

    String msg() default "";
}
