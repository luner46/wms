package kr.co.wisesys.whms.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.co.wisesys.whms.service.WhmsService;

@Controller
@RequestMapping(value={"/whmsCont/*"})
public class WhmsContController {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private WhmsService service;
	
	@ResponseBody
	@RequestMapping(value = "/currentServerData.do")
	public ArrayList<HashMap<String, Object>> selectCurrentServer(Model model) {
		Date currentDate = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String currentDateFormat = format.format(currentDate);
		ArrayList<HashMap<String, Object>> currentServer = service.currentServerData(currentDateFormat);
		model.addAttribute("currentServer",currentServer);
		return currentServer;
	}
	
	@ResponseBody
	@RequestMapping(value = "/networkStateData.do")
	public ArrayList<HashMap<String, Object>> selectNetworkState(HttpServletRequest req, Model model) {
		String server_id = req.getParameter("server_id")==null?"":req.getParameter("server_id");
		ArrayList<HashMap<String, Object>> networkState = service.networkStateData(server_id);
		model.addAttribute("networkState",networkState);
		return networkState;
	}
	
	@ResponseBody
	@RequestMapping(value = "/rankData.do")
	public ArrayList<HashMap<String, Object>> selectTempRank(HttpServletRequest req, Model model, HashMap<String, String> param, @RequestParam("currentDateFormat") String currentDateFormat) {
		String chartType = req.getParameter("chartType")==null?"":req.getParameter("chartType");
		param.put("currentDateFormat", currentDateFormat);
		param.put("chartType", chartType);
		ArrayList<HashMap<String, Object>> rank = service.rankData(param);
		model.addAttribute("rank",rank);
		return rank;
	}
	
	@ResponseBody
	@RequestMapping(value = "/riskCheckData.do")
	public ArrayList<HashMap<String, Object>> selectRiskCheck(HttpServletRequest req, Model model, HashMap<String, String> param, @RequestParam("riskCheckDay") int riskCheckDay, @RequestParam("currentDateFormat") String currentDateFormat, @RequestParam("endDateFormat") String endDateFormat) {
		param.put("riskCheckDay", String.valueOf(riskCheckDay));
		param.put("currentDateFormat", currentDateFormat);
		param.put("endDateFormat", endDateFormat);
		ArrayList<HashMap<String, Object>> riskCheck = service.riskCheckData(param);
		model.addAttribute("riskCheck",riskCheck);
		return riskCheck;
	}
	
	@ResponseBody
	@RequestMapping(value = "/riskStateData.do")
	public ArrayList<HashMap<String, Object>> selectRiskState(Model model, HashMap<String, String> param, @RequestParam("riskStateOrder") String riskStateOrder) {
		Date currentDate = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
		String currentDateFormat = format.format(currentDate);
		param.put("currentDateFormat", currentDateFormat);
		param.put("riskStateOrder", riskStateOrder);
		ArrayList<HashMap<String, Object>> riskState = service.riskStateData(param);
		model.addAttribute("riskState",riskState);
		return riskState;
	}
	
	@ResponseBody
	@RequestMapping(value = "/updateRiskStateData.do")
	public Map<String,String> updateRiskState(@RequestParam("server_id") String server_id, @RequestParam("init_tm") String init_tm) {
		Map<String,String> resultMap = new HashMap<String,String>();
		
        int updateRiskStateCnt = service.updateRiskState(server_id, init_tm);
        
        resultMap.put("result", updateRiskStateCnt + "");

		return resultMap;
    }
}
