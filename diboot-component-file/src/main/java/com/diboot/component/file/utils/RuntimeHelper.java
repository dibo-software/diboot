package com.diboot.component.file.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/***
 * Java执行命令行辅助类
 * @author Mazc
 */
public class RuntimeHelper {
	private static final Logger logger = LoggerFactory.getLogger(RuntimeHelper.class);
	
	public static boolean run(String command){
		Runtime runtime = null;
		try{
			runtime = Runtime.getRuntime();
			Process process = runtime.exec(command); //  -ar 8000 -ac 1 -y -ab 12.4k 
			// 转换成功
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String line;
			while((line=reader.readLine())!=null){
				logger.debug(line);
			}
			int result = process.waitFor();
			if(result == 0){
				process.destroy();
				return true;
			}
			else{
				process.destroy();
				throw new RuntimeException("运行命令失败: "+command);  
			}
		}
		catch(Exception e){
			logger.error("运行命令失败: ", e);
			return false;
		}		
		finally{
			if(runtime!=null){
				runtime.freeMemory();
			}
		}
	}
}
