package cn.listenerhe;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.TypeUtil;
import cn.listenerhe.validation.verifier.ValidateAbstract;
import com.jfinal.plugin.IPlugin;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @Auther: hehh
 * @Date: 2018/12/26 09:08
 * @Description:
 */
public class ValidatePlugin implements IPlugin {

    private static Set<Class<?>> validates = ClassUtil.scanPackageBySuper("",ValidateAbstract.class);
    @Override
    public boolean start() {
        if(CollUtil.isNotEmpty(validates)){
             validates.forEach(new Consumer<Class<?>>() {
                 @Override
                 public void accept(Class<?> aClass) {
                      if(ValidateAbstract.class.isAssignableFrom(aClass)){
                          try {
                              Type t =aClass .getGenericSuperclass();

                            //预防增强类 or Jfinal 3.5 @Inject 注解时无法实例化的问题
                            if(!(t instanceof ParameterizedType)){
                                Class<?> superclass = (Class<?>) aClass.getSuperclass();
                                t = superclass.getGenericSuperclass();
                            }
                            //返回表示此类型实际类型参数的 Type 对象的数组
                            Type[]params = ((ParameterizedType) t).getActualTypeArguments();
                            Type param = params[0];
                              ValidateAbstract.validates.put(TypeUtil.getClass(param),(ValidateAbstract)aClass.newInstance());
                          }catch (Exception e){
                             e.printStackTrace();
                          }

                      }
                 }
             });
        }
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }
}
