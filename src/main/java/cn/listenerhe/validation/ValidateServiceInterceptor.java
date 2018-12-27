package cn.listenerhe.validation;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.listenerhe.annotation.TakeError;
import cn.listenerhe.utils.result.Code;
import cn.listenerhe.utils.result.ErrorResult;
import cn.listenerhe.utils.result.Result;
import cn.listenerhe.validation.annotation.Validated;
import cn.listenerhe.validation.verifier.ValidateAbstract;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @Auther: hehh
 * @Date: 2018/12/24 17:38
 * @Description: 验证拦截器
 */
public class ValidateServiceInterceptor implements Interceptor {


    @Override
    public void intercept(Invocation inv) {
        Method method = inv.getMethod();
        Validated annotation = method.getAnnotation(Validated.class);
        if (annotation == null) {
            annotation = inv.getTarget().getClass().getAnnotation(Validated.class);
        }
        /**声明是否要接管异常（一般是非aciotn）*/
        TakeError takeError = method.getAnnotation(TakeError.class);

        if (annotation != null) {
            /**得到参数*/
            Parameter[] parameters = method.getParameters();

            if (parameters != null && ArrayUtil.isNotEmpty(parameters)) {
                for (int i = 0; i < parameters.length; i++) {
                    Parameter parameter = parameters[i];
                    if (parameter != null) {
                        /**得到参数注解*/
                        Annotation[] annotations = parameter.getAnnotations();
                        if (annotations != null) {
                            for (Annotation parameterAnnotation : annotations) {
                                if (parameterAnnotation != null) {
                                    if (MapUtil.isNotEmpty(ValidateAbstract.validates)) {
                                            /**注解是否符合指定校验类型*/
                                            if ( ValidateAbstract.validates.containsKey(parameterAnnotation.annotationType())) {
                                                /**得到校验类*/
                                                ValidateAbstract iValidate = ValidateAbstract.validates.get(parameterAnnotation.annotationType());
                                                /**校验是否符合规则*/
                                                if (iValidate.supports(method, parameter, parameterAnnotation.annotationType())) {
                                                    /**校验*/
                                                    Result verify = iValidate.verify(parameterAnnotation, inv.getArg(i), method, parameter);
                                                    /**code != 0 为失败 */
                                                    if (verify.getCode() != 0) {
                                                            if (takeError != null && Result.class.isAssignableFrom(method.getReturnType())) {
                                                                inv.setReturnValue(verify);
                                                                return;
                                                            }
                                                            throw new ValidateException(verify.getMsg());
                                                            //TODO 不是action而又没接管就直接报错
                                                        }
                                                    }
                                                }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            try {
                inv.invoke();
            } catch (Exception e) {
                if (takeError != null && Result.class.isAssignableFrom(method.getReturnType())) {
                    for (Class<? extends Exception> aClass : takeError.throwable()) {
                        if (aClass.isAssignableFrom(e.getClass())) {
                            inv.setReturnValue(new ErrorResult<>(Code.FATAL_ERROR, e.getMessage(), e));
                        }
                    }
                }
            }
        }




}
