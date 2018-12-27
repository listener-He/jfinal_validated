package cn.listenerhe;

import cn.listenerhe.core.annotation.ControllerKey;
import cn.listenerhe.core.annotation.CrossOrigin;
import cn.listenerhe.core.annotation.RequestBody;
import cn.listenerhe.core.annotation.validation.NotBlank;
import cn.listenerhe.core.annotation.validation.Size;
import cn.listenerhe.core.annotation.validation.Validated;
import com.jfinal.core.Controller;


/**
 * @Auther: Administrator
 * @Date: 2018/9/21 16:38
 * @Description:
 */
@ControllerKey("/user")
public class TestController extends Controller{



    @Validated
    @RequestBody
    @CrossOrigin
    public void index(@NotBlank(msg = "不能为空啊!!!")  @Size(min = 5,msg = "长度不要小于5啊!!!") String name){
        renderJson(name);
    }


    public void error(){
        System.out.println(getRequest().getRequestURI());
        renderJson("xx");
    }
}
