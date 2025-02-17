package kr.co.wisesys.wsms.dao;

import java.util.ArrayList;
import java.util.HashMap;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class WsmsDAO{
	
	@Autowired
	private SqlSessionTemplate sqlSessionMysql;
	
	/**
	 * 시스템 정보 등록
	 * @param 
	 * @return 
	*/
	public void inputSystemInfo(HashMap<String, Object> param) {
		try {
			sqlSessionMysql.insert("wsms.inputSystemInfo",param);
		} catch(NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 시스템 max_id
	 * @param 
	 * @return 
	*/
	public int systemMaxOrd() {
		int result = 0;
		try {
			result = sqlSessionMysql.selectOne("wsms.systemMaxOrd");
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
		try {
			sqlSessionMysql.update("wsms.updateSystemInfo",param);
		} catch(NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 시스템 정보 삭제
	 * @param 
	 * @return 
	*/
	public void deleteSystemInfo(HashMap<String, Object> param) {
		try {
			sqlSessionMysql.insert("wsms.deleteSystemInfo",param);
		} catch(NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 시스템 리스트
	 * @param 
	 * @return 
	*/
	public ArrayList<HashMap<String, Object>> allSystemInfo() {
		ArrayList<HashMap<String, Object>> result = new ArrayList<>();
		try {
			result.addAll(sqlSessionMysql.selectList("wsms.allSystemInfo"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result; 
	}
	
	/**
	 * 시스템 리스트
	 * @param 
	 * @return 
	*/
	public ArrayList<HashMap<String, Object>> selectSystemInfo() {
		ArrayList<HashMap<String, Object>> result = new ArrayList<>();
		try {
			result.addAll(sqlSessionMysql.selectList("wsms.selectSystemInfo"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result; 
	}
	
	public ArrayList<HashMap<String, Object>> getLatestData(HashMap<String, Object> param) {
		ArrayList<HashMap<String, Object>> result = new ArrayList<>();
		try {
			result.addAll(sqlSessionMysql.selectList("wsms.getLatestData",param));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result; 
	}
	
}
