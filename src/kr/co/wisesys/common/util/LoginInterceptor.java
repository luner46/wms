package kr.co.wisesys.common.util;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


public class LoginInterceptor implements HandlerInterceptor {

	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	
	// preHandle
	// : 컨트롤러로 request 들어가기 전에 수행
	@Override
	public boolean preHandle (HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String requestURI = request.getRequestURI();
		
        if (requestURI.contains("/whms/") || requestURI.contains("/wdms/") || requestURI.contains("/wsms/")) {
            String session_id = (String) request.getSession().getAttribute("user_id");
            
            if (session_id == null) {
                // 로그인이 되어있지 않은 경우 로그인 페이지로 리다이렉트
                response.sendRedirect(request.getContextPath() + "/user/user_loginForm.do");
                return false;
            }
        }
		
		//return HandlerInterceptor.super.preHandle(request, response, handler);
		
		return true;
	}
	
	
	// postHandle
	// : 컨트롤러 실행 후, 뷰 실행 전
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		//log.info("[postHandle]");
	}
	
	// afterCompletion
	// : 뷰(화면) response 끝난 후 실행
	@Override
	public void afterCompletion (HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		
		
		//log.info("[afterCompletion]");
	}
	
}
