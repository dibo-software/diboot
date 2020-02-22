# diboot-file: 文件处理组件
## 组件特性
* EasyExcel最佳实践封装，支持自动校验与字典转换，优化校验错误的提示内容
* 封装常用的文件本地存储、上传下载、图片压缩水印等常用处理
* Starter启动自动安装依赖的数据表

### 1. EasyExcel增强优化
* 支持基于Java validator注解的自动数据校验，支持@BindDict注解自动转换字典显示值-存储值

使用示例：
~~~java
@NotNull(message = "状态不能为空") // 自动校验
@BindDict(type = "USER_STATUS")    // 自动转换数据字典 label-value
@ExcelProperty(value = "状态", index = 4, converter = DictConverter.class)
private String userStatus;
~~~

* 轻量封装增强的Excel Data Listener
继承后只需要实现自定义校验additionalValidate() 和 保存数据的saveData()方法。
~~~
// 适用于已知固定表头的excel读取
FixedHeadExcelListener

// 适用于动态表头的excel读取
DynamicHeadExcelListener
~~~

* 优化汇总校验错误的提示内容
校验失败提示示例：
~~~json
{
    "msg": "数据校验不通过: 1行3列: '测试' 无匹配字典定义; 2行: 姓名不能为空，职位长度不能超过10",
    "code": 500
}
~~~

### 2. 常用文件处理封装

* 提供BaseFileController用于文件上传下载的Controller继承
使用示例：
~~~java
    //上传文件
    @PostMapping("/upload")
    public JsonResult upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception{
        // 保存文件并创建UploadFile上传记录
        return super.uploadFile(file, Dictionary.class, request);
    }

    // 下载文件
    @GetMapping("/download/{fileUuid}")
    public JsonResult download(@PathVariable("fileUuid")String fileUuid, HttpServletResponse response) throws Exception {
        UploadFile uploadFile = uploadFileService.getEntity(fileUuid);
        if(uploadFile == null){
            return new JsonResult(Status.FAIL_VALIDATION, "文件不存在");
        }
        // 下载
        HttpHelper.downloadLocalFile(uploadFile.getStoragePath(), "导出文件.txt", response);
        return null;
    }
~~~

* 提供BaseExcelFileController用于Excel导入导出类的Controller继承
使用示例：
~~~java
    //预览excel数据
    @PostMapping("/preview")
    public JsonResult preview(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
        return super.excelPreview(file, request);
    }

    //预览无校验错误后 提交
    @PostMapping("/previewSave")
    public <T> JsonResult previewSave(@RequestParam("previewFileName")String previewFileName, @RequestParam("originFileName")String originFileName, HttpServletRequest request) throws Exception {
        return super.excelPreviewSave(Department.class, previewFileName, originFileName, request);
    }

    //无预览 直接导入
    @PostMapping("/import")
    public <T> JsonResult upload(@RequestParam("file")MultipartFile file, HttpServletRequest request) throws Exception {
        return super.uploadExcelFile(file, Department.class, request);
    }

~~~

* 其他常用文件处理相关工具类
~~~
// 保存上传文件至本地
FileHelper.saveFile(MultipartFile file, String fileName)

// 下载本地文件
HttpHelper.downloadLocalFile(String localFilePath, String exportFileName, HttpServletResponse response)

// 下载网络文件至本地
HttpHelper.downloadHttpFile(String fileUrl, String targetFilePath)

// 图片保存，压缩，加水印等 （需依赖Thumbnails组件）
ImageHelper.saveImage(MultipartFile file, String imgName)
ImageHelper.generateThumbnail(String sourcePath, String targetPath, int width, int height)
ImageHelper.addWatermark(String filePath, String watermark)

// zip压缩
ZipHelper.zipFile(String srcRootDir, File file, ZipOutputStream zos, String... matchKeyword)
~~~

### 3. Starter启动自动安装依赖的数据表
file组件依赖一张表 upload_file ，用于存储文件/附件上传记录。该表将由diboot-file-starter初次加载时自动初始化。

diboot-file组件有以下一个配置项，用于设置本地文件的存储起始目录（子目录会按分类与日期自动创建）
~~~
files.storage.directory=/myfile
~~~

## 样例参考 - [diboot-file-example](https://github.com/dibo-software/diboot-v2-example/tree/master/diboot-file-example)
