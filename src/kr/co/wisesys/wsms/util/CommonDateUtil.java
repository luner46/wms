package kr.co.wisesys.wsms.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonDateUtil {
	
	private static final Logger log = LoggerFactory.getLogger(CommonDateUtil.class);
	
	/**
	 * 날짜 빈 곳 채우기 (채울 리스트, 포맷, 모드, 단위)
	 * <pre>
	 * ex)
	 * ArrayList {@literal <String>} dateArrayList = new ArrayList{@literal <String>} ();
	 * dateArrayList.add("20060900");
	 * dateArrayList.add("20061000");
	 * dateArrayList = Common.insertDateList(dateArrayList, "yyMMddHH", "hour", 1);
	 * System.out.println(dateArrayList.toString());
	 * </pre>
	 * @author  안주영
	 * @version 1.0
	 * @param <b>formatString (String)</b> ex) "yyMMddHH"
	 * @param <b>returnArrayList (ArrayList {@literal <String>})</b> ex) ["20060900", "20061000"]
	 * @param <b>fillMod (String)</b> ex) "year", "month", "day", "hour", "minute"
	 * @param <b>timeUnit (int)</b> ex) 1
	 * @return <b>ArrayList {@literal <String>} returnArrayList</b> ex) ["20060900", ... , "20061000"]
	*/
	public static ArrayList <String> insertDateList(ArrayList <String> returnArrayList, String formatString, String fillMod, int timeUnit) {
		ArrayList <Date> dateArrayList = new ArrayList <Date>();
		ArrayList <Date> compareArrayList = new ArrayList <Date>();
		Calendar calMin = Calendar.getInstance();
		Calendar calMax = Calendar.getInstance();
		SimpleDateFormat transFormat;
		int calMod;
		
		try {
			switch (fillMod) {
				case "year":
					calMod = Calendar.YEAR;
					break;
				case "month":
					calMod = Calendar.MONTH;
					break;
				case "day":
					calMod = Calendar.DAY_OF_YEAR;
					break;
				case "hour":
					calMod = Calendar.HOUR;
					break;
				case "minute":
					calMod = Calendar.MINUTE;
					break;
				default:
					System.out.println("fillMod 확인필요.");
					return null;
			}
			
			// 포맷 확인 및 날짜리스트로 치환
			transFormat = new SimpleDateFormat(formatString);
			transFormat.setLenient(false);
			
			for (String value : returnArrayList) {
				dateArrayList.add(transFormat.parse(value));
			}
			
			// 최대 최소 구하고 채우기
			calMin.setTime(Collections.min(dateArrayList));
			calMax.setTime(Collections.max(dateArrayList));
			
			while(calMin.compareTo(calMax) == -1) {
				compareArrayList.add(calMin.getTime());
				calMin.add(calMod, Math.abs(timeUnit));
			}
			
			compareArrayList.add(calMax.getTime());
			
			// 초기화 후 대입하고 반환
            returnArrayList.clear();
            
            for (Date value : compareArrayList) {
            	returnArrayList.add(transFormat.format(value));
			}
            
            return returnArrayList;
            
		}catch(NullPointerException e) {
			// e.printStackTrace();
			log.error(e.toString());
        	return null;
		} catch (ParseException e) {
			// e.printStackTrace();
			log.error(e.toString());
			return null;
		}
		
	}
	
	
	/**
	 * 현재 시간 호출
	 * <pre>
	 * ex) 
	 * String nowDt = CommonDateUtil.selectNowDate();
	 * </pre>
	 * @author  안주영
	 * @version 1.0
	 * @return <b>returnString (String) </b> ex) "202001011200"
	*/
	public static String selectNowDate() {
		String returnString = "";
		String dateFormat = "";
		Date useDate;
		
		try {
			dateFormat = "yyyyMMddHHmm";
			useDate = new Date();
			SimpleDateFormat transFormat = new SimpleDateFormat(dateFormat);
			
			returnString = transFormat.format(useDate);
			
		}catch(NullPointerException e) {
			// e.printStackTrace();
			log.error(e.toString());
		}
		
		return returnString;
	}
	
	
	/**
	 * 현재 시간 호출 오버로딩(형식으로 받을 포맷)
	 * <pre>
	 * ex)
	 * String nowDt = CommonDateUtil.selectNowDate("yyyyMMdd");
	 * </pre>
	 * @author  안주영
	 * @version 1.0
	 * @param <b>formatString (String)</b> ex) "yyyyMMdd"
	 * @return <b>returnString (String)</b> ex) "20200101"
	*/
	public static String selectNowDate(String formatString) {
		String returnString = "";
		String dateFormat = formatString;
		Date useDate;
		
		try {
			useDate = new Date();
			SimpleDateFormat transFormat = new SimpleDateFormat(dateFormat);
			
			returnString = transFormat.format(useDate);
			
		}catch(NullPointerException e) {
			// e.printStackTrace(); 
			log.error(e.toString());
		} 
		
		return returnString;
	}
	
	
	/**
	 * 날짜 포맷 변화(날짜문자열, 넣을포맷, 받을포맷)
	 * <pre>
	 * ex)
	 * String nowDt = CommonDateUtil.selectNowDate("20200101", "yyyyMMdd", "yyyy-MM-dd");
	 * </pre>
	 * @author  안주영
	 * @version 1.0
	 * @param <b>paramDateString (String)</b> ex) "20200101"
	 * @param <b>paramFormatString (String)</b> ex) "yyyyMMdd"
	 * @param <b>returnFormatString (String)</b> ex) "yyyy-MM-dd"
	 * @return <b>returnString (String)</b> ex) "2020-01-01"
	*/
	public static String updateDateFormat(String paramDateString, String paramFormatString, String returnFormatString) {
		String returnString = "";
		Date useDate;
		
		try {
			SimpleDateFormat paramFormat = new SimpleDateFormat(paramFormatString);
			SimpleDateFormat returnFormat = new SimpleDateFormat(returnFormatString);
			
			useDate = paramFormat.parse(paramDateString);
			
			returnString = returnFormat.format(useDate);
			
		}catch(NullPointerException e) {
			// e.printStackTrace(); 
			log.error(e.toString());
		} catch (ParseException e) {
			// e.printStackTrace();
			log.error(e.toString());
		}
		
		return returnString;
	}
	
	
	/**
	 * 전, 후 시간 호출(시간, 모드, +-단위, 갯수, 들어오는 포맷, 나가는 포맷) 
	 * <pre>
	 * ex) 
	 * ArrayList{@literal <String>} returnStringList = CommonDateUtil.selectListDate("202001101200", "day", -1, 9,"yyyyMMddHHmm", "yyyyMMdd");
	 * </pre>
	 * @author  안주영
	 * @version 1.0
	 * @param <b>selectDate (String)</b> ex) "202001101200"
	 * @param <b>dateMod (String)</b> ex) "year", "month", "day", "hour", "minute"
	 * @param <b>timeUnit (int)</b> ex) -1
	 * @param <b>dateCount (int)</b> ex) 9
	 * @param <b>dateFormatstr (String)</b> ex) "yyyyMMddHHmm"
	 * @param <b>transFormatstr (String)</b> ex) "yyyyMMdd"
	 * @return <b>returnStringList (ArrayList{@literal <String>})</b> ex) ["2020010", ... , "2020001"]
	*/
	public static ArrayList<String> selectListDate(String selectDate, String dateMod, int timeUnit, int dateCount, String dateFormatString, String transFormatString) {
		ArrayList<String> returnStringList = new ArrayList<String>();
		Date toDate;
		Calendar cal = Calendar.getInstance();
		int calMod;
		
		try {
			SimpleDateFormat transFormat = new SimpleDateFormat(transFormatString);
			SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatString);
			
			if (dateCount <= 0) {
				System.out.println("dateCount 값 확인 필요.");
				return null;
			}
			
			switch (dateMod) {
				case "year":
					calMod = Calendar.YEAR;
					break;
				case "month":
					calMod = Calendar.MONTH;
					break;
				case "day":
					calMod = Calendar.DAY_OF_YEAR;
					break;
				case "hour":
					calMod = Calendar.HOUR;
					break;
				case "minute":
					calMod = Calendar.MINUTE;
					break;
				default:
					System.out.println("fillMod 확인필요.");
					return null;
			}
			
			dateFormat.setLenient(false);
			toDate = dateFormat.parse(selectDate);
			returnStringList.add(transFormat.format(toDate));
			cal.setTime(toDate);
			
			for (int i = 1; i < dateCount ; i++) {
				cal.add(calMod, timeUnit);
				returnStringList.add(transFormat.format(cal.getTime()));
			}
			
		}catch(NullPointerException e) {
			// e.printStackTrace(); 
			log.error(e.toString());
			return null;
		} catch (ParseException e) {
			// e.printStackTrace();
			log.error(e.toString());
			return null;
		}
		
		return returnStringList;
	}
	
	
	/**
	 * 전, 후 시간 호출 포맷같을때(시간, 모드, +-단위, 갯수, 포맷) 
	 * <pre>
	 * ex) 
	 * ArrayList{@literal <String>} returnStringList = CommonDateUtil.selectListDate("202001101200", "day", -1, 9,"yyyyMMddHHmm");
	 * </pre>
	 * @author  안주영
	 * @version 1.0
	 * @param <b>selectDate (String)</b> ex) "202001011200"
	 * @param <b>dateMod (String)</b> ex) "year", "month", "day", "hour", "minute"
	 * @param <b>timeUnit (int)</b> ex) -1
	 * @param <b>dateCount (int)</b> ex) 9
	 * @param <b>dateFormatString (String)</b> ex) "yyyyMMddHHmm"
	 * @return <b>returnStringList (ArrayList{@literal <String>})</b> ex) ["202001101200", ... , "202001011200"]
	*/
	public static ArrayList<String> selectListDate(String selectDate, String dateMod, int timeUnit, int dateCount, String dateFormatString) {
		ArrayList<String> returnStringList = new ArrayList<String>();
		Date toDate;
		Calendar cal = Calendar.getInstance();
		int calMod;
		
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatString);
			
			if (dateCount <= 0) {
				System.out.println("dateCount 값 확인 필요.");
				return null;
			}
			
			switch (dateMod) {
				case "year":
					calMod = Calendar.YEAR;
					break;
				case "month":
					calMod = Calendar.MONTH;
					break;
				case "day":
					calMod = Calendar.DAY_OF_YEAR;
					break;
				case "hour":
					calMod = Calendar.HOUR;
					break;
				case "minute":
					calMod = Calendar.MINUTE;
					break;
				default:
					System.out.println("fillMod 확인필요.");
					return null;
			}
			
			dateFormat.setLenient(false);
			toDate = dateFormat.parse(selectDate);
			returnStringList.add(dateFormat.format(toDate));
			cal.setTime(toDate);
			
			for (int i = 1; i < dateCount ; i++) {
				cal.add(calMod, timeUnit);
				returnStringList.add(dateFormat.format(cal.getTime()));
			}
			
		}catch(NullPointerException e) {
			// e.printStackTrace(); 
			log.error(e.toString());
			return null;
		} catch (ParseException e) {
			// e.printStackTrace();
			log.error(e.toString());
			return null;
		}
		
		return returnStringList;
	}
	
	
	/**
	 * 전, 후 시간 호출 포맷기본 yyyyMMddHHmm(시간, 모드, +-단위, 갯수) 
	 * <pre>
	 * ex) 
	 * ArrayList{@literal <String>} returnStringList = CommonDateUtil.selectListDate("202001101200", "day", -1, 9);
	 * </pre>
	 * @author  안주영
	 * @version 1.0
	 * @param <b>selectDate (String)</b> ex) "202001011200"
	 * @param <b>dateMod (String)</b> ex) "year", "month", "day", "hour", "minute"
	 * @param <b>timeUnit (int)</b> ex) -1
	 * @param <b>dateCount (int)</b> ex) 9
	 * @return <b>returnStringList (ArrayList{@literal <String>})</b> ex) ["202001101200", ... , "202001011200"]
	*/
	public static ArrayList<String> selectListDate(String selectDate, String dateMod, int timeUnit, int dateCount) {
		ArrayList<String> returnStringList = new ArrayList<String>();
		String dateFormatString = "yyyyMMddHHmm";
		Date toDate;
		Calendar cal = Calendar.getInstance();
		int calMod;
		
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatString);
			
			if (dateCount <= 0) {
				System.out.println("dateCount 값 확인 필요.");
				return null;
			}
			
			switch (dateMod) {
				case "year":
					calMod = Calendar.YEAR;
					break;
				case "month":
					calMod = Calendar.MONTH;
					break;
				case "day":
					calMod = Calendar.DAY_OF_YEAR;
					break;
				case "hour":
					calMod = Calendar.HOUR;
					break;
				case "minute":
					calMod = Calendar.MINUTE;
					break;
				default:
					System.out.println("fillMod 확인필요.");
					return null;
			}
			
			dateFormat.setLenient(false);
			toDate = dateFormat.parse(selectDate);
			returnStringList.add(dateFormat.format(toDate));
			cal.setTime(toDate);
			
			for (int i = 1; i < dateCount ; i++) {
				cal.add(calMod, timeUnit);
				returnStringList.add(dateFormat.format(cal.getTime()));
			}
			
		}catch(NullPointerException e) {
			// e.printStackTrace(); 
			log.error(e.toString());
			return null;
		} catch (ParseException e) {
			// e.printStackTrace();
			log.error(e.toString());
			return null;
		}
		
		return returnStringList;
	}
	
	public static String addMinutesToDate(String issut_date, int minutes) {
	    String dateFormatString = "yyyyMMddHHmm";
	    SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatString);
	    Calendar cal = Calendar.getInstance();

	    try {
	        Date toDate = dateFormat.parse(issut_date);
	        cal.setTime(toDate);
	        cal.add(Calendar.MINUTE, minutes);
	        return dateFormat.format(cal.getTime());
	    } catch (ParseException e) {
	        log.error(e.toString());
	        return null;
	    }
	}
	
}