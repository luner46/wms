package kr.co.wisesys.wdms.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.*;

public class CommonFtpUtil {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    /*
     * FTP 파일 카운트 (기준 날짜, 기준 파일 경로)
     * 
     * ex)
     * CommonFtpUtil commonFtpUtil = new CommonFtpUtil();
	 *	
	 * int file_count = commonFtpUtil.countFile(boardTime, stdObject.get("file_path").toString());
     * 
     * @param String boardTime
     * @param String filePath
     * @return fileCountResult
     * 
     */

	public int countFile(String boardTime, String filePath, String sftp_host, int sftp_port, String sftp_id, String sftp_pwd) {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
 	    try {
			calendar.setTime(format.parse(boardTime));
		} catch (Exception e) {
			e.toString();
		}
 	    
 	    String yyyy = Integer.toString(calendar.get(Calendar.YEAR));
 	    String month = String.format("%02d",calendar.get(Calendar.MONTH) + 1);
 	    String day = String.format("%02d",calendar.get(Calendar.DAY_OF_MONTH));

 	    Session session = null;
		ChannelSftp channelSftp = null;
		int fileCountResult = 0;

		try {
			JSch jsch = new JSch();
			
			session = jsch.getSession(sftp_id, sftp_host, sftp_port);
			session.setPassword(sftp_pwd);

			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			
			session.connect();
			channelSftp = (ChannelSftp) session.openChannel("sftp");
			channelSftp.connect();
			
			String remoteDir = filePath.replace("yyyy", yyyy).replace("mm", month).replace("dd", day);
			log.info("success remoteDir={}",remoteDir);
			
			@SuppressWarnings("unchecked")
	        Vector<ChannelSftp.LsEntry> fileList = channelSftp.ls(remoteDir);
	        for (ChannelSftp.LsEntry entry : fileList) {
	            String fileName = entry.getFilename();
	            
	            // 현재 디렉토리(`.`)와 부모 디렉토리(`..`)는 제외
	            if (!fileName.equals(".") && !fileName.equals("..")) {
	                if (!entry.getAttrs().isDir()) {
	                	fileCountResult++;
	                }
	            }
	        }
		} catch (Exception e) {
			log.error(e.toString(),filePath);
		} finally {
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
		return fileCountResult;
	}
}