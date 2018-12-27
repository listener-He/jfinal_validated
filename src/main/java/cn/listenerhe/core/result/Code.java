package cn.listenerhe.core.result;

/**
 *  响应的code
 */
public enum Code {
	OK(0),  //成功
	OVER(1000),//结束
	VERIFY_ERROR (4001), // 验证异常
	SIGN_ERROR (4002),// 签名错误
	PARAM_ERROR(4003), //参数异常
	REST_ERROR(4004),//资源异常
	FATAL_ERROR (5000), //系统异常
	SERVICE_ERROR(5003);//业务异常

	private final int code;
	Code(int code) {
		this.code = code;
	}
	public int getName(){
		return this.code;
	}

	/**
	 *    得到code
	 * @param code
	 * @return
	 */
	public static Code get(int code){
		Code[] values = values();
		for (Code value : values) {
			if(value.code == code){
				 return value;
			}
		}
		return null;
	}

}
