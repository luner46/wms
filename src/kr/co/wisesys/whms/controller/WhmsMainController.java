package kr.co.wisesys.whms.controller;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.co.wisesys.whms.service.WhmsService;

@Controller
@RequestMapping(value={"/whms/*"})
public class WhmsMainController {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private WhmsService service;

	@RequestMapping(value = "/whms_monitoring.do")
	public String mainWhms(HttpServletRequest req, Model model) {
		
		Date currentDate = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat endFormat = new SimpleDateFormat("yyyyMMddHHmm");
		
		String currentDateFormat = format.format(currentDate);
		String endDateFormat = endFormat.format(currentDate);
		
		model.addAttribute("currentDateFormat",currentDateFormat);
		model.addAttribute("endDateFormat",endDateFormat);
		
	    return "whms/whms_monitoring";
	}
	
	@RequestMapping(value = "/whms_stateUpdate.do")
	public String mainStateUpdate(HttpServletRequest req, Model model) {
		
	    return "whms/whms_stateUpdate";
	}
}
