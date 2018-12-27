package cn.listenerhe.core.validation.verifier;

import cn.listenerhe.core.result.Code;
import cn.listenerhe.core.result.ErrorResult;
import cn.listenerhe.core.result.PoJoResult;
import cn.listenerhe.core.result.Result;
import cn.listenerhe.core.validation.ValidateException;
import cn.listenerhe.core.annotation.validation.NotBlank;
import com.jfinal.kit.StrKit;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @Auther: hehh
 * @Date: 2018/12/25 11:54
 * @Description: 验证字符串是否为null
 */
public class ValidateNotBlank extends ValidateAbstract<NotBlank> {

    @Override
    protected boolean supportsParameter(Parameter parameter, Class<NotBlank> notBlank) {
        return parameter != null && parameter.getAnnotation(NotBlank.class) != null;
    }

    @Override
    protected Result verifyParameter(Object o, Method method, Parameter parameter) {
        String msg = annotation.msg();
        if(StrKit.isBlank(msg)){
            msg = parameter.getName()+"不能为空";
        }
        if(o == null){
            return new ErrorResult(Code.PARAM_ERROR,msg);
        }
        if(!(o instanceof String)){
            throw new ValidateException("错误的使用：hehh.core.validation.verifier.ValidateNotBlank Method verify nonsupport "+o.getClass()+" expect java.lang.String");
        }
        if(StrKit.isBlank(o.toString())){
            return new ErrorResult(Code.PARAM_ERROR,msg);
        }
        return  PoJoResult.succeed(o.toString().trim());
    }
}
