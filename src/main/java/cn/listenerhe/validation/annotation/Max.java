package cn.listenerhe.validation.annotation;

/**
 * @Auther: hehh
 * @Date: 2018/12/24 18:02
 * @Description:
 */
public @interface Max {

    int value();

    String msg() default "";
}
