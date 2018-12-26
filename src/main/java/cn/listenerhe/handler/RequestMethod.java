package cn.listenerhe.handler;

/**
 *  其他类的支持枚举
 * @Auther: hehh
 * @Date: 2018/9/15 18:06
 * @Description:
 */
public enum RequestMethod {
        GET("GET"),
        POST("POST"),
        HEAD("HEAD"),
        PUT("PUT"),
        PATCH("PATCH"),
        DELETE("DELETE"),
        OPTIONS("OPTIONS"),
        TRACE("TRACE");
    private final String method;
    private RequestMethod(String method) {
        this.method = method;
    }
    public String getMethod(){
        return this.method;
    }
}
