package kr.co.wisesys.wdms.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import kr.co.wisesys.wdms.controller.WdmsContController;

@Component
public class CommonScheduleUtil {

	@Autowired
	private SqlSessionTemplate sqlSessionMysql;
	
	@Autowired
    private WdmsContController contController;
	 
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    // updateMeStnInfo_매일 02시에 실행
    @Scheduled(cron = "0 0 2 * * *")
    public void scheduledUpdateStnInfo() {
        int updateResult = updateStnInfo();
        log.info("Scheduled Update={}",updateResult);
    }

    // insertFileCount_매 시간 1분에 실행
    @Scheduled(cron = "0 1 * * * *")
    public void scheduledInsertFileCount() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String boardTime = format.format(calendar.getTime());

        contController.insertFileCount(boardTime, "t");
        log.info("Scheduled Insert");
    }
    
    private int updateStnInfo() {
    	int updateResult = 0;
    	Calendar calendar = Calendar.getInstance();
	    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
	    calendar.add(Calendar.DATE, -1);
	    String today = format.format(calendar.getTime());
	    calendar.add(Calendar.DATE, -1);
	    String yesterday = format.format(calendar.getTime());
	    
	    HashMap<String, Object> param = new HashMap<>();
    	param.put("today", today);
    	param.put("yesterday", yesterday);
	    
		try {
	        // me_rn_stn_info
	        updateResult += sqlSessionMysql.update("wdms.updateMeRnStnInfoFlag", param);
	        updateResult += sqlSessionMysql.update("wdms.updateMeRnStnInfoDelFlag", param);
	
	        // me_wl_stn_info
	        updateResult += sqlSessionMysql.update("wdms.updateMeWlStnInfoFlag", param);
	        updateResult += sqlSessionMysql.update("wdms.updateMeWlStnInfoDelFlag", param);
	
	        // me_dam_stn_info
	        updateResult += sqlSessionMysql.update("wdms.updateMeDamStnInfoFlag", param);
	        updateResult += sqlSessionMysql.update("wdms.updateMeDamStnInfoDelFlag", param);
	
	        return updateResult;
	    } catch (Exception e) {
	        throw new RuntimeException("Update fail", e);
	    }
    }
}