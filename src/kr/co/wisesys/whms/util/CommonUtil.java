package kr.co.wisesys.whms.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CommonUtil {
	public void copy(File sourceF, File targetF) {
		File[] target_file = sourceF.listFiles();
		
		for(File file : target_file) {
			File temp = new File(targetF.getAbsolutePath() + File.separator + file.getName());
			if(file.isDirectory()) {
				temp.mkdir();
				copy(file, temp);
			} else {
				FileInputStream fis = null;
				FileOutputStream fos = null;
				
				try {
					fis = new FileInputStream(file);
					fos = new FileOutputStream(temp);
					
					byte[] b = new byte[4096];
					int cnt = 0;
					
					while((cnt=fis.read(b)) != -1) {
						fos.write(b, 0, cnt);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					
				} finally{
					try {
						fis.close();
						fos.close();
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
				}
			}
		}	
    }
	
	public String getCurrentHour() throws Exception { 
		Date now = new Date();
		
		SimpleDateFormat date = new SimpleDateFormat("HH", Locale.KOREAN);
		String result = date.format(now);
		return result;
	}
	
}