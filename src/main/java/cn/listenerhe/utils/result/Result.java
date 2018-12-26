package cn.listenerhe.utils.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auther: hehh
 * @Date: 2018/12/11 14:39
 * @Description: 返回类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Result<T>{
    protected Integer code;
    protected String msg;

    public abstract T getData();
}
