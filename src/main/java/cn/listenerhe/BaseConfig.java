package cn.listenerhe;

import cn.hutool.core.util.ClassUtil;
import cn.listenerhe.core.plugin.RequestAdivcePlugin;
import cn.listenerhe.core.plugin.ValidatePlugin;
import cn.listenerhe.core.handler.ActionHandlerAdivce;
import cn.listenerhe.core.annotation.ControllerKey;
import cn.listenerhe.core.interceptor.ValidateAPIInterceptor;
import cn.listenerhe.model._MappingKit;
import com.jfinal.config.*;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.Engine;

import java.util.Set;

/**
 * @Auther: hehh
 * @Date: 2018/12/26 15:20
 * @Description:
 */
public class BaseConfig extends JFinalConfig{

    /**
     * 运行此 main 方法可以启动项目，此main方法可以放置在任意的Class类定义中，不一定要放于此

     */
    public static void main(String[] args) {
        /**
         * 特别注意：Eclipse 之下建议的启动方式
         */
        //JFinal.start("WebRoot", 9088, "/", 5);

        /**
         * 特别注意：IDEA 之下建议的启动方式，仅比 eclipse 之下少了最后一个参数
         */
        JFinal.start("src/main/webapp", 9188, "/",5);
    }




    /**
     * 配置常量
     */
    public void configConstant(Constants me) {
        loadPropertyFile("config.properties");
        PropKit.use("config.properties");
        me.setMaxPostSize(104857600);
        me.setDevMode(true);
        // 支持 Controller、Interceptor 之中使用 @Inject 注入业务层，并且自动实现 AOP
        me.setInjectDependency(true);
    }



    /**
     * 配置路由
     */
    public void configRoute(Routes me) {

        //对文件夹中的所有的controller进行路径映射,第二个参数是controller的类包
        addControllerAnnoRequestMap(me,"");

    }

    /**
     * 配置 全局共享的函数模板
     */
    public void configEngine(Engine me) {
    }

    //方便给逆向类使用
    public static DruidPlugin createDruidPlugin() {
        return new DruidPlugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password"));
    }

    /**
     * 配置插件
     */
    public void configPlugin(Plugins me) {
        // 配置数据库连接池插件
//        DruidPlugin druidPlugin = createDruidPlugin();
//        me.add(druidPlugin);
//
//        // 配置ActiveRecord插件
//        ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
//        // 所有映射在 MappingKit 中自动化搞定
//        _MappingKit.mapping(arp);
//        me.add(arp);

        ValidatePlugin validatePlugin = new ValidatePlugin();
        me.add(validatePlugin);
        RequestAdivcePlugin requestAdivcePlugin = new RequestAdivcePlugin();
        me.add(requestAdivcePlugin);

    }

    /**
     * 配置全局拦截器
     */
    public void configInterceptor(Interceptors me) {

        ValidateAPIInterceptor validateaPIInterceptor = new ValidateAPIInterceptor();
        me.addGlobalActionInterceptor(validateaPIInterceptor);


    }

    public void afterJFinalStart() {
    }





    /**
     * 配置处理器 如在规模开发中  定制Handler来实现自定义的url映射
     */
    public void configHandler(Handlers me) {
        me.setActionHandler(new ActionHandlerAdivce());
    }


    /**
     * 增加controller路由定义
     * @param me 路由表
     * @param controllerPackageName controller所在的包名
     */
    @SuppressWarnings("all")
    private void addControllerAnnoRequestMap(Routes me,String controllerPackageName){
        Set<Class<?>> classes = ClassUtil.scanPackageByAnnotation(controllerPackageName, ControllerKey.class);
        for(Class<?> c:classes){
            ControllerKey requestMap=c.getAnnotation(ControllerKey.class);
            if(requestMap != null){
                String controllerKey = requestMap.value();
                if(StrKit.isBlank(controllerKey)) controllerKey = c.getName();
                if( StrKit.notBlank(controllerKey)){
                    me.add(controllerKey,(Class<? extends Controller>) c);
                }
            }
        }
    }

}
