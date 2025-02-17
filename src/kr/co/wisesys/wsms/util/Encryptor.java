package kr.co.wisesys.wsms.util;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.extern.slf4j.Slf4j;

public class Encryptor {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	public static void main(String[] args) {
		
		Encryptor enc = new Encryptor();
		
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setAlgorithm("PBEWithMD5AndDES");  
        encryptor.setPassword("TEST_KEY"); 
        
        // DB정보 암호화 (driverClassName,URL,ID,PWD)
        String encryptedDriverClassName = encryptor.encrypt("com.mysql.jdbc.Driver");
        String encryptedURL = encryptor.encrypt("jdbc:mysql://211.209.185.143:3306/wise?useUnicode=true&characterEncoding=utf-8");
        String encryptedID = encryptor.encrypt("wise_dev");
        String encryptedPWD= encryptor.encrypt("fD@Vn2Fhf7");
        
        // work 서버 접속정보 암호화 (ip,port,id,pwd)
        String encryptedHost = encryptor.encrypt("192.168.0.164");
        String encryptedPort = encryptor.encrypt("6021");
        String encryptedUser = encryptor.encrypt("wisesys");
        String encryptedPassword = encryptor.encrypt("#sRJj!wy77YQhg");
        
        //enc.log.info("DB : " + encryptedDriverClassName+ " | Encrypted URL : " + encryptedURL + " | Encrypted ID : " + encryptedID + " | Encrypted PWD : " + encryptedPWD );
        
        //enc.log.info("Encrypted Host: " + encryptedHost);
        //enc.log.info("Encrypted Port: " + encryptedPort);
        //enc.log.info("Encrypted User: " + encryptedUser);
        //enc.log.info("Encrypted Password: " + encryptedPassword);
        
        // DB정보 복호화 (driverClassName,URL,ID,PWD)
        String decryptedDriverClassName = encryptor.decrypt(encryptedDriverClassName);
        String decryptedURL = encryptor.decrypt(encryptedURL);
        String decryptedID = encryptor.decrypt(encryptedID);
        String decryptedPWD = encryptor.decrypt(encryptedPWD);
        
        // work 서버 접속정보 복호화 (ip,port,id,pwd)
        String decryptedHost = encryptor.decrypt(encryptedHost);
        String decryptedPort = encryptor.decrypt(encryptedPort);
        String decryptedUser = encryptor.decrypt(encryptedUser);
        String decryptedPassword = encryptor.decrypt(encryptedPassword);
        
        //enc.log.info("DB : " + decryptedDriverClassName+ " | Decrypted URL : " + decryptedURL + " | Decrypted ID : " + decryptedID + " | Decrypted PWD : " + decryptedPWD );
        
        //enc.log.info("decrypted Host: " + decryptedHost);
        //enc.log.info("decrypted Port: " + decryptedPort);
        //enc.log.info("decrypted User: " + decryptedUser);
        //enc.log.info("decrypted Password: " + decryptedPassword);
        
		
	}
	
	
}
