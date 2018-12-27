package cn.listenerhe.core.advice;

import com.jfinal.core.Action;
import com.jfinal.render.Render;
import com.jfinal.render.RenderManager;

/**
 * @Auther: hehh
 * @Date: 2018/12/27 12:40
 * @Description: 注意异常向下兼容
 */
public interface IErrorRequestRequestAdvice<T extends Throwable,E extends Render>{

    /**验证支持*/
    boolean supports(T throwable);

    /**异常处理*/
    E laterBodyWrite(T throwable, RenderManager renderManager, String[] urlPara, Action action, int code);

}
