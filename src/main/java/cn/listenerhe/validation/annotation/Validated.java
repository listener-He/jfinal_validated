package cn.listenerhe.validation.annotation;


import java.lang.annotation.*;

/**
 * @Auther: hehh
 * @Date: 2018/12/24 17:02
 * @Description: 标识要验证
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Validated {

}
