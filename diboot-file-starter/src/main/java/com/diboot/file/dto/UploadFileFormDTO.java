package com.diboot.file.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 上传文件提交的form
 *
 * @author : uu
 * @version : v1.0
 * @Date 2020-05-18  17:32
 */
@Setter
@Getter
@ToString
public class UploadFileFormDTO implements Serializable {

    private static final long serialVersionUID = -3467553770266812902L;

    /**
     * 上传的文件
     */
    @NotNull(message = "上传文件不能为空")
    private MultipartFile file;

    /**
     * 关联对象类
     */
    @NotNull(message = "关联对象类不能为空！")
    private String relObjType;

    /**
     * 关联对象属性
     */
    @NotNull(message = "关联对象属性不能为空！")
    private String relObjField;

    /**
     * 描述
     */
    private String description;
}
