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

import com.diboot.core.util.D;
import com.diboot.core.util.S;
import com.diboot.file.config.Cons;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

/***
 *  图片操作辅助类
 * @author mazc@dibo.ltd
 * @version v2.0
 */
@Slf4j
public class ImageHelper {
	private static final String DATA_IMAGE_FLAG = "data:image/";
	private static final String BASE_64_FLAG = "base64,";
	private static final String PATH_IMG = "/upload/img";
	/**
	 * image验证
	 */
	public static final String VALID_IMAGE_SUFFIX = "|png|jpg|jpeg|gif|bmp|";

	/**
	 * 是否是图片类型
	 * @param ext
	 * @return
	 */
	public static boolean isImage(String ext){
		return VALID_IMAGE_SUFFIX.contains("|"+ext.toLowerCase()+"|");
	}

	/**
	 * 保存图片
	 * @param file 上传文件
	 * @param imgName 图片名称
	 */
	public static String saveImage(MultipartFile file, String imgName){
		// 生成图片路径
		String accessPath = getImageStoragePath(imgName);
		String fullPath = FileHelper.getFileStorageDirectory() + accessPath;
		try {
			// 创建文件夹
			FileHelper.makeDirectory(fullPath);
			FileUtils.writeByteArrayToFile(new File(fullPath), file.getBytes());
			if(log.isDebugEnabled()){
				log.debug("保存图片成功！路径为: {}", accessPath);
			}
			return accessPath;
		}
		catch (IOException e1) {
			log.error("保存原图片失败: image={}", accessPath, e1);
			return null;
		}
	}

	/**
	 * 保存图片
	 * @param file 上传文件
	 */
	public static String saveImage(MultipartFile file){
		String fileName = file.getOriginalFilename();
		String ext = S.substringAfterLast(fileName,".");
		String newFileName = S.newUuid() + "." + ext;
		return saveImage(file, newFileName);
	}

	/**
	 * 保存图片
	 * @param file 上传文件
	 * @param imgName 图片名称
	 * @param reserve 是否保留原图片
	 * @return
	 */
	public static String saveImage(File file, String imgName, boolean reserve){
		// 生成图片路径
		String accessPath = getImageStoragePath(imgName);
		String fullPath = FileHelper.getFileStorageDirectory() + accessPath;
		try{
			// 创建文件夹
			FileHelper.makeDirectory(fullPath);
			FileUtils.copyFile(new File(fullPath), file);
			if(log.isDebugEnabled()){
				log.debug("保存图片成功！路径为: {}", accessPath);
			}
			// 如果原文件与目标文件不相等且不保留原文件，则删除原文件
			if (!reserve && !StringUtils.equals(file.getAbsolutePath(), fullPath)){
				FileUtils.forceDelete(file);
			}
			return accessPath;
		} catch (Exception e){
			log.error("保存原图片失败: image={}", accessPath, e);
			return null;
		}
	}

	/**
	 * 得到图片存储的全路径
	 * @param fileName
	 * @return
	 */
	public static String getImageStoragePath(String fileName){
		StringBuilder sb = new StringBuilder();
		sb.append(PATH_IMG).append(Cons.FILE_PATH_SEPARATOR).append(D.getYearMonth()).append(Cons.FILE_PATH_SEPARATOR).append(fileName);
		return sb.toString();
	}

	/**
	 * 将Base64转换为图片
	 * @param base64Str
	 * @param fullFilePath
	 * @return
	 * @throws IOException
	 */
	public static boolean convertBase64ToImage(String base64Str, String fullFilePath){
		if(base64Str == null){
			return false;
		}
		if(base64Str.contains(BASE_64_FLAG)){
			//int suffixStart = StringUtils.indexOf(base64Str, DATA_IMAGE_FLAG)+ DATA_IMAGE_FLAG.length();
			//String suffix = StringUtils.substring(base64Str, suffixStart, StringUtils.indexOf(base64Str, ";", suffixStart));
			base64Str = base64Str.substring(base64Str.indexOf(BASE_64_FLAG)+ BASE_64_FLAG.length());
		}
		try{
			byte[] data = Base64.getDecoder().decode(base64Str);
			File file = new File(fullFilePath);
			FileUtils.writeByteArrayToFile(file, data);
			return true;
		}
		catch(Exception e){
			log.warn("base", e);
			return false;
		}
	}

}
