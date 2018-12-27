package cn.listenerhe.core.validation.verifier;

import cn.listenerhe.core.result.Code;
import cn.listenerhe.core.result.ErrorResult;
import cn.listenerhe.core.result.PoJoResult;
import cn.listenerhe.core.result.Result;
import cn.listenerhe.core.validation.ValidateException;
import cn.listenerhe.core.annotation.validation.AssertFalse;
import com.jfinal.kit.StrKit;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @Auther: hehh
 * @Date: 2018/12/25 13:32
 * @Description:
 */
public class ValidateAssertFalse extends ValidateAbstract<AssertFalse> {
    @Override
    protected boolean supportsParameter(Parameter parameter, Class<AssertFalse> assertFalse) {
        return parameter != null && parameter.getAnnotation(AssertFalse.class) != null;
    }

    @Override
    protected Result verifyParameter(Object o, Method method, Parameter parameter) {
        if(o != null){
            try {
                boolean termp = (Boolean) o;
                if(termp){
                    String msg = annotation.msg();
                    if(StrKit.isBlank(msg)){
                        msg = parameter.getName()+"只能为false";
                    }
                    return new ErrorResult(Code.PARAM_ERROR,msg);
                }
            }catch (Exception e){
                 e.printStackTrace();
                throw new ValidateException("错误的配置：hehh.core.validation.annotation.AssertFalse expect java.lang.Boolean");
            }
        }
        return PoJoResult.succeed(o);
    }


}
