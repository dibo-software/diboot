# 后端使用规范

## 适用范围

用于 Diboot 业务项目中的 Java 后端代码，包括 Entity、VO、DTO、Controller、Service、Mapper、CRUD 接口和关联绑定。先遵守 `common-code-style.md`。

## 工具类

当 Diboot 工具类能表达同一意图时，优先使用 Diboot 工具类，不新增重复 helper。

- 字符串操作优先使用 `S`。
- 判空、非空、相等、不相等、校验谓词优先使用 `V`。
- JSON 解析、序列化、反序列化优先使用 `JSON`。
- 对象转换、属性复制、树构建、字段提取等场景优先使用 Diboot `BeanUtils`。

## 无 SQL 关联绑定

常见关联数据补全优先使用 Diboot 无 SQL 绑定注解，不手写 join 查询或循环逐条补全。

- 字典 label 绑定使用 `@BindDict`。
- 关联字段绑定使用 `@BindField`、`@BindFieldList`。
- 关联对象绑定使用 `@BindEntity`、`@BindEntityList`。
- 关联数量统计使用 `@BindCount`。
- 国际化绑定按场景使用 `@BindI18n`。

绑定注解优先写在 VO/DTO，不写入 Entity。Entity 保持表映射和持久化状态职责。

### VO 绑定示例

```java
import com.diboot.core.binding.annotation.BindCount;
import com.diboot.core.binding.annotation.BindDict;
import com.diboot.core.binding.annotation.BindEntityList;
import com.diboot.core.binding.annotation.BindField;
import com.example.api.entity.Department;
import com.example.api.entity.Role;
import com.example.api.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserVO extends User {
    private static final long serialVersionUID = 1L;

    @BindDict(type = "USER_STATUS", field = "status")
    private String statusLabel;

    @BindField(entity = Department.class, field = "name", condition = "this.department_id=id")
    private String departmentName;

    @BindEntityList(entity = Role.class, condition = "this.id=user_role.user_id AND user_role.role_id=id", orderBy = "code")
    private List<Role> roleList;

    @BindCount(entity = Role.class, condition = "this.id=user_role.user_id AND user_role.role_id=id")
    private Integer roleCount;
}
```

### 绑定调用示例

```java
/**
 * 查询用户列表并转换为已绑定关联字段的 VO。
 */
public List<UserVO> getUserViewList(QueryWrapper<User> queryWrapper) {
    List<User> userList = this.list(queryWrapper);
    if (V.isEmpty(userList)) {
        return Collections.emptyList();
    }
    return Binder.convertAndBindRelations(userList, UserVO.class);
}
```

如果数据已经是 VO 类型，直接调用：

```java
/**
 * 为用户 VO 列表补全字典、部门、角色等关联展示字段。
 */
private void bindUserRelations(List<UserVO> userVOList) {
    if (V.isEmpty(userVOList)) {
        return;
    }
    Binder.bindRelations(userVOList);
}
```

Service 层已有视图对象加载能力时，优先使用 `BaseService#getViewObject*` 相关方法。

## CRUD 结构

除非相邻代码已有更具体模式，否则遵循 Diboot 生成代码的后端结构：

- Controller 保持 REST 风格，接口形态与生成 CRUD API 一致。
- 请求和响应结构放在 DTO/VO 中，不直接暴露临时内部结构。
- 业务逻辑放在 Service，Controller 保持轻量。
- 自定义 Mapper SQL 只用于 wrapper、service API、绑定注解难以覆盖的场景。

## 后端方法、日志与 OOP

- 新增 public 方法、Service 方法、复杂私有方法必须写 Javadoc 或清晰注释。
- 单方法超过 80 行时必须拆分，不用长方法承载完整业务流程。
- 日志使用项目 logger，异常日志传入异常对象。
- 不输出敏感信息，不使用 `System.out.println` 或 `printStackTrace`。
- 魔法值提取为常量、枚举或字典值。

## Import

使用项目已有 Diboot 包和工具类 import。不要为了已有 Diboot 能力引入无关工具库。
