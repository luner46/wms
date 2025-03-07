package kr.co.wisesys.wdms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import kr.co.wisesys.wdms.dao.WdmsDao;

@Configuration
@Service
public class WdmsService {
	
	@Autowired
	private WdmsDao dao;

	public ArrayList<HashMap<String, Object>> fileListData(@RequestParam String boardTime) {
	    ArrayList<HashMap<String, Object>> fileList = new ArrayList<HashMap<String,Object>>();
	    try {
	    	fileList = dao.fileListData(boardTime);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return fileList;
	}
	
	public void updateCorrectionData(int fileId, String issuedate, int updateValue, String repoId) {
		List<Map<String, Object>> correctionData = new ArrayList<>();
	    Map<String, Object> param = new HashMap<>();
		
		param.put("fileId", fileId);
		param.put("repoId", repoId);
		param.put("issuedate", issuedate);
		param.put("updateValue", updateValue);
		
		correctionData.add(param);
		
		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("correctionData", correctionData);
	    
	    try {
	        dao.updateCorrectionData(parameters);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public ArrayList<HashMap<String, Object>> meRnStnInfoData(HashMap<String, Object> param) {
	    ArrayList<HashMap<String, Object>> meRnStnInfoList = new ArrayList<HashMap<String,Object>>();
	    try {
	    	meRnStnInfoList = dao.meRnStnInfoData(param);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return meRnStnInfoList;
	}
	
	public ArrayList<HashMap<String, Object>> meWlStnInfoData(HashMap<String, Object> param) {
	    ArrayList<HashMap<String, Object>> meWlStnInfoList = new ArrayList<HashMap<String,Object>>();
	    try {
	    	meWlStnInfoList = dao.meWlStnInfoData(param);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return meWlStnInfoList;
	}
	
	public ArrayList<HashMap<String, Object>> meDamStnInfoData(HashMap<String, Object> param) {
	    ArrayList<HashMap<String, Object>> meDamStnInfoList = new ArrayList<HashMap<String,Object>>();
	    try {
	    	meDamStnInfoList = dao.meDamStnInfoData(param);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return meDamStnInfoList;
	}
}
