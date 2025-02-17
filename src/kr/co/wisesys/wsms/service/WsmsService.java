package kr.co.wisesys.wsms.service;



import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;


import kr.co.wisesys.wsms.dao.WsmsDAO;

@Configuration
@Service
public class WsmsService {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private WsmsDAO dao;
	
	/**
	 * 시스템 정보 등록
	 * @param 
	 * @return 
	*/
	public void inputSystemInfo(HashMap<String, Object> param) {
		dao.inputSystemInfo(param);
	}
	
	/**
	 * 시스템 max_id
	 * @param 
	 * @return 
	*/
	public int systemMaxOrd() {
		int result = 0;
	    try {
	    	result = dao.systemMaxOrd();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return result;
	}
	
	/**
	 * 시스템 정보 수정
	 * @param 
	 * @return 
	*/
	public void updateSystemInfo(HashMap<String, Object> param) {
		dao.updateSystemInfo(param);
	}
	
	/**
	 * 시스템 정보 삭제
	 * @param 
	 * @return 
	*/
	public void deleteSystemInfo(HashMap<String, Object> param) {
		dao.deleteSystemInfo(param);
	}
	
	/**
	 * 시스템 리스트
	 * @param 
	 * @return 
	*/
	public ArrayList<HashMap<String, Object>> allSystemInfo() {
	    ArrayList<HashMap<String, Object>> dataList = new ArrayList<HashMap<String,Object>>();
	    try {
	        dataList = dao.allSystemInfo();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return dataList;
	}
	
	/**
	 * 시스템 리스트
	 * @param 
	 * @return 
	*/
	public ArrayList<HashMap<String, Object>> selectSystemInfo() {
	    ArrayList<HashMap<String, Object>> dataList = new ArrayList<HashMap<String,Object>>();
	    try {
	        dataList = dao.selectSystemInfo();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return dataList;
	}
	
	public ArrayList<HashMap<String, Object>> getLatestData(HashMap<String, Object> param) {
	    ArrayList<HashMap<String, Object>> dataList = new ArrayList<HashMap<String,Object>>();
	    try {
	        dataList = dao.getLatestData(param);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return dataList;
	}
	
}
