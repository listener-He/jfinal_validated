package cn.listenerhe.core.validation.verifier;


import cn.listenerhe.core.result.Result;
import cn.listenerhe.core.validation.ValidateException;
import cn.listenerhe.core.annotation.validation.Validated;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: hehh
 * @Date: 2018/12/25 10:23
 * @Description: 参数校验
 */
public abstract class ValidateAbstract<T extends Annotation> implements IValidate<T>{

    protected Boolean isSupports = false;

    protected T annotation;

    /**二级验证支持子类实现 */
    protected abstract boolean supportsParameter(Parameter parameter,Class<T> t);

    /**子类*/
    public static Map<Class<?>, ValidateAbstract> validates = new HashMap<>();

    /**一级验证支持*/
    @Override
    public boolean supports(Method method, Parameter parameter,Class<T> t) {
        this.isSupports =  method != null && (method.getAnnotation(Validated.class) != null
                                                                            ?true:method.getDeclaringClass().getAnnotation(Validated.class)!= null);
        if(this.isSupports){
            this.isSupports = supportsParameter(parameter,t);
        }
        return isSupports;
    }

    /** 验证 */
    @Override
    public Result verify(T t, Object o, Method method, Parameter parameter) {
        if(isSupports){
            if(method==null || parameter == null){
                throw new ValidateException("错误的调用：hehh.core.validation.verifier.ValidateAbstract Method verify("+t.getClass()+",java.lang.Object,java.lang.reflect.Method,java.lang.reflect.Parameter) method Parameter not null");
            }
            this.annotation = t;
            return verifyParameter(o,method,parameter);
        }
        throw new ValidateException("错误的调用：hehh.core.validation.verifier.ValidateAbstract 不支持验证.:"+method.getName()+" "+parameter.getName());
    }

    /** 真正验证的方法 */
    protected abstract Result verifyParameter(Object o, Method method, Parameter parameter);
}
