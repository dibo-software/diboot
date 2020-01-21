package com.diboot.iam.starter;

import com.diboot.core.service.DictionaryService;
import com.diboot.core.util.JSON;
import com.diboot.core.vo.DictionaryVO;
import com.diboot.iam.config.Cons;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.entity.IamRole;
import com.diboot.iam.entity.IamUser;
import com.diboot.iam.entity.IamUserRole;
import com.diboot.iam.service.IamAccountService;
import com.diboot.iam.service.IamRoleService;
import com.diboot.iam.service.IamUserRoleService;
import com.diboot.iam.service.IamUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * IAM组件相关的初始化
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/23
 */
@Component
@Slf4j
public class IamBaseInitializer{

    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private IamRoleService iamRoleService;
    @Autowired
    private IamUserService iamUserService;
    @Autowired
    private IamUserRoleService iamUserRoleService;
    @Autowired
    private IamAccountService iamAccountService;

    /**
     * 插入初始化数据
     */
    public void insertInitData(){
        // 插入iam-base所需的数据字典
        String[] DICT_INIT_DATA = {
            "{\"type\":\"AUTH_TYPE\", \"itemName\":\"登录认证方式\", \"description\":\"IAM用户登录认证方式\", \"children\":[{\"itemName\":\"用户名密码\", \"itemValue\":\"PWD\", \"sortId\":1},{\"itemName\":\"公众号\", \"itemValue\":\"WX_MP\", \"sortId\":2},{\"itemName\":\"企业微信\", \"itemValue\":\"WX_CP\", \"sortId\":3}]}",
            "{\"type\":\"ACCOUNT_STATUS\", \"itemName\":\"账号状态\", \"description\":\"IAM登录账号状态\", \"children\":[{\"itemName\":\"有效\", \"itemValue\":\"A\", \"sortId\":1},{\"itemName\":\"无效\", \"itemValue\":\"I\", \"sortId\":2},{\"itemName\":\"锁定\", \"itemValue\":\"L\", \"sortId\":3}]}",
            "{\"type\":\"USER_STATUS\", \"itemName\":\"用户状态\", \"description\":\"IAM用户状态\", \"editable\":true, \"children\":[{\"itemName\":\"在职\", \"itemValue\":\"A\", \"sortId\":1},{\"itemName\":\"离职\", \"itemValue\":\"I\", \"sortId\":2}]}",
            "{\"type\":\"PERMISSION_TYPE\", \"itemName\":\"权限类型\", \"description\":\"IAM权限类型\", \"children\":[{\"itemName\":\"菜单\", \"itemValue\":\"MENU\", \"sortId\":1},{\"itemName\":\"操作\", \"itemValue\":\"OPERATION\", \"sortId\":2}]}"
        };
        // 插入数据字典
        for(String dictJson : DICT_INIT_DATA){
            DictionaryVO dictVo = JSON.toJavaObject(dictJson, DictionaryVO.class);
            dictionaryService.createDictAndChildren(dictVo);
        }
        DICT_INIT_DATA = null;

        // 插入超级管理员用户及角色
        IamRole iamRole = new IamRole();
        iamRole.setName("超级管理员").setCode("SUPER_ADMIN");
        iamRoleService.createEntity(iamRole);

        IamUser iamUser = new IamUser();
        iamUser.setOrgId(0L).setRealname("DIBOOT").setUserNum("0000").setGender("M").setMobilePhone("10000000000");
        iamUserService.createEntity(iamUser);

        // 插入对象
        IamUserRole iamUserRole = new IamUserRole(IamUser.class.getSimpleName(), iamUser.getId(), iamRole.getId());
        iamUserRoleService.getMapper().insert(iamUserRole);

        // 创建账号
        IamAccount iamAccount = new IamAccount();
        iamAccount.setUserType(IamUser.class.getSimpleName()).setUserId(iamUser.getId())
                .setAuthType(Cons.DICTCODE_AUTH_TYPE.PWD.name())
                .setAuthAccount("admin").setAuthSecret("123456");
        iamAccountService.createEntity(iamAccount);
    }
}