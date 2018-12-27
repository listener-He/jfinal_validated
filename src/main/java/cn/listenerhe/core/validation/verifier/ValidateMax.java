package cn.listenerhe.core.validation.verifier;

import cn.hutool.core.util.NumberUtil;
import cn.listenerhe.core.result.Code;
import cn.listenerhe.core.result.ErrorResult;
import cn.listenerhe.core.result.PoJoResult;
import cn.listenerhe.core.result.Result;
import cn.listenerhe.core.validation.ValidateException;
import cn.listenerhe.core.annotation.validation.Max;
import com.jfinal.kit.StrKit;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @Auther: hehh
 * @Date: 2018/12/25 14:49
 * @Description:
 */
public class ValidateMax extends ValidateAbstract<Max> {
    @Override
    protected boolean supportsParameter(Parameter parameter, Class<Max> t) {
        return parameter != null && parameter.getAnnotation(Max.class) != null;
    }

    @Override
    protected Result verifyParameter(Object o, Method method, Parameter parameter) {
        if(o != null){
            if(NumberUtil.isLong(o.toString())){
                if(NumberUtil.parseLong(o.toString()) > annotation.value()){
                    String msg = annotation.msg();
                    if(StrKit.isBlank(msg)){
                         msg = parameter.getName()+"不得大于"+annotation.value();
                    }
                    return new ErrorResult(Code.PARAM_ERROR,msg);
                }
            }else{
                throw new ValidateException("错误的配置:hehh.core.validation.annotation.Max expect java.lang.Integer not "+o.getClass());
            }
        }
        return PoJoResult.succeed(o);
    }
}
