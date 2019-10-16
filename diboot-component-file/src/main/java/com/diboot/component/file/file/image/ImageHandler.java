package com.diboot.component.file.file.image;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

/***
 *  图片操作辅助类
 * @author Mazc
 */
public class ImageHandler {
	private static final Logger logger = LoggerFactory.getLogger(ImageHandler.class);

	private static final String DATA_IMAGE_FLAG = "data:image/";
	private static final String BASE_64_FLAG = "base64,";

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
			int suffixStart = StringUtils.indexOf(base64Str, DATA_IMAGE_FLAG)+ DATA_IMAGE_FLAG.length();
			String suffix = StringUtils.substring(base64Str, suffixStart, StringUtils.indexOf(base64Str, ";", suffixStart));
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
			logger.warn("base", e);
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
				logger.warn("创建文件夹"+sourcePath+"失败。");
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
