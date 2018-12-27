package cn.listenerhe.annotation;

import java.lang.annotation.*;

/**
 *   大都用于非service层 而验证注解有不想被统一异常接管，想自己处理的
 *     说明返回值必须是 hehh.core.result.pojo.Result 才会生效
 * @Auther: hehh
 * @Date: 2018/12/6 14:17
 * @Description: 标注要接管异常
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface TakeError {

    /**哪些异常需要处理啊
     *  默认注解验证的异常
     * */
    Class<? extends Exception>[] throwable() default Exception.class;
}
