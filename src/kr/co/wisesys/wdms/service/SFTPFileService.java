package kr.co.wisesys.wdms.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import kr.co.wisesys.wdms.util.CommonFileUtil;

@Service
public class SFTPFileService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    @Value("#{config['khnp_host']}") String khnp_host;
    @Value("#{config['khnp_port']}") String khnp_port;
    @Value("#{config['khnp_id']}") String khnp_id;
    @Value("#{config['khnp_pwd']}") String khnp_pwd;
    
    @Value("#{config['rnd_host']}") String rnd_host;
    @Value("#{config['rnd_port']}") String rnd_port;
    @Value("#{config['rnd_id']}") String rnd_id;
    @Value("#{config['rnd_pwd']}") String rnd_pwd;
    
    @Value("#{config['khnp_directory_path']}") String khnp_directory_path;
    @Value("#{config['rnd_directory_path']}") String rnd_directory_path;
    
    private Session session;
    private ChannelSftp channelSftp;

    /**
     * SFTP 연결 설정
     */
    public void connectSFTP(String repoId) throws JSchException {
    	
    	String host, port, id, pwd;
    	
    	int repo_id = Integer.parseInt(repoId);
    	
    	if(repo_id == 1) { // rnd server
    		host = rnd_host; port = rnd_port; id = rnd_id; pwd = rnd_pwd;
    	} else if (repo_id == 2) { // khnp server
            host = khnp_host; port = khnp_port; id = khnp_id; pwd = khnp_pwd;
        } else {
            throw new IllegalArgumentException("Invalid repoId: " + repoId);
        }
    	
        JSch jsch = new JSch();
        session = jsch.getSession(id, host, Integer.parseInt(port));
        session.setPassword(pwd);

        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        session.connect();
        channelSftp = (ChannelSftp) session.openChannel("sftp");
        channelSftp.connect();
    }

    /**
     * SFTP 연결 해제
     */
    public void disconnectSFTP() {
        if (channelSftp != null) {
            channelSftp.disconnect();
        }
        if (session != null) {
            session.disconnect();
        }
    }

    /**
     * fileId에 따른 SFTP 원격 디렉토리 반환
     */
    private String getRemoteDir(int fileId, LocalDate date) {
        String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String formattedYear = date.format(DateTimeFormatter.ofPattern("yyyy"));
        String formattedMonth = date.format(DateTimeFormatter.ofPattern("MM"));

        if (fileId == 1) {
            return khnp_directory_path + "asos_ssb_pet_10min/" + formattedDate;
        } else if (fileId == 31) {
            return khnp_directory_path + "kma_aws_10min_qc/" + formattedDate;
        } else if (fileId == 28) {
            return khnp_directory_path + "kma_asos_1hr/" + formattedDate;
        } else if (fileId == 2) {
            return khnp_directory_path + "asos_ssb_pet_1day/" + formattedYear + "/" + formattedMonth;
        } else if (fileId == 25) {
            return khnp_directory_path + "kma_asos_1day/" + formattedYear + "/" + formattedMonth;
        } else if (fileId == 21) {
            return khnp_directory_path + "g120_v070_erea_unis_han_172ssb_10m/" + formattedDate + "/03";
        } else if (fileId == 22) {
            return khnp_directory_path + "g120_v070_erea_unis_han_172ssb_10m/" + formattedDate + "/09";
        } else if (fileId == 23) {
            return khnp_directory_path + "g120_v070_erea_unis_han_172ssb_10m/" + formattedDate + "/15";
        } else if (fileId == 24) {
            return khnp_directory_path + "g120_v070_erea_unis_han_172ssb_10m/" + formattedDate + "/21";
        } else if (fileId == 42) {
            return khnp_directory_path + "l015_v070_erlo_unis_han_172ssb_10m/" + formattedDate + "/03";
        } else if (fileId == 43) {
            return khnp_directory_path + "l015_v070_erlo_unis_han_172ssb_10m/" + formattedDate + "/09";
        } else if (fileId == 44) {
            return khnp_directory_path + "l015_v070_erlo_unis_han_172ssb_10m/" + formattedDate + "/15";
        } else if (fileId == 45) {
            return khnp_directory_path + "l015_v070_erlo_unis_han_172ssb_10m/" + formattedDate + "/21";
        } else if(fileId == 49) {
        	return rnd_directory_path + "me_bo_10min_qc/" + formattedDate;
        } else if(fileId == 50) {
        	return rnd_directory_path + "me_bo_1day_qc/" + formattedYear + "/" + formattedMonth;
        } else if(fileId == 51) {
        	return rnd_directory_path + "me_bo_1hr_qc/" + formattedDate;
        } else if(fileId == 52) {
        	return rnd_directory_path + "me_dam_10min_qc/" + formattedDate;
        } else if(fileId == 53) {
        	return rnd_directory_path + "me_dam_1day_qc/" + formattedYear + "/" + formattedMonth;
        } else if(fileId == 54) {
        	return rnd_directory_path + "me_dam_1hr_qc/" + formattedDate;
        } else if(fileId == 55) {
        	return rnd_directory_path + "me_rn_10min_qc/" + formattedDate;
        } else if(fileId == 56) {
        	return rnd_directory_path + "me_rn_1day_qc/" + formattedYear + "/" + formattedMonth;
        } else if(fileId == 57) {
        	return rnd_directory_path + "me_rn_1hr_qc/" + formattedDate;
        } else if(fileId == 58) {
        	return rnd_directory_path + "me_wl_10min_qc/" + formattedDate;
        } else if(fileId == 59) {
        	return rnd_directory_path + "me_wl_1day_qc/" + formattedYear + "/" + formattedMonth;
        } else if(fileId == 60) {
        	return rnd_directory_path + "me_wl_1hr_qc/" + formattedDate;
        } else if(fileId == 66) {
        	return rnd_directory_path + "tm_ssb_pcp_10min/tm_ef/" + formattedDate; 
        } else if(fileId == 67) {
        	return rnd_directory_path + "tm_ssb_pcp_10min/tm_ssb/" + formattedDate; 
        } else if(fileId == 68) {
        	return rnd_directory_path + "tm_ssb_pcp_10min/tm_thi/" + formattedDate;
        } else if(fileId == 69) {
        	return rnd_directory_path + "tm_ssb_pcp_10min/tm_ef/" + formattedYear + "/" + formattedMonth;
        } else if(fileId == 70) {
        	return rnd_directory_path + "tm_ssb_pcp_10min/tm_ssb/" + formattedYear + "/" + formattedMonth;
        } else if(fileId == 71) {
        	return rnd_directory_path + "tm_ssb_pcp_10min/tm_thi/" + formattedYear + "/" + formattedMonth;
        }
        
        throw new IllegalArgumentException("Invalid fileId: " + fileId);
    }

    /**
     * fileId에 따른 백업 디렉토리 반환
     */
    private String getBackupDir(int fileId, LocalDate date) {
        if(fileId == 2 || fileId == 25) {
            return getRemoteDir(fileId, date.minusMonths(1));
        }else if((fileId >= 21 && fileId <= 24) || (fileId >= 42 && fileId <= 45)) {
            return getRemoteDir(fileId, date.minusDays(0));
        }else {
            return getRemoteDir(fileId, date.minusDays(1));
            
        }
    }
    
    /**
     * SFTP 서버에서 폴더가 존재하지 않으면 생성
     */
    private void createRemoteDirectoryIfNotExists(String directoryPath) throws SftpException {
        String[] pathParts = directoryPath.split("/");
        StringBuilder currentPath = new StringBuilder();

        for (String folder : pathParts) {
            if (!folder.isEmpty()) {
                currentPath.append("/").append(folder);
                try {
                    channelSftp.ls(currentPath.toString());  // 디렉토리 존재 확인
                } catch (SftpException e) {
                    if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) { // 폴더가 없을 경우 생성
                        //log.warn("SFTP 폴더 없음, 생성 시도: {}", currentPath);
                        channelSftp.mkdir(currentPath.toString());
                        //log.info("SFTP 폴더 생성 완료: {}", currentPath);
                    } else {
                        throw e; // 다른 오류는 다시 던짐
                    }
                }
            }
        }
    }
    
    /**
     * SFTP 디렉토리의 파일 목록 가져오기
     */
    private Set<String> getExistingFiles(String directory) throws SftpException {
        Set<String> files = new HashSet<>();
        try {
            Vector<ChannelSftp.LsEntry> fileList = channelSftp.ls(directory);
            for (ChannelSftp.LsEntry entry : fileList) {
                files.add(entry.getFilename());
            }
        } catch (SftpException e) {

            // 폴더가 없을 경우 생성
            createRemoteDirectoryIfNotExists(directory);

            // 폴더를 생성한 후 다시 파일 목록을 가져옴
            try {
                Vector<ChannelSftp.LsEntry> fileList = channelSftp.ls(directory);
                for (ChannelSftp.LsEntry entry : fileList) {
                    files.add(entry.getFilename());
                }
            } catch (SftpException ex) {
                log.error("SFTP 폴더 생성 후에도 파일 목록을 가져올 수 없음: " + directory, ex);
            }
        }
        return files;
    }


    /**
     * 특정 파일을 건너뛰어야 하는지 확인
     */
    private boolean shouldSkipFileCreation(LocalDate issuedDate, boolean isToday, boolean isYesterday, boolean isBeforeNoon, int hour, String time, int fileId) {
        LocalTime currentTime = LocalTime.now();
        int flooredMinute = (currentTime.getMinute() / 10) * 10;
        LocalTime latestAllowedTime = LocalTime.of(currentTime.getHour(), flooredMinute);

        LocalTime parsedTime = (fileId == 28) ? LocalTime.of(hour, 0) : LocalTime.parse(time, DateTimeFormatter.ofPattern("HHmm"));

        // 오늘 날짜일 경우 제한 조건 적용
        if (isToday) {
            if (parsedTime.isAfter(latestAllowedTime)) {
                return true;
            }
            if (fileId == 28 && parsedTime.isAfter(LocalTime.of(currentTime.getHour(), 0))) {
                return true;
            }
        }

        return false;
    }
    
    /**
     * 파일 내용 수정
     */
    public void updateFileContent(File targetFile, String oldDate, String newDate, String fileType) {
        File tempFile = new File(targetFile.getAbsolutePath() + ".tmp");

        try (Stream<String> lines = Files.lines(targetFile.toPath());
             BufferedWriter writer = Files.newBufferedWriter(tempFile.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {

            lines.map(line -> line.replace(oldDate, newDate))
                 .forEach(line -> {
                     try {
                         writer.write(line);
                         writer.newLine();
                     } catch (IOException e) {
                         throw new UncheckedIOException(e);
                     }
                 });

            writer.flush(); 

        } catch (IOException e) {
            log.error("{} 파일 수정 중 오류 발생: {}", fileType.toUpperCase(), targetFile.getName(), e);
            return;
        }

        // 기존 파일을 삭제하지 않고 덮어쓰도록 처리
        try {
            Files.move(tempFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            //log.info("파일 업데이트 완료: {}", targetFile.getName());
        } catch (IOException e) {
            log.error("파일 교체 중 오류 발생: {}", targetFile.getName(), e);
        }
    }
    
    /**
     * 파일을 생성하고 업로드
     */
    private void createAndUploadFile(String remoteDir, String backupDir, String fileName, String backupFileName, Set<String> existingFiles, Set<String> backupFiles, int fileId) throws SftpException {
        if (existingFiles.contains(fileName)) {
            return;
        }

        String backupFilePath = backupDir + "/" + backupFileName;
        
        if (!backupFiles.contains(backupFileName)) {
            log.warn("백업 파일이 존재하지 않음, 건너뜀: " + backupFilePath);
            return;
        }

        File tempFile = null;
        
        String fileType;
        
        try {
            if (fileId == 25 || fileId == 28 || fileId == 31 || fileId == 49 || fileId == 52 || fileId == 55 || fileId == 58) {
                tempFile = File.createTempFile("temp_", ".csv");
                fileType = "CSV";
            } else {
                tempFile = File.createTempFile("temp_", ".txt");
                fileType = "TXT";
            }
            
            channelSftp.get(backupFilePath, tempFile.getAbsolutePath());
            
            if ((fileId >= 21 && fileId <= 24) || (fileId >= 42 && fileId <= 45)) {
                String oldDate = backupFileName.substring(4, 16);
                String newDate = fileName.substring(4, 16);
                updateFileContent(tempFile, oldDate, newDate, fileType);
            } else if(fileId == 1 || fileId == 31 || fileId == 49 || fileId == 52 || fileId == 55 || fileId == 58){
                updateFileContent(tempFile, backupFileName.substring(0, 12), fileName.substring(0, 12), fileType);
            } else if(fileId == 28){
                updateFileContent(tempFile, backupFileName.substring(0, 10), fileName.substring(0, 10), fileType);
            } else {
                updateFileContent(tempFile, backupFileName.substring(0, 8), fileName.substring(0, 8), fileType);
            }
            
            
            channelSftp.put(tempFile.getAbsolutePath(), remoteDir + "/" + fileName);
            log.info("수정된 파일 업로드 완료: " + fileName);
            existingFiles.add(fileName);
        } catch (IOException e) {
            log.error("파일 생성 실패: " + fileName, e);
        } finally {
            if (tempFile != null) {
                tempFile.delete();
            }
        }
    }

    /**
     * 1일 단위 파일 생성 및 업로드 (fileId: 2, 25)
     */
    private void createAndUpload1DayFiles(String remoteDir, String backupBaseDir, int fileId) throws SftpException {
        CommonFileUtil commonFileUtil = new CommonFileUtil();
        Set<String> existingFiles = getExistingFiles(remoteDir);

        String[] pathParts = remoteDir.split("/");
        int year = Integer.parseInt(pathParts[pathParts.length - 2]);  
        int month = Integer.parseInt(pathParts[pathParts.length - 1]); 

        LocalDate issuedMonth = LocalDate.of(year, month, 1); 
        LocalDate startDate = issuedMonth.withDayOfMonth(1); 
        LocalDate endDate = issuedMonth.withDayOfMonth(issuedMonth.lengthOfMonth());

        LocalDate today = LocalDate.now();
        if (issuedMonth.getMonth() == today.getMonth() && issuedMonth.getYear() == today.getYear()) {
            if (fileId == 2) {
                endDate = today.minusDays(1);
            } else if (fileId == 25) {
                endDate = today; 
            }
        }

        String fileExtension = commonFileUtil.fileExtension(fileId);

        for (LocalDate issuedDate = startDate; !issuedDate.isAfter(endDate); issuedDate = issuedDate.plusDays(1)) {
            String issueYear = issuedDate.format(DateTimeFormatter.ofPattern("yyyy"));
            String issueMonth = issuedDate.format(DateTimeFormatter.ofPattern("MM"));
            String issueDay = issuedDate.format(DateTimeFormatter.ofPattern("dd"));

            String fileName = issueYear + issueMonth + issueDay + fileExtension;

            if (existingFiles.contains(fileName)) {
                continue; 
            }

            String backupFileName = getNearestBackupFile(issuedDate, backupBaseDir, fileExtension, startDate);

            if (backupFileName == null) {
                log.warn(issueMonth + "월 내에서 백업 파일을 찾을 수 없음: " + fileName);
                continue;
            }

            String backupDir = backupBaseDir.replaceFirst("/\\d{4}/\\d{2}$", "/" + issueYear + "/" + issueMonth);

            Set<String> backupFiles = getExistingFiles(backupDir);

            createAndUploadFile(remoteDir, backupDir, fileName, backupFileName, existingFiles, backupFiles, fileId);
        }
    }

    /**
     * 가장 가까운 백업 파일을 찾는 메서드 (최대 7일 전까지 조회)
     */
    private String getNearestBackupFile(LocalDate targetDate, String backupBaseDir, String fileExtension, LocalDate startDate) {
        for (int i = 1; i <= 7; i++) { 
            LocalDate backupDate = targetDate.minusDays(i);

            if (backupDate.isBefore(startDate)) {
                break;
            }

            String backupYear = backupDate.format(DateTimeFormatter.ofPattern("yyyy"));
            String backupMonth = backupDate.format(DateTimeFormatter.ofPattern("MM"));
            String backupDay = backupDate.format(DateTimeFormatter.ofPattern("dd"));

            String backupFileName = backupYear + backupMonth + backupDay + fileExtension;
            String backupDir = backupBaseDir.replaceFirst("/\\d{4}/\\d{2}$", "/" + backupYear + "/" + backupMonth);

            try {
                Set<String> backupFiles = getExistingFiles(backupDir);
                if (backupFiles.contains(backupFileName)) {
                    return backupFileName; 
                }
            } catch (SftpException e) {
                log.warn("백업 파일 조회 중 오류 발생: " + backupDir, e);
            }
        }
        return null; 
    }
    
    /**
     * 10분 및 1시간 단위 파일 생성 및 업로드 (fileId: 1, 31, 28)
     */
    private void createAndUploadHourlyFiles(String remoteDir, String backupBaseDir, int fileId, String issuedate, boolean isToday, boolean isYesterday, boolean isBeforeNoon) throws SftpException {

        CommonFileUtil commonFileUtil = new CommonFileUtil();
        Set<String> existingFiles = getExistingFiles(remoteDir);

        LocalDate issuedDate = LocalDate.parse(issuedate.substring(0, 8), DateTimeFormatter.ofPattern("yyyyMMdd"));
        String fileExtension = commonFileUtil.fileExtension(fileId);

        for (int hour = 0; hour < 24; hour++) {
            for (int minute = 0; minute < 60; minute += 10) {

                String time = (fileId == 28) ? String.format("%02d", hour) : String.format("%02d%02d", hour, minute);
                String fileName = issuedate.substring(0, 8) + time + fileExtension;

                if (existingFiles.contains(fileName)) {
                    continue;
                }

                LocalDateTime fileDateTime = LocalDateTime.of(issuedDate, LocalTime.of(hour, minute));
                LocalDateTime backupDateTime = fileDateTime.minusMinutes(10);

                String backupFileName = null;
                String backupDir = null;
                Set<String> backupFiles = new HashSet<>();

                for (int i = 1; i <= 1008; i++) {
                    backupDateTime = fileDateTime.minusMinutes(i * 10);
                    String backupYear = backupDateTime.format(DateTimeFormatter.ofPattern("yyyy"));
                    String backupMonth = backupDateTime.format(DateTimeFormatter.ofPattern("MM"));
                    String backupDay = backupDateTime.format(DateTimeFormatter.ofPattern("dd"));
                    String backupTime = backupDateTime.format(DateTimeFormatter.ofPattern((fileId == 28) ? "HH" : "HHmm"));
                    String tempBackupFileName = backupYear + backupMonth + backupDay + backupTime + fileExtension;

                    backupDir = backupBaseDir.replaceFirst("/\\d{4}/\\d{2}/\\d{2}$", "/" + backupYear + "/" + backupMonth + "/" + backupDay);
                    
                    if (shouldSkipFileCreation(issuedDate, isToday, isYesterday, isBeforeNoon, hour, time, fileId)) {
                        continue;
                    }
                    
                    try {
                        backupFiles = getExistingFiles(backupDir);
                    } catch (SftpException e) {
                        continue; 
                    }

                    if (backupFiles.contains(tempBackupFileName)) {
                        backupFileName = tempBackupFileName;
                        break; 
                    }
                }

                if (backupFileName == null) {
                    log.warn("7일 내 적절한 백업 파일을 찾을 수 없음: " + fileName);
                    continue;
                }

                createAndUploadFile(remoteDir, backupDir, fileName, backupFileName, existingFiles, backupFiles, fileId);
            }
        }
    }
    
    /**
     * gdaps,ldaps 파일 생성 및 업로드 (fileId: 21~24, 42~45)
     */
    private void createAndUploadG120Files(String remoteDir, String backupDir, int fileId, String issuedate) throws SftpException {
        CommonFileUtil commonFileUtil = new CommonFileUtil();
        Set<String> existingFiles = getExistingFiles(remoteDir);
        Set<String> backupFiles = getExistingFiles(backupDir);

        LocalDate issuedDate = LocalDate.parse(issuedate.substring(0, 8), DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDateTime startDateTime;

        if (fileId == 21 || fileId == 42) {
            startDateTime = LocalDateTime.of(issuedDate, LocalTime.of(3, 10));
        } else if (fileId == 22 || fileId == 43) {
            startDateTime = LocalDateTime.of(issuedDate, LocalTime.of(9, 10));
        } else if (fileId == 23 || fileId == 44) {
            startDateTime = LocalDateTime.of(issuedDate, LocalTime.of(15, 10));
        } else if (fileId == 24 || fileId == 45) {
            startDateTime = LocalDateTime.of(issuedDate, LocalTime.of(21, 10));
        } else {
            throw new IllegalArgumentException("Invalid fileId: " + fileId);
        }

        int loopCount = (fileId >= 42) ? 288 : 522;
        String[] prefixes = {"pcp_", "pet_"};

        for (int i = 0; i < loopCount; i++) {
            LocalDateTime currentDateTime = startDateTime.plusMinutes(i * 10);
            String datePart = currentDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String timePart = currentDateTime.format(DateTimeFormatter.ofPattern("HHmm"));

            for (String prefix : prefixes) {
                String fileExtension = commonFileUtil.fileExtension(fileId);
                String fileName = prefix + datePart + timePart + fileExtension;
                
                if (existingFiles.contains(fileName)) {
                    continue;
                }
                
                String backupFileName = findBestBackupFile(prefix, datePart, timePart, fileExtension, backupDir);

                if (backupFileName != null) {
                    //log.info("백업 파일 사용: {} -> {}", backupFileName, fileName);
                    createAndUploadFile(remoteDir, backupDir, fileName, backupFileName, existingFiles, backupFiles, fileId);
                    
                    existingFiles.add(fileName);
                    backupFiles.add(fileName);
                    
                } else {
                    log.warn("적절한 백업 파일을 찾을 수 없음: " + fileName);
                }
            }
        }
    }
    
    /**
     * gdaps,ldaps 가장 적절한 백업 파일을 찾는 메서드
     */
    private String findBestBackupFile(String prefix, String datePart, String timePart, String fileExtension, String backupDir) {
        LocalDateTime originalTime = LocalDateTime.parse(datePart + timePart, DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        String backupFileName;

        try {
            Set<String> backupFiles = getExistingFiles(backupDir); 
            
            //log.info("백업 디렉토리({}) 내 파일 목록: {}", backupDir, backupFiles);

            // -10분씩 최대 하루 전(-1440분)
            for (int i = 1; i <= 144; i++) { 
                LocalDateTime previousTime = originalTime.minusMinutes(i * 10);
                backupFileName = prefix + previousTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")) + fileExtension;

                if (backupFiles.contains(backupFileName)) {
                    //log.info("-{}분 파일 선택: {}", i * 10, backupFileName);
                    return backupFileName;
                }
            }

            // +10분씩 최대 하루 후(+1440분)
            for (int i = 1; i <= 144; i++) { 
                LocalDateTime nextTime = originalTime.plusMinutes(i * 10);
                backupFileName = prefix + nextTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")) + fileExtension;

                if (backupFiles.contains(backupFileName)) {
                    //log.info(" +{}분 파일 선택: {}", i * 10, backupFileName);
                    return backupFileName;
                }
            }
        } catch (Exception e) {
            log.error("백업 파일 확인 중 오류 발생", e);
        }

        log.warn("24시간 내 대체할 백업 파일을 찾을 수 없음: {}", prefix + datePart + timePart + fileExtension);
        return null;
    }
    
    /**
     * 메인 파일 생성 메서드
     */
    public void createFile(String fileType, String issuedate, String repoId) {
        try {
            connectSFTP(repoId);
            CommonFileUtil commonFileUtil = new CommonFileUtil();
            Map<String, Integer> fileMapping = commonFileUtil.getFileMapping();
            Integer fileId = fileMapping.get(fileType);
            
            LocalDate issuedDate = LocalDate.parse(issuedate.substring(0, 8), DateTimeFormatter.ofPattern("yyyyMMdd"));
            LocalDate today = LocalDate.now();
            boolean isToday = issuedDate.equals(today);
            boolean isYesterday = issuedDate.equals(today.minusDays(1));
            boolean isBeforeNoon = LocalTime.now().getHour() < 12;
            
            String remoteDir = getRemoteDir(fileId, issuedDate);
            String backupDir = getBackupDir(fileId, issuedDate);
            
            if (fileId == 2 || fileId == 25) {
                createAndUpload1DayFiles(remoteDir, backupDir, fileId);
            } else if (fileId == 1 || fileId == 31 || fileId == 28 || fileId == 49 || fileId == 52 || fileId == 55 || fileId == 58) {
                createAndUploadHourlyFiles(remoteDir, backupDir, fileId, issuedate, isToday, isYesterday, isBeforeNoon);
            } else {
                createAndUploadG120Files(remoteDir, backupDir, fileId, issuedate);
            }
        } catch (Exception e) {
            log.error("SFTP 파일 작업 중 오류 발생", e);
        } finally {
            disconnectSFTP();
        }
    }
    
}