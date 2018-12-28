package cn.listenerhe.core.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.listenerhe.core.advice.IErrorRequestRequestAdvice;
import cn.listenerhe.core.advice.IResponseAdvice;
import cn.listenerhe.core.advice.impl.ResponseAdviceExceptor;
import cn.listenerhe.core.result.Code;
import cn.listenerhe.core.result.ErrorResult;
import cn.listenerhe.core.utils.RequestBodyUtil;
import com.jfinal.aop.Invocation;
import com.jfinal.core.*;
import com.jfinal.log.Log;
import com.jfinal.render.Render;
import com.jfinal.render.RenderException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @Auther: hehh
 * @Date: 2018/12/26 17:41
 * @Description:
 */
public class ActionHandlerAdivce extends ActionHandler {

    private static final Log log = Log.getLog(ActionHandlerAdivce.class);

    /** 响应处理类 */
    public static Map<Class<? extends Render>,List<? extends IResponseAdvice<? extends Render>>> responseAdviceMap = new ConcurrentHashMap();

    /**异常处理器*/
    public static Map<Class<? extends Throwable>,IErrorRequestRequestAdvice> errorAdviceMap = new ConcurrentHashMap<>();

    /***
     *  获取异常处理器
     * @param aClass
     * @return
     */
    private List<IErrorRequestRequestAdvice> getErrorAdvice(Class<? extends Throwable> aClass){
        if(aClass == null){
             return null;
        }

        List<Class<? extends Throwable>> temp = new ArrayList<>();
        errorAdviceMap.keySet().forEach(new Consumer<Class<? extends Throwable>>() {
            @Override
            public void accept(Class<? extends Throwable> bClass) {
                if(bClass.isAssignableFrom(aClass) || bClass.equals(aClass)){
                    temp.add(bClass);
                }
            }
        });
        if(CollUtil.isEmpty(temp)){return  null;}

        List<Class<? extends Throwable>> sort = CollUtil.sort(temp, new Comparator<Class<? extends Throwable>>() {
            @Override
            public int compare(Class<? extends Throwable> o1, Class<? extends Throwable> o2) {
                return o1.isAssignableFrom(o2) ? -1 : 1;
            }
        });
        if(CollUtil.isEmpty(sort)){return  null;}
        List<IErrorRequestRequestAdvice> advice = new ArrayList<>();

        sort.forEach(new Consumer<Class<? extends Throwable>>() {
            @Override
            public void accept(Class<? extends Throwable> cClass) {
                advice.add(errorAdviceMap.get(cClass));
            }
        });

        return advice;
    }


    /**
     * handle
     * 1: Action action = actionMapping.getAction(target)
     * 2: new Invocation(...).invoke()
     * 3: render(...)
     *
     * @param target
     * @param request
     * @param response
     * @param isHandled
     */
    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        if (target.indexOf('.') != -1) {
            return ;
        }

        isHandled[0] = true;
        String[] urlPara = {null};
        Action action = actionMapping.getAction(target, urlPara);

        if (action == null) {
            String qs = request.getQueryString();
            String error = "404 Action Not Found: " + (qs == null ? target : target + "?" + qs);
            if (log.isWarnEnabled()) {
                log.warn(error);
            }

            List<IErrorRequestRequestAdvice> errorAdvice = getErrorAdvice(RenderException.class);
            Render render = renderManager.getRenderFactory().getErrorRender(404).setContext(request, response);
            if(CollUtil.isEmpty(errorAdvice)){
                render.render();
                return;
            }else{
                throw  new RenderException(error);
            }
        }

        /**跨域在前*/
        Boolean aBoolean = RequestBodyUtil.configurationCrossOrigin(action, request, response);
        if(aBoolean == null){
            renderManager.getRenderFactory().getJsonRender(new ErrorResult<>(Code.REST_ERROR,"请求错误")).setContext(request,response).render();
            return;
        }else{
            if(!aBoolean){
                renderManager.getRenderFactory().getNullRender().setContext(request,response).render();
                return;
            }
        }
        RequestBodyUtil.resolutionBody(action,request);//解析body

        Controller controller = null;
        Render render = null;
        try {
            controller = controllerFactory.getController(action.getControllerClass());
            if (injectDependency) {com.jfinal.aop.Aop.inject(controller);}
            CPI._init_(controller,action,request,response,urlPara[0]);

            //TODO 进入拦截器前
            if (devMode) {
                if (ActionReporter.isReportAfterInvocation(request)) {
                    new Invocation(action, controller).invoke();
                    ActionReporter.report(target, controller, action);
                } else {
                    ActionReporter.report(target, controller, action);
                    new Invocation(action, controller).invoke();
                }
            }
            else {
                new Invocation(action, controller).invoke();
            }

             render = controller.getRender();
            if (render instanceof ForwardActionRender) {
                String actionUrl = ((ForwardActionRender)render).getActionUrl();
                if (target.equals(actionUrl)) {
                    throw new RuntimeException("The forward action url is the same as before.");
                } else {
                    handle(actionUrl, request, response, isHandled);
                }
                return ;
            }

            if (render == null) {
                render = renderManager.getRenderFactory().getDefaultRender(action.getViewPath() + action.getMethodName());
            }



        } catch (Exception e) {
            boolean temp = true;
            /**异常处理*/
            List<IErrorRequestRequestAdvice> errorAdvice = getErrorAdvice(e.getClass());
            if(CollUtil.isNotEmpty(errorAdvice)){
                int code = e instanceof RenderException ?404:(e instanceof ActionException ?((ActionException)e).getErrorCode():500);
                for (IErrorRequestRequestAdvice iErrorRequestRequestAdvice : errorAdvice) {
                    if(iErrorRequestRequestAdvice.supports(e)){
                        Render render1 = iErrorRequestRequestAdvice.laterBodyWrite(e, renderManager, urlPara, action, code);
                        if(render1 != null){
                            render = render1;
                            //render1.setContext(request,response).render();
                            temp = false;
                            break;
                        }
                    }
                }
            }
                if(temp){
                    //TODO 异常响应不处理
                    if(e instanceof RenderException){
                        if (log.isErrorEnabled()) {
                            String qs = request.getQueryString();
                            log.error(qs == null ? target : target + "?" + qs, e);
                        }
                    }else if(e instanceof ActionException){
                        handleActionException(target, request, response, action, (ActionException)e);
                    }else{
                        if (log.isErrorEnabled()) {
                            String qs = request.getQueryString();
                            log.error(qs == null ? target : target + "?" + qs, e);
                        }
                        renderManager.getRenderFactory().getErrorRender(500).setContext(request, response, action.getViewPath()).render();
                    }
                    return;
                }
        } finally {
            if (controller != null) {
                CPI._clear_(controller);
            }
        }
        //TODO 响应前 执行响应处理链
        if(MapUtil.isNotEmpty(responseAdviceMap)){
            List<? extends IResponseAdvice<? extends Render>> iResponseAdvices = responseAdviceMap.get(render.getClass());
            if(CollUtil.isNotEmpty(iResponseAdvices)){
                for (IResponseAdvice<? extends Render> iResponseAdvice : iResponseAdvices) {
                    if(iResponseAdvice.supports(render.getClass(),render)){
                        Render beforeRender = iResponseAdvice.beforeBodyWrite(render, renderManager, urlPara, action, controller);
                        if(beforeRender == null){
                            throw new ResponseAdviceExceptor("响应处理异常:"+iResponseAdvice.getClass()+" beforeBodyWrite 方法不能返回 null.");
                        }
                        render = beforeRender;
                    }
                }
            }
        }
        render.setContext(request, response, action.getViewPath()).render();
    }


    /**
     * 抽取出该方法是为了缩短 handle 方法中的代码量，确保获得 JIT 优化，
     * 方法长度超过 8000 个字节码时，将不会被 JIT 编译成二进制码
     *
     * 通过开启 java 的 -XX:+PrintCompilation 启动参数得知，handle(...)
     * 方法(73 行代码)已被 JIT 优化，优化后的字节码长度为 593 个字节，相当于
     * 每行代码产生 8.123 个字节
     */
    private void handleActionException(String target, HttpServletRequest request, HttpServletResponse response, Action action, ActionException e) {
        int errorCode = e.getErrorCode();
        String msg = null;
        if (errorCode == 404) {
            msg = "404 Not Found: ";
        } else if (errorCode == 400) {
            msg = "400 Bad Request: ";
        } else if (errorCode == 401) {
            msg = "401 Unauthorized: ";
        } else if (errorCode == 403) {
            msg = "403 Forbidden: ";
        }

        if (msg != null) {
            if (log.isWarnEnabled()) {
                String qs = request.getQueryString();
                msg = msg + (qs == null ? target : target + "?" + qs);
                if (e.getMessage() != null) {
                    msg = msg + "\n" + e.getMessage();
                }
                log.warn(msg);
            }
        } else {
            if (log.isErrorEnabled()) {
                String qs = request.getQueryString();
                log.error(errorCode + " Error: " + (qs == null ? target : target + "?" + qs), e);
            }
        }

        e.getErrorRender().setContext(request, response, action.getViewPath()).render();
    }
}
