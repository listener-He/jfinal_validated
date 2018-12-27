package cn.listenerhe.core.advice;

import com.jfinal.core.Action;
import com.jfinal.core.Controller;
import com.jfinal.render.Render;
import com.jfinal.render.RenderManager;

/**
 * @Auther: hehh
 * @Date: 2018/12/26 18:00
 * @Description: 响应切面
 */
public interface  IResponseAdvice<T extends Render> {

    /**
     *  排序
      * @return
     */
     default int sort(){ return Integer.MAX_VALUE;}

     /**
      *  是否支持
      * @param clazz 响应的render calss
      * @param t render
      * @return
      */
     boolean supports(Class<? extends Render> clazz, Render t);

    /**
     *  处理响应
      * @param rqquestBody　响应render
     * @param renderManager render工厂
      * @param urlPara 请求url
      * @param action 请求action
      * @param controller 请求controller
      * @return
     */
     T beforeBodyWrite(Render rqquestBody, RenderManager renderManager, String[] urlPara, Action action, Controller controller);

}
