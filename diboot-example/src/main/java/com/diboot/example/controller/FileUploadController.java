package com.diboot.example.controller;

import com.diboot.core.config.BaseConfig;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Status;
import com.diboot.example.util.QiniuHelper;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/upload")
public class FileUploadController {
    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    private static String SAVE_PATH = BaseConfig.getProperty("files.storage.directory");

    /*
    * 单图片
    * */
    @PostMapping("/image")
    public JsonResult imageUpload(@RequestParam("image") MultipartFile image, ModelMap modelMap, HttpServletRequest request){

        String fileName = saveFile(image);
        if(V.notEmpty(fileName)){
            modelMap.put("url", fileName);
            modelMap.put("status", "done");
            return new JsonResult(modelMap);
        }

        modelMap.put("status", "error");
        return new JsonResult(Status.FAIL_OPERATION, modelMap);
    }

    /*
     * 多图片
     * */
    @PostMapping("/images")
    public JsonResult imagesUpload(@RequestParam("images") MultipartFile[] images, ModelMap modelMap, HttpServletRequest request){
        if(V.notEmpty(images)){
            List<String> fileNameList = new ArrayList();
            for(MultipartFile image : images){
                String fileName = saveFile(image);
                if(V.notEmpty(fileName)){
                    fileNameList.add(fileName);
                }else{
                    logger.warn("有文件上传失败："+image.getOriginalFilename());
                }
            }

            if(V.notEmpty(fileNameList)){
                modelMap.put("url", fileNameList);
                modelMap.put("status", "done");
                return new JsonResult(modelMap);
            }
        }

        modelMap.put("status", "error");
        return new JsonResult(Status.FAIL_OPERATION, modelMap);
    }

    /*
    * 单文件
    * */
    @PostMapping("/office")
    public JsonResult officeUpload(@RequestParam("office") MultipartFile office, ModelMap modelMap, HttpServletRequest request){
        String fileName = saveFile(office);
        if(V.notEmpty(fileName)){
            modelMap.put("url", fileName);
            modelMap.put("status", "done");
            return new JsonResult(modelMap);
        }
        modelMap.put("status", "error");
        return new JsonResult(Status.FAIL_OPERATION, modelMap);
    }

    /*
     * 多文件
     * */
    @PostMapping("/offices")
    public JsonResult officesUpload(@RequestParam("offices") MultipartFile[] offices, ModelMap modelMap, HttpServletRequest request){
        if(V.notEmpty(offices)){
            List<String> fileNameList = new ArrayList();
            for(MultipartFile office : offices){
                String fileName = saveFile(office);
                if(V.notEmpty(fileName)){
                    fileNameList.add(fileName);
                }else{
                    logger.warn("有文件上传失败："+office.getOriginalFilename());
                }
            }

            if(V.notEmpty(fileNameList)){
                modelMap.put("url", fileNameList);
                modelMap.put("status", "done");
                return new JsonResult(modelMap);
            }
        }
        return new JsonResult(Status.FAIL_OPERATION, modelMap);
    }

    public String saveFile(MultipartFile file){
        if(V.isEmpty(file)){
            return null;
        }
        String fileName = file.getOriginalFilename();
        String ext = fileName.substring(fileName.lastIndexOf(".")+1);
        String newFileName = S.newUuid() + "." + ext;
        String fullPath = SAVE_PATH + newFileName;
        File f = new File(fullPath);
        if(!f.exists()){
            try {
                FileUtils.writeByteArrayToFile(f, file.getBytes());
                if(logger.isDebugEnabled()){
                    logger.info("文件保存成功");
                    return fullPath;
                    //如果上传文件到七牛，打开此注释
                    /*String link = QiniuHelper.uploadFile2Qiniu(newFileName, SAVE_PATH);
                    if(V.notEmpty(link)){
                        logger.info("文件上传七牛成功");
                        return link;
                    }*/
                }
            } catch (IOException e) {
                logger.error("文件保存失败");
                e.printStackTrace();
            }
        }

        return null;
    }

}
