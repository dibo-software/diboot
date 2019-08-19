

# diboot-core

## 安装

### diboot-core是什么？

>  diboot-core是diboot2.0版本的核心基础框架，基于Spring，Mybatis-plus等知名开源项目。

* 使用diboot-core可以更加简单且方便地创建您的web后端应用，您之前的诸多工作也将得到极大的放松，我们为您准备了常用的各种工具类函数；
* 我们为你配置好了mybatis-plus，方便你进行单表CRUD无SQL的探索；
* 我们也为您提供了多表关联查询的无SQL实现方案，这里的多表不只是两张表之间的关联，而且还可以处理三张表之间的关联，只需要一个注解，你就可以快速处理完以往要好些代码才能处理好的事情；
* 我们为您提供了数据字典的管理与接口；
* 我们在diboot-core中还提供了其他常用开发场景的简单封装。

### 依赖引入

可以从[maven中央仓库]([https://mvnrepository.com](https://mvnrepository.com/))搜索[diboot-core](http://)，刷新项目即可。

### 初始化数据库

diboot-core在初次运行中，会自动安装所需数据库表，如果已经存在，则不做操作。

## 实体（Entity）

> diboot-core中的实体与1.x版本的model一样，我们为了命名更加规范，这里改用entity，下文简称实体。

所有实体统一存放在entity包名下，命名一般将表名转换为驼峰命名法命名再首字母大写即可，比如**sys_user**的实体名为**SysUser。**

### BaseEntity

> BaseEntity是diboot-core提供的基础实体类，提供了我们默认数据表结构的默认字段，比如id、deleted、create_time等，默认的方法如toMap、toString方法等。

### BaseExtEntity

> BaseExtEntity是基于BaseEntity的扩展实体类，对数据表结构的扩展字段extdata添加了相关处理方法，extdata将以json字符串形式存储在数据库中。

* getFromExt方法

```java
/***
* 从extdata JSON中提取扩展属性值
* @param extAttrName
* @return
*/
public Object getFromExt(String extAttrName){
  if(this.extdataMap == null){
  	return null;
  }
  return this.extdataMap.get(extAttrName);
}

```

该方法传入一个属性名称，返回该属性值

* addIntoExt

```java
/***
* 添加扩展属性和值到extdata JSON中
* @param extAttrName
* @param extAttrValue
*/
public void addIntoExt(String extAttrName, Object extAttrValue){
  if(extAttrName == null && extAttrValue == null){
  	return;
  }
  if(this.extdataMap == null){
  	this.extdataMap = new LinkedHashMap<>();
  }
  this.extdataMap.put(extAttrName, extAttrValue);
}
```

该方法传入一个属性名与属性值，即可将该值设置到extdata的json字符串中。

### 数据校验

> 我们在数据提交过程中可能会需要后端进行数据格式校验，我们默认是用validation来做后端数据校验，我们可以在实体中需要校验的字段上添加校验相关的注解，如下：

```java
@NotNull(message = "上级ID不能为空，如无请设为0")
private Long parentId;
```

### @TableField注解

> 如果mapper也继承我们core的BaseMapper来处理，那么实体中的所有字段都被认为在相对应的数据表中是存在的，如果某个字段在对应数据表中不存在，则会执行SQL时报错。

所以，如果某个字段在相对应的数据表中不存在，而我们又需要使用到该字段，那么可以添加@Tablefield注解，并且设置@TableField(exist = false)中的exist参数为false即可，如下：

```java
@TableField(exist = false)
private List<Long> userIdList;
```

## Service与实现

## Mapper及自定义

## 接口的艺术（Controller）

### BaseController

> BaseController是BaseCrudRestController的父类，提供请求参数处理等方法的封装。

### BaseCrudRestController

> 增删改查通用controller,以后的controller都可以继承该类，减少代码量。
接下来会对BaseCrudRestController中的一些通用方法进行介绍。

* getService 抽象方法
```java
/**
 * 获取service实例
 *
 * @return
 */
protected abstract BaseService getService();
```
该方法是用来获取当前类中相关业务Service实例。
所有继承了BaseCrudRestController的实体类都要实现该方法，如下：
* getService 方法实现
```java
@Autowired
private DictionaryService dictionaryService;

@Override
protected BaseService getService() {
    return dictionaryService;
}
```

* getEntityList 方法
```java
/***
 * 获取某资源的集合
 * @return JsonResult
 * @throws Exception
 */
protected JsonResult getEntityList(Wrapper queryWrapper) throws Exception {
    // 查询当前页的数据
    List entityList = getService().getEntityList(queryWrapper);
    // 返回结果
    return new JsonResult(Status.OK, entityList);
}
```
该方法用于获取数据集合,入参为查询条件（queryWrapper），
调用该方法成功后会返回所有符合查询条件的数据集合，该方法无分页功能。

* getEntityListWithPaging 方法
```java
/***
 * 获取某资源的集合
 * @return JsonResult
 * @throws Exception
 */
protected JsonResult getEntityListWithPaging(Wrapper queryWrapper, Pagination pagination) throws Exception {
    // 查询当前页的数据
    List entityList = getService().getEntityList(queryWrapper, pagination);
    // 返回结果
    return new JsonResult(Status.OK, entityList).bindPagination(pagination);
}
```
该方法用于获取数据集合,入参为查询条件（queryWrapper）、分页条件（pagination），
调用该方法成功后会返回符合查询条件的当前页数据集合，该方法有分页功能。

* getVOListWithPaging 方法
```java
 /***
 * 获取某VO资源的集合
 * @return JsonResult
 * @throws Exception
 */
protected <T> JsonResult getVOListWithPaging(Wrapper queryWrapper, Pagination pagination, Class<T> clazz) throws Exception {
    // 查询当前页的数据
    List<T> voList = getService().getViewObjectList(queryWrapper, pagination, clazz);
    // 返回结果
    return new JsonResult(Status.OK, voList).bindPagination(pagination);
}
```
该方法用来获取数据VO集合，入参为查询条件（queryWrapper）、分页条件（pagination）、类类型（clazz），
调用该方法成功后会返回符合查询条件的当前页数据VO集合，该方法有分页功能。

* createEntity 方法
```java
/***
 * 创建资源对象
 * @param entity
 * @param result
 * @return JsonResult
 * @throws Exception
 */
protected JsonResult createEntity(BaseEntity entity, BindingResult result) throws Exception {
    // Model属性值验证结果
    if (result != null && result.hasErrors()) {
        return new JsonResult(Status.FAIL_INVALID_PARAM, super.getBindingError(result));
    }
    // 执行创建资源前的操作
    String validateResult = this.beforeCreate(entity);
    if (validateResult != null) {
        return new JsonResult(Status.FAIL_VALIDATION, validateResult);
    }
    // 执行保存操作
    boolean success = getService().createEntity(entity);
    if (success) {
        // 执行创建成功后的操作
        this.afterCreated(entity);
        // 组装返回结果
        Map<String, Object> data = new HashMap<>(2);
        data.put(PARAM_ID, entity.getId());
        return new JsonResult(Status.OK, data);
    } else {
        log.warn("创建操作未成功，model=" + entity.getClass().getSimpleName());
        // 组装返回结果
        return new JsonResult(Status.FAIL_OPERATION);
    }
}
```
该方法用来新建数据，入参为数据实体（entity）、绑定结果（result），调用该方法成功后会在相关表中插入一条数据。

* updateEntity 方法
```java
/***
 * 根据ID更新资源对象
 * @param entity
 * @param result
 * @return JsonResult
 * @throws Exception
 */
protected JsonResult updateEntity(BaseEntity entity, BindingResult result) throws Exception {
    // Model属性值验证结果
    if (result.hasErrors()) {
        return new JsonResult(Status.FAIL_INVALID_PARAM, super.getBindingError(result));
    }
    // 执行更新资源前的操作
    String validateResult = this.beforeUpdate(entity);
    if (validateResult != null) {
        return new JsonResult(Status.FAIL_VALIDATION, validateResult);
    }
    // 执行保存操作
    boolean success = getService().updateEntity(entity);
    if (success) {
        // 执行更新成功后的操作
        this.afterUpdated(entity);
        // 组装返回结果
        Map<String, Object> data = new HashMap<>(2);
        data.put(PARAM_ID, entity.getId());
        return new JsonResult(Status.OK, data);
    } else {
        log.warn("更新操作失败，model=" + entity.getClass().getSimpleName() + ", id=" + entity.getId());
        // 返回操作结果
        return new JsonResult(Status.FAIL_OPERATION);
    }
}
```
该方法用来更新数据，入参为数据实体（entity）、绑定结果（result），调用该方法成功后会更新相关表中的数据。

* deleteEntity 方法
```java
 /***
 * 根据id删除资源对象
 * @param id
 * @return
 * @throws Exception
 */
protected JsonResult deleteEntity(Serializable id) throws Exception {
    if (id == null) {
        return new JsonResult(Status.FAIL_INVALID_PARAM, "请选择需要删除的条目！");
    }
    BaseEntity model = (BaseEntity) getService().getEntity(id);
    // 执行删除操作之前的操作
    String validateResult = beforeDelete(model);
    if (validateResult != null) {
        // 返回json
        return new JsonResult(Status.FAIL_OPERATION, validateResult);
    }
    // 执行删除操作
    boolean success = getService().deleteEntity(id);
    if (success) {
        log.info("删除操作成功，model=" + model.getClass().getSimpleName() + ", id=" + id);
        // 组装返回结果
        Map<String, Object> data = new HashMap<>(2);
        data.put(PARAM_ID, model.getId());
        return new JsonResult(Status.OK, data);
    } else {
        log.warn("删除操作未成功，model=" + model.getClass().getSimpleName() + ", id=" + id);
        return new JsonResult(Status.FAIL_OPERATION);
    }
}
```
该方法用来删除数据，入参为数据ID（id），调用该方法成功后会删除相关表中的数据。

* convertToVoAndBindRelations 方法
```java
/**
 * 自动转换为VO并绑定关联关系
 *
 * @param entityList
 * @param voClass
 * @param <VO>
 * @return
 */
protected <VO> List<VO> convertToVoAndBindRelations(List entityList, Class<VO> voClass) {
    // 转换为VO
    List<VO> voList = RelationsBinder.convertAndBind(entityList, voClass);
    return voList;
}
```
该方法用来将数据实体集合转化为数据实体VO集合，入参为实体集合（entityList）、类类型（voClass），
调用该方法成功后返回数据实体VO集合。

* beforeCreate 方法
```java
 /***
 * 创建前的相关处理
 * @param entity
 * @return
 */
protected String beforeCreate(BaseEntity entity);
```
该方法用来处理新建数据之前的逻辑，如数据校验等，需要子类继承BaseCrudRestController时重写并实现具体处理逻辑。

* afterCreated 方法
```java
 /***
  * 创建成功后的相关处理
  * @param entity
  * @return
  */
 protected String afterCreated(BaseEntity entity);
```
该方法用来处理新建数据之后的逻辑，需要子类继承BaseCrudRestController时重写并实现具体处理逻辑。

* beforeUpdate 方法
```java
 /***
 * 更新前的相关处理
 * @param entity
 * @return
 */
protected String beforeUpdate(BaseEntity entity);
```
该方法用来处理更新数据之前的逻辑，需要子类继承BaseCrudRestController时重写并实现具体处理逻辑。

* afterUpdated 方法
```java
 /***
  * 更新成功后的相关处理
  * @param entity
  * @return
  */
 protected String afterUpdated(BaseEntity entity);
```
该方法用来处理更新数据之后的逻辑，需要子类继承BaseCrudRestController时重写并实现具体处理逻辑。

* beforeDelete 方法
```java
/***
 * 是否有删除权限，如不可删除返回错误提示信息，如 Status.FAIL_NO_PERMISSION.label()
 * @param entity
 * @return
 */
protected String beforeDelete(BaseEntity entity);
```
该方法主要用来处理删除数据之前的逻辑，如检验是否具有删除权限等，需要子类继承BaseCrudRestController时重写并实现具体处理逻辑。

## 查询条件（DTO）

## 单表关联

> 当两张表数据之间有直接的依赖关系时，如主外键关联关系，即可使用单表关联。在通过diboot-core的封装后，
单表关联时开发者不再需要写大量java代码和SQL查询，只要使用相关注解即可快速绑定它们之间的关联关系。

### @BindField 注解

该注解在单表关联时使用，用来将数据表中的某个字段映射到实体的对应字段，使用方法如下：
```java
@BindField(entity = Organization.class, field = "name", condition = "this.org_id=id")
private String orgName;
```
使用@BindField注解时需传三个参数，分别是entity、field、condition，
entity表示关联实体，field表示关联表字段名，condition表示关联条件。

### @BindEntity 注解

该注解在单表关联时使用，用来将数据表中的某条数据映射到对应实体，使用方法如下：
```java
@BindEntity(entity = Department.class, condition = "this.id = if_employee_position_department.employee_id AND if_employee_position_department.department_id = id AND if_employee_position_department.deleted = 0")
private Department department;
```
使用@BindEntity注解时需传两个参数，分别是entity和condition，
entity表示关联实体，condition表示关联条件。

## 多表关联

> 当两张表数据之间是通过第三张表来产生依赖关系时，即可使用多表关联。在通过diboot-core的封装后，
多表关联时开发者不再需要写大量java代码和SQL查询，只要使用相关注解即可快速绑定它们之间的关联关系。

### @BindEntityList 注解

该注解在多表关联时使用，用来将数据表中的单条或多条数据映射到对应的实体集合，使用方法如下：
```java
@BindEntityList(entity = Department.class, condition = "this.id = if_position_department.position_id AND if_position_department.department_id = id AND if_position_department.deleted = 0")
private List<Department> departmentList;
```
使用@BindEntityList注解时需传两个参数，分别是entity和condition，
entity表示关联实体，condition表示关联条件。

## 数据字典关联

> 当表中的字段为数据字典类型的值时，可使用数据字典关联来绑定表字段与数据字典的关联关系。在通过diboot-core的封装后，
数据字典关联时开发者不再需要写大量java代码和SQL查询，只要使用相关注解即可快速绑定它们之间的关联关系。

### @BindDict 注解

该注解在数据字典关联时使用，用来将数据字典表中的某条数据映射到实体的对应字段，使用方法如下：
```java
@BindDict(type = "POSITION_LEVEL", field = "level")
private String levelLabel;
```
使用@BindDict注解时需传两个参数，分别是type和field，
type表示关联的数据字典类型，field表示关联字段。

## 异常处理

## 常用工具类

### BeanUtils（Bean）

* copyProperties 方法
```java
public static Object copyProperties(Object source, Object target);
```
该方法用来复制一个对象的属性到另一个对象，入参为被复制对象（source）、作为返回值的目标对象（target），
使用该方法时直接通过类名调用即可，如：BeanUtils.copyProperties(param1, param2)。

* convert 方法
```java
public static <T> T convert(Object source, Class<T> clazz);
```
该方法用来将一个对象转换为另外的对象实例，入参为被转化对象（source）、目标对象的类类型（clazz），
使用该方法时直接通过类名调用即可，如：BeanUtils.convert(param1, param2)。

* convertList 方法
```java
 public static <T> List<T> convertList(List sourceList, Class<T> clazz);
```
该方法用来将对象集合转换为另外的对象集合实例，入参为被转化对象集合（sourceList）、目标对象的类类型（clazz），
使用该方法时直接通过类名调用即可，如：BeanUtils.convertList(param1, param2)。

* bindProperties 方法
```java
public static void bindProperties(Object model, Map<String, Object> propMap);
```
该方法用来绑定Map中的属性值到Model，入参为被绑定对象（model）、属性值Map（propMap），
使用该方法时直接通过类名调用即可，如：BeanUtils.bindProperties(param1, param2)。

* getProperty 方法
```java
public static Object getProperty(Object obj, String field);
```
该方法用来获取对象的属性值，入参为目标对象（obj）、对象字段名（field），
使用该方法时直接通过类名调用即可，如：BeanUtils.getProperty(param1, param2)。

* getStringProperty 方法
```java
public static String getStringProperty(Object obj, String field);
```
该方法用来获取对象的属性值并转换为字符串类型，入参为目标对象（obj）、字段名（field），
使用该方法时直接通过类名调用即可，如：BeanUtils.getStringProperty(param1, param2)。

* setProperty 方法
```java
public static void setProperty(Object obj, String field, Object value);
```
该方法用来设置对象属性值，入参为目标对象（obj）、字段名（field）、字段值（value），
使用该方法时直接通过类名调用即可，如：BeanUtils.setProperty(param1, param2，param3)。

* convertToStringKeyObjectMap 方法
```java
public static <T> Map<String, T> convertToStringKeyObjectMap(List<T> allLists, String... fields);
```
该方法用来将对象集合转化成键值对为String-Object的Map形式，入参为目标对象集合（allLists）、字段名（fields），
使用该方法时直接通过类名调用即可，如：BeanUtils.convertToStringKeyObjectMap(param1, param2)。

* convertToStringKeyObjectListMap 方法
```java
public static <T> Map<String, List<T>> convertToStringKeyObjectListMap(List<T> allLists, String... fields);
```
该方法用来将对象集合转化成键值对为String-List的Map形式，入参为目标对象集合（allLists）、字段名（fields），
使用该方法时直接通过类名调用即可，如：BeanUtils.convertToStringKeyObjectListMap(param1, param2)。

* buildTree 方法
```java
/***
* 该方法用来构建上下级关联的实体关系树形结构，顶层父级实体的parentId必须是为null或0，入参为对象集合（allModels）
* 使用该方法时直接通过类名调用即可，如：BeanUtils.buildTree(param1)
*/
public static <T extends BaseEntity> List<T> buildTree(List<T> allModels);

/***
* 该方法用来构建上下级关联的实体关系树形结构，去除顶层父级实体的parentId必须是为null或0的限制，入参为对象集合（allModels）
* 使用该方法时直接通过类名调用即可，如：BeanUtils.buildTree(param1, param2)
*/
public static <T extends BaseEntity> List<T> buildTree(List<T> parentModels, List<T> allModels);
```

* extractDiff 方法
```java
/***
* 该方法用来提取两个model的差异值，入参为两个实体对象oldModel、newModel
* 使用该方法时直接通过类名调用即可，如：BeanUtils.extractDiff(param1, param2)
*/
public static String extractDiff(BaseEntity oldModel, BaseEntity newModel);

/***
* 该方法用来提取两个model的差异值，只对比指定字段，入参为两个实体对象oldModel、newModel,以及指定字段fields
* 使用该方法时直接通过类名调用即可，如：BeanUtils.extractDiff(param1, param2, param3)
*/
public static String extractDiff(BaseEntity oldModel, BaseEntity newModel, Set<String> fields);
```

* collectToList 方法
```java
/***
* 该方法用来从集合列表中提取指定属性值到新的集合，入参为对象集合（objectList）、IGetter对象（getterFn）
* 使用该方法时直接通过类名调用即可，如：BeanUtils.collectToList(param1, param2)
*/
public static <E,T> List collectToList(List<E> objectList, IGetter<T> getterFn);

/***
* 该方法用来从集合列表中提取指定属性值到新的集合，入参为对象集合（objectList）、字段名（getterPropName）
* 使用该方法时直接通过类名调用即可，如：BeanUtils.collectToList(param1, param2)
*/
public static <E> List collectToList(List<E> objectList, String getterPropName);
```

* collectIdToList 方法
```java
public static <E> List collectIdToList(List<E> objectList);
```
该方法用来从集合列表中提取Id主键值到新的集合，入参为对象集合（objectList），
使用该方法时直接通过类名调用即可，如：BeanUtils.collectIdToList(param1)。

* bindPropValueOfList 方法
```java
/***
* 该方法用来绑定Map中的属性值到集合，入参为ISetter对象、目标集合（fromList）、IGetter对象、Map对象（valueMatchMap）
* 使用该方法时直接通过类名调用即可，如：BeanUtils.bindPropValueOfList(param1, param2, param3, param4)
*/
public static <T1,T2,R,E> void bindPropValueOfList(ISetter<T1,R> setFieldFn, List<E> fromList, IGetter<T2> getFieldFun, Map valueMatchMap);

/***
* 该方法用来绑定Map中的属性值到集合，入参为字段名（setterFieldName）、目标集合（fromList）、字段名（getterFieldName）、Map对象（valueMatchMap）
* 使用该方法时直接通过类名调用即可，如：BeanUtils.bindPropValueOfList(param1, param2, param3, param4)
*/
public static <E> void bindPropValueOfList(String setterFieldName, List<E> fromList, String getterFieldName, Map valueMatchMap);
```

* convertToFieldName 方法
```java
/***
* 该方法用来转换方法引用为属性名，入参为IGetter对象。
* 使用该方法时直接通过类名调用即可，如：BeanUtils.convertToFieldName(param1)
*/
public static <T> String convertToFieldName(IGetter<T> fn);

/***
* 该方法用来转换方法引用为属性名，入参为ISetter对象。
* 使用该方法时直接通过类名调用即可，如：BeanUtils.convertToFieldName(param1)
*/
public static <T,R> String convertToFieldName(ISetter<T,R> fn);
```

* extractAllFields 方法
```java
public static List<Field> extractAllFields(Class clazz);
```
该方法用来获取类的所有属性（包含父类），入参为类类型（clazz），
使用该方法时直接通过类名调用即可，如：BeanUtils.extractAllFields(param1)。

### ContextHelper（Spring上下文）

* setApplicationContext 方法
```java
public void setApplicationContext(ApplicationContext applicationContext);
```
该方法用来设置ApplicationContext上下文，入参为上下文对象（applicationContext），
使用该方法时直接通过类名调用即可，如：ContextHelper.setApplicationContext(param1)。

* getApplicationContext 方法
```java
public static ApplicationContext getApplicationContext();
```
该方法用来获取ApplicationContext上下文，
使用该方法时直接通过类名调用即可，如：ContextHelper.getApplicationContext()。

* getBean 方法
```java
/***
* 该方法用来根据beanId获取Bean实例，入参为beanId
* 使用该方法时直接通过类名调用即可，如：ContextHelper.getBean(param1)
*/
public static Object getBean(String beanId);

/***
* 该方法用来获取指定类型的单个Bean实例，入参为类类型（type）
* 使用该方法时直接通过类名调用即可，如：ContextHelper.getBean(param1)
*/
public static Object getBean(Class type);
```

* getBeans 方法
```java
public static <T> List<T> getBeans(Class<T> type);
```
该方法用来获取指定类型的全部实例，入参为类类型（type），
使用该方法时直接通过类名调用即可，如：ContextHelper.getBeans(param1)。

* getBeansByAnnotation 方法
```java
public static List<Object> getBeansByAnnotation(Class<? extends Annotation> annotationType);
```
该方法用来根据注解获取beans，入参为类类型（annotationType），
使用该方法时直接通过类名调用即可，如：ContextHelper.getBeansByAnnotation(param1)。

* getServiceByEntity 方法
```java
public static IService getServiceByEntity(Class entity);
```
该方法用来根据Entity获取对应的Service，入参为类类型（entity），
使用该方法时直接通过类名调用即可，如：ContextHelper.getServiceByEntity(param1)。

### D（日期时间）

* 日期、时间、星期格式常量
```java
public static final String FORMAT_DATE_y2M = "yyMM";                        //例：1908  				  表示 19年8月
public static final String FORMAT_DATE_y2Md = "yyMMdd";					   //例：190816  			  表示 19年8月16日
public static final String FORMAT_DATE_y4 = "yyyy";   					   //例：2019  				  表示 2019年
public static final String FORMAT_DATE_y4Md = "yyyyMMdd";				   //例：20190816  			  表示 2019年8月16日
public static final String FORMAT_DATE_Y4MD = "yyyy-MM-dd";				   //例：2019-08-16  		  表示 2019年8月16日
public static final String FORMAT_TIMESTAMP = "yyMMddhhmmss";			   //例：190816121212  		  表示 19年8月16日12时12分12秒
public static final String FORMAT_TIME_HHmm = "HH:mm";					   //例：12:30  			      表示 12点30分
public static final String FORMAT_TIME_HHmmss = "HH:mm:ss";				   //例：12:30:30  			  表示 12点30分30秒
public static final String FORMAT_DATETIME_Y4MDHM = "yyyy-MM-dd HH:mm";	   //例：2019-08-16 12:30  	  表示 2019年8月16日 12点30分
public static final String FORMAT_DATETIME_Y4MDHMS = "yyyy-MM-dd HH:mm:ss";//例：2019-08-16 12:30:30  表示 2019年8月16日 12点30分30秒
protected static final String[] WEEK = new String[]{"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
```
格式常量表示某种日期时间格式的一个字符串类型的常量，D类中已经内置了如下列表所示的格式常量，
可以通过类似D.FORMAT_DATE_y2M这样的调用来获取这些常量的内容。

* now 方法
```java
public static String now(String format);
```
该方法用来获取当前的日期时间的字符串形式，入参为日期匹配格式（format），
使用该方法时直接通过类名调用即可，如：D.now(param1)。

* toTimestamp 方法
```java
public static String toTimestamp(Date date);
```
该方法用来获取当前日期时间的时间戳形式，入参为日期（date），
使用该方法时直接通过类名调用即可，如：D.toTimestamp(param1)。

* getMonth 方法
```java
public static String getMonth();
```
该方法用来获取当前月份，使用该方法时直接通过类名调用即可，如：D.getMonth()。

* today 方法
```java
public static String today();
```
该方法用来获取今天的日期，使用该方法时直接通过类名调用即可，如：D.today()。

* convert2FormatDate 方法
```java
public static Date convert2FormatDate(String datetime, String fmt);
```
该方法用来转换日期字符串为日期类型，入参为日期字符串（datetime）、日期匹配格式（fmt），
使用该方法时直接通过类名调用即可，如：D.convert2FormatDate(param1,param2)。

* convert2FormatString 方法
```java
public static String convert2FormatString(Date date, String fmt);
```
该方法用来转换日期为格式化字符串，入参为日期（date）、日期匹配格式（fmt），
使用该方法时直接通过类名调用即可，如：D.convert2FormatString(param1,param2)。

* getDate 方法
```java
public static String getDate(Date date, int... daysOffset);
```
该方法用来获取格式化的日期，入参为日期（date）、日期偏移量（daysOffset），
使用该方法时直接通过类名调用即可，如：D.getDate(param1,param2)。

* getDateTime 方法
```java
public static String getDateTime(Date date, int... daysOffset);
```
该方法用来获取格式化的日期时间，入参为日期（date）、日期偏移量（daysOffset），
使用该方法时直接通过类名调用即可，如：D.getDateTime(param1,param2)。

* isWorkingTime 方法
```java
public static boolean isWorkingTime();
```
该方法用来判断是否是工作时间段，用于后台程序等，
使用该方法时直接通过类名调用即可，如：D.isWorkingTime()。

* getAmPm 方法
```java
public static String getAmPm();
```
该方法用来获取现在是上午/下午，
使用该方法时直接通过类名调用即可，如：D.getAmPm()。

* getYearMonth 方法
```java
public static String getYearMonth();
```
该方法用来得到当前的年月YYMM，用于生成文件夹名称，
使用该方法时直接通过类名调用即可，如：D.getYearMonth()。

* getYearMonthDay 方法
```java
public static String getYearMonthDay();
```
该方法用来得到当前的年月日YYMMDD，用于生成文件夹名称，
使用该方法时直接通过类名调用即可，如：D.getYearMonthDay()。

* getDay 方法
```java
public static int getDay();
```
该方法用来得到当前日期为多少号，
使用该方法时直接通过类名调用即可，如：D.getDay()。

* getWeek 方法
```java
public static String getWeek(Date date);
```
该方法用来得到当前日期为星期几，入参为日期（date），
使用该方法时直接通过类名调用即可，如：D.getWeek(param1)。

* timeMillis2Date 方法
```java
public static Date timeMillis2Date(Long timeMillis);
```
该方法用来毫秒数转日期，入参为时间毫秒数（timeMillis），
使用该方法时直接通过类名调用即可，如：D.timeMillis2Date(param1)。

* datetimeString2Date 方法
```java
public static Date datetimeString2Date(String value);
```
该方法用来字符串时间戳转日期，入参为时间字符串（value），
使用该方法时直接通过类名调用即可，如：D.datetimeString2Date(param1)。

* convert2Date 方法
```java
public static Date convert2Date(String date);
```
该方法用来字符串时间戳转日期，入参为日期字符串（date），
使用该方法时直接通过类名调用即可，如：D.convert2Date(param1)。

* convert2DateTime 方法
```java
public static Date convert2DateTime(String dateTime, String... dateFormat);
```
该方法用来字符串时间戳转日期，入参为时间字符串（dateTime）、时间匹配格式（dateFormat），
使用该方法时直接通过类名调用即可，如：D.convert2DateTime(param1,param2)。

* fuzzyConvert 方法
```java
public static Date fuzzyConvert(String dateString);
```
该方法用来模糊转换日期，入参为时间字符串（dateString），
使用该方法时直接通过类名调用即可，如：D.fuzzyConvert(param1)。

### Encryptor（加解密）

* encrypt 方法
```java
public static String encrypt(String input, String... key);
```
该方法用来加密字符串（可指定加密密钥），入参为需加密字符串（input）、加密秘钥（key），
使用该方法时直接通过类名调用即可，如：Encryptor.encrypt(param1,param2)。

* decrypt 方法
```java
public static String decrypt(String input, String... key);
```
该方法用来解密字符串，入参为需解密字符串（input）、解密秘钥（key），
使用该方法时直接通过类名调用即可，如：Encryptor.decrypt(param1,param2)。

### JSON

* stringify 方法
```java
public static String stringify(Object object);
```
该方法用来将Java对象转换为Json字符串，入参为对象（object），
使用该方法时直接通过类名调用即可，如：JSON.stringify(param1)。

* toMap 方法
```java
public static Map toMap(String jsonStr);
```
该方法用来将JSON字符串转换为Map对象，入参为JSON字符串（jsonStr），
使用该方法时直接通过类名调用即可，如：JSON.toMap(param1)。

* toLinkedHashMap 方法
```java
public static LinkedHashMap toLinkedHashMap(String jsonStr);
```
该方法用来将JSON字符串转换为LinkedHashMap对象，入参为JSON字符串（jsonStr），
使用该方法时直接通过类名调用即可，如：JSON.toLinkedHashMap(param1)。

* toJavaObject 方法
```java
public static <T> T toJavaObject(String jsonStr, Class<T> clazz);
```
该方法用来将JSON字符串转换为java对象，入参为JSON字符串（jsonStr）、类类型（clazz），
使用该方法时直接通过类名调用即可，如：JSON.toJavaObject(param1,param2)。

### PropertiesUtils（配置文件）

* get 方法
```java
public static String get(String key, String... propertiesFileName);
```
该方法用来读取配置项的值，入参为配置字段名（key）、配置文件名（propertiesFileName），
使用该方法时直接通过类名调用即可，如：PropertiesUtils.get(param1,param2)。

* getInteger 方法
```java
public static Integer getInteger(String key, String... propertiesFileName);
```
该方法用来读取int型的配置项，入参为配置字段名（key）、配置文件名（propertiesFileName），
使用该方法时直接通过类名调用即可，如：PropertiesUtils.getInteger(param1,param2)。

* getBoolean 方法
```java
public static boolean getBoolean(String key, String... propertiesFileName);
```
该方法用来读取boolean型的配置项，入参为配置字段名（key）、配置文件名（propertiesFileName），
使用该方法时直接通过类名调用即可，如：PropertiesUtils.getBoolean(param1,param2)。

### S（字符串）

* cut 方法
```java
/***
* 该方法用来裁剪字符串，入参为字符串（input）
* 使用该方法时直接通过类名调用即可，如：S.cut(param1)
*/
public static String cut(String input);

/***
* 该方法用来裁剪字符串，入参为字符串（input）、裁剪长度（cutLength）
* 使用该方法时直接通过类名调用即可，如：S.cut(param1,param2)
*/
public static String cut(String input, int cutLength);
```

* join 方法
```java
/***
* 该方法用来将集合拼接成字符串，默认分隔符为：“，”，入参为字符串集合（stringList）
* 使用该方法时直接通过类名调用即可，如：S.join(param1)
*/
public static String join(List<String> stringList);

/***
* 该方法用来将数组拼接成字符串，默认分隔符为：“，”，入参为字符串数组（stringArray）
* 使用该方法时直接通过类名调用即可，如：S.join(param1)
*/
public static String join(String[] stringArray);
```

* split 方法
```java
public static String[] split(String joinedStr);
```
该方法用来按“，”拆分字符串，入参为字符串（joinedStr），
使用该方法时直接通过类名调用即可，如：S.split(param1)。

* toStringArray 方法
```java
public static String[] toStringArray(List<String> stringList);
```
该方法用来转换为String数组，入参为字符串集合（stringList），
使用该方法时直接通过类名调用即可，如：S.toStringArray(param1)。

* toSnakeCase 方法
```java
public static String toSnakeCase(String camelCaseStr);
```
该方法用来转换成蛇形命名（用于Java属性转换为数据库列名），入参为字符串（camelCaseStr），
使用该方法时直接通过类名调用即可，如：S.toSnakeCase(param1)。

* toLowerCaseCamel 方法
```java
public static String toLowerCaseCamel(String snakeCaseStr);
```
该方法用来转换成首字母小写的驼峰命名（用于数据库列名转换为Java属性），入参为字符串（snakeCaseStr），
使用该方法时直接通过类名调用即可，如：S.toLowerCaseCamel(param1)。

* toLong 方法
```java
public static Long toLong(String strValue);
```
该方法用来转换为Long类型，入参为字符串（strValue），
使用该方法时直接通过类名调用即可，如：S.toLong(param1)。

* toInt 方法
```java
public static Integer toInt(String strValue);
```
该方法用来转换为Integer类型，入参为字符串（strValue），
使用该方法时直接通过类名调用即可，如：S.toInt(param1)。

* toBoolean 方法
```java
public static boolean toBoolean(String strValue);
```
该方法用来转换为boolean类型，入参为字符串（strValue），
使用该方法时直接通过类名调用即可，如：S.toBoolean(param1)。

* removeDuplicateBlank 方法
```java
public static String removeDuplicateBlank(String input);
```
该方法用来将多个空格替换为一个，入参为字符串（input），
使用该方法时直接通过类名调用即可，如：S.removeDuplicateBlank(param1)。

* newUuid 方法
```java
public static String newUuid();
```
该方法用来获得随机串，使用该方法时直接通过类名调用即可，如：S.newUuid()。

* newRandomNum 方法
```java
public static String newRandomNum(int length);
```
该方法用来生成指定位数的数字/验证码，入参为指定字符长度（length），
使用该方法时直接通过类名调用即可，如：S.newRandomNum(param1)。

* uncapFirst 方法
```java
public static String uncapFirst(String input);
```
该方法用来将首字母转为小写，入参为字符串（input），
使用该方法时直接通过类名调用即可，如：S.uncapFirst(param1)。

* capFirst 方法
```java
public static String capFirst(String input);
```
该方法用来将首字母转为大写，入参为字符串（input），
使用该方法时直接通过类名调用即可，如：S.capFirst(param1)。

### SqlExecutor（SQL执行）

* executeQuery 方法
```java
public static <E> List<Map<String,E>> executeQuery(String sql, List<E> params);
```
该方法用来执行Select语句，入参为SQL语句（sql）、查询参数（params），
使用该方法时直接通过类名调用即可，如：SqlExecutor.executeQuery(param1,param2)。

* executeQueryAndMergeOneToOneResult 方法
```java
public static <E> Map<String, Object> executeQueryAndMergeOneToOneResult(String sql, List<E> params, String keyName, String valueName);
```
该方法用来执行一对一关联查询和合并结果并将结果Map的key转成String类型，入参为SQL语句（sql）、查询参数（params）、字段名（keyName）、字段名（valueName），
使用该方法时直接通过类名调用即可，如：SqlExecutor.executeQueryAndMergeOneToOneResult(param1,param2,param3,param4)。

* executeQueryAndMergeOneToManyResult 方法
```java
public static <E> Map<String, List> executeQueryAndMergeOneToManyResult(String sql, List<E> params, String keyName, String valueName);
```
该方法用来执行查询和合并结果并将结果Map的key转成String类型，入参为SQL语句（sql）、查询参数（params）、字段名（keyName）、字段名（valueName），
使用该方法时直接通过类名调用即可，如：SqlExecutor.executeQueryAndMergeOneToOneResult(param1,param2,param3,param4)。

* executeUpdate 方法
```java
public static boolean executeUpdate(String sql, List params);
```
该方法用来执行更新操作，入参为SQL语句（sql）、更新参数（params），
使用该方法时直接通过类名调用即可，如：SqlExecutor.executeUpdate(param1,param2)。

### V（校验）

* isEmpty 方法
```java
/***
* 判断对象是否为空
*/
public static boolean isEmpty(Object obj);
/***
* 判断字符串是否为空
*/
public static boolean isEmpty(String value);
/***
* 判断字符串数组是否为空
*/
public static boolean isEmpty(String[] values);
/***
* 判断集合是否为空
*/
public static <T> boolean isEmpty(Collection<T> list);
/***
* 判断Map是否为空
*/
public static boolean isEmpty(Map obj);
```
使用该方法时直接通过类名调用即可，如：V.isEmpty(param1)。

* notEmpty 方法
```java
/***
* 判断对象是否不为空
*/
public static boolean notEmpty(Object obj);
/***
* 判断字符串是否不为空
*/
public static boolean notEmpty(String value);
/***
* 判断数组是否不为空
*/
public static boolean notEmpty(String[] values);
/***
* 判断集合是否不为空
*/
public static <T> boolean notEmpty(Collection<T> list);
/***
* 判断Map是否不为空
*/
public static boolean notEmpty(Map obj);
```
使用该方法时直接通过类名调用即可，如：V.notEmpty(param1)。

* notEmptyOrZero 方法
```java
/***
* 判断Long类型对象是否不为空且不为0
*/
public static boolean notEmptyOrZero(Long longObj);
/***
* 判断Integer类型对象是否不为空且不为0
*/
public static boolean notEmptyOrZero(Integer intObj);
```
使用该方法时直接通过类名调用即可，如：V.notEmptyOrZero(param1)。

* isNumber 方法
```java
public static boolean isNumber(String str);
```
该方法用来判断是否为整型数字，使用该方法时直接通过类名调用即可，如：V.isNumber(param1)。

* isNumeric 方法
```java
public static boolean isNumeric(String str);
```
该方法用来判断是否为数字（允许小数点），使用该方法时直接通过类名调用即可，如：V.isNumeric(param1)。

* isEmail 方法
```java
public static boolean isEmail(String str);
```
该方法用来判断是否为正确的邮件格式，使用该方法时直接通过类名调用即可，如：V.isEmail(param1)。

* isPhone 方法
```java
public static boolean isPhone(String str);
```
该方法用来判断是否为电话号码，使用该方法时直接通过类名调用即可，如：V.isPhone(param1)。

* isValidBoolean 方法
```java
public static boolean isValidBoolean(String value);
```
该方法用来判断是否为合法boolean类型，使用该方法时直接通过类名调用即可，如：V.isValidBoolean(param1)。

* isTrue 方法
```java
public static boolean isTrue(String value);
```
该方法用来判定是否为true，使用该方法时直接通过类名调用即可，如：V.isTrue(param1)。

* validate 方法
```java
public static String validate(String value, String validation);
```
该方法用来根据指定规则校验字符串的值是否合法，入参为需要校验的字符串（value）、校验种类（validation），
使用该方法时直接通过类名调用即可，如：V.validate(param1,param2)。

* notEquals 方法
```java
public static boolean notEquals(Object source, Object target);
```
该方法用来判定两个对象是否不同类型或不同值，使用该方法时直接通过类名调用即可，如：V.notEquals(param1,param2)。

* equals 方法
```java
public static <T> boolean equals(T source, T target);
```
该方法用来判定两个对象是否类型相同值相等，使用该方法时直接通过类名调用即可，如：V.equals(param1,param2)。

* fuzzyEqual 方法
```java
public static boolean fuzzyEqual(Object source, Object target);
```
该方法用来模糊对比是否相等（类型不同的转成String对比），使用该方法时直接通过类名调用即可，如：V.fuzzyEqual(param1,param2)。