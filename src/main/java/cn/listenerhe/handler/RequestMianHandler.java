package cn.listenerhe.handler;

import com.jfinal.core.Action;
import com.jfinal.core.JFinal;
import com.jfinal.handler.Handler;
import com.jfinal.kit.HandlerKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 解决跨域的handler
 *
 * @author hehh
 *
 */
public class RequestMianHandler extends Handler {

	/** 日志 */
	private static Logger logger = LoggerFactory.getLogger(RequestMianHandler.class);


	/**
	 * Handle target
	 *
	 * @param target
	 *            url target of this web http request 此web http请求的目标url目标
	 * @param request
	 *            HttpServletRequest of this http request
	 * @param response
	 *            HttpServletRequest of this http request
	 * @param isHandled
	 *            JFinalFilter will invoke doFilter() method if isHandled[0] ==
	 *            false, it is usually to tell Filter should handle the static
	 *            resource.
	 *
	 *            isHandled JFinalFilter如果isHandled[0] == false，将调用doFilter()方法，
	 *            通常是告诉过滤器应该处理静态资源
	 *              若最后 isHandled[0] = false，会执行下一个 Filter。
	 */
	@Override
	public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {

		isHandled[0] = false;
		String[] urlPara = {null};


		Action action = JFinal.me().getAction(target, urlPara);


		if (action == null) {
			isHandled[0] = true;
			HandlerKit.renderError404(request, response, isHandled);
			logger.warn("404 Action Not Found 请求资源不存在  " + action.getMethod() + ":" + target + "?"
					+ request.getQueryString());
			return;
		}
		RequestBodyUtil.resolutionBody(action,request);//解析body

		// 指向下一个handler
		next.handle(target, request, response, isHandled);
	}


}
