package com.diboot.example.controller;

import com.diboot.commons.entity.BaseFile;
import com.diboot.commons.file.FileHelper;
import com.diboot.commons.service.BaseFileService;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/upload")
public class FileUploadController {
    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @Autowired
    private BaseFileService baseFileService;

    /*
     * 单图片
     * */
    @PostMapping("/image")
    public JsonResult imageUpload(@RequestParam("image") MultipartFile image, ModelMap modelMap, HttpServletRequest request){
        String fileName = image.getOriginalFilename();
        String ext = fileName.substring(fileName.lastIndexOf(".")+1);
        String newFileName = com.diboot.commons.utils.S.newUuid() + "." + ext;
        String filePath = FileHelper.saveImage(image, newFileName);
        BaseFile file = new BaseFile();
        file.setName(image.getOriginalFilename());
        file.setFileType(ext);
        file.setPath(filePath);
        baseFileService.createEntity(file);
        if(V.notEmpty(filePath)){
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
                String fileName = FileHelper.saveImage(image);
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
        String fileName = FileHelper.saveImage(office);
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
                String fileName = FileHelper.saveImage(office);
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

}