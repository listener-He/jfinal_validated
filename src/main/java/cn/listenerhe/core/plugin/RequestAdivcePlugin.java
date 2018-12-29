package cn.listenerhe.core.plugin;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.TypeUtil;
import cn.listenerhe.core.advice.IErrorRequestResponseAdvice;
import cn.listenerhe.core.advice.IResponseAdvice;
import cn.listenerhe.core.advice.impl.ResponseAdviceMapSort;
import cn.listenerhe.core.handler.ActionHandlerAdivce;
import com.jfinal.plugin.IPlugin;
import com.jfinal.render.Render;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @Auther: hehh
 * @Date: 2018/12/26 09:08
 * @Description: 处理响应
 */
public class RequestAdivcePlugin implements IPlugin {

    /**初始化 响应处理器*/
    private void initRequestAdivce(){

        ThreadUtil.excAsync(new Runnable() {
            Set<Class<?>> validates = ClassUtil.scanPackageBySuper("",IResponseAdvice.class);
            @Override
            public void run() {
                if(CollUtil.isNotEmpty(validates)){
                    Map<ResponseAdviceMapSort,IResponseAdvice> responseAdviceMap = new HashMap<>();
                    validates.forEach(new Consumer<Class<?>>() {
                        @Override
                        public void accept(Class<?> aClass) {
                            if(!ClassUtil.isAbstract(aClass) && !aClass.isInterface()){

                                Type t =aClass.getGenericInterfaces()[0];

                                //返回表示此类型实际类型参数的 Type 对象的数组
                                Type[]params = ((ParameterizedType) t).getActualTypeArguments();
                                Type param = params[0];
                                try {
                                    IResponseAdvice iResponseAdvice = (IResponseAdvice) aClass.newInstance();
                                    ResponseAdviceMapSort eesponseAdviceMapSort = new ResponseAdviceMapSort();
                                    eesponseAdviceMapSort.setAClass((Class<? extends Render>) TypeUtil.getClass(param));
                                    eesponseAdviceMapSort.setSort(iResponseAdvice.sort());
                                    responseAdviceMap.put(eesponseAdviceMapSort,iResponseAdvice);
                                } catch (InstantiationException e) {
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }


                            }
                        }
                    });

                    if(MapUtil.isNotEmpty(responseAdviceMap)){

                        for (ResponseAdviceMapSort responseAdviceMapSort : responseAdviceMap.keySet()) {
                            Class<? extends Render> aClass = responseAdviceMapSort.getAClass();
                            if(!ActionHandlerAdivce.responseAdviceMap.containsKey(aClass)) {
                                List<? extends IResponseAdvice<? extends Render>> responseAdvice = getResponseAdvice(aClass, responseAdviceMap);
                                ActionHandlerAdivce.responseAdviceMap.put(aClass, responseAdvice);
                            }
                        }

                    }
                }
            }
        },true);
    }

    /**
     *  初始化异常处理
     */
    private void initError(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Set<Class<?>> validates = ClassUtil.scanPackageBySuper("",IErrorRequestResponseAdvice.class);
                if(CollUtil.isNotEmpty(validates)){
                    for (Class<?> aClass : validates) {
                        Type t =aClass.getGenericInterfaces()[0];

                        //返回表示此类型实际类型参数的 Type 对象的数组
                        Type[]params = ((ParameterizedType) t).getActualTypeArguments();
                        Type param = params[0];
                        try {
                            Class<? extends Throwable> aClass1 = (Class<? extends Throwable>) TypeUtil.getClass(param);
                            if(ActionHandlerAdivce.errorAdviceMap.containsKey(aClass1)){
                                throw new RuntimeException(ActionHandlerAdivce.class+"errorAdviceMap "+aClass1+" 已存在请勿重复配置扑捉.");
                            }
                            ActionHandlerAdivce.errorAdviceMap.put(aClass1,(IErrorRequestResponseAdvice)aClass.newInstance());
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public boolean start() {
        initRequestAdivce();
        initError();
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }




    /***
     *  得到响应处理器
     * @param clazz
     * @param <T>
     * @return
     */
    private  <T extends Render> List<IResponseAdvice<T>> getResponseAdvice(Class<T> clazz, Map<ResponseAdviceMapSort,IResponseAdvice> responseAdviceMap){
        if(clazz == null || MapUtil.isEmpty(responseAdviceMap)){ return null; }
        /***
         *  得到指定处理 T 的处理器
         */
        List<ResponseAdviceMapSort> filter = new ArrayList<>();
        responseAdviceMap.forEach(new BiConsumer<ResponseAdviceMapSort, IResponseAdvice>() {
            @Override
            public void accept(ResponseAdviceMapSort responseAdviceMapSort, IResponseAdvice iResponseAdvice) {
                if (responseAdviceMapSort.getAClass().isAssignableFrom(clazz) || responseAdviceMapSort.getAClass().equals(clazz)) {
                    filter.add(responseAdviceMapSort);
                }
            }
        });

        if(CollUtil.isEmpty(filter)){
            return null;
        }

        /***
         *  排序
         */
        List<ResponseAdviceMapSort> sort = CollUtil.sort(filter, new Comparator<ResponseAdviceMapSort>() {

            /**compare方法大于0，就把前一个数和后一个数交换，也就是把大的数放后面了，即所谓的升序了*/
            @Override
            public int compare(ResponseAdviceMapSort o1, ResponseAdviceMapSort o2) {
                return o1.getSort() - o2.getSort();
            }
        });

        List<IResponseAdvice<T>> temp = new ArrayList<>();

        sort.forEach(new Consumer<ResponseAdviceMapSort>() {
            @Override
            public void accept(ResponseAdviceMapSort responseAdviceMapSort) {
                temp.add(responseAdviceMap.get(responseAdviceMapSort));
            }
        });
        return temp;
    }
}
