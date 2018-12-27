package cn.listenerhe.core.advice.impl;

import cn.hutool.json.JSONObject;
import cn.listenerhe.core.advice.IResponseAdvice;
import com.jfinal.core.Action;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.render.JsonRender;
import com.jfinal.render.Render;
import com.jfinal.render.RenderManager;

/**
 * @Auther: hehh
 * @Date: 2018/12/27 09:54
 * @Description: 处理json 有数据响应
 */
public class RequestJsonAdvice implements IResponseAdvice<JsonRender> {
    /**
     * 排序
     *
     * @return
     */
    @Override
    public int sort() {
        return 0;
    }

    /**
     * 是否支持
     *
     * @param clazz      响应的render calss
     * @param jsonRender render
     * @return
     */
    @Override
    public boolean supports(Class<? extends Render> clazz, Render jsonRender) {
        boolean b = jsonRender != null && jsonRender != null && jsonRender.getClass().equals(clazz);
        if(b){
            JsonRender jsonRender1 = (JsonRender) jsonRender;
            if(StrKit.notBlank(jsonRender1.getJsonText())){
                JSONObject object = new JSONObject(jsonRender1.getJsonText());
                if(null == object.getInt("code")){
                    b = false;
                }
            }
        }
        return b;
    }

    /**
     * 处理响应
     *
     * @param rqquestBody   　响应render
     * @param renderManager render工厂
     * @param urlPara       请求url
     * @param action        请求action
     * @param controller    请求controller
     * @return
     */
    @Override
    public JsonRender beforeBodyWrite(Render rqquestBody, RenderManager renderManager, String[] urlPara, Action action, Controller controller) {
        JsonRender rqquestBody1 = (JsonRender) rqquestBody;
        System.out.println("============处理响应："+rqquestBody1.getJsonText());
        rqquestBody1 = new JsonRender("bbbbb");
        return rqquestBody1;
    }
}
