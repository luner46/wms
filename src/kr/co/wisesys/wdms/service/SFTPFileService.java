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
    
    @Value("#{config['sftp_host']}") String sftp_host;
    @Value("#{config['sftp_port']}") String sftp_port;
    @Value("#{config['sftp_id']}") String sftp_id;
    @Value("#{config['sftp_pwd']}") String sftp_pwd;
    
    @Value("#{config['directory_path']}") String directory_path;
    
    private Session session;
    private ChannelSftp channelSftp;

    /**
     * SFTP 연결 설정
     */
    public void connectSFTP() throws JSchException {
        JSch jsch = new JSch();
        session = jsch.getSession(sftp_id, sftp_host, Integer.parseInt(sftp_port));
        session.setPassword(sftp_pwd);

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
            return directory_path + "asos_ssb_pet_10min/" + formattedDate;
        } else if (fileId == 31) {
            return directory_path + "kma_aws_10min_qc/" + formattedDate;
        } else if (fileId == 28) {
            return directory_path + "kma_asos_1hr/" + formattedDate;
        } else if (fileId == 2) {
            return directory_path + "asos_ssb_pet_1day/" + formattedYear + "/" + formattedMonth;
        } else if (fileId == 25) {
            return directory_path + "kma_asos_1day/" + formattedYear + "/" + formattedMonth;
        } else if (fileId == 21) {
            return directory_path + "g120_v070_erea_unis_han_172ssb_10m/" + formattedDate + "/03";
        } else if (fileId == 22) {
            return directory_path + "g120_v070_erea_unis_han_172ssb_10m/" + formattedDate + "/09";
        } else if (fileId == 23) {
            return directory_path + "g120_v070_erea_unis_han_172ssb_10m/" + formattedDate + "/15";
        } else if (fileId == 24) {
            return directory_path + "g120_v070_erea_unis_han_172ssb_10m/" + formattedDate + "/21";
        } else if (fileId == 42) {
            return directory_path + "l015_v070_erlo_unis_han_172ssb_10m/" + formattedDate + "/03";
        } else if (fileId == 43) {
            return directory_path + "l015_v070_erlo_unis_han_172ssb_10m/" + formattedDate + "/09";
        } else if (fileId == 44) {
            return directory_path + "l015_v070_erlo_unis_han_172ssb_10m/" + formattedDate + "/15";
        } else if (fileId == 45) {
            return directory_path + "l015_v070_erlo_unis_han_172ssb_10m/" + formattedDate + "/21";
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
            if (fileId == 25 || fileId == 28 || fileId == 31) {
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
            } else if(fileId == 1 || fileId == 31){
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

        LocalDate today = LocalDate.now();
        LocalDate issuedMonth = LocalDate.of(today.getYear(), today.getMonth(), 1); // 현재 월의 첫 날
        LocalDate startDate = issuedMonth.withDayOfMonth(1); // 현재 달의 1일부터 시작
        LocalDate endDate = issuedMonth.withDayOfMonth(issuedMonth.lengthOfMonth()); // 현재 달의 마지막 날까지

        String fileExtension = commonFileUtil.fileExtension(fileId);

        for (LocalDate issuedDate = startDate; !issuedDate.isAfter(endDate); issuedDate = issuedDate.plusDays(1)) {
            String issueYear = issuedDate.format(DateTimeFormatter.ofPattern("yyyy"));
            String issueMonth = issuedDate.format(DateTimeFormatter.ofPattern("MM"));
            String issueDay = issuedDate.format(DateTimeFormatter.ofPattern("dd"));

            String fileName = issueYear + issueMonth + issueDay + fileExtension;

            // ✅ 1일이면 이전 달 마지막 날짜를 백업 파일로 사용, 아니면 전날 백업
            LocalDate backupDateLocal;
            if (issuedDate.getDayOfMonth() == 1) {
                backupDateLocal = issuedDate.minusMonths(1).withDayOfMonth(issuedDate.minusMonths(1).lengthOfMonth());
            } else {
                backupDateLocal = issuedDate.minusDays(1);
            }


            String backupYear = backupDateLocal.format(DateTimeFormatter.ofPattern("yyyy"));
            String backupMonth = backupDateLocal.format(DateTimeFormatter.ofPattern("MM"));
            String backupDay = backupDateLocal.format(DateTimeFormatter.ofPattern("dd"));

            String backupFileName = backupYear + backupMonth + backupDay + fileExtension;

            // ✅ **1월이면 1월에서만 찾도록 설정 (절대 2월로 넘어가지 않도록)**
            String backupDir = "/home/kms/data/asos_ssb_pet_1day/" + issueYear + "/" + issueMonth;

            log.info("📂 backupDir : " + backupDir);
            log.info("📄 backupFileName : " + backupFileName);

            Set<String> backupFiles = getExistingFiles(backupDir);

            createAndUploadFile(remoteDir, backupDir, fileName, backupFileName, existingFiles, backupFiles, fileId);
        }
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

                // 최대 7일 동안 과거데이터 조회
                for (int i = 1; i <= 1008; i++) {
                    backupDateTime = fileDateTime.minusMinutes(i * 10); // -10분씩 감소
                    String backupYear = backupDateTime.format(DateTimeFormatter.ofPattern("yyyy"));
                    String backupMonth = backupDateTime.format(DateTimeFormatter.ofPattern("MM"));
                    String backupDay = backupDateTime.format(DateTimeFormatter.ofPattern("dd"));
                    String backupTime = backupDateTime.format(DateTimeFormatter.ofPattern((fileId == 28) ? "HH" : "HHmm"));
                    String tempBackupFileName = backupYear + backupMonth + backupDay + backupTime + fileExtension;

                    // 백업 폴더 경로 업데이트
                    backupDir = backupBaseDir.replaceFirst("/\\d{4}/\\d{2}/\\d{2}$", "/" + backupYear + "/" + backupMonth + "/" + backupDay);
                    
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
     * 가장 적절한 백업 파일을 찾는 메서드
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
    public void createFile(String fileType, String issuedate) {
        try {
            connectSFTP();
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
            } else if (fileId == 1 || fileId == 31 || fileId == 28) {
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