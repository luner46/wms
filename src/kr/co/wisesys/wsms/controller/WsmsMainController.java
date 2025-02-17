package kr.co.wisesys.wsms.controller;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.co.wisesys.wsms.service.WsmsService;


@Controller
@RequestMapping(value={"/wsms/*"})
public class WsmsMainController {
	
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private WsmsService service;
	
	/**
	 * 메인 시스템 모니터링 페이지
	 * @return String
	*/
	@RequestMapping(value = {"/wsms_monitoring.do"})
	public String mainSystemMonotoring(Model model) {
		ArrayList<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();
		
		try {
			
			dataList = service.selectSystemInfo();

	    } catch (NullPointerException e) {
	        log.error(e.toString());
	    }
		
		//log.info("dataList : " + dataList);
		
		model.addAttribute("dataList", dataList);
		
		return "wsms/wsms_monitoring";
	}
	
	/**
	 * 메인 시스템 관리 페이지
	 * @return String
	*/
	@RequestMapping(value = {"/wsms_management.do"})
	public String mainSystemManagement(Model model) {
		ArrayList<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();
		
		try {
	       
			dataList = service.allSystemInfo();
			
			//log.info("dataList : " + dataList);

	    } catch (NullPointerException e) {
	        log.error(e.toString());
	    }
		
		model.addAttribute("dataList", dataList);
		
		return "wsms/wsms_management";
	}
	
	
	
}
