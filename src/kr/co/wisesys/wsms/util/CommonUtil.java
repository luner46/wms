package kr.co.wisesys.wsms.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonUtil {
	
	private static final Logger log = LoggerFactory.getLogger(CommonUtil.class);
	
	/**
	 * 해당 디렉토리 경로 읽은후 파일리스트 담아서 리턴
	 * <pre>
	 * ex)
	 * @param <b>directoryPath (String)</b> ex) C:\workspace\eclipse_workspace\wise\sufps\WebContent\radar_image\
	 * @param <b>issueDate (ArrayList {@literal <String>})</b> ex) ["202405281000"]
	 * @param <b>fcstDtList (ArrayList {@literal <String>})</b> ex) ["202405281010", "202405281020"]
	 * @return <b>ArrayList {@literal <String>} fileList</b> ex) [C:\workspace\eclipse_workspace\wise\sufps\WebContent\radar_image\radar_image_202405281020_202405281030.jpg]
	*/
	public static HashMap<Integer, String> getFiles(String directoryPath, String issueDate, ArrayList<String> fcstDtList) {
	    HashMap<Integer, String> fileMap = new HashMap<>();
	    int interval = 10;

	    for (int i = 0; i < 18; i++) { 
	        String fcstDtTime = String.format("%03d", (i + 1) * 10);
	        String filePath = directoryPath + "uff_fcst_radar/" + issueDate.substring(0, 4) + "/" + issueDate.substring(4, 6) + "/" + issueDate.substring(6, 8) + "/" + issueDate + "+" + fcstDtTime + "min.png";
	        int key = (i + 1) * interval;

	        if (urlExists(filePath)) {
	            fileMap.put(key, filePath);
	        } else {
	            fileMap.put(key, null); // 파일이 존재하지 않을 경우 null 
	        }
	    }
	    // log.info("fileMap : " + fileMap);
	    return fileMap;
	}

	private static boolean urlExists(String urlString) {
	    try {
	        URL url = new URL(urlString);
	        HttpURLConnection huc = (HttpURLConnection) url.openConnection();
	        huc.setRequestMethod("GET"); // GET 요청으로 변경
	        huc.setConnectTimeout(5000); // 연결 시간 초과 설정 (5초)
	        huc.setReadTimeout(5000); // 읽기 시간 초과 설정 (5초)

	        // 응답 코드를 확인합니다.
	        int responseCode = huc.getResponseCode();
	        if (responseCode == HttpURLConnection.HTTP_OK) {
	            // 응답 내용을 읽어 특정 문자열을 검색합니다.
	            InputStream inputStream = huc.getInputStream();
	            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
	            StringBuilder content = new StringBuilder();
	            String line;
	            while ((line = reader.readLine()) != null) {
	                content.append(line);
	            }
	            reader.close();

	            return !content.toString().contains("잘못된 경로입니다.");
	        } else {
	            return false;
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	
	
	
}