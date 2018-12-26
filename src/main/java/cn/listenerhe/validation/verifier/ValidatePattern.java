package cn.listenerhe.validation.verifier;

import cn.hutool.core.util.ReUtil;
import cn.listenerhe.utils.result.Code;
import cn.listenerhe.utils.result.ErrorResult;
import cn.listenerhe.utils.result.PoJoResult;
import cn.listenerhe.utils.result.Result;
import cn.listenerhe.validation.ValidateException;
import cn.listenerhe.validation.annotation.Pattern;
import com.jfinal.kit.StrKit;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @Auther: hehh
 * @Date: 2018/12/25 15:01
 * @Description:
 */
public class ValidatePattern extends ValidateAbstract<Pattern> {

    @Override
    protected boolean supportsParameter(Parameter parameter, Class<Pattern> t) {
        return parameter != null && parameter.getAnnotation(Pattern.class) != null;
    }

    @Override
    protected Result verifyParameter(Object o, Method method, Parameter parameter) {
        if(o != null && StrKit.notBlank(annotation.regular())){
            if(o instanceof String){
                if(!ReUtil.isMatch(annotation.regular(),o.toString())){
                    String msg = annotation.msg();
                    if(StrKit.isBlank(msg)){
                        msg = parameter.getName()+"必须符合指定规则";
                    }
                     return new ErrorResult(Code.PARAM_ERROR,msg);
                }
            }else{
                 throw new ValidateException("错误的配置 hehh.core.validation.annotation.Pattern expect java.lang.String not "+o.getClass());
            }
        }
        return PoJoResult.succeed(o);
    }
}
