package cn.listenerhe.core.interceptor;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.listenerhe.core.result.Result;
import cn.listenerhe.core.annotation.validation.Validated;
import cn.listenerhe.core.validation.verifier.ValidateAbstract;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Set;

/**
 * @Auther: hehh
 * @Date: 2018/12/24 17:38
 * @Description: 验证拦截器
 */
public class ValidateAPIInterceptor implements Interceptor {


    @Override
    public void intercept(Invocation inv) {

        Method method = inv.getMethod();
        Validated annotation = method.getAnnotation(Validated.class);
        if (annotation == null) {
            annotation = inv.getTarget().getClass().getAnnotation(Validated.class);
        }

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
                                            if (ValidateAbstract.validates.containsKey(parameterAnnotation.annotationType())) {
                                                /**得到校验类*/
                                                ValidateAbstract iValidate = ValidateAbstract.validates.get(parameterAnnotation.annotationType());
                                                /**校验是否符合规则*/
                                                if (iValidate.supports(method, parameter, parameterAnnotation.annotationType())) {
                                                    /**校验*/
                                                    Result verify = iValidate.verify(parameterAnnotation,inv.getArg(i), method, parameter);
                                                    /**code != 0 为失败 */
                                                    if (verify.getCode() != 0) {
                                                        inv.getController().renderJson(verify);
                                                        return;
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
        inv.invoke();
    }
}
