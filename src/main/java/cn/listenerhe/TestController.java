package cn.listenerhe;

import cn.listenerhe.handler.ControllerKey;
import cn.listenerhe.handler.RequestBody;
import cn.listenerhe.validation.annotation.NotBlank;
import cn.listenerhe.validation.annotation.Size;
import cn.listenerhe.validation.annotation.Validated;
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
    public void index(@NotBlank(msg = "不能为空啊!!!")  @Size(min = 5,msg = "长度不要小于5啊!!!") String name){
        renderJson(name);
    }


}
