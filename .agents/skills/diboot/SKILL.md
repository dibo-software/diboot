---
name: diboot
description: Diboot 项目代码规范与生成约束。用于修改、优化、审查或生成 Diboot 项目代码，包括 Java 后端、CRUD 接口、VO/DTO 无 SQL 关联绑定、注释规约、日志规约、命名规约、OOP 规约、PC 端 Vue 管理页面、移动端页面、ES6 语法、Composition API 逻辑关注点分组、hooks/utils 复用，以及保持业务项目结构与 Diboot 约定一致。
---

# Diboot 项目规范

## 使用流程

在修改或生成 Diboot 项目代码前使用本 skill。

1. 先读取 `references/common-code-style.md`，遵守通用注释、日志、命名、OOP、方法长度、前端 ES6 和 Vue 逻辑分组约束。
2. 再识别目标层：后端业务代码、PC 管理端、移动端，或跨端改动。
3. 根据目标层读取对应参考文件后再决定实现：
   - 后端业务代码：`references/backend-usage.md`
   - PC 管理端：`references/admin-ui.md`
   - 移动端：`references/mobile-ui.md`
4. 优先复用已有 Diboot API、注解、hooks、utils、组件和项目既有模式。
5. 生成代码时，只手写未被目标项目自动导入的 import。

## 基础规则

- 不依赖外部文档 URL、本机下载文件或个人路径，skill 迁移后必须可直接使用。
- 如果当前项目代码与外部资料不一致，以当前项目代码为准。
- 生成或修改代码前，先观察相邻文件的结构、命名、导入和样式。
- 除非已有能力无法覆盖，否则不要新增局部 helper 或重复封装。
