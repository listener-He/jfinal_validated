package cn.listenerhe;

import cn.listenerhe.core.annotation.ControllerKey;
import cn.listenerhe.core.annotation.CrossOrigin;
import cn.listenerhe.core.annotation.validation.NotBlank;
import cn.listenerhe.core.annotation.validation.Validated;
import cn.listenerhe.core.sql.ModelExample;
import cn.listenerhe.model.Poems;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;


/**
 * @Auther: Administrator
 * @Date: 2018/9/21 16:38
 * @Description:
 */
@ControllerKey("/user")
public class TestController extends Controller{

    @Inject
    public PoemsDb poemsBaseDb;

    @Validated
    @CrossOrigin
    public void index(@NotBlank(msg = "不能为空啊!!!")  String name){
        ModelExample modelExampleSql = new ModelExample(Poems.class);
        ModelExample.Criteria criteria = modelExampleSql.createCriteria();
        criteria.andLike("author","%和%");
        renderJson(poemsBaseDb.pageByExample(1,5,modelExampleSql));
    }

}
