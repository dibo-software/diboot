package com.diboot.component.file.util;

import com.diboot.component.file.config.Cons;
import com.diboot.core.util.D;
import com.diboot.core.util.S;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

/***
 *  图片操作辅助类
 * @author Mazc
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
				log.debug("保存图片成功！路径为: " + accessPath);
			}
			return accessPath;
		}
		catch (IOException e1) {
			log.error("保存原图片失败(image=" + accessPath + "): ", e1);
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
		String fullPath = FileHelper.getFileStorageDirectory() + accessPath;
		try{
			// 创建文件夹
			FileHelper.makeDirectory(fullPath);
			FileUtils.copyFile(new File(fullPath), file);
			if(log.isDebugEnabled()){
				log.debug("保存图片成功！路径为: " + accessPath);
			}
			// 如果原文件与目标文件不相等且不保留原文件，则删除原文件
			if (!reserve && !StringUtils.equals(file.getAbsolutePath(), fullPath)){
				FileUtils.forceDelete(file);
			}
			return accessPath;
		} catch (Exception e){
			log.error("保存原图片失败(image=" + accessPath + "): ", e);
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
	 * 压缩图片
	 * @param imgUrl
	 * @return
	 */
	public static String generateThumbnail(String imgUrl, int width, int height){
		String file = imgUrl.substring(imgUrl.indexOf("/img/"));
		String imageFileDirectory = FileHelper.getFileStorageDirectory() + file;
		try {
			// 压缩图片
			String targetFile = imgUrl.replace(".", "_tn.");
			Thumbnails.of(imageFileDirectory).size(width, height).outputQuality(0.7f).toFile(FileHelper.getFileStorageDirectory() + targetFile);
			return targetFile;
		}
		catch (IOException e1) {
			log.error("压缩图片异常(image=" + imageFileDirectory + "): ", e1);
		}
		return imgUrl;
	}

	/**
	 * 将Base64转换为图片
	 * @param base64Str
	 * @param fullFilePath
	 * @return
	 * @throws IOException
	 */
	private static boolean convertBase64ToImage(String base64Str, String fullFilePath){
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
			data = null;
			return true;
		}
		catch(Exception e){
			log.warn("base", e);
			return false;
		}
	}
	
	/**
	 * 生成缩略图
	 * @return
	 * @throws Exception
	 */
	public static String generateThumbnail(String sourcePath, String targetPath, int width, int height) throws Exception{
		// 创建文件
		File file = new File(sourcePath);
		if(!file.exists()){
			boolean result = file.mkdir();
			if(!result){
				log.warn("创建文件夹 {} 失败", sourcePath);
			}
		}
		// 生成缩略图
		Thumbnails.of(sourcePath).size(width, height).toFile(targetPath);
		return targetPath;
	}
	
	/**
	 * 给图片添加水印
	 * @param filePath
	 */
	private static void addWatermark(String filePath, String watermark) throws Exception{
		Thumbnails.of(filePath).watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File(watermark)), 0.8f).toFile(filePath);
    }

}
