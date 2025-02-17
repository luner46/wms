package kr.co.wisesys.whms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import kr.co.wisesys.whms.dao.WhmsDao;

import java.util.ArrayList;
import java.util.HashMap;

@Configuration
@Service
public class WhmsService {
	
	@Autowired
	private WhmsDao dao;
	
	public ArrayList<HashMap<String, Object>> currentServerData(@RequestParam("currentDateFormat") String currentDateFormat) {
	    ArrayList<HashMap<String, Object>> currentServer = new ArrayList<HashMap<String,Object>>();
	    try {
	    	currentServer = dao.currentServerData(currentDateFormat);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return currentServer;
	}
	
	public ArrayList<HashMap<String, Object>> networkStateData(@RequestParam("server_id") String server_id) {
	    ArrayList<HashMap<String, Object>> networkState = new ArrayList<HashMap<String,Object>>();
	    try {
	    	networkState = dao.networkStateData(server_id);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return networkState;
	}
	
	public ArrayList<HashMap<String, Object>> rankData(HashMap<String, String> param) {
	    ArrayList<HashMap<String, Object>> rank = new ArrayList<HashMap<String,Object>>();
	    try {
	    	rank = dao.rankData(param);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return rank;
	}
	
	public ArrayList<HashMap<String, Object>> riskCheckData(HashMap<String, String> param) {
	    ArrayList<HashMap<String, Object>> riskCheck = new ArrayList<HashMap<String,Object>>();
	    try {
	    	riskCheck = dao.riskCheckData(param);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return riskCheck;
	}
	
	public ArrayList<HashMap<String, Object>> riskStateData(HashMap<String, String> param) {
	    ArrayList<HashMap<String, Object>> riskState = new ArrayList<HashMap<String,Object>>();
	    try {
	    	riskState = dao.riskStateData(param);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return riskState;
	}
	
	public int updateRiskState(@RequestParam("server_id") String server_id, @RequestParam("init_tm") String init_tm) {
		int updateRiskStateCnt = 0;
        try {
            updateRiskStateCnt = dao.updateRiskStateData(server_id, init_tm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return updateRiskStateCnt;
	}
}
