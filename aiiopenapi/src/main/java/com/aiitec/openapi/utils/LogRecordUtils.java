package com.aiitec.openapi.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import android.os.Environment;

public class LogRecordUtils {

	private static final String TAG = "aiitec_err";
	
	public static void record(String content) {
		if(isSDCardExit()){
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
			String day = format.format(new Date());
			write("log"+day+".txt", content+"\n");
		}
	}
	private static void write(String fileName, String content){
		String path = Environment.getExternalStorageDirectory().getPath();
		File dir = new File(path);
		if(!dir.exists()){
			dir.mkdir();
		}
		File dirAii = new File(path+"/aiiLog");
		if(!dirAii.exists()){
			dirAii.mkdir();
		}
		
		LogUtil.e(TAG, path+"\n"+content);
		try {
			File targetFile = new File(dirAii.getAbsolutePath()+"/"+fileName);
			if(!targetFile.exists()){
				targetFile.createNewFile();
			}
			//以指定文件创建RandomAccessFile对象
			RandomAccessFile raf = new RandomAccessFile(targetFile, "rw");
			//将文件记录指针移动到最后
			raf.seek(targetFile.length());
			//输出文件内容
			byte[] data = content.getBytes("utf-8");
			raf.write(data);
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	  //判断sd卡是否存在 
	private static boolean isSDCardExit(){
		 return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); 
	}
	
	/**
	 * 读取当天错误日志信息
	 * @return
	 * @throws IOException
	 */
	public static String readLog() throws IOException {
		StringBuffer sb = new StringBuffer();
		if(isSDCardExit()){
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
			String day = format.format(new Date());
			String path = Environment.getExternalStorageDirectory().getPath();
			String fileName = path+"/aiiLog/log"+day+".txt";
			File file = new File(fileName);
			if(file.exists()){
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line = "";
				while((line = br.readLine())!=null){
					sb.append(line);
				}
				br.close();
			}
		}
		return sb.toString();
	}

}
