package cn.listenerhe.core.advice.impl;

/**
 * @Auther: hehh
 * @Date: 2018/12/27 11:40
 * @Description:
 */
public class ResponseAdviceExceptor extends RuntimeException {

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public ResponseAdviceExceptor(String message) {
        super(message);
    }
}
