package com.diboot.iam.starter;

import com.diboot.core.plugin.PluginManager;
import com.diboot.core.starter.SqlHandler;
import com.diboot.core.util.ContextHelper;
import com.diboot.iam.annotation.process.AnnotationExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

/**
 * IAM组件相关的初始化
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/23
 */
@Slf4j
public class IamBasePluginManager implements PluginManager {

    // 验证SQL
    private static final String VALIDATE_SQL = "SELECT id FROM ${SCHEMA}.iam_permission WHERE id=0";

    public void initPlugin(IamBaseProperties iamBaseProperties){
        // 检查数据库字典是否已存在
        if(iamBaseProperties.isInitSql()){
            Environment environment = ContextHelper.getApplicationContext().getEnvironment();
            SqlHandler.init(environment);
            if(SqlHandler.checkIsTableExists(VALIDATE_SQL) == false){
                log.info("执行初始化SQL");
                // 执行初始化SQL
                SqlHandler.initBootstrapSql(this.getClass(), environment, "iam-base");
                // 插入相关数据：Dict，Role等
                ContextHelper.getBean(IamBaseInitializer.class).insertInitData();
                log.info("初始化SQL执行完成");
            }
        }
        // 异步更新注解
        ContextHelper.getBean(AnnotationExtractor.class).updatePermissions(iamBaseProperties.isEnablePermissionUpdate());
    }
}