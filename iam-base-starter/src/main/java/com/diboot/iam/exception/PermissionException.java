package com.diboot.iam.exception;

import com.diboot.core.exception.BusinessException;
import com.diboot.core.vo.Status;

/**
 * 权限校验异常
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/30
 */
public class PermissionException extends BusinessException {

    public PermissionException() {
        super(Status.FAIL_NO_PERMISSION);
    }

    public PermissionException(Throwable ex) {
        super(Status.FAIL_NO_PERMISSION, ex);
    }

    public PermissionException(String msg) {
        super(Status.FAIL_NO_PERMISSION, msg);
    }

    public PermissionException(String msg, Throwable ex) {
        super(Status.FAIL_NO_PERMISSION, msg, ex);
    }

}
