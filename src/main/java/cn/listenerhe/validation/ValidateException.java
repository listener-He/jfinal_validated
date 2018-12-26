package cn.listenerhe.validation;

/**
 * @Auther: hehh
 * @Date: 2018/12/25 09:27
 * @Description:
 */
public class ValidateException  extends RuntimeException{


    public ValidateException(String message) {
        this(message,null);
    }

    /**
     * @since 1.4
     */
    public ValidateException(String message, Throwable cause) {
        super(message, cause);
    }
}
