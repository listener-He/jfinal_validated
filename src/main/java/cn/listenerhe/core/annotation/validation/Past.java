package cn.listenerhe.core.annotation.validation;

import java.lang.annotation.*;

/**
 * @Auther: hehh
 * @Date: 2018/12/24 17:30
 * @Description: 被注释的元素必须是一个过去的日期
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Past {
    /***
     *  验证类型：
     *          1：毫秒、2：秒、3：分、4：时 、5：天
     *          6：周、7：月、8：年
     * @return
     */
    int type() default 5;

    String msg() default "";
}
