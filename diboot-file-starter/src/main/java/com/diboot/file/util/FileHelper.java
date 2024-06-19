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

import com.diboot.core.exception.BusinessException;
import com.diboot.core.exception.InvalidUsageException;
import com.diboot.core.util.ContextHolder;
import com.diboot.core.util.D;
import com.diboot.core.util.PropertiesUtils;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.file.config.Cons;
import com.diboot.file.service.FileStorageService;
import com.diboot.file.service.impl.AliyunOssFileStorageServiceImpl;
import com.diboot.file.service.impl.LocalFileStorageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

/***
 * 文件操作辅助类
 * @author mazc@dibo.ltd
 * @version v2.0
 */
@Slf4j
public class FileHelper{
	/**
	 * file验证
	 */
	public static final List<String> DANGER_FILE_SUFFIX = Arrays.asList("exe","bat","bin","dll","sh","php","pl","py","cgi","asp","aspx","jsp","php5","php4","php3","js","htm","html","go");

	/**
	 * excel格式
	 */
	private static final List<String> EXCEL_SUFFIX = Arrays.asList("csv", "xls", "xlsx", "xlsm");

	/**
	 * 文件存储路径参数名
	 */
	public static final String FILE_STORAGE_DIRECTORY = "diboot.file.storage-directory";
	/**
	 * 文件存储路径
	 */
	private static final String PATH_FILE = "/upload/file";

	public static final String POINT = ".";
	public static final String HTTP = "http";
	public static final String QUESTION_MARK = "?";

	/**
	 * 文件大小单位
	 */
	private static final String[] SIZE_UNIT = {"Bytes", "KB", "MB", "GB", "TB", "PB"};
	/**
	 * 文件和图片的后台存储路径
	 */
	private static String fileStorageDirectory = null;

	/**
	 * 是否为本地存储
	 */
    private static Boolean isLocalStorage;

	/***
	 * 是否为合法的文件类型
	 * @param ext
	 * @return
	 */
	public static boolean isValidFileExt(String ext){
		return !DANGER_FILE_SUFFIX.contains(ext.toLowerCase());
	}

	/**
	 * 是否是Excel文件
	 * @param fileName
	 * @return
	 */
	public static boolean isExcel(String fileName){
		String ext = FileHelper.getFileExtByName(fileName);
		return EXCEL_SUFFIX.contains(ext.toLowerCase());
	}

    /**
     * 判断是否为本地存储
     *
     * @return
     * @since v2.4.0
     */
    public static boolean isLocalStorage() {
        if (isLocalStorage == null) {
			FileStorageService fileStorageService = ContextHolder.getBean(FileStorageService.class);
            isLocalStorage = fileStorageService instanceof LocalFileStorageServiceImpl;
        }
        return Boolean.TRUE.equals(isLocalStorage);
    }

	/***
	 * 获取系统临时目录
	 * @return
	 */
	public static String getSystemTempDir(){
		return System.getProperty("java.io.tmpdir");
	}

	/***
	 * 上传文件
	 * @param file 上传文件
	 * @param fileName 文件名
	 * @return
	 */
	public static String saveFile(MultipartFile file, String fileName) {
		// 生成文件路径
		String fullPath = getFullPath(fileName);
		try {
			// 创建文件夹
			makeDirectory(fullPath);
			FileUtils.writeByteArrayToFile(new File(fullPath), file.getBytes());
			if(log.isDebugEnabled()){
				log.debug("保存文件成功！路径为: {}", fullPath);
			}
			return fullPath;
		}
		catch (IOException e1) {
			log.error("保存文件失败: file={}",  fullPath, e1);
			return null;
		}
	}

	/***
	 * 上传文件
	 * @param inputStream 文件流
	 * @param fileName 文件名
	 * @return
	 */
	public static String saveFile(InputStream inputStream, String fileName) {
		// 生成文件路径
		String fullPath = getFullPath(fileName);
		try {
			// 创建文件夹
			makeDirectory(fullPath);
			FileUtils.copyInputStreamToFile(inputStream, new File(fullPath));
			if(log.isDebugEnabled()){
				log.debug("保存文件成功！路径为: {}", fullPath);
			}
			return fullPath;
		}
		catch (IOException e1) {
			log.error("保存文件失败: file={}", fullPath, e1);
			return null;
		}
	}

	/***
	 * 根据名称取得后缀
	 * @param fileName
	 * @return
	 */
	public static String getFileExtByName(String fileName){
		if(V.isEmpty(fileName)) {
			throw new BusinessException(Status.FAIL_INVALID_PARAM, "文件名为空：{}", fileName);
		}
		if(fileName.startsWith(HTTP) && fileName.contains(Cons.FILE_PATH_SEPARATOR)){
			fileName = getFileName(fileName);
		}
		if(fileName.lastIndexOf(POINT) > 0){
			return fileName.substring(fileName.lastIndexOf(POINT)+1).toLowerCase();
		}
		log.debug("检测到没有后缀的文件名: {}", fileName);
		return "";
	}

	/***
	 * 获取文件的相对路径
	 * @param fileName 仅文件名，不含相对路径
	 * @return
	 */
	public static String getRelativePath(String fileName) {
		StringBuilder sb = new StringBuilder();
		sb.append(PATH_FILE).append("/").append(D.getYearMonth()).append("/").append(fileName);
		return sb.toString();
	}

	/***
	 * 获取文件的完整存储路径
	 * @param fileName 仅文件名，不含相对路径
	 * @return
	 */
	public static String getFullPath(String fileName) {
		String relativePath = getRelativePath(fileName);
		return getFileStorageDirectory() + relativePath;
	}

	/**
	 * 根据文件URL解析出其文件名
	 * @param fileUrl
	 * @return
	 */
	public static String getFileName(String fileUrl){
		String temp = StringUtils.substring(fileUrl, fileUrl.lastIndexOf(Cons.FILE_PATH_SEPARATOR)+1);
		if(StringUtils.contains(fileUrl, QUESTION_MARK)){
			temp = StringUtils.substring(temp, 0, temp.lastIndexOf(QUESTION_MARK));
		}
		return temp;
	}

	/**
	 * 文件的存储路径
	 * @return
	 */
	public static String getFileStorageDirectory(){
		if(fileStorageDirectory == null){
			fileStorageDirectory = PropertiesUtils.get(FILE_STORAGE_DIRECTORY);
			if(fileStorageDirectory == null){
				throw new InvalidUsageException("exception.invalidUsage.fileHelper.getFileStorageDirectory.message", FILE_STORAGE_DIRECTORY);
			}
		}
		return fileStorageDirectory;
	}

	/***
	 * 创建文件夹
	 * @param dirPath
	 * @return
	 */
	public static boolean makeDirectory(String dirPath){
		String directory = StringUtils.substringBeforeLast(dirPath, Cons.FILE_PATH_SEPARATOR);
		File dir = new File(directory);
		if(!dir.exists()){
			try {
				FileUtils.forceMkdir(dir);
				return true;
			}
			catch (IOException e) {
				log.error("创建文件夹失败", e);
				return false;
			}
		}
		return false;
	}

	/****
	 * 删除文件
	 * @param fileStoragePath
	 */
	public static boolean deleteFile(String fileStoragePath) {
		File file = new File(fileStoragePath);
		if(file.exists()){
			return file.delete();
		}
		return false;
	}

	/**
	 * 格式化文件大小
	 * @param bytes
	 * @return
	 */
	public static String formatFileSize(Long bytes) {
		if(bytes == null || bytes.equals(0L)) {
			return "-";
		}
		int index = (int) (Math.floor(Math.log(bytes) / Math.log(1024)));
		double size = bytes / Math.pow(1024, index);
		size = Double.valueOf(new DecimalFormat("#.0").format(size));
		if(index > SIZE_UNIT.length -1) {
			return "?PB";
		}
		return size + " " +SIZE_UNIT[index];
	}

}
