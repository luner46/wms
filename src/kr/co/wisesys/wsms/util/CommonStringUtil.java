package kr.co.wisesys.wsms.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
//import java.util.Base64;
//import java.util.Base64.Decoder;
//import java.util.Base64.Encoder;
import java.util.Collections;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonStringUtil {
	
	private static final Logger log = LoggerFactory.getLogger(CommonStringUtil.class);
	
	/**
	 *  updateSHA256 암호화 (복호화 불가능)
	 * <pre>
	 * ex) 
	 * String nameSHA256 = CommonStringUtil.updateSHA256("name");
	 * </pre>
	 * @author  안주영
	 * @version 1.0
	 * @param <b>targetString (String)</b> ex) "name"
	 * @return <b>targetString (String)</b>
	*/
	public static String updateSHA256(String targetString){
		
		try{
			MessageDigest sh = MessageDigest.getInstance("SHA-256"); 
			sh.update(targetString.getBytes()); 
			byte byteData[] = sh.digest();
			StringBuffer sb = new StringBuffer(); 
			
			for(int i = 0 ; i < byteData.length ; i++){
				sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
			}
			
			targetString = sb.toString();
			
		}catch(NoSuchAlgorithmException e){
			log.error(e.toString());
			// e.printStackTrace(); 
			targetString = null; 
		}
		
		return targetString;
	}
	
	/**
	 *  updateAES256 암호화 (복호화 가능, 키 길이가 16이여야함)
	 * <pre>
	 * ex) 
	 * String nameAES256 = CommonStringUtil.updateAES256("name", "secret_key123456");
	 * </pre>
	 * @author  안주영
	 * @version 1.0
	 * @param <b>targetString (String)</b> ex) "name"
	 * @param <b>cryptoKey (String)</b> ex) "secret_key123456"
	 * @return <b>enStr (String)</b> ex) "ZL+fiPNlnVipWniEt1adcg=="
	*/
	public static String updateAES256(String targetString, String cryptoKey){
		String iv;
		String enStr = null;
		
		try{
			iv = cryptoKey.substring(0, 16);
			byte[] keyBytes = new byte[16];
			byte[] b = iv.getBytes("UTF-8");
			int len = b.length;
			if(len > keyBytes.length)	len = keyBytes.length;
			System.arraycopy(b, 0, keyBytes, 0, len);
			Key keySpec = new SecretKeySpec(keyBytes, "AES");
			
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));

			byte[] encrypted = c.doFinal(targetString.getBytes("UTF-8"));
			Encoder encoder = Base64.getEncoder();

			enStr = new String(encoder.encode(encrypted));
			
		}catch(IllegalArgumentException e){
			log.error(e.toString());
			// e.printStackTrace(); 
		} catch (UnsupportedEncodingException e) {
			log.error(e.toString());
			// e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			log.error(e.toString());
			// e.printStackTrace();
		} catch (BadPaddingException e) {
			log.error(e.toString());
			// e.printStackTrace();
		} catch (InvalidKeyException e) {
			log.error(e.toString());
			// e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			log.error(e.toString());
			// e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			log.error(e.toString());
			// e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			log.error(e.toString());
			// e.printStackTrace();
		}
		
		return enStr;
	}
	
	/**
	 *  updateAES256 복호화(키길이가 16이여야함)
	 * <pre>
	 * ex) 
	 * String name = CommonStringUtil.updateAES256ToBefore("ZL+fiPNlnVipWniEt1adcg==", "secret_key123456");
	 * </pre>
	 * @author  안주영
	 * @version 1.0
	 * @param <b>targetString (String)</b> ex) "ZL+fiPNlnVipWniEt1adcg=="
	 * @param <b>cryptoKey (String)</b> ex) "secret_key123456"
	 * @return <b>deStr (String)</b> ex) "name"
	*/
	public static String updateAES256ToBefore(String targetString, String cryptoKey){
		String iv;
		String deStr = null;
		
		try{
			iv = cryptoKey.substring(0, 16);
			byte[] keyBytes = new byte[16];
			byte[] b = iv.getBytes("UTF-8");
			int len = b.length;
			if(len > keyBytes.length)	len = keyBytes.length;
			System.arraycopy(b, 0, keyBytes, 0, len);
			Key keySpec = new SecretKeySpec(keyBytes, "AES");
			
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes("UTF-8")));

			Decoder decoder = Base64.getDecoder();

			deStr = new String(c.doFinal(decoder.decode(targetString.getBytes())),"UTF-8");

		}catch(IllegalArgumentException e){
			log.error(e.toString());
			// e.printStackTrace(); 
		} catch (UnsupportedEncodingException e) {
			log.error(e.toString());
			// e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			log.error(e.toString());
			// e.printStackTrace();
		} catch (BadPaddingException e) {
			log.error(e.toString());
			// e.printStackTrace();
		} catch (InvalidKeyException e) {
			log.error(e.toString());
			// e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			log.error(e.toString());
			// e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			log.error(e.toString());
			// e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			log.error(e.toString());
			// e.printStackTrace();
		}
		
		return deStr;
	}
	
	/**
	 * 관측소 빈곳 채워 새로운 파일 만들기(라인별 리스트 생성후 비교)
	 * <pre>
	 * ex)
	 * String S = File.separator;
	 * String baseArrayListPath = "src"+S+"resource"+S+"ASOS_AWS_STN_LIST.txt";
	 * String returnArrayListPath = "src"+S+"resource"+S+"ASOS_AWS_STN_LIST2.txt";
	 * String downloadFilePath = "src"+S+"resource"+S+"ASOS_AWS_STN_LIST5.txt";
	 * CommonStringUtil.insertStnListFile(baseArrayListPath, returnArrayListPath, downloadFilePath);
	 * 
	 * file ex)
	 * """
	 * 90
	 * 92
	 * ...
	 * 100
	 * """
	 * </pre>
	 * @author  안주영
	 * @version 1.0
	 * @param <b>baseArrayListPath (String)</b> ex) "src/resource/ASOS_AWS_STN_LIST.txt"
	 * @param <b>returnArrayListPath (String)</b> ex) "src/resource/ASOS_AWS_STN_LIST2.txt"
	 * @param <b>downloadFilepath (String)</b> ex) "src/resource/ASOS_AWS_STN_LIST5.txt"
	*/
	public static void insertStnListFile(String baseArrayListFilepath, String compareArrayListFilepath, String downloadFilepath) {
		ArrayList <String> baseArrayList = new ArrayList <String>();
		ArrayList <String> compareArrayList = new ArrayList <String>();
		ArrayList <Integer> intArrayList = new ArrayList <Integer>();
        File useFile;
        FileReader useFilereader;
        BufferedReader useBufReader;
        String useline;
        
		try {
			// 파일을 arrayList로
			useFile = new File(baseArrayListFilepath);
			useFilereader = new FileReader(useFile);
			useBufReader = new BufferedReader(useFilereader);
			useline = "";
			
            while((useline = useBufReader.readLine()) != null){
                baseArrayList.add(useline);
            }          
            
            useBufReader.close();
            
            useFile = new File(compareArrayListFilepath);
			useFilereader = new FileReader(useFile);
			useBufReader = new BufferedReader(useFilereader);
			useline = "";
			
            while((useline = useBufReader.readLine()) != null){
                baseArrayList.add(useline);
            }          
            
            useBufReader.close();
			
			// 리스트 비교 후 채운뒤 없는 부분 채우기
			for(String baseItem : baseArrayList) {
	            if(!compareArrayList.contains(baseItem)) {
	            	compareArrayList.add(baseItem);
	            }
	        }
			
			// String 리스트를 Integer 리스트로 변환 후 정렬
            intArrayList = new ArrayList<Integer>(compareArrayList.size()) ;
            
            for (String myInt : compareArrayList) { 
            	intArrayList.add(Integer.valueOf(myInt)); 
            }
            
            Collections.sort(intArrayList);
            
            // Integer 리스트를 String 리스트로
            compareArrayList = new ArrayList<String>(intArrayList.size());
            
            for (Integer myString : intArrayList) { 
            	compareArrayList.add(Integer.toString(myString)); 
            }
            
            // 채운 리스트 파일쓰기
            useFile = new File(downloadFilepath);
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(useFile));
            
            if(useFile.isFile() && useFile.canWrite()){
                for(String i : compareArrayList) {
                    bufferedWriter.write(i);
                    bufferedWriter.newLine();
                }
                
                bufferedWriter.close();
            }
            
		}catch(IOException e) {
			log.error(e.toString());
			// e.printStackTrace();
		}
	}
	
	/**
	 * null 체크(string반환)
	 * <pre>
	 * ex)
	 * String test = CommonStringUtil.updateNullToString("test"); 
	 * </pre>
	 * @author  안주영
	 * @version 1.0
	 * @param <b>obj (Object)</b> ex) null
	 * @return <b>returnString (String)</b> ex) ""
	*/
	public static String updateNullToString(Object obj) {
		String returnString = "";
		
		try {
			if (obj instanceof String) { 
				returnString = (String)obj;
				
			}  else if (Objects.isNull(obj) ||  Double.isNaN((Double)obj) ||  Double.isInfinite((Double)obj)) { 
				// System.out.println("null값 확인 필요.");
				
			}  else {
				returnString = obj.toString();
				// System.out.println("값 확인 필요.");
			}
			
			return returnString;
			
		}catch(NullPointerException e) {
			log.error(e.toString());
			// e.printStackTrace();
        	return returnString;
		}
		
	}
	
	
	/**
	 * null 체크 (오브젝트, 디폴트)
	 * <pre>
	 * ex)
	 * String test = (String) CommonStringUtil.updateNullToDefault("test", "0"); 
	 * </pre>
	 * @author  안주영
	 * @version 1.0
	 * @param <b>confirmObj (Object)</b> ex) "test"
	 * @param <b>defaultObj (Object)</b> ex) "0"
	 * @return <b>returnObj (Object)</b> ex) "test"
	*/
	public static Object updateNullToDefault(Object confirmObj, Object defaultObj) {
		Object returnObj = defaultObj;
		
		try {
			if (!Objects.isNull(confirmObj)) { 
				// System.out.println("null값 확인 필요.");
				returnObj = confirmObj;
			}  
			
			return returnObj;
			
		}catch(NullPointerException e) {
			log.error(e.toString());
			// e.printStackTrace();
        	return returnObj;
		}
		
	}
	
}