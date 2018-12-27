package cn.listenerhe.core.validation.verifier;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.listenerhe.core.result.Code;
import cn.listenerhe.core.result.ErrorResult;
import cn.listenerhe.core.result.PoJoResult;
import cn.listenerhe.core.result.Result;
import cn.listenerhe.core.validation.ValidateException;
import cn.listenerhe.core.annotation.validation.NotEmpty;
import com.jfinal.kit.StrKit;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * @Auther: hehh
 * @Date: 2018/12/25 12:12
 * @Description:
 */
public class ValidateNotEmpty extends ValidateAbstract<NotEmpty> {

    @Override
    protected boolean supportsParameter(Parameter parameter, Class<NotEmpty> notEmpty) {
        return parameter != null && parameter.getAnnotation(NotEmpty.class) != null;
    }

    @Override
    protected Result verifyParameter(Object o, Method method, Parameter parameter) {
        String msg = annotation.msg();
        if(StrKit.isBlank(msg)){
            msg = parameter.getName()+"不能为空";
        }
        if (o != null) {
            Class<?> aClass = o.getClass();
            boolean array = aClass.isArray();
            boolean coll = Collection.class.isAssignableFrom(aClass);
            boolean map = Map.class.isAssignableFrom(aClass);
            if (array || coll || map) {
                boolean temp = false;
                if (array) {
                    temp = ArrayUtil.isEmpty(o);
                } else if (coll) {
                    if (o instanceof Set) {
                        o = new ArrayList<Object>((Set) o);
                    }
                    temp = CollUtil.isEmpty((List) o);
                } else {
                    temp = MapUtil.isEmpty((Map) o);
                }
                if (temp) {

                    return new ErrorResult(Code.PARAM_ERROR, msg);
                }
            } else {
                throw new ValidateException("错误的配置：hehh.core.validation.annotation.NotEmpty cannot verify " + aClass);
            }
        } else {
            return new ErrorResult(Code.PARAM_ERROR, msg);
        }
        return PoJoResult.succeed(o);
    }
}
