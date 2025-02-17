package kr.co.wisesys.wdms.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

@Repository
public class WdmsDao {
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private SqlSessionTemplate sqlSessionMysql;
	
	public ArrayList<HashMap<String, Object>> fileListData(@RequestParam String boardTime) {
		ArrayList<HashMap<String, Object>> fileList = new ArrayList<>();
		try {
			fileList.addAll(sqlSessionMysql.selectList("wdms.selectFileList", boardTime));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileList;
	}
	
	public ArrayList<HashMap<String, Object>> meRnStnInfoData(HashMap<String, Object> param) {
	    ArrayList<HashMap<String, Object>> meRnStnInfoList = new ArrayList<>();
	    try {
	    	meRnStnInfoList.addAll(sqlSessionMysql.selectList("wdms.selectMeRnStnInfo", param));
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return meRnStnInfoList;
	}
	
	public ArrayList<HashMap<String, Object>> meWlStnInfoData(HashMap<String, Object> param) {
	    ArrayList<HashMap<String, Object>> meWlStnInfoList = new ArrayList<>();
	    try {
	    	meWlStnInfoList.addAll(sqlSessionMysql.selectList("wdms.selectMeWlStnInfo", param));
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return meWlStnInfoList;
	}
	
	public ArrayList<HashMap<String, Object>> meDamStnInfoData(HashMap<String, Object> param) {
	    ArrayList<HashMap<String, Object>> meDamStnInfoList = new ArrayList<>();
	    try {
	    	meDamStnInfoList.addAll(sqlSessionMysql.selectList("wdms.selectMeDamStnInfo", param));
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return meDamStnInfoList;
	}
	
	public ArrayList<HashMap<String, Object>> meRnAgcnmInfoData() {
	    ArrayList<HashMap<String, Object>> meRnAgcnmInfoList = new ArrayList<>();
	    try {
	    	meRnAgcnmInfoList.addAll(sqlSessionMysql.selectList("wdms.selectMeRnAgcnmInfo"));
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return meRnAgcnmInfoList;
	}
	
	public ArrayList<HashMap<String, Object>> meWlAgcnmInfoData() {
	    ArrayList<HashMap<String, Object>> meWlAgcnmInfoList = new ArrayList<>();
	    try {
	    	meWlAgcnmInfoList.addAll(sqlSessionMysql.selectList("wdms.selectMeWlAgcnmInfo"));
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return meWlAgcnmInfoList;
	}
	
	public ArrayList<HashMap<String, Object>> meDamAgcnmInfoData() {
	    ArrayList<HashMap<String, Object>> meDamAgcnmInfoList = new ArrayList<>();
	    try {
	    	meDamAgcnmInfoList.addAll(sqlSessionMysql.selectList("wdms.selectMeDamAgcnmInfo"));
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return meDamAgcnmInfoList;
	}
	
	public void updateCorrectionData(Map<String, Object> param) {
		try {
			sqlSessionMysql.update("wdms.updateCorrectionData", param);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
