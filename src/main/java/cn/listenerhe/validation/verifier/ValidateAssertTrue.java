package cn.listenerhe.validation.verifier;

import cn.listenerhe.utils.result.Code;
import cn.listenerhe.utils.result.ErrorResult;
import cn.listenerhe.utils.result.PoJoResult;
import cn.listenerhe.utils.result.Result;
import cn.listenerhe.validation.ValidateException;
import cn.listenerhe.validation.annotation.AssertTrue;
import com.jfinal.kit.StrKit;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @Auther: hehh
 * @Date: 2018/12/25 13:40
 * @Description:
 */
public class ValidateAssertTrue extends ValidateAbstract<AssertTrue> {
    @Override
    protected boolean supportsParameter(Parameter parameter, Class<AssertTrue> assertFalse) {
        return parameter != null && parameter.getAnnotation(AssertTrue.class) != null;
    }

    @Override
    protected Result verifyParameter(Object o, Method method, Parameter parameter) {
        if (o != null) {
            try {
                boolean termp = (Boolean) o;
                if (!termp) {
                    String msg = annotation.msg();
                    if(StrKit.isBlank(msg)){
                        msg = parameter.getName()+"只能为true";
                    }
                    return new ErrorResult(Code.PARAM_ERROR, msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new ValidateException("错误的配置：hehh.core.validation.annotation.AssertTrue expect java.lang.Boolean");
            }
        }
        return PoJoResult.succeed(o);
    }
}
