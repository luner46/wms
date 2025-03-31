package kr.co.wisesys.wdms.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

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
        updateStnInfo();
        log.info("Scheduled Update");
    }
    
    // updateStnInfoMonthly 매 월 02일 02시에 실행 
    @Scheduled(cron = "0 0 2 2 * *")
    public void scheduledUpdateStnInfoMonthly() {
    	updateStnInfoMonthly();
        log.info("Scheduled Monthly Update");
    }

    // insertFileCount_매 시간 30초에 실행
    @Scheduled(cron = "30 0 * * * *")
    public void scheduledInsertFileCount() {
    	// 당일 데이터 INSERT
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String todayboardTime = format.format(calendar.getTime());
        // 전일 데이터 INSERT
        calendar.add(Calendar.DATE, -1);
        String ydayBoardTime = format.format(calendar.getTime());

        contController.insertFileCount(ydayBoardTime, "y", "all");
        contController.insertFileCount(todayboardTime, "t", "all");
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
    
    private int updateStnInfoMonthly() {
    	int updateResult = 0;
    	
    	ArrayList<HashMap<String, Object>> selectKmaAsosYmList = new ArrayList<>();
    	ArrayList<HashMap<String, Object>> selectKmaAwsYmList = new ArrayList<>();
    	
    	selectKmaAsosYmList.addAll(sqlSessionMysql.selectList("wdms.selectKmaAsosYmList"));
    	selectKmaAwsYmList.addAll(sqlSessionMysql.selectList("wdms.selectKmaAwsYmList"));
	    
	    int asosLatestYyyymm = Integer.MIN_VALUE;
	    int asosPrevYyyymm = Integer.MIN_VALUE;

	    int awsLatestYyyymm = Integer.MIN_VALUE;
	    int awsPrevYyyymm = Integer.MIN_VALUE;
	    
	    for (int i = 0; i < selectKmaAsosYmList.size(); i++) {
	    	int asosStdYyyymm = Integer.parseInt(selectKmaAsosYmList.get(i).get("yyyymm").toString());

	        if (asosStdYyyymm > asosLatestYyyymm) {
	        	asosPrevYyyymm = asosLatestYyyymm;
	            asosLatestYyyymm = asosStdYyyymm;
	        } else if (asosStdYyyymm > asosPrevYyyymm && asosStdYyyymm != asosLatestYyyymm) {
	        	asosPrevYyyymm = asosStdYyyymm;
	        }
	    }

	    for (int i = 0; i < selectKmaAwsYmList.size(); i++) {
	    	int awsStdYyyymm = Integer.parseInt(selectKmaAwsYmList.get(i).get("yyyymm").toString());

	        if (awsStdYyyymm > awsLatestYyyymm) {
	        	awsPrevYyyymm = awsLatestYyyymm;
	            awsLatestYyyymm = awsStdYyyymm;
	        } else if (awsStdYyyymm > awsPrevYyyymm && awsStdYyyymm != awsLatestYyyymm) {
	        	awsPrevYyyymm = awsStdYyyymm;
	        }
	    }
	    
	    HashMap<String, Object> paramAsos = new HashMap<>();
	    paramAsos.put("latestYyyymm", asosLatestYyyymm);
	    paramAsos.put("prevYyyymm", asosPrevYyyymm);
    	
    	HashMap<String, Object> paramAws = new HashMap<>();
    	paramAws.put("latestYyyymm", awsLatestYyyymm);
    	paramAws.put("prevYyyymm", awsPrevYyyymm);

	    try {
	        // kma_asos_stn_info
	        updateResult += sqlSessionMysql.update("wdms.updateKmaAsosStnInfoFlag", paramAsos);
	        updateResult += sqlSessionMysql.update("wdms.updateKmaAsosStnInfoDelFlag", paramAsos);
	
	        // kma_aws_stn_info
	        updateResult += sqlSessionMysql.update("wdms.updateKmaAwsStnInfoFlag", paramAws);
	        updateResult += sqlSessionMysql.update("wdms.updateKmaAwsStnInfoDelFlag", paramAws);
	
	        return updateResult;
	    } catch (Exception e) {
	        throw new RuntimeException("Update fail", e);
	    }
    }
}