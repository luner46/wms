package kr.co.wisesys.wsms.controller;







import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import kr.co.wisesys.wsms.service.WsmsService;


@Controller
@RequestMapping(value={"/wsmsCont/*"})
public class WsmsContController {
	
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Value("#{config['host']}") private String host;
	@Value("#{config['port']}") private int port;
	@Value("#{config['user']}") private String user;
	@Value("#{config['password']}") private String password;

	@Autowired
	private WsmsService service;
	
	/**
	 * 시스템 정보 등록
	 * @return String
	*/
	@RequestMapping(value = "/inputSystemInfo.do", method = RequestMethod.POST)
	@ResponseBody
	public HashMap<String, String> inputSystemInfo(@RequestBody HashMap<String, Object> param) {
	    HashMap<String, String> response = new HashMap<>();
	    try {
	    	int max_system_ord = service.systemMaxOrd();

	    	String systemOrdStr = "";
	    	if (param.get("system_ord") != null && !param.get("system_ord").toString().isEmpty()) {
	    	    systemOrdStr = param.get("system_ord").toString();
	    	} else {
	    	    systemOrdStr = String.valueOf(max_system_ord);
	    	}

	    	int system_ord = Integer.parseInt(systemOrdStr);

	    	//log.info("system_ord : " + system_ord);
	        
	        String system_nm = param.get("system_nm") == null ? "" : param.get("system_nm").toString();
	        String system_url = param.get("system_url") == null ? "" : param.get("system_url").toString();
	        String req_time = param.get("system_req") == null ? "" : param.get("system_req").toString();
	        String res_time = param.get("system_res") == null ? "" : param.get("system_res").toString();
	        String system_plc = param.get("system_plc") == null ? "" : param.get("system_plc").toString();
	        String system_agc = param.get("system_agc") == null ? "" : param.get("system_agc").toString();
	        
	        //log.info("system_ord : " + system_ord);
	        //log.info("system_nm : " + system_nm);
	        //log.info("system_url : " + system_url);
	        //log.info("req_time : " + req_time);
	        //log.info("res_time : " + res_time);
	        //log.info("system_plc : " + system_plc);
	        //log.info("system_agc : " + system_agc);
	        
	        param.put("system_ord", system_ord);
	        param.put("system_nm", system_nm);
	        param.put("system_url", system_url);
	        param.put("req_time", req_time);
	        param.put("res_time", res_time);
	        param.put("server_place", system_plc);
	        param.put("order_agency", system_agc);

	        service.inputSystemInfo(param);

	        response.put("status", "success");
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("status", "error");
	    }

	    return response; 
	}
	
	/**
	 * 시스템 정보 업데이트
	 * @return String
	*/
	@RequestMapping(value = "/updateSystemInfo.do", method = RequestMethod.POST)
	@ResponseBody
	public HashMap<String, String> updateSystemInfo(@RequestBody HashMap<String, Object> param) {
	    HashMap<String, String> response = new HashMap<>();
	    try {
	        int system_id = Integer.parseInt(param.get("system_id").toString());
	        int system_ord = Integer.parseInt(param.get("system_ord").toString());
	        
	        String system_nm = param.get("system_nm") == null ? "" : param.get("system_nm").toString();
	        String system_url = param.get("system_url") == null ? "" : param.get("system_url").toString();
	        String req_time = param.get("system_req") == null ? "" : param.get("system_req").toString();
	        String res_time = param.get("system_res") == null ? "" : param.get("system_res").toString();
	        String system_agc = param.get("system_agc") == null ? "" : param.get("system_agc").toString();
	        String system_plc = param.get("system_plc") == null ? "" : param.get("system_plc").toString();
	        
	        //log.info("system_id : " + system_id);
	        //log.info("system_ord : " + system_ord);
	        
	        //log.info("system_nm : " + system_nm);
	        //log.info("system_url : " + system_url);
	        //log.info("req_time : " + req_time);
	        //log.info("res_time : " + res_time);
	        //log.info("system_agc : " + system_agc);
	        //log.info("system_plc : " + system_plc);
	        
	        param.put("system_id", system_id);
	        param.put("system_ord", system_ord);
	        param.put("system_nm", system_nm);
	        param.put("system_url", system_url);
	        param.put("req_time", req_time);
	        param.put("res_time", res_time);
	        param.put("order_agency", system_agc);
	        param.put("server_place", system_plc);
	        
	        service.updateSystemInfo(param);

	        response.put("status", "success");
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("status", "error");
	    }

	    return response; 
	}
	
	/**
	 * 시스템 정보 삭제
	 * @return String
	*/
	@RequestMapping(value = "/deleteSystemInfo.do", method = RequestMethod.POST)
	@ResponseBody
	public HashMap<String, String> deleteSystemInfo(@RequestBody HashMap<String, Object> param) {
	    HashMap<String, String> response = new HashMap<>();
	    try {
	        int system_id = Integer.parseInt(param.get("system_id").toString());
	       
	        //log.info("system_id : " + system_id);
	        
	        param.put("system_id", system_id);
	        
	        service.deleteSystemInfo(param);

	        response.put("status", "success");
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("status", "error");
	    }

	    return response; 
	}
	
	/**
     * 셸스크립트 실행
     * @return HashMap<String, String>
     */
	@RequestMapping(value = "/executeShellScript.do", method = RequestMethod.POST)
	@ResponseBody
	public HashMap<String, String> executeShellScript(@RequestBody HashMap<String, Object> param) {
	    HashMap<String, String> response = new HashMap<>();
	    int system_id = Integer.parseInt(param.get("system_id").toString());

	    String cmd = "/bin/sh /data/_wisesys_utils/_select_system_status/_proc_cron/select_system_cron.sh " + system_id;
	    //log.info("셸스크립트 실행 요청: system_id = " + system_id);

	    Session session = null;
	    ChannelExec channel = null;
	    BufferedReader reader = null;
	    BufferedReader errorReader = null;
	    StringBuilder output = new StringBuilder();
	    StringBuilder errorOutput = new StringBuilder();

	    try {
	        // SSH 연결 설정
	        JSch jsch = new JSch();
	        session = jsch.getSession(user, host, port);
	        session.setPassword(password);
	        
	        // SSH 설정 - 타임아웃과 호스트 키 확인 비활성화
	        session.setConfig("StrictHostKeyChecking", "no");
	        session.connect();

	        // 채널 설정 및 명령어 실행
	        channel = (ChannelExec) session.openChannel("exec");
	        channel.setCommand(cmd);
	        channel.setInputStream(null);
	        channel.setErrStream(System.err);

	        // 출력과 에러 스트림 설정
	        reader = new BufferedReader(new InputStreamReader(channel.getInputStream(), "EUC-KR"));
	        errorReader = new BufferedReader(new InputStreamReader(channel.getErrStream(), "EUC-KR"));

	        channel.connect();

	        // 표준 출력 수집
	        String line;
	        while ((line = reader.readLine()) != null) {
	            output.append(line).append(System.lineSeparator());
	        }

	        // 에러 출력 수집
	        while ((line = errorReader.readLine()) != null) {
	            errorOutput.append(line).append(System.lineSeparator());
	        }

	        int exitStatus = channel.getExitStatus();
	        if (exitStatus == 0) {
	            response.put("status", "success");
	            response.put("output", output.toString());
	        } else {
	            response.put("status", "error");
	            response.put("output", errorOutput.toString());
	        }
	        
	        log.info("스크립트 실행 완료: status = " + response.get("status"));

	    } catch (Exception e) {
	        log.error("Shell script execution error", e);
	        response.put("status", "error");
	        response.put("message", e.getMessage());
	        response.put("stackTrace", Arrays.toString(e.getStackTrace())); // 스택 트레이스 추가

	    } finally {
	        // 자원 해제
	        if (channel != null) channel.disconnect();
	        if (session != null) session.disconnect();
	        try {
	            if (reader != null) reader.close();
	            if (errorReader != null) errorReader.close();
	        } catch (Exception e) {
	            log.error("Error closing resources", e);
	        }
	    }

	    return response;
	}
	
	@ResponseBody
	@RequestMapping(value = "/selectSystemInfo.do")
	public ArrayList<HashMap<String, Object>> selectSystemInfo(Model model,HttpServletRequest req) {
		ArrayList<HashMap<String, Object>> dataList = new ArrayList<HashMap<String,Object>>();
		try {
			
			dataList = service.selectSystemInfo();
			
			//log.info("dataList : " +dataList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return dataList;
	}
	
	@ResponseBody
	@RequestMapping(value = "/getLatestData.do", method = RequestMethod.GET)
	public ArrayList<HashMap<String, Object>> getLatestData(@RequestParam("system_id") int system_id) {
	    ArrayList<HashMap<String, Object>> dataList = new ArrayList<>();
	    try {
	        HashMap<String, Object> param = new HashMap<>();
	        param.put("system_id", system_id);

	        dataList = service.getLatestData(param);
	        
	        //log.info("dataList : " + dataList);
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return dataList;
	}
	
}
