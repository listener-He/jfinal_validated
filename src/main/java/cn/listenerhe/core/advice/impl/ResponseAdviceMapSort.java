package cn.listenerhe.core.advice.impl;

import com.jfinal.render.Render;
import lombok.Data;

import java.io.Serializable;

/**
 * @Auther: hehh
 * @Date: 2018/12/27 10:28
 * @Description:
 */
@Data
public class ResponseAdviceMapSort implements Serializable {

    private Class<? extends Render> aClass;

    private Integer sort;

    public int getSort(){
         if(sort == null){
              this.sort = Integer.MAX_VALUE;
         }
         return sort;
    }


}
