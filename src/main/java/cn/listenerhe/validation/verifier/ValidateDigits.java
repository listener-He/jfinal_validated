package cn.listenerhe.validation.verifier;

import cn.hutool.core.util.NumberUtil;
import cn.listenerhe.utils.result.Code;
import cn.listenerhe.utils.result.ErrorResult;
import cn.listenerhe.utils.result.PoJoResult;
import cn.listenerhe.utils.result.Result;
import cn.listenerhe.validation.ValidateException;
import cn.listenerhe.validation.annotation.Digits;
import com.jfinal.kit.StrKit;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @Auther: hehh
 * @Date: 2018/12/25 13:42
 * @Description:
 */
public class ValidateDigits extends ValidateAbstract<Digits> {
    @Override
    protected boolean supportsParameter(Parameter parameter, Class<Digits> digits) {
        return parameter != null && parameter.getAnnotation(Digits.class) != null;
    }

    @Override
    protected Result verifyParameter(Object o, Method method, Parameter parameter) {
        if(annotation.integer() < 1 || annotation.fraction() < 0){
             throw new ValidateException("非法的配置: hehh.core.validation.annotation.Digits integer not less 1 and fraction not less 0");
        }
        if(o != null){
            if(NumberUtil.isNumber(o.toString())){
                String s = o.toString();
                /**去掉多余的0 */
                while (s.endsWith("0")){
                    s = s.substring(0, s.length() - 1);
                }
                String[] split = s.split(".");
                String msg = annotation.msg();
                if(split[0].length() > annotation.integer()){
                    if(StrKit.isBlank(msg)){
                         msg = parameter.getName()+"整数部分不得大于"+annotation.integer()+"位.";
                    }
                    return new ErrorResult(Code.PARAM_ERROR,msg);
                }else if(split.length>1 && split[1].length() > annotation.fraction()){
                    if(StrKit.isBlank(msg)){
                        msg = parameter.getName()+"小数部分不得大于"+annotation.fraction()+"位.";
                    }
                    return new ErrorResult(Code.PARAM_ERROR,msg);
                }
            }else{
                 throw new ValidateException("错误的配置：hehh.core.validation.annotation.Digits expect java.lang.Number not "+o.getClass());
            }
        }
        return PoJoResult.succeed(o);
    }
}
