package cn.listenerhe.core.validation.verifier;

import cn.hutool.core.util.ArrayUtil;
import cn.listenerhe.core.result.Code;
import cn.listenerhe.core.result.ErrorResult;
import cn.listenerhe.core.result.PoJoResult;
import cn.listenerhe.core.result.Result;
import cn.listenerhe.core.validation.ValidateException;
import cn.listenerhe.core.annotation.validation.Size;
import com.jfinal.kit.StrKit;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @Auther: hehh
 * @Date: 2018/12/25 14:32
 * @Description: 判断长度
 */
public class ValidateSize extends ValidateAbstract<Size> {

    @Override
    protected boolean supportsParameter(Parameter parameter, Class<Size> t) {
        return parameter != null && parameter.getAnnotation(Size.class) != null;
    }

    @Override
    protected Result verifyParameter(Object o, Method method, Parameter parameter) {
        if(o != null){
            int max = annotation.max();
            int min = annotation.min();
            if(min < 0 || max > Integer.MAX_VALUE){
                 throw new ValidateException("非法的配置：hehh.core.validation.annotation.Size max not greater Integer.MAX_VALUE min not less 0");
            }
            String msg = annotation.msg();
            if(StrKit.isBlank(msg)){
                msg = parameter.getName()+"长度必须小于"+max+" 大于"+min;
            }
            if(o instanceof String){
                String s = o.toString();
                if(s.length() > max || s.length() < min){
                    return new ErrorResult(Code.PARAM_ERROR,msg);
                }
            }else if(Collection.class.isAssignableFrom(o.getClass())){
                if (o instanceof Set) {
                    o = new ArrayList<Object>((Set) o);
                }
                int size = ((List) o).size();
                if(size > max || size < min){
                    return new ErrorResult(Code.PARAM_ERROR,msg);
                }
            }else if(o.getClass().isArray()){
                Object[] s = ArrayUtil.wrap(o);
                if(s.length > max || s.length < min){
                    return new ErrorResult(Code.PARAM_ERROR,msg);
                }
            }else{
                 throw new ValidateException("错误的配置：hehh.core.validation.annotation.Size expect java.lang.String or array");
            }

        }
        return PoJoResult.succeed(o);
    }
}
