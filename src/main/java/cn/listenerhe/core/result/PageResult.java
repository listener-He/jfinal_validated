package cn.listenerhe.core.result;

import com.jfinal.plugin.activerecord.Page;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auther: hehh
 * @Date: 2018/12/17 10:37
 * @Description:
 */
@Data
@NoArgsConstructor
public class PageResult<T> extends Result<Page<T>> {

    /**分页数据*/
    private Page<T> data;

    public PageResult(Page<T> data, String msg){
         super.code = 0;
         super.msg = msg;
         this.data =data;
    }

    @Override
    public Page<T> getData() {
        return this.data;
    }

    /**成功响应*/
    public static <T> PageResult<T> succeed(Page<T> data){
        return new PageResult<T>(data,null);
    }
    public static <T> PageResult<T> succeed(Page<T> data, String msg){
        return new PageResult<T>(data,msg);
    }
}
