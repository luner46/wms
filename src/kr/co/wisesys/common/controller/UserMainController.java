package kr.co.wisesys.common.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.co.wisesys.common.service.UserService;


@Controller
@RequestMapping(value={"/user/*"})
public class UserMainController {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private UserService userService;

	/**
	 * 유저 로그인 페이지
	 * @return String
	*/
	@RequestMapping(value = {"/user_loginForm.do"})
	public String userLoginForm() {
		return "user/user_loginForm";
	}
	
	@PostMapping(value = {"/user_login.do"})
	public String userLogin(HttpServletRequest req, HttpSession session, RedirectAttributes redirectAttributes) {
		
		String user_id = req.getParameter("user_id")==null?"":req.getParameter("user_id");
		String user_pw = req.getParameter("user_pw")==null?"":req.getParameter("user_pw");
	    
	    HashMap<String, Object> param = new HashMap<>();
	    
	    param.put("user_id", user_id);
	    param.put("user_pw", user_pw);
	    
	    boolean isSuccess = userService.userLogin(param);
	    
	    if(isSuccess) {
	    	
	    	session.setAttribute("user_id", user_id);
	    	
	    	session.setMaxInactiveInterval(-1);  // 로그인후 세션 무한대
	    	
	    	return "redirect:/whms/whms_monitoring.do";
	    }else {
	    	redirectAttributes.addFlashAttribute("errorMessage", "아이디 또는 비밀번호가 잘못되었습니다.");
	    	return "redirect:/user/user_loginForm.do";
	    }

	}
	
	@RequestMapping(value = {"/user_logout.do"})
	public String userLogOut(HttpSession session) {
        session.removeAttribute("user_id");
		return "redirect:/user/user_loginForm.do"; 
	}
	
}
