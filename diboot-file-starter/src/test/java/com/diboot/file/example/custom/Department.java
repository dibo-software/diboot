package com.diboot.file.example.custom;

import com.diboot.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * 测试样例部门Entity
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2020/02/19
 */
@Getter @Setter
public class Department extends BaseEntity {

    @NotNull(message = "父ID不能为空")
    private Long parentId;

    @NotNull(message = "必须指定单位")
    private Long orgId;

    @NotNull
    @Length(max = 10, message = "标题长度不能超过10")
    private String title;

    private Integer memCount;

    private String userStatus;

}
