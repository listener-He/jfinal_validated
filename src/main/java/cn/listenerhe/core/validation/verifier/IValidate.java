package cn.listenerhe.core.validation.verifier;


import cn.listenerhe.core.result.Result;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @Auther: hehh
 * @Date: 2018/12/25 10:10
 * @Description: 验证处理器
 */
public interface IValidate<T>{

    /**是否支持*/
    boolean supports(Method method, Parameter parameter, Class<T> t);

    /**校验 核心方法*/
    Result verify(T t, Object object, Method method, Parameter parameter);

    /**失败*/
//    void error(Object source,Throwable throwable,Method method,Parameter parameter);
}
