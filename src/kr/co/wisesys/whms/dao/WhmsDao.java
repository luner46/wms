package kr.co.wisesys.whms.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.mybatis.spring.SqlSessionTemplate;

@Repository
public class WhmsDao {

	@Autowired
	private SqlSessionTemplate sqlSessionMysql;

	private final Logger log = Logger.getLogger(getClass());

	public ArrayList<HashMap<String, Object>> currentServerData(@RequestParam("currentDateFormat") String currentDateFormat) {
		ArrayList<HashMap<String, Object>> currentServerResult = new ArrayList<>();
		try {
			currentServerResult.addAll(sqlSessionMysql.selectList("whms.selectCurrentServer", currentDateFormat));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return currentServerResult;
	}

	public ArrayList<HashMap<String, Object>> networkStateData(@RequestParam("server_id") String server_id) {
		ArrayList<HashMap<String, Object>> networkStateResult = new ArrayList<>();
		try {
			networkStateResult.addAll(sqlSessionMysql.selectList("whms.selectNetworkState", server_id));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return networkStateResult;
	}

	public ArrayList<HashMap<String, Object>> rankData(HashMap<String, String> param) {
		ArrayList<HashMap<String, Object>> rankResult = new ArrayList<>();
		try {
			rankResult.addAll(sqlSessionMysql.selectList("whms.selectRankData", param));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rankResult;
	}

	public ArrayList<HashMap<String, Object>> riskCheckData(HashMap<String, String> param) {
		ArrayList<HashMap<String, Object>> riskCheck = new ArrayList<>();
		try {
			riskCheck.addAll(sqlSessionMysql.selectList("whms.selectRiskCheck", param));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return riskCheck;
	}

	public ArrayList<HashMap<String, Object>> riskStateData(HashMap<String, String> param) {
		ArrayList<HashMap<String, Object>> riskState = new ArrayList<>();
		try {
			riskState.addAll(sqlSessionMysql.selectList("whms.selectRiskState", param));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return riskState;
	}

	public int updateRiskStateData(@RequestParam("server_id") String server_id, @RequestParam("init_tm") String init_tm){
	    int updateRiskStateCnt = 0;
	    try {
	        Map<String, String> paramMap = new HashMap<>();
	        paramMap.put("server_id", server_id);
	        paramMap.put("init_tm", init_tm);
	        updateRiskStateCnt = sqlSessionMysql.update("whms.updateRiskState", paramMap);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return updateRiskStateCnt;
	}
	
	public ArrayList<HashMap<String, Object>> selectWmsRemoteSessions() {
		ArrayList<HashMap<String, Object>> dataList = new ArrayList<>();
		try {
			dataList.addAll(sqlSessionMysql.selectList("whms.selectWmsRemoteSessions"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataList;
	}
}
