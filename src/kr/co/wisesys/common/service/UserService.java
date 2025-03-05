package kr.co.wisesys.common.service;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import kr.co.wisesys.common.dao.UserDAO;


@Configuration
@Service
public class UserService {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private UserDAO dao;

	public boolean userLogin(HashMap<String, Object> param) {
        
        int count_check = dao.userLogin(param);

        if (count_check == 1) {
            return true; 
        }
        return false; 
    }
	
	
}
