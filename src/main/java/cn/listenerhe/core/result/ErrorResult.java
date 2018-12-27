package cn.listenerhe.core.result;

import lombok.Data;

/**
 * @Auther: hehh
 * @Date: 2018/12/19 16:26
 * @Description: 错误返回类
 */
@Data
public class ErrorResult<T extends Throwable> extends Result<T> {

    private T data;

    @Override
    public T getData() {
        return data;
    }

    public ErrorResult(Code code,String msg,T t){
        super(code.getName(),msg);
        this.data = t;
    }
    public ErrorResult(Code code,String msg){
         this(code,msg,null);
    }
}
