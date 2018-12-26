package cn.listenerhe.handler;

import cn.hutool.json.JSONNull;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jfinal.core.Action;
import com.jfinal.core.paragetter.ParaGetter;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.StrKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 *   request请求相关工具类
 * @Auther: hehh
 * @Date: 2018/9/21 13:12
 * @Description:
 */
public final class RequestBodyUtil {


    /** 日志 */
    private static Logger logger = LoggerFactory.getLogger(RequestBodyUtil.class);

    /**
     *    配置body 解析
     * @param action
     * @param request
     */
    public static void resolutionBody(Action action, HttpServletRequest request){
        if(action == null)return;
        //获取当前请求的注解
        RequestBody body = action.getMethod().getAnnotation(RequestBody.class);
        if (body == null)//当当前action没有配置CrossOrigin注解时，使用Controller上的CrossOrigin注解
                body = action.getControllerClass().getAnnotation(RequestBody.class);
        if(body == null)return;

        boolean isMtrhod = false; //获取注解配置的请求方法
        for(RequestMethod rm:body.mtrhod()){
                if(rm.getMethod().equalsIgnoreCase(request.getMethod()))isMtrhod = true;
        }
        if(!isMtrhod)return;

        String readData = HttpKit.readData(request); //解析body
        if(StrKit.isBlank(readData))return;
        if(!JSONUtil.isJson(readData))return;

        JSONObject json = new JSONObject(readData);// 转换成 map

        if(json == null || json.isEmpty())return;
        Set<String> strings = json.keySet();

        List<String> lose = Arrays.asList(body.lose());

        Parameter[] parameters = action.getMethod().getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Object o = json.get(parameters[i].getName());
            if(o != null && JSONNull.class.equals(o.getClass())){
                 o = null;
            }

            ObjectGetter objectGetter = new ObjectGetter(parameters[i].getName(), o);
            action.getParameterGetter().addParaGetter(i,objectGetter);
            request.setAttribute(parameters[i].getName(), o);
        }



    }



}
