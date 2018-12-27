package cn.listenerhe.core.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

/**
 * @Auther: hehh
 * @Date: 2018/12/17 10:42
 * @Description:
 */
@Data
@NoArgsConstructor
public class ArrayResult<T> extends Result<Collection<T>> {

    private Collection<T> data;

    @Override
    public Collection<T> getData() {
        return this.data;
    }

    public ArrayResult(Collection<T> list, String msg){
        super.code = 0;
        super.msg = msg;
        this.data = list;
    }
    public static <T> ArrayResult<T> succeed(Collection<T> list){
         return new ArrayResult<>(list,null);
    }
    public static <T> ArrayResult<T> succeed(Collection<T> list,String msg){
        return new ArrayResult<>(list,msg);
    }
}
