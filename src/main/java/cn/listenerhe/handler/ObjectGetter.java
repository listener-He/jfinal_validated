package cn.listenerhe.handler;


import cn.listenerhe.utils.CopyKit;
import com.jfinal.core.Action;
import com.jfinal.core.Controller;
import com.jfinal.core.paragetter.ParaGetter;

/**
 * @Auther: hehh
 * @Date: 2018/12/26 10:39
 * @Description:
 */
public class ObjectGetter<T> extends ParaGetter<T> {

    public Class<T> type;

    public T value;
    public ObjectGetter(String parameterName, T defaultValue) {
        super(parameterName, defaultValue == null?"":defaultValue.toString());
       type = (Class<T>) Object.class;
        if(defaultValue != null){
            type = (Class<T>) defaultValue.getClass();
            this.value = defaultValue;
        }
    }



    @Override
    protected T to(String v) {
        return null;
    }

    @Override
    public T get(Action action, Controller c) {
        Object attr = c.getAttr(super.getParameterName(),super.getDefaultValue());
        if(attr == null){
             return this.value;
        }
        return CopyKit.toVo(attr,type);
    }
}
