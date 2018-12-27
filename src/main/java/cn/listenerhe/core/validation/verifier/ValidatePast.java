package cn.listenerhe.core.validation.verifier;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.listenerhe.core.result.Code;
import cn.listenerhe.core.result.ErrorResult;
import cn.listenerhe.core.result.PoJoResult;
import cn.listenerhe.core.result.Result;
import cn.listenerhe.core.validation.ValidateException;
import cn.listenerhe.core.annotation.validation.Past;
import com.jfinal.kit.StrKit;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Date;

/**
 * @Auther: hehh
 * @Date: 2018/12/25 14:29
 * @Description: 过去的日期
 */
public class ValidatePast extends ValidateAbstract<Past> {

    @Override
    protected boolean supportsParameter(Parameter parameter, Class<Past> t) {
        return parameter != null && parameter.getAnnotation(Past.class) != null;
    }

    @Override
    protected Result verifyParameter(Object o, Method method, Parameter parameter) {
        if(o != null){
            if(o instanceof Date){
                boolean temp = false;
                Date o1 = (Date) o;
                switch (annotation.type()){
                    case 1: temp = o1.getTime()-System.currentTimeMillis() >0?true:false; break;
                    case 2: temp =o1.getTime() / 1000L - DateUtil.currentSeconds() >0 ?true:false; break;
                    case 3: temp = DateUtil.between(DateUtil.date(),o1, DateUnit.MINUTE)>0 ?true:false;break;
                    case 4: temp = DateUtil.between(DateUtil.date(),o1, DateUnit.HOUR)>0 ?true:false; break;
                    case 5: temp = DateUtil.between(DateUtil.date(),o1, DateUnit.DAY)>0 ?true:false; break;
                    case 6: temp = DateUtil.between(DateUtil.date(),o1, DateUnit.WEEK)>0 ?true:false; break;
                    case 7: temp = DateUtil.betweenMonth(DateUtil.date(),o1,true)>0 ?true:false; break;
                    case 8: temp = DateUtil.betweenYear(DateUtil.date(),o1,true)>0 ?true:false; break;
                    default:
                        throw new ValidateException("错误的配置：hehh.core.validation.annotation.Past type not "+annotation.type());
                }
                if(temp){
                    String msg = annotation.msg();
                    if(StrKit.isBlank(msg)){
                        msg = parameter.getName()+"必须是一个过去的日期";
                    }
                    return new ErrorResult(Code.PARAM_ERROR,msg);
                }
            }else{
                throw new ValidateException("错误的配置：hehh.core.validation.annotation.Past expect java.util.Date not "+o.getClass());
            }
        }
        return PoJoResult.succeed(o);
    }
}
