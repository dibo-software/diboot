package com.diboot.shiro.authz.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 是否开启存储枚举
 * @author : wee
 * @version : v 2.0
 * @Date 2019-06-18  23:06
 */
@Getter
@AllArgsConstructor
public enum  EnableStorageEnum {
    TRUE(true),
    FALSE(false);
    private boolean storagePermissions;

}
