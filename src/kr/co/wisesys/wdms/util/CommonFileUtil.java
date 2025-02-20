package kr.co.wisesys.wdms.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonFileUtil {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
	public HashMap<String, Object> checkFileCount(int file_count, Map<String, Object> stdObject, String boardTime, String dayParam) {
		HashMap<String, Object> checkedFileList = new HashMap<>();
	    Calendar calendar = Calendar.getInstance();
	    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	    try {
		    String server_nm = stdObject.get("server_nm").toString();
	        String repo_nm = stdObject.get("repo_nm").toString();
	        int file_id = (int) stdObject.get("file_id");
	        String file_path = stdObject.get("file_path").toString();
	        int std_count = Integer.parseInt(calculateChangedValue(boardTime, stdObject, dayParam, calendar, format));
	        String error_flag = updateErrorFlag(file_count, std_count);
	
	        checkedFileList.put("server_nm", server_nm);
	        checkedFileList.put("repo_nm", repo_nm);
	        checkedFileList.put("file_id", file_id);
	        checkedFileList.put("file_path", file_path);
	        checkedFileList.put("file_count", file_count);
	        checkedFileList.put("std_count", std_count);
	        checkedFileList.put("std_tm", stdObject.get("std_tm"));
	        checkedFileList.put("error_flag", error_flag);
	        
	    } catch (Exception e) {
	        log.error(e.toString());
	    }
	    return checkedFileList;
	}
	
	private String updateErrorFlag(int fileCount, int changedValue) {
		String error_flag = "";
		if (fileCount < changedValue) {error_flag = "y";} else {error_flag = "n";}
		return error_flag;
    }
	
	public String calculateChangedValue(String stdTime, Map<String, Object> stdObject, String dayParam, Calendar calendar, SimpleDateFormat format) throws Exception {
    	String stdDate = stdTime.substring(8,10);
    	String stdHour = stdTime.substring(11, 13);
    	
		int parseStdDate = Integer.parseInt(stdDate);
        int parseStdHour = Integer.parseInt(stdHour);
        
        if (dayParam.equals("p")) {
            return pastStdCount(stdObject, parseStdDate);
        } else if (dayParam.equals("y")) {
            return yDayStdCount(stdObject, stdTime, parseStdDate, format, calendar);
        } else if (dayParam.equals("t")) {
            return todayStdCount(stdObject, stdTime, parseStdDate, parseStdHour, format, calendar);
        }
        
        return stdObject.get("std_count").toString();
    }
	
	private String pastStdCount(Map<String, Object> stdObject, int parseStdDate) {
		String changedValue = "";
        if (stdObject.get("std_count").toString().contains("dd - 2")) {
            changedValue = Integer.toString(parseStdDate - 2);
        } else if (stdObject.get("std_count").toString().contains("dd - 1")) {
            changedValue = Integer.toString(parseStdDate - 1);
        } else if (stdObject.get("std_count").toString().contains("dd")) {
        	changedValue = Integer.toString(parseStdDate);
        } else {
        	changedValue = stdObject.get("std_count").toString();
        }
        stdObject.put("std_count", changedValue);
		return changedValue;
	}
	
	private String previousStdCount(Map<String, Object> stdObject, int parseStdDate) {
		String changedValue = "";
        if (stdObject.get("prev_std_count").toString().contains("dd - 2")) {
            changedValue = Integer.toString(parseStdDate - 2);
        } else if (stdObject.get("prev_std_count").toString().contains("dd - 1")) {
            changedValue = Integer.toString(parseStdDate - 1);
        } else {
        	changedValue = stdObject.get("prev_std_count").toString();
        }
        stdObject.put("std_count", changedValue);
        stdObject.remove("prev_std_count");
        stdObject.remove("next_std_count");
        return changedValue;
    }

    private String nextStdCount(Map<String, Object> stdObject, int parseStdDate) {
    	String changedValue = "";
        if (stdObject.get("next_std_count").toString().contains("dd - 1")) {
            changedValue = Integer.toString(parseStdDate - 1);
        } else if (stdObject.get("next_std_count").toString().contains("dd")) {
        	changedValue = Integer.toString(parseStdDate);
        } else {
        	changedValue = stdObject.get("next_std_count").toString();
        }
        stdObject.put("std_count", changedValue);
        stdObject.remove("prev_std_count");
        stdObject.remove("next_std_count");
        return changedValue;
    }

    private String hourlyStdCount(Map<String, Object> stdObject, int parseStdHour) {
    	String changedValue = "";
        if (stdObject.get("std_count").toString().contains("(hh * 6) - 5")) {
        	changedValue = Integer.toString((int) ((parseStdHour * 6) - 5));
        	if (Integer.parseInt(changedValue) < 1) {
        		changedValue = "0";
        	}
        } else if (stdObject.get("std_count").toString().contains("hh * 6")) {
        	changedValue = Integer.toString((int) (parseStdHour * 6));
        } else if (stdObject.get("std_count").toString().contains("hh - 1")) {
        	changedValue = Integer.toString((int) (parseStdHour - 1));
        } else if (stdObject.get("std_count").toString().contains("hh")) {
        	changedValue = Integer.toString(parseStdHour);
    	} else {
        	changedValue = stdObject.get("std_count").toString();
        }
        stdObject.put("std_count", changedValue);
        return changedValue;
    }
    
    private String todayStdCount(Map<String, Object> stdObject, String stdTime, int parseStdDate, int parseStdHour, SimpleDateFormat format, Calendar calendar) throws Exception {
    	if (stdObject.containsKey("prev_std_count") || stdObject.containsKey("next_std_count")) {
	    	String stdYear = stdTime.substring(0,4);
	    	String stdMonth = stdTime.substring(5,7);
	    	String stdDate = stdTime.substring(8,10);
	    	
	    	Date tDayparseStdDate = calendar.getTime();
	        String stdTimePart = ((String) stdObject.get("std_tm")).substring(11);
	        String updatedStdTm = stdYear + "-" + stdMonth + "-" + stdDate + " " + stdTimePart;
	        stdObject.put("std_tm", updatedStdTm);
	        Date stdDateFormat = format.parse((String) stdObject.get("std_tm"));
	        
	        if (tDayparseStdDate.before(stdDateFormat)) {
	            return previousStdCount(stdObject, parseStdDate);
	        } else if (tDayparseStdDate.after(stdDateFormat)) {
	            return nextStdCount(stdObject, parseStdDate);
	        } 
    	} else if (((String) stdObject.get("std_tm")).contains("hh")) {
            return hourlyStdCount(stdObject, parseStdHour);
        }
        return stdObject.get("std_count").toString();
    }
    
    private String yDayStdCount(Map<String, Object> stdObject, String stdTime, int parseStdDate, SimpleDateFormat format, Calendar calendar) throws Exception {
        if (stdObject.containsKey("prev_std_count")||stdObject.containsKey("next_std_count")) {
            calendar.set(Calendar.DAY_OF_MONTH, parseStdDate);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            String stdYear = stdTime.substring(0,4);
	    	String stdMonth = stdTime.substring(5,7);
            Date yDayparseStdDate = calendar.getTime();
            String stdTimePart = ((String) stdObject.get("std_tm")).substring(11);
            String updatedStdTm = stdYear + "-" + stdMonth + "-" + String.format("%02d",parseStdDate + 1) + " " + stdTimePart;
            
            stdObject.put("std_tm", updatedStdTm);
            Date parseStdDateFormat = format.parse((String) stdObject.get("std_tm"));
            if (yDayparseStdDate.before(parseStdDateFormat)) {
                return previousStdCount(stdObject, parseStdDate);
            } else {
                return nextStdCount(stdObject, parseStdDate);
            }
        } else {
            return pastStdCount(stdObject, parseStdDate);
        }
    }
    
    /**
     * 파일 아이디 매핑
     */
	public Map<String, Integer> getFileMapping() {
	    Map<String, Integer> fileMapping = new HashMap<>();
	    
	    fileMapping.put("asos_ssb_pet_10min", 1);
	    fileMapping.put("asos_ssb_pet_1day", 2);
	    fileMapping.put("g120_v070_erea_unis_han_172ssb_10m/03", 21);
	    fileMapping.put("g120_v070_erea_unis_han_172ssb_10m/09", 22);
	    fileMapping.put("g120_v070_erea_unis_han_172ssb_10m/15", 23);
	    fileMapping.put("g120_v070_erea_unis_han_172ssb_10m/21", 24);
	    fileMapping.put("kma_asos_1day", 25);
	    fileMapping.put("kma_asos_1hr", 28);
	    fileMapping.put("kma_aws_10min_qc", 31);
	    fileMapping.put("l015_v070_erlo_unis_han_172ssb_10m/03", 42);
	    fileMapping.put("l015_v070_erlo_unis_han_172ssb_10m/09", 43);
	    fileMapping.put("l015_v070_erlo_unis_han_172ssb_10m/15", 44);
	    fileMapping.put("l015_v070_erlo_unis_han_172ssb_10m/21", 45);
	    return fileMapping;
	}
	
	/**
     * 파일 확장자 설정
     */
    public String fileExtension(int fileId) {
    	String fileExtension = "";
    	
    	if (fileId == 1) fileExtension = ".txt"; 
    	if (fileId == 2) fileExtension = ".txt";
    	if (fileId == 21) fileExtension = ".txt";
    	if (fileId == 22) fileExtension = ".txt";
    	if (fileId == 23) fileExtension = ".txt";
    	if (fileId == 24) fileExtension = ".txt";
    	if (fileId == 42) fileExtension = ".txt";
    	if (fileId == 43) fileExtension = ".txt";
    	if (fileId == 44) fileExtension = ".txt";
    	if (fileId == 45) fileExtension = ".txt";
    	if (fileId == 25) fileExtension = ".csv";
    	if (fileId == 28) fileExtension = ".csv";
    	if (fileId == 31) fileExtension = ".csv";
    	
	    return fileExtension;
	}
	
    /**
     * 타입 시간별 값 반환, DB 업데이트용
     */ 
   public int getHourlyValue(String fileType,String issuedate) {
	   
	   Map<String, Integer> fileMapping = getFileMapping();
	   Integer fileId = fileMapping.get(fileType);
	   
	   DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
	   LocalDateTime dateTime = LocalDateTime.parse(issuedate, formatter);
	   LocalDate issuedDateParsed = dateTime.toLocalDate(); 

	   LocalDate today = LocalDate.now();
	   LocalDate yesterday = today.minusDays(1);
	   LocalTime now = LocalTime.now();
	   
	   boolean isPreviousMonth = (issuedDateParsed.getMonthValue() < today.getMonthValue() && 
               issuedDateParsed.getYear() == today.getYear()) ||
               (issuedDateParsed.getYear() < today.getYear());
	   
	   String suffix = "";
	   
	   int hour = now.getHour() -1;
	   
	   //log.info("현재시간 : " + hour);
	   
	   if(fileId == 1) { // asos_ssb_pet_10min
		   if (issuedDateParsed.isEqual(today)) return hour * 7 - (hour-1); // 오늘일때
		   if (issuedDateParsed.isEqual(yesterday)) return now.isBefore(LocalTime.of(12, 0)) ? 139 : 144; // 어제일때 12:00 이전: 139, 이후:144 반환
		   if (issuedDateParsed.isBefore(yesterday)) return 144; // 과거일때
	   } else if (fileId == 31) { // kma_aws_10min_qc
		   if (issuedDateParsed.isEqual(today)) return (hour + 1) * 6; // 오늘일때
		   else return 144; // 어제,과거일때 144 반환
       } else if (fileId == 28) { // kma_asos_1hr
    	   if (issuedDateParsed.isEqual(today)) return hour + 1; // 오늘일때
    	   else return 24; // 어제,과거일때 24 반환
       } else if (fileId == 21) { // g120_v070_erea_unis_han_172ssb_10m/03
    	   if (issuedDateParsed.isEqual(today)) return now.isBefore(LocalTime.of(7, 0)) ? 0 : 1044; // 오늘일때 07:00 이전:0, 이후:1044 반환
    	   else return 1044; // 어제,과거일때 1044 반환
       } else if (fileType.startsWith("g120_v070_erea_unis_han_172ssb_10m")) {
    	   suffix = fileType.substring(fileType.lastIndexOf("/"));
    	   if(suffix.equals("/03")) {
    		   if (issuedDateParsed.isEqual(today)) return now.isBefore(LocalTime.of(7, 0)) ? 0 : 1044; // 오늘일때 07:00 이전: 0, 이후:1044 반환
    		   else return 1044;
    	   } else if(suffix.equals("/09")) {
    		   if (issuedDateParsed.isEqual(today)) return now.isBefore(LocalTime.of(13, 0)) ? 0 : 1044; // 오늘일때 13:00 이전: 0, 이후:1044 반환
    		   else return 1044;
    	   } else if(suffix.equals("/15")) {
    		   if (issuedDateParsed.isEqual(today)) return now.isBefore(LocalTime.of(19, 0)) ? 0 : 1044; // 오늘일때 19:00 이전: 0, 이후:1044 반환
    		   else return 1044;
    	   } else if(suffix.equals("/21")) {
    		   if(issuedDateParsed.isEqual(today)) { // 오늘일때 0
    			   return 0;
    		   } else if(issuedDateParsed.isEqual(yesterday)) { // 어제일때
    			   return now.isBefore(LocalTime.of(2, 0)) ? 0 : 1044; // 오늘일때 02:00 이전: 0, 이후:1044 반환
    		   } else {
    			   return 1044;
    		   }
    	   } 
       } else if (fileType.startsWith("l015_v070_erlo_unis_han_172ssb_10m")) {
    	   suffix = fileType.substring(fileType.lastIndexOf("/"));
    	   if(suffix.equals("/03")) {
    		   if (issuedDateParsed.isEqual(today)) return now.isBefore(LocalTime.of(7, 0)) ? 0 : 576; // 오늘일때 07:00 이전: 0, 이후:576 반환
    		   else return 576;
    	   } else if(suffix.equals("/09")) {
    		   if (issuedDateParsed.isEqual(today)) return now.isBefore(LocalTime.of(13, 0)) ? 0 : 576; // 오늘일때 13:00 이전: 0, 이후:576 반환
    		   else return 576;
    	   } else if(suffix.equals("/15")) {
    		   if (issuedDateParsed.isEqual(today)) return now.isBefore(LocalTime.of(19, 0)) ? 0 : 576; // 오늘일때 19:00 이전: 0, 이후:576 반환
    		   else return 576;
    	   } else if(suffix.equals("/21")) {
    		   if(issuedDateParsed.isEqual(today)) { // 오늘일때 0
    			   return 0;
    		   } else if(issuedDateParsed.isEqual(yesterday)) { // 어제일때
    			   return now.isBefore(LocalTime.of(2, 0)) ? 0 : 576; // 오늘일때 02:00 이전: 0, 이후:576 반환
    		   } else {
    			   return 576;
    		   }
    	   } 
       } else if(fileId == 2) { // asos_ssb_pet_1day
    	   if (issuedDateParsed.isEqual(today)) {
    		   if((hour+1) < 12) return today.getDayOfMonth() -2;
    		   else return today.getDayOfMonth() -1;
    	   } else if(isPreviousMonth) {
    		   int lastDayOfMonth = issuedDateParsed.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();
    		   return lastDayOfMonth;
    	   } else {
    		   if((hour+1) < 12) return today.getDayOfMonth() -2; 
    		   else return today.getDayOfMonth() -1;  
    	   }
       } else if(fileId == 25) { // kma_asos_1day
    	   if (issuedDateParsed.isEqual(today)) {
    		   if((hour+1) < 12) return today.getDayOfMonth() -1;
    		   else return today.getDayOfMonth();
    	   } else if(isPreviousMonth) {
    		   int lastDayOfMonth = issuedDateParsed.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();
    		   return lastDayOfMonth;
    	   } else {
    		   if((hour+1) < 12) return today.getDayOfMonth() -1;
    		   else return today.getDayOfMonth();  
    	   }
       }
	   throw new IllegalArgumentException("처리되지 않은 파일 타입: " + fileType);
   }
}