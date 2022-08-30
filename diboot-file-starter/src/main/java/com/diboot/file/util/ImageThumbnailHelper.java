/*
 * Copyright (c) 2015-2029, www.dibo.ltd (service@dibo.ltd).
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
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * 图片缩略图相关工具类
 * @author JerryMa
 * @version v2.7.0
 * @copyright dibo.ltd
 */
@Slf4j
public class ImageThumbnailHelper extends ImageHelper {

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
    public static void addWatermark(String filePath, String watermark) throws Exception{
        Thumbnails.of(filePath).watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File(watermark)), 0.8f).toFile(filePath);
    }

}
