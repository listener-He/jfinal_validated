package cn.listenerhe.core.validation.verifier;

import cn.listenerhe.core.result.Code;
import cn.listenerhe.core.result.ErrorResult;
import cn.listenerhe.core.result.PoJoResult;
import cn.listenerhe.core.result.Result;
import cn.listenerhe.core.annotation.validation.NotNull;
import com.jfinal.kit.StrKit;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @Auther: hehh
 * @Date: 2018/12/25 11:37
 * @Description: 验证是否为null
 */
public class ValidateNotNull extends ValidateAbstract<NotNull>{

    @Override
    protected boolean supportsParameter(Parameter parameter, Class<NotNull> notNull) {
        return parameter != null && parameter.getAnnotation(NotNull.class) != null;
    }

    @Override
    protected Result verifyParameter(Object o, Method method, Parameter parameter) {
        if(annotation.value()){
             if(o != null){ return PoJoResult.succeed(o); }
        }else{
             if(o == null){ return PoJoResult.succeed(o); }
        }
        String msg = annotation.msg();
        if(StrKit.isBlank(msg)){
            if(!annotation.value()){
                msg = parameter.getName()+"必须为null";
            }else{
                 msg = parameter.getName()+"不能为null";
            }
        }

        return new ErrorResult(Code.PARAM_ERROR,msg);
    }
}
