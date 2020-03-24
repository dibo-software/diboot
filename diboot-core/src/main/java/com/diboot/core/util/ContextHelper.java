package com.diboot.core.util;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.toolkit.JdbcUtils;
import com.diboot.core.config.Cons;
import com.diboot.core.service.BaseService;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Spring上下文帮助类
 * @author mazc@dibo.ltd
 * @version 2.0
 * @date 2019/01/01
 */
@Component
@Lazy(false)
public class ContextHelper implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(ContextHelper.class);

    /***
     * ApplicationContext上下文
     */
    private static ApplicationContext APPLICATION_CONTEXT = null;

    /**
     * Entity-对应的Service缓存
     */
    private static Map<String, IService> ENTITY_SERVICE_CACHE = null;
    /**
     * Entity-对应的BaseService缓存
     */
    private static Map<String, BaseService> ENTITY_BASE_SERVICE_CACHE = null;
    /**
     * 存储主键字段非id的Entity
     */
    private static Map<String, String> PK_NID_ENTITY_CACHE = new ConcurrentHashMap<>();
    /**
     * 数据库类型
     */
    private static String DATABASE_TYPE = null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(APPLICATION_CONTEXT == null){
            APPLICATION_CONTEXT = applicationContext;
        }
    }

    /***
     * 获取ApplicationContext上下文
     */
    public static ApplicationContext getApplicationContext() {
        if (APPLICATION_CONTEXT == null){
            return ContextLoader.getCurrentWebApplicationContext();
        }
        return APPLICATION_CONTEXT;
    }

    /***
     * 根据beanId获取Bean实例
     * @param beanId
     * @return
     */
    public static Object getBean(String beanId){
        return getApplicationContext().getBean(beanId);
    }

    /***
     * 获取指定类型的单个Bean实例
     * @param clazz
     * @return
     */
    public static <T> T getBean(Class<T> clazz){
        return getApplicationContext().getBean(clazz);
    }

    /***
     * 获取指定类型的全部实例
     * @param type
     * @param <T>
     * @return
     */
    public static <T> List<T> getBeans(Class<T> type){
        // 获取所有的定时任务实现类
        Map<String, T> map = getApplicationContext().getBeansOfType(type);
        if(V.isEmpty(map)){
            return null;
        }
        List<T> beanList = new ArrayList<>();
        beanList.addAll(map.values());
        return beanList;
    }

    /***
     * 根据注解获取beans
     * @param annotationType
     * @return
     */
    public static List<Object> getBeansByAnnotation(Class<? extends Annotation> annotationType){
        Map<String, Object> map = getApplicationContext().getBeansWithAnnotation(annotationType);
        if(V.isEmpty(map)){
            return null;
        }
        List<Object> beanList = new ArrayList<>();
        beanList.addAll(map.values());
        return beanList;
    }

    /**
     * 根据Entity获取对应的Service (已废弃，请调用getIServiceByEntity)
     * @param entity
     * @return
     */
    @Deprecated
    public static IService getServiceByEntity(Class entity){
        return getIServiceByEntity(entity);
    }

    /**
     * 根据Entity获取对应的IService实现
     * @param entity
     * @return
     */
    @Deprecated
    public static IService getIServiceByEntity(Class entity){
        if(ENTITY_SERVICE_CACHE == null){
            ENTITY_SERVICE_CACHE = new ConcurrentHashMap<>();
            Map<String, IService> serviceMap = getApplicationContext().getBeansOfType(IService.class);
            if(V.notEmpty(serviceMap)){
                for(Map.Entry<String, IService> entry : serviceMap.entrySet()){
                    Class entityClass = BeanUtils.getGenericityClass(entry.getValue(), 1);
                    if(entityClass != null){
                        ENTITY_SERVICE_CACHE.put(entityClass.getName(), entry.getValue());
                    }
                }
            }
        }
        IService iService = ENTITY_SERVICE_CACHE.get(entity.getName());
        if(iService == null){
            log.error("未能识别到Entity: "+entity.getName()+" 的IService实现！");
        }
        return iService;
    }

    /**
     * 根据Entity获取对应的BaseService实现
     * @param entity
     * @return
     */
    public static BaseService getBaseServiceByEntity(Class entity){
        if(ENTITY_BASE_SERVICE_CACHE == null){
            ENTITY_BASE_SERVICE_CACHE = new ConcurrentHashMap<>();
            Map<String, BaseService> serviceMap = getApplicationContext().getBeansOfType(BaseService.class);
            if(V.notEmpty(serviceMap)){
                for(Map.Entry<String, BaseService> entry : serviceMap.entrySet()){
                    Class entityClass = BeanUtils.getGenericityClass(entry.getValue(), 1);
                    if(entityClass != null){
                        ENTITY_BASE_SERVICE_CACHE.put(entityClass.getName(), entry.getValue());
                    }
                }
            }
        }
        BaseService baseService =  ENTITY_BASE_SERVICE_CACHE.get(entity.getName());
        if(baseService == null){
            log.error("未能识别到Entity: "+entity.getName()+" 的Service实现！");
        }
        return baseService;
    }

    /**
     * 获取Entity主键
     * @return
     */
    public static String getPrimaryKey(Class entity){
        if(!PK_NID_ENTITY_CACHE.containsKey(entity.getName())){
            String pk = Cons.FieldName.id.name();
            List<Field> fields = BeanUtils.extractAllFields(entity);
            if(V.notEmpty(fields)){
                for(Field fld : fields){
                    TableId tableId = fld.getAnnotation(TableId.class);
                    if(tableId == null){
                        continue;
                    }
                    TableField tableField = fld.getAnnotation(TableField.class);
                    if(tableField != null && tableField.exist() == false){
                        continue;
                    }
                    pk = fld.getName();
                    break;
                }
            }
            PK_NID_ENTITY_CACHE.put(entity.getName(), pk);
        }
        return PK_NID_ENTITY_CACHE.get(entity.getName());
    }

    /***
     * 获取JdbcUrl
     * @return
     */
    public static String getJdbcUrl() {
        Environment environment = getApplicationContext().getEnvironment();
        String jdbcUrl = environment.getProperty("spring.datasource.url");
        if(jdbcUrl == null){
            String master = environment.getProperty("spring.datasource.dynamic.primary");
            jdbcUrl = environment.getProperty("spring.datasource.dynamic.datasource."+master+".url");
        }
        return jdbcUrl;
    }

    /**
     * 获取数据库类型
     * @return
     */
    public static String getDatabaseType(){
        if(DATABASE_TYPE != null){
            return DATABASE_TYPE;
        }
        String jdbcUrl = getJdbcUrl();
        if(jdbcUrl != null){
            DbType dbType = JdbcUtils.getDbType(jdbcUrl);
            DATABASE_TYPE = dbType.getDb();
            if(DATABASE_TYPE.startsWith(DbType.SQL_SERVER.getDb())){
                DATABASE_TYPE = DbType.SQL_SERVER.getDb();
            }
        }
        else{
            SqlSessionFactory sqlSessionFactory = getBean(SqlSessionFactory.class);
            if(sqlSessionFactory != null){
                DATABASE_TYPE = sqlSessionFactory.getConfiguration().getDatabaseId();
            }
        }
        if(DATABASE_TYPE == null){
            log.warn("无法识别数据库类型，请检查配置！");
        }
        return DATABASE_TYPE;
    }

}