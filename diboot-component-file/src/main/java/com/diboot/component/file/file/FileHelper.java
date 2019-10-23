package com.diboot.component.file.file;

import com.diboot.component.file.file.http.CustomSSLSocketFactory;
import com.diboot.core.util.PropertiesUtils;
import com.diboot.core.util.S;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * 文件操作辅助类
 * @author Mazc
 */
public class FileHelper{
	private static final Logger logger = LoggerFactory.getLogger(FileHelper.class);

	/**
	 * image验证
	 */
	public static final String VALID_IMAGE_SUFFIX = "|png|jpg|jpeg|gif|bmp|";
	/**
	 * file验证
	 */
	public static final String DANGER_FILE_SUFFIX = "|exe|bat|bin|dll|sh";
	/**
	 * excel格式
	 */
	public static final String EXCEL_SUFFIX = "|xls|xlsx|xlsm|";
	/**
	 * 文件存储路径参数名
	 */
	public static final String FILE_STORAGE_DIRECTORY = "files.storage.directory";

	/**
	 * 文件存储路径
	 */
	private static final String PATH_FILE = "/upload/file";
	private static final String PATH_IMG = "/upload/img";
	private static final String PATH_AUDIO = "/upload/audio";

	/**
	 * 日期格式-文件夹名称
	 */
	public static final String FORMAT_DATE_Y2M = "yyMM";
	public static final String FORMAT_DATE_Y2MD = "yyMMdd";

	/***
	 * 默认contextType
	 */
	private static final String DEFAULT_CONTEXT_TYPE = "application/octet-stream";

	/***
	 * 文件扩展名-ContentType的对应关系
	 */
	private static Map<String, String> EXT_CONTENT_TYPE_MAP = new HashMap(){{
		put("xls", "application/x-msdownload");
		put("xlsx", "application/x-msdownload");
		put("doc", "application/x-msdownload");
		put("docx", "application/x-msdownload");
		put("dot", "application/x-msdownload");
		put("ppt", "application/x-msdownload");
		put("pptx", "application/x-msdownload");
		put("pdf", "application/pdf");
		put("avi", "video/avi");
		put("bmp", "application/x-bmp");
		put("css", "text/css");
		put("dll", "application/x-msdownload");
		put("dtd", "text/xml");
		put("exe", "application/x-msdownload");
		put("gif", "image/gif");
		put("htm", "text/html");
		put("html", "text/html");
		put("ico", "image/x-icon");
		put("jpeg", "image/jpeg");
		put("jpg", "image/jpeg");
		put("js", "application/x-javascript");
		put("mp3", "audio/mp3");
		put("mp4", "video/mpeg4");
		put("png", "image/png");
		put("svg", "text/xml");
		put("swf", "application/x-shockwave-flash");
		put("tif", "image/tiff");
		put("tiff", "image/tiff");
		put("tld", "text/xml");
		put("torrent", "application/x-bittorrent");
		put("tsd", "text/xml");
		put("txt", "text/plain");
		put("wav", "audio/wav");
		put("wma", "audio/x-ms-wma");
		put("wmf", "application/x-wmf");
		put("wsdl", "text/xml");
		put("xhtml", "text/html");
		put("xml", "text/xml");
		put("xsd", "text/xml");
		put("xsl", "text/xml");
		put("xslt", "text/xml");
		put("apk", "application/vnd.android.package-archive");
	}};

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
		return !DANGER_FILE_SUFFIX.contains("|"+ext.toLowerCase());
	}
	
	/**
	 * 是否是图片类型
	 * @param ext
	 * @return
	 */
	public static boolean isImage(String ext){
		return VALID_IMAGE_SUFFIX.contains("|"+ext.toLowerCase()+"|");
	}

	/**
	 * 是否是Excel文件
	 * @param fileName
	 * @return
	 */
	public static boolean isExcel(String fileName){
		String ext = getFileExtByName(fileName);
		return EXCEL_SUFFIX.contains("|"+ext.toLowerCase()+"|");
	}

	/***
	 * 获取系统临时目录
	 * @return
	 */
	public static String getSystemTempDir(){
		return System.getProperty("java.io.tmpdir");
	}

	/***
	 * 将文件保存到系统临时目录
	 * @param file
	 * @param fileName
	 * @return
	 */
	public static String saveFile2TempDir(MultipartFile file, String fileName){
		String fullPath = getSystemTempDir() + fileName;
		try {
			FileUtils.writeByteArrayToFile(new File(fullPath), file.getBytes());
			return fileName;
		}
		catch (IOException e1) {
			logger.error("保存原图片失败(image=" + fileName + "): ", e1);
			return null;
		}
	}

	/**
	 * 保存图片
	 * @param file 上传文件
	 * @param imgName 图片名称
	 */
	public static String saveImage(MultipartFile file, String imgName){
		// 生成图片路径
		String accessPath = getImageStoragePath(imgName);
		String fullPath = getFileStorageDirectory() + accessPath;
		try {
			// 创建文件夹
			makeDirectory(fullPath);
			FileUtils.writeByteArrayToFile(new File(fullPath), file.getBytes());
			if(logger.isDebugEnabled()){
				logger.debug("保存图片成功！路径为: " + accessPath);
			}
			return accessPath;
		}
		catch (IOException e1) {
			logger.error("保存原图片失败(image=" + accessPath + "): ", e1);
			return null;
		}
	}

	/**
	 * 保存图片
	 * @param file 上传文件
	 */
	public static String saveImage(MultipartFile file){
		String fileName = file.getOriginalFilename();
		String ext = fileName.substring(fileName.lastIndexOf(".")+1);
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
		String fullPath = getFileStorageDirectory() + accessPath;
		try{
			// 创建文件夹
			makeDirectory(fullPath);
			FileUtils.copyFile(new File(fullPath), file);
			if(logger.isDebugEnabled()){
				logger.debug("保存图片成功！路径为: " + accessPath);
			}
			// 如果原文件与目标文件不相等且不保留原文件，则删除原文件
			if (!reserve && !StringUtils.equals(file.getAbsolutePath(), fullPath)){
				FileUtils.forceDelete(file);
			}
			return accessPath;
		} catch (Exception e){
			logger.error("保存原图片失败(image=" + accessPath + "): ", e);
			return null;
		}
	}
	
	/***
	 * 上传文件
	 * @param file 上传文件
	 * @param fileName 文件名
	 * @return
	 */
	public static String saveFile(MultipartFile file, String fileName) {
		// 生成文件路径
		String accessPath = getFileStoragePath(fileName);
		String fullPath = getFileStorageDirectory() + accessPath;
		try {
			// 创建文件夹
			makeDirectory(fullPath);
			FileUtils.writeByteArrayToFile(new File(fullPath), file.getBytes());
			if(logger.isDebugEnabled()){
				logger.debug("保存文件成功！路径为: " + fullPath);
			}
			return accessPath;
		}
		catch (IOException e1) {
			logger.error("保存文件失败(file=" + fullPath + "): ", e1);
			return null;
		}
	}

	/***
	 * 上传文件
	 * @param file 上传文件
	 * @return
	 */
	public static String saveFile(MultipartFile file){
		String fileName = file.getOriginalFilename();
		String ext = fileName.substring(fileName.lastIndexOf(".")+1);
		String newFileName = S.newUuid() + "." + ext;
		return saveFile(file, newFileName);
	}

	/**
	 * 上传文件
	 * @param file 上传文件
	 * @param fileName 文件名
	 * @param reserve 保留原文件
	 * @return
	 */
	public static String saveFile(File file, String fileName, boolean reserve){
		// 生成文件路径
		String accessPath = getFileStoragePath(fileName);
		String fullPath = getFileStorageDirectory() + accessPath;
		try{
			// 创建文件夹
			makeDirectory(fullPath);
			FileUtils.copyFile(new File(fullPath), file);
			if(logger.isDebugEnabled()){
				logger.debug("保存文件成功！路径为: " + fullPath);
			}
			// 如果原文件与目标文件不相等且不保留原文件，则删除原文件
			if (!reserve && !StringUtils.equals(file.getAbsolutePath(), fullPath)){
				FileUtils.forceDelete(file);
			}
			return accessPath;
		} catch (Exception e){
			logger.error("保存文件失败(file=" + fullPath + "): ", e);
			return null;
		}
	}

	/***
	 * 根据名称取得后缀
	 * @param fileName
	 * @return
	 */
	public static String getFileExtByName(String fileName){
		if(fileName.startsWith("http") && fileName.contains("/")){
			fileName = getFileName(fileName);
		}
		if(fileName.lastIndexOf(".") > 0){
			return fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();
		}
		logger.warn("检测到没有后缀的文件名:" + fileName);
		return "";
	}
		
	/**
	 * 得到图片存储的全路径
	 * @param fileName
	 * @return
	 */
	public static String getImageStoragePath(String fileName){
		StringBuilder sb = new StringBuilder();
		sb.append(PATH_IMG).append("/").append(getYearMonth()).append("/").append(fileName);
		return sb.toString();
	}
	
	/***
	 * 获取文件的存储路径
	 * @param name
	 * @return
	 */
	public static String getFileStoragePath(String name) {
		StringBuilder sb = new StringBuilder();
		sb.append(PATH_FILE).append("/").append(getYearMonth()).append("/").append(name);
		return sb.toString();
	}

	/***
	 * 获取录音文件的存储路径
	 * @param category
	 * @return
	 */
	public static String getAudioStoragePath(String category, String name) {
		StringBuilder sb = new StringBuilder();
		sb.append(PATH_AUDIO);
		if(StringUtils.isNotBlank(category)){
			sb.append("/").append(category);
		}
		sb.append("/").append(getYearMonthDay()).append("/").append(name);
		return sb.toString();
	}
	
	/***
	 * 获取文件的全路径
	 * @return
	 */
	public static String getFullFilePath(String filePath){
		return getFileStorageDirectory()+filePath;
	}
	
	/**
	 * 根据文件URL解析出其文件名
	 * @param fileURL
	 * @return
	 */
	public static String getFileName(String fileURL){
		String temp = StringUtils.substring(fileURL, fileURL.lastIndexOf("/")+1);
		if(StringUtils.contains(fileURL, "?")){
			temp = StringUtils.substring(temp, 0, temp.lastIndexOf("?"));
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
		}
		return fileStorageDirectory;
	}
	
	/***
	 * 创建文件夹
	 * @param dirPath
	 * @return
	 */
	public static boolean makeDirectory(String dirPath){
		String imageDirectory = StringUtils.substringBeforeLast(dirPath, "/");
		File dir = new File(imageDirectory);
		if(!dir.exists()){
			try {
				FileUtils.forceMkdir(dir);
				return true;
			}
			catch (IOException e) {
				return false;
			}
		}
		return false;
	}
	
	/**
	 * 压缩图片
	 * @param imgUrl
	 * @return
	 */
	public static String generateThumbnail(String imgUrl, int width, int height){
		String file = imgUrl.substring(imgUrl.indexOf("/img/"));
		String imageFileDirectory = getFileStorageDirectory() + file;
		try {
			// 压缩图片
			String targetFile = imgUrl.replace(".", "_tn.");
			Thumbnails.of(imageFileDirectory).size(width, height).outputQuality(0.7f).toFile(getFileStorageDirectory() + targetFile);				
			return targetFile;
		}
		catch (IOException e1) {
			logger.error("压缩图片异常(image=" + imageFileDirectory + "): ", e1);
		}
		return imgUrl;
	}

	/**
	 * 根据文件路径与请求信息下载文件
	 * @param path
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public static void downloadLocalFile(String path, HttpServletRequest request, HttpServletResponse response) throws Exception{
		request.setCharacterEncoding("UTF-8");  
        String downloadPath = path;
    	BufferedInputStream bis = null;
    	BufferedOutputStream bos = null;
    	try{
    		String outFileName = getFileName(path);
    		String fileName = new String(outFileName.getBytes("utf-8"), "ISO8859-1");
    		long fileLength = new File(downloadPath).length();
    		response.setContentType(getContextType(fileName));        
    		response.setHeader("Content-disposition", "attachment; filename="+ fileName);  
    		response.setHeader("Content-Length", String.valueOf(fileLength));
    		bis = new BufferedInputStream(new FileInputStream(downloadPath));  
    		bos = new BufferedOutputStream(response.getOutputStream());
    		byte[] buff = new byte[2048];
    		int bytesRead;
    		while(-1 != (bytesRead = bis.read(buff, 0, buff.length))) {  
    			bos.write(buff, 0, bytesRead);
    		}
    	}
    	catch (Exception e) {  
    		logger.error("下载导出文件失败:"+path, e);
    	}
    	finally {
    		if (bis != null) {
				bis.close();
			}
    		if (bos != null) {
				bos.close();
			}
    	}  
	}

	/****
	 * HTTP下载文件
	 * @param fileUrl
	 * @param targetFilePath
	 * @return
	 */
	public static boolean downloadRemoteFile(String fileUrl, String targetFilePath) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(fileUrl);
		try {
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity entity = httpResponse.getEntity();
			long length = entity.getContentLength();
			if(length <= 0){
				logger.warn("下载文件不存在！");
				return false;
			}
			FileUtils.copyInputStreamToFile(entity.getContent(), new File(targetFilePath));
			return true;
		}
		catch (MalformedURLException e) {
			logger.error("下载文件URL异常(url=" + fileUrl + "): ", e);
			return false;
		}
		catch (IOException e) {
			logger.error("下载文件IO异常(url=" + fileUrl + "): ", e);
			return false;
		}
		finally{
			try {
				httpClient.close();
			}
			catch (IOException e) {
				logger.error("关闭httpClient下载连接异常:", e);
			}
		}
	}

	/****
	 * HTTP下载文件
	 * @param fileUrl
	 * @param
	 * @return
	 */
	public static boolean downloadRemoteFileViaHttps(String fileUrl, String targetFilePath) {
		HttpClient httpClient = CustomSSLSocketFactory.newHttpClient();
		try {
			HttpGet httpGet = new HttpGet();
			httpGet.setURI(new URI(fileUrl));
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity entity = httpResponse.getEntity();
			long length = entity.getContentLength();
			if(length <= 0){
				logger.warn("下载文件不存在！");
				return false;
			}
			FileUtils.copyInputStreamToFile(entity.getContent(), new File(targetFilePath));
			return true;
		}
		catch (MalformedURLException e) {
			logger.error("下载文件URL异常(url=" + fileUrl + "): ", e);
			return false;
		}
		catch (IOException e) {
			logger.error("下载文件IO异常(url=" + fileUrl + "): ", e);
			return false;
		}
		catch (Exception e) {
			logger.error("下载文件异常(url=" + fileUrl + "): ", e);
			return false;
		}
	}
	
	/****
	 * 删除文件
	 * @param fileStoragePath
	 */
	public static void deleteFile(String fileStoragePath) {
		File wavFile = new File(fileStoragePath);
		if(wavFile.exists()){
			wavFile.delete();
		}
	}

	/**
	 * 根据文件类型获取ContentType
	 * @param fileName
	 * @return
	 */
	public static String getContextType(String fileName){
		String ext = S.substringAfterLast(fileName, ".");
		String contentType = EXT_CONTENT_TYPE_MAP.get(ext);
		if(contentType == null){
			contentType = DEFAULT_CONTEXT_TYPE;
		}
		return contentType + ";charset=utf-8";
	}

	/**
	 * 得到当前的年月YYMM，用于生成文件夹名称
	 * @return
	 */
	private static String getYearMonth(){
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE_Y2M);
		return sdf.format(cal.getTime());
	}
	
	/**
	 * 得到当前的年月YYMM，用于生成文件夹
	 * @return
	 */
	private static String getYearMonthDay(){
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE_Y2MD);
		return sdf.format(cal.getTime());
	}

	/***
	 * 获取请求中的多个文件数据
	 * @param request
	 * @param fileInputName
	 * @return
	 */
	public static List<MultipartFile> getFilesFromRequest(HttpServletRequest request, String... fileInputName){
		// 解析上传文件
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if(!isMultipart){
			logger.warn("请上传文件！");
			return null;
		}
		// 获取附件文件名
		String inputName = "attachedFiles";
		if(fileInputName != null && fileInputName.length > 0){
			inputName = fileInputName[0];
		}
		// 解析上传文件
		List<MultipartFile> files = ((MultipartHttpServletRequest)request).getFiles(inputName);
		return files;
	}

	/***
	 * 获取请求中的单个文件数据
	 * @param request
	 * @param fileInputName
	 * @return
	 */
	public static MultipartFile getFileFromRequest(HttpServletRequest request, String... fileInputName){
		// 解析上传文件
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if(!isMultipart){
			logger.warn("请上传文件！");
			return null;
		}
		// 获取附件文件名
		String inputName = "attachedFiles";
		if(fileInputName != null && fileInputName.length > 0){
			inputName = fileInputName[0];
		}
		// 解析上传文件
		MultipartFile file = ((MultipartHttpServletRequest)request).getFile(inputName);
		return file;
	}

}