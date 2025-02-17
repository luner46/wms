package kr.co.wisesys.wsms.util;


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
		return true; // true를 리턴하면 컨트롤러 핸들러 진행
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
