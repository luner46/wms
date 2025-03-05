package kr.co.wisesys.common.dao;


import java.util.HashMap;

import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAO{
	
	@Autowired
	private SqlSessionTemplate sqlSessionMysql;
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	/**
	 *  사용자 로그인 체크
	 * @param 
	 * @return map
	*/
	public int userLogin(HashMap<String, Object> param) {
		int result = 0;
		try {
			result = sqlSessionMysql.selectOne("wms.userLogin", param);
		} catch(NullPointerException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
}
