package cn.listenerhe.core.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONNull;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.listenerhe.core.annotation.CrossOrigin;
import cn.listenerhe.core.annotation.RequestBody;
import cn.listenerhe.core.ObjectGetter;
import com.jfinal.core.Action;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.StrKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Parameter;
import java.nio.charset.Charset;
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

    /**
     *     配置跨域相关
     * @param action
     * @param request
     * @param response
     */
    public static  Boolean  configurationCrossOrigin(Action action,HttpServletRequest request ,HttpServletResponse response){
        if(action == null || request == null || response == null)return null;
        CrossOrigin annotation = action.getMethod().getAnnotation(CrossOrigin.class);
        if (annotation == null)//当当前action没有配置CrossOrigin注解时，使用Controller上的CrossOrigin注解
            annotation = action.getControllerClass().getAnnotation(CrossOrigin.class);

        if (annotation != null) {
            //获取客户端的Headers
            String header = request.getHeader("Access-Control-Request-Headers");
            //如果没有设置指定的Headers 那么就使用客户端的Headers
            if(annotation.Headers().length ==1 && "*".equals(annotation.Headers()[0])){
                //发现对于火狐不能太兼容,*号并不能解析。默认设置为当前客户端的Headers
            }else{
                header = lengthEQ01(annotation.Headers());
            }
            response.setHeader("Access-Control-Allow-Origin", lengthEQ01(annotation.origins()));
            RequestMethod[] method = annotation.method();
            StringBuilder sb = new StringBuilder();
            for (RequestMethod requestMethod : method) {
                sb.append(requestMethod.getMethod()).append(",");
            }
            if(sb.length() > 1){
                sb =  sb.deleteCharAt(sb.length()-1);
            }
            response.setHeader("Access-Control-Allow-Methods", sb.toString());
            response.setHeader("Access-Control-Max-Age", annotation.maxAge() < 0 ? "3600" : annotation.maxAge() + "");
            response.setHeader("Access-Control-Allow-Headers", header);
            response.setHeader("Access-Control-Allow-Credentials", annotation.credentials() + "");
        }

        //跨域 OPTIONS请求响应空
        if("OPTIONS".equalsIgnoreCase(request.getMethod())){
            return false;
        }
        return true;
    }

    private static String lengthEQ01(String[] strs) {
        return strs.length >= 1 ? StrUtil.join(",", strs) : strs[0];
    }



}
