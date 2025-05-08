package kr.co.wisesys.common.util;


import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


public class LoginInterceptor implements HandlerInterceptor {

	@Value("#{config['ipWhiteList']}") private String configIpWhiteList;
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	// preHandle
	// : 컨트롤러로 request 들어가기 전에 수행
	@Override
	public boolean preHandle (HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String requestURI = request.getRequestURI();
		String remoteIp = request.getRemoteAddr();
		List<String> ipWhiteList = Arrays.asList(configIpWhiteList.split("\\s*,\\s*"));

		if (requestURI.contains("/whms/") || requestURI.contains("/wdms/") || requestURI.contains("/wsms/")) {
			if (("guest").equals(request.getSession().getAttribute("user_id"))) {
				request.getSession().removeAttribute("user_id");
			}

			// 접속자 ip 확인
			if (!ipWhiteList.contains(remoteIp)) {
		        String session_id = (String) request.getSession().getAttribute("user_id");
		        // 접속자 로그인 아이디 확안
		        if (session_id == null) {
		            response.sendRedirect(request.getContextPath() + "/user/user_loginForm.do");
		            return false;
		        }
		    }
			
			if (ipWhiteList.contains(remoteIp) && request.getSession().getAttribute("user_id") == null){
		    	request.getSession().setAttribute("user_id", "guest");
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
