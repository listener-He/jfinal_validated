package cn.listenerhe.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *  克隆类
 * @Auther: hehh
 * @Date: 2018/10/16 18:14
 * @Description:
 */
public class CopyKit {

    /**
     *    json方式 list转list
     * @param object
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T>List<T> toListJson(Object object,Class<T> clazz){
        if(object != null){
                JSONArray array = JSONUtil.createArray();
                array.addAll((List)object);
                return JSONUtil.toList(array,clazz);
        }
        return null;
    }

    /**
     *    克隆方式复制list
     * @param object
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> toList(Object object,Class<T> clazz){
        if(object == null && clazz == null){
            return null;
        }
        return (List<T> )Convert.toCollection(ArrayList.class, clazz, object);
    }

    /**
     *    json方式 list转list
     * @param object
     * @param className 类全面
     * @param <T>
     * @return
     */
    public static <T>List<T> toList(Object object,String className){
        return toList(object,ClassUtil.loadClass(className,false));
    }



    /**
     *   pojo转vo
     * @param object
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T toVo(Object object,Class<T> clazz){
        return  Convert.convert(clazz,object);
    }

    /**
     *   更加class字符串转vo
     * @param object
     * @param calssStr
     * @return
     */
   public static <T> T toVo(Object object,String calssStr){
       return toVo(object, ClassUtil.loadClass(calssStr,false));
   }


    /**
     *   obj转 vo 根据json
     * @param obj
     * @param backResultClass
     * @return
     */
    public static <T>T toVoRelyJson(Object obj, Class<T> backResultClass) {
        if(obj == null){
            return  null;
        }
        String s = JSONUtil.toJsonStr(obj);
        if(StrUtil.isBlank(s)){
            return null;
        }
        return JSONUtil.toBean(s,backResultClass);
    }


}
