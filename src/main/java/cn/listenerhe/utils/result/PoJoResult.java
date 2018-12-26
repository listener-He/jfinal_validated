package cn.listenerhe.utils.result;

import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @Auther: hehh
 * @Date: 2018/12/17 10:46
 * @Description:
 */
@Data
@NoArgsConstructor
public class PoJoResult<T> extends Result<T> {

    private T data;

    public PoJoResult(T t,String msg){
        super.code = 0;
        super.msg = msg;
        this.data = t;
    }

    @Override
    public T getData() {
        return this.data;
    }

    public static <T> PoJoResult<T> succeed(T t){
        return new PoJoResult<>(t,null);
    }
    public static <T> PoJoResult<T> succeed(T t,String msg){
        return new PoJoResult<>(t,msg);
    }
}
