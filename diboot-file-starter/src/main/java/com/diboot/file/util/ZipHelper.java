/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.diboot.file.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/***
 * 文件压缩操作辅助类
 * @author mazc@dibo.ltd
 * @version v2.0
 */
@Slf4j
public class ZipHelper {

	/**
	 * 递归压缩文件
	 * @param srcRootDir
	 * @param file
	 * @param zos
	 * @return
	 * @throws Exception
	 */
	private static void zipFile(String srcRootDir, File file, ZipOutputStream zos, String... matchKeyword) throws Exception {
		if (file == null) {
			log.error("[Zip]file对象为空,压缩失败！");
			return;
		}
		//如果是文件，则直接压缩该文件
		if (file.isFile()) {
			int count, bufferLen = 1024;  
		    byte[] data = new byte[bufferLen];
		    String fileName = file.getName();
		    
		    //包含在指定文件名范围内的文件进行压缩
		    if (matchKeyword == null || matchKeyword.length == 0 || fileName.indexOf(matchKeyword[0]) >= 0) {
		    	//获取文件相对于压缩文件夹根目录的子路径
			    String subPath = file.getAbsolutePath(); 
			    subPath = subPath.replace("\\", "/"); //解决文件路径分割符Unix和Windows不兼容的问题
			    if (subPath.indexOf(srcRootDir) != -1) {
			        subPath = subPath.substring(srcRootDir.length() + File.separator.length());
			    }
			    ZipEntry entry = new ZipEntry(subPath);
			    zos.putNextEntry(entry);
			    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			    while ((count = bis.read(data, 0, bufferLen)) != -1) {
			        zos.write(data, 0, count);
			    }
			    log.info("[Zip]压缩成功：" + file.getName());
			    bis.close();
			    zos.closeEntry();
		    }
		}
		else {
	    	//压缩目录中的文件或子目录
        	File[] childFileList = file.listFiles();
        	String filePath = "";
        	if(childFileList != null) {
        		for (int n=0; n<childFileList.length; n++)
            	{
            		if (!childFileList[n].isFile()) {
            			filePath = childFileList[n].getPath();
            			//不在指定目录名范围内的目录不进行压缩
            			if (matchKeyword != null && matchKeyword.length > 0 && filePath.indexOf(matchKeyword[0]) == -1) {
            				continue;
            			}
            		}
            	    zipFile(srcRootDir, childFileList[n], zos, matchKeyword);
            	}
        	}
        }
	}
	
	/** 
	 * 对文件或文件目录进行压缩 
	 * @param srcPath 要压缩的源文件路径。如果压缩一个文件，则为该文件的全路径；如果压缩一个目录，则为该目录的顶层目录路径 
	 * @param zipPath 压缩文件保存的路径。注意：zipPath不能是srcPath路径下的子文件夹 
	 * @param zipFileName 压缩文件名 
	 * @param excludeKeyword 需要剔除
	 * @throws Exception 
	 */  
	public static boolean zip(String srcPath, String zipPath, String zipFileName, String... excludeKeyword) throws Exception {  
	    if (StringUtils.isEmpty(srcPath)){
	    	log.error("[Zip]源文件路径为空，压缩失败！");
			return false;
	    }
	    if (StringUtils.isEmpty(zipPath)){
	    	log.error("[Zip]压缩文件保存的路径为空，压缩失败！");
			return false;
	    }
	    if (StringUtils.isEmpty(zipFileName)){
	    	log.error("[Zip]压缩文件名为空，压缩失败！");
			return false;
	    }
	    CheckedOutputStream cos = null;
	    ZipOutputStream zos = null;
        File srcFile = new File(srcPath);
        if (srcFile.exists()) {
        	//判断压缩文件保存的路径是否为源文件路径的子文件夹，如果是，则终止压缩
            if (srcFile.isDirectory() && zipPath.indexOf(srcPath)!=-1) {
            	log.error("[Zip]压缩文件保存的路径是源文件的字文件夹,压缩失败！");
    			return false;
    	    }
            //判断压缩文件保存的路径是否存在，如果不存在，则创建目录
            File zipDir = new File(zipPath);  
            if (!zipDir.exists() || !zipDir.isDirectory()) {
                boolean success = zipDir.mkdirs();
				if(!success) {
					log.warn("创建文件夹: {} 失败", zipPath);
				}
            }  
             //创建压缩文件保存的文件对象
             String zipFilePath = zipPath + "/" + zipFileName;
             File zipFile = new File(zipFilePath);
             if (zipFile.exists()){
                 //删除已存在的目标文件
				 boolean success = zipFile.delete();
				 if(!success) {
					 log.warn("删除文件夹: {} 失败", zipFilePath);
				 }
             }
             cos = new CheckedOutputStream(new FileOutputStream(zipFile), new CRC32());
             zos = new ZipOutputStream(cos);
             //如果只是压缩一个文件，则需要截取该文件的父目录
             String srcRootDir = srcPath;
             if (srcFile.isFile()){
                 int index = srcPath.lastIndexOf("/");
                 if (index != -1){
                     srcRootDir = srcPath.substring(0, index);
                 }
             }
             //调用递归压缩方法进行目录或文件压缩
             zipFile(srcRootDir, srcFile, zos, excludeKeyword);  
             zos.flush();
			 zos.close();
             return true;
        }
        else {
        	log.error("[Zip]当前源目录不存在，压缩失败！" + srcPath);
        	return false;
        }
	}
}
