package cn.listenerhe.core.advice.impl;

import cn.listenerhe.core.advice.IErrorRequestResponseAdvice;
import cn.listenerhe.core.result.Code;
import cn.listenerhe.core.result.ErrorResult;
import cn.listenerhe.core.validation.ValidateException;
import com.jfinal.core.Action;
import com.jfinal.render.JsonRender;
import com.jfinal.render.RenderManager;

/**
 * @Auther: hehh
 * @Date: 2018/12/27 13:56
 * @Description:
 */
public class ValidateErrorResponsetAdvice implements IErrorRequestResponseAdvice<ValidateException,JsonRender> {
    /**
     * 验证支持
     *
     * @param throwable
     */
    @Override
    public boolean supports(ValidateException throwable) {
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
    public JsonRender laterBodyWrite(ValidateException throwable, RenderManager renderManager, String[] urlPara, Action action, int code) {
        return (JsonRender)renderManager.getRenderFactory().getJsonRender(new ErrorResult<>(Code.PARAM_ERROR,throwable.getMessage()));
    }
}
