package kr.co.wisesys.common.controller;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.wisesys.common.service.UserService;
import kr.co.wisesys.wdms.service.SFTPFileService;
import kr.co.wisesys.wdms.service.WdmsService;
import kr.co.wisesys.wdms.util.CommonFileUtil;
import kr.co.wisesys.wdms.util.CommonFtpUtil;

@Controller
@RequestMapping(value = {"/userCont/*"})
public class UserContController {
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    @Autowired
	private UserService userService;
    

    @ResponseBody
	@RequestMapping(value ="/userLogInChk.do")
	public int userLogInChk(HttpServletRequest req, @RequestBody Map<String, Object> map) {
		HashMap<String, Object> param = new HashMap<>();
		
		int val = 0;
		
		try {
			String user_id = (String) map.get("user_id");
			String user_pw = (String) map.get("user_pw");
			
			param.put("user_id", user_id);
		    param.put("user_pw", user_pw);
		    
		    boolean isLoginSuccess = userService.userLogin(param);
		    
		    if(isLoginSuccess) {
		    	val = 1;
		    	return val;
		    }else {
		    	return val;
		    }
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
	    
		return val;
	}

}
