package kr.co.wisesys.wdms.controller;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value={"/wdms/*"})
public class WdmsMainController {
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@RequestMapping(value = "/wdms_monitoring.do")
	public String mainWdms(HttpServletRequest req, Model model) {
		
		return "wdms/wdms_monitoring";
	}
	
	@RequestMapping(value = "/wdms_specMng.do")
	public String specMng(HttpServletRequest req, Model model) {
		
		return "wdms/wdms_specMng";
	}
}