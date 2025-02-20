package kr.co.wisesys.wdms.controller;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.wisesys.wdms.service.SFTPFileService;
import kr.co.wisesys.wdms.service.WdmsService;
import kr.co.wisesys.wdms.util.CommonFileUtil;
import kr.co.wisesys.wdms.util.CommonFtpUtil;

@Controller
@RequestMapping(value = {"/wdmsCont/*"})
public class WdmsContController {
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    // KHNP
    @Value("#{config['khnp_host']}") private String khnp_host;
    @Value("#{config['khnp_port']}") private int khnp_port;
    @Value("#{config['khnp_id']}") private String khnp_id;
    @Value("#{config['khnp_pwd']}") private String khnp_pwd;
    
    // RND
    @Value("#{config['rnd_host']}") private String rnd_host;
    @Value("#{config['rnd_port']}") private int rnd_port;
    @Value("#{config['rnd_id']}") private String rnd_id;
    @Value("#{config['rnd_pwd']}") private String rnd_pwd;
    
    @Autowired
	private WdmsService service;
    
    @Autowired
	private SFTPFileService sftpFileService;
    
    @Autowired
    private ServletContext servletContext;
    
    @Autowired
    private SqlSessionTemplate sqlSessionMysql;

    @RequestMapping(value = "/fileListData.do")
    @ResponseBody
    public ArrayList<HashMap<String, Object>> fileListData(@RequestParam("boardTime") String boardTime, @RequestParam("dayParam") String dayParam, Model model) {
        try {
            ArrayList<HashMap<String, Object>> fileList = service.fileListData(boardTime);
            for (int i = 0; i < fileList.size(); i++) {
            	HashMap<String, Object> file = fileList.get(i);
            	if (file.get("file_count").equals("-")) {
            		file.put("error_flag", "y");
            	}
            }
            model.addAttribute("fileList", fileList);
            return fileList;
        } catch (Exception e) {
            log.error(e.toString());
            return new ArrayList<>();
        }
    }

    @RequestMapping(value = "/insertFileCount.do")
    @ResponseBody
    public String insertFileCount(@RequestParam("boardTime") String boardTime, @RequestParam("dayParam") String dayParam) {
    	int insertResult = 0;

    	CommonFileUtil commonFileUtil = new CommonFileUtil();
    	CommonFtpUtil commonFtpUtil = new CommonFtpUtil();
    	
    	try {
	        // JSON에 저장된 기준 데이터를 순환
        	for (int j = 0; j < jsonPath(dayParam).size(); j++) {
                Map<String, Object> stdObject = jsonPath(dayParam).get(j);

                String server_nm = stdObject.get("server_nm").toString();
                String repo_nm = stdObject.get("repo_nm").toString();
                int type_id = (int) stdObject.get("type_id");
                int file_id = (int) stdObject.get("file_id");
                int file_count = 0;
	                if (server_nm.equals("khnp") && repo_nm.equals("/home/outer/data")) {
	                	file_count = commonFtpUtil.countFile(boardTime, stdObject.get("file_path").toString(), khnp_host, khnp_port, khnp_id, khnp_pwd);
	                } else if (server_nm.equals("rnd") && repo_nm.equals("/nas/met")){
	                	file_count = commonFtpUtil.countFile(boardTime, stdObject.get("file_path").toString(), rnd_host, rnd_port, rnd_id, rnd_pwd);
	                }
                String error_flag = (String) commonFileUtil.checkFileCount(file_count, stdObject, boardTime, dayParam).get("error_flag");
                int std_count = (int) commonFileUtil.checkFileCount(file_count, stdObject, boardTime, dayParam).get("std_count");
                
                HashMap<String, Object> param = new HashMap<>();
                param.put("server_id", getServerId(server_nm));
                param.put("repo_id", getRepoId(repo_nm));
                param.put("type_id", type_id);
                param.put("file_id", file_id);
                param.put("boardTime", formatBoardTime(boardTime));
                param.put("file_count", file_count);
                param.put("error_flag", error_flag);
                param.put("std_count", std_count);

                insertResult += sqlSessionMysql.insert("wdms.insertFileCount", param);
        	}
            return (insertResult > 0) ? "Insert success" : "Insert error";
        } catch (Exception e) {
            log.error(e.toString());
            return e.toString();
        }
    }
    
    @RequestMapping(value = "/updateCorrectionData.do")
	@ResponseBody
	public Map<String, Object> insertCorrectionData(@RequestBody List<Map<String, String>> correctionData) {
	    Map<String, Object> response = new HashMap<>();
	    try {
	    	
	    	String issuedate = correctionData.get(0).get("issuedate");
	    	
	    	LocalDate today = LocalDate.now();
	    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	    	
	    	String issuedateStr = issuedate.substring(0, 8);
	    	LocalDate issueDateLocal = LocalDate.parse(issuedateStr, formatter);

	    	CommonFileUtil commonFileUtil = new CommonFileUtil();
	    	
	    	Map<String, Integer> fileMapping = commonFileUtil.getFileMapping();
	    	
	    	for (Map<String, String> data : correctionData) {
	    	    try {
	    	        String fileType = data.get("fileType");
	    	        String repoId = data.get("repoId");

	    	        Integer fileId = fileMapping.get(fileType);
	    	        
	    	        if (fileId != null) {

	    	        	if (!issueDateLocal.equals(today)) {
	    	        		
	    	        		int hour = LocalDateTime.MAX.getHour();
	    		    		String max_hour = String.format("%02d", hour);
	    	        		
	    	        		issuedate = issuedate.substring(0, 8) + max_hour + issuedate.substring(10,12);
	    	        		
	    		    	}
	    	        	
	    	            int updateValue = commonFileUtil.getHourlyValue(fileType, issuedate);
	    	            
	    	            service.updateCorrectionData(fileId, issuedate, updateValue, repoId);
	    	             
	    	            sftpFileService.createFile(fileType, issuedate);
	    	            
	    	        } else {
	    	            //log.info("fileType 매핑 실패: " + fileType); 
	    	        }
	    	    } catch (Exception e) {
	    	        log.error("error message : " + data, e);
	    	    }
	    	}

	    } catch (Exception e) {
	        response.put("error", e.getMessage());
	    }
	    return response;
	}
    
    @RequestMapping(value = "/selectMeStnInfo.do")
    @ResponseBody
    public ArrayList<HashMap<String, Object>> selectMeRnStnInfo(Model model, @RequestParam String fileType, @RequestParam(required = false) String agcType, @RequestParam String init_dt, @RequestParam boolean endObsCheck, @RequestParam String stateOrder) {
        ArrayList<HashMap<String, Object>> selectList = new ArrayList<>();
        HashMap<String, Object> param = new HashMap<>();
        param.put("agcType", agcType);
        param.put("init_dt", init_dt);
        param.put("endObsCheck", endObsCheck);
        param.put("stateOrder", stateOrder);
        if (fileType.equals("rnStn")) {
            selectList = service.meRnStnInfoData(param);
        } else if (fileType.equals("dam")) {
            selectList = service.meDamStnInfoData(param);
        } else if (fileType.equals("wlStn")) {
            selectList = service.meWlStnInfoData(param);
        } else {log.info("No Valid FileType");}
        model.addAttribute("selectList", selectList);
        return selectList;
    }
    
    @RequestMapping(value = "/selectMeAgcnmInfo.do")
    @ResponseBody
    public ArrayList<HashMap<String, Object>> selectMeAgcnmInfo(Model model, @RequestParam String agcType){
        ArrayList<HashMap<String, Object>> selectAgcnmList = new ArrayList<>();
        if (agcType.equals("rnStn")) {
            selectAgcnmList = service.meRnAgcnmInfoData();
        } else if (agcType.equals("dam")) {
            selectAgcnmList = service.meDamAgcnmInfoData();
        } else if (agcType.equals("wlStn")) {
            selectAgcnmList = service.meWlAgcnmInfoData();
        } else {log.info("No Valid FileType");}
        model.addAttribute("selectAgcnmList", selectAgcnmList);
        return selectAgcnmList;
    }
    
    @RequestMapping(value = "/updateStnInfoFlag.do")
    @ResponseBody
    public String updateStnInfoFlag(Model model, @RequestParam String init_dt, @RequestParam String yday_dt){
        String updateResult = "";
        HashMap<String, Object> param = new HashMap<>();
        param.put("init_dt", init_dt);
        param.put("yday_dt", yday_dt);
        
        updateResult += sqlSessionMysql.update("wdms.updateMeStnInfoFlag", param);
        updateResult += sqlSessionMysql.update("wdms.updateMeStnInfoDelFlag", param);
        return updateResult;
    }
    
    private String formatBoardTime(String boardTime) throws Exception {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMddHHmm");

        Date date = inputFormat.parse(boardTime);

        return outputFormat.format(date);
    }
    
    private String selectJson(String dayParam) {
    	String filePath = "";
    	
    	if (dayParam.equals("p")) {
            filePath = servletContext.getRealPath("/json/wdms_file_count_past.json");
        } else if (dayParam.equals("y")) {
            filePath = servletContext.getRealPath("/json/wdms_file_count_yday.json");
        } else if (dayParam.equals("t")) {
            filePath = servletContext.getRealPath("/json/wdms_file_count_tday.json");
        } else {
            log.error("No Suitable JSON");
        }
    	return filePath;
    }
    
    @RequestMapping(value = "/downloadCSV.do")
    public ResponseEntity<byte[]> downloadCSV(@RequestParam String fileType, @RequestParam(required = false) String agcType, @RequestParam String init_dt, @RequestParam boolean endObsCheck, @RequestParam String stateOrder, @RequestParam String stn_info) {
        String csvData = createCSV(fileType, agcType, init_dt, endObsCheck, stateOrder);

        byte[] bom = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] csvBytes;
        try {
            csvBytes = csvData.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        
        byte[] finalCsvBytes = new byte[bom.length + csvBytes.length];
        System.arraycopy(bom, 0, finalCsvBytes, 0, bom.length);
        System.arraycopy(csvBytes, 0, finalCsvBytes, bom.length, csvBytes.length);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=MS949"));
        headers.setContentDispositionFormData("attachment", init_dt + "_" + stn_info + ".csv");

        return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);
    }
    
    private String createCSV(String fileType, String agcType, String init_dt, boolean endObsCheck, String stateOrder) {
        String data = "";
        ArrayList<HashMap<String, Object>> selectList = new ArrayList<>();
        HashMap<String, Object> param = new HashMap<>();
        param.put("agcType", agcType);
        param.put("init_dt", init_dt);
        param.put("endObsCheck", endObsCheck);
        param.put("stateOrder", stateOrder);
        if (fileType.equals("rnStn")) {
          selectList = service.meRnStnInfoData(param);
          data += "init_dt, rfobscd, obsnm, agcnm, addr, etcaddr, lat, lon, status" + "\n";
          for (int i = 0; i < selectList.size(); i++) {
              data += selectList.get(i).get("init_dt") + ",";
              data += selectList.get(i).get("rfobscd") + ",";
              data += selectList.get(i).get("obsnm") + ",";
              data += selectList.get(i).get("agcnm") + ",";
              data += selectList.get(i).get("addr") + ",";
              data += selectList.get(i).get("etcaddr") + ",";
              data += selectList.get(i).get("lat") + ",";
              data += selectList.get(i).get("lon") + ",";
              if (selectList.get(i).get("flag_nm").equals("기본")) {
                data += "-" + "\n";
              } else {
                data += selectList.get(i).get("flag_nm") + "\n";
              }
            }
        } else if (fileType.equals("dam")) {
          selectList = service.meDamStnInfoData(param);
          data += "init_dt, dmobscd, obsnm, agcnm, addr, etcaddr, lat, lon, fldlmtwl, pfh, status" + "\n";
          for (int i = 0; i < selectList.size(); i++) {
              data += selectList.get(i).get("init_dt") + ",";
              data += selectList.get(i).get("dmobscd") + ",";
              data += selectList.get(i).get("obsnm") + ",";
              data += selectList.get(i).get("agcnm") + ",";
              data += selectList.get(i).get("addr") + ",";
              data += selectList.get(i).get("etcaddr") + ",";
              data += selectList.get(i).get("lat") + ",";
              data += selectList.get(i).get("lon") + ",";
              data += selectList.get(i).get("fldlmtwl") + ",";
              data += selectList.get(i).get("pfh") + ",";
              if (selectList.get(i).get("flag_nm").equals("기본")) {
                data += "-" + "\n";
              } else {
                data += selectList.get(i).get("flag_nm") + "\n";
              }
            }
        } else if (fileType.equals("wlStn")) {
          selectList = service.meWlStnInfoData(param);
          data += "init_dt, wlobscd, obsnm, agcnm, addr, etcaddr, lat, lon, gdt, attwl, wrnwl, almwl, srswl, pfh, fstnyn, status" + "\n";
          for (int i = 0; i < selectList.size(); i++) {
              data += selectList.get(i).get("init_dt") + ",";
              data += selectList.get(i).get("wlobscd") + ",";
              data += selectList.get(i).get("obsnm") + ",";
              data += selectList.get(i).get("agcnm") + ",";
              data += selectList.get(i).get("addr") + ",";
              data += selectList.get(i).get("etcaddr") + ",";
              data += selectList.get(i).get("lat") + ",";
              data += selectList.get(i).get("lon") + ",";
              data += selectList.get(i).get("gdt") + ",";
              data += selectList.get(i).get("attwl") + ",";
              data += selectList.get(i).get("wrnwl") + ",";
              data += selectList.get(i).get("almwl") + ",";
              data += selectList.get(i).get("srswl") + ",";
              data += selectList.get(i).get("pfh") + ",";
              data += selectList.get(i).get("fstnyn") + ",";
              if (selectList.get(i).get("flag_nm").equals("기본")) {
                data += "-" + "\n";
              } else {
                data += selectList.get(i).get("flag_nm") + "\n";
              }
            }
        } else {log.info("No Valid FileType");}
        
        return data;
    }
    
    private int getServerId(String server_nm) {
    	if (server_nm.equals("rnd")) {return 1;} else if (server_nm.equals("khnp")) {return 2;} else if (server_nm.equals("system")) {return 3;} else {log.error("No Valid server_id"); return -1;}
    }
    
    private int getRepoId(String repo_nm) {
    	if (repo_nm.equals("/nas/met")) {return 1;} else if (repo_nm.equals("/home/outer/data")) {return 2;} else if (repo_nm.equals("/nas_khnp_met")) {return 3;} else {log.error("No Valid repo_id"); return -1;}
    }
    
    private List<Map<String, Object>> jsonPath(String dayParam) {
    	try {
	    	String filePath = selectJson(dayParam);
			ObjectMapper objectMapper = new ObjectMapper();
	        List<Map<String, Object>> stdJson = objectMapper.readValue(new File(filePath), new TypeReference<List<Map<String, Object>>>(){});
	        return stdJson;
    	} catch (Exception e) {
            log.error(e.toString());
            return new ArrayList<>();
        }
    }
}
