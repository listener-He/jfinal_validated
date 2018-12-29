package cn.listenerhe.error;

import cn.listenerhe.core.advice.IErrorRequestResponseAdvice;
import com.jfinal.core.Action;
import com.jfinal.render.JsonRender;
import com.jfinal.render.RenderManager;
import com.jfinal.render.TextRender;

/**
 * @Auther: hehh
 * @Date: 2018/12/29 14:33
 * @Description:
 */
public class MaxRespobseAdvice implements IErrorRequestResponseAdvice<Exception,JsonRender> {
    /**
     * 验证支持
     *
     * @param throwable
     */
    @Override
    public boolean supports(Exception throwable) {
        return true;
    }

    /**
     * 异常处理
     *
     * @param throwable
     * @param renderManager
     * @param urlPara
     * @param action
     * @param code
     */
    @Override
    public JsonRender laterBodyWrite(Exception throwable, RenderManager renderManager, String[] urlPara, Action action, int code) {
        return (JsonRender) renderManager.getRenderFactory().getTextRender("服务器异常"+code);
    }
}
