package com.diboot.file.util;

import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.D;
import com.diboot.core.util.PropertiesUtils;
import com.diboot.core.vo.Status;
import com.diboot.file.config.Cons;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/***
 * 文件操作辅助类
 * @author Mazc
 */
@Slf4j
public class FileHelper{
	/**
	 * file验证
	 */
	public static final List<String> DANGER_FILE_SUFFIX = Arrays.asList("exe", "bat", "bin", "dll", "sh");

	/**
	 * excel格式
	 */
	private static final List<String> EXCEL_SUFFIX = Arrays.asList("xls", "xlsx", "xlsm");

	/**
	 * 文件存储路径参数名
	 */
	public static final String FILE_STORAGE_DIRECTORY = "files.storage.directory";
	/**
	 * 文件存储路径
	 */
	private static final String PATH_FILE = "/upload/file";

	public static final String POINT = ".";
	public static final String HTTP = "http";
	public static final String QUESTION_MARK = "?";

	/**
	 * 文件和图片的后台存储路径
	 */
	private static String fileStorageDirectory = null;
	
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
				log.debug("保存文件成功！路径为: " + fullPath);
			}
			return fullPath;
		}
		catch (IOException e1) {
			log.error("保存文件失败(file=" + fullPath + "): ", e1);
			return null;
		}
	}

	/***
	 * 根据名称取得后缀
	 * @param fileName
	 * @return
	 */
	public static String getFileExtByName(String fileName){
		if(fileName.startsWith(HTTP) && fileName.contains(Cons.FILE_PATH_SEPARATOR)){
			fileName = getFileName(fileName);
		}
		if(fileName.lastIndexOf(POINT) > 0){
			return fileName.substring(fileName.lastIndexOf(POINT)+1).toLowerCase();
		}
		log.warn("检测到没有后缀的文件名:" + fileName);
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
				throw new BusinessException(Status.FAIL_VALIDATION, "文件存储路径参数 "+FILE_STORAGE_DIRECTORY+" 未配置.");
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

}