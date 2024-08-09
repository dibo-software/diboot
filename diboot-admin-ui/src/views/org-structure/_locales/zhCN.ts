const zhCN = {
  org: {
    name: '名称',
    code: '编码',
    type: '类型',
    managerName: '负责人',
    sortId: '排序号',
    parentId: '上级部门',
    orgComment: '备注',
    comp: '公司',
    dept: '部门',
    primaryPosition: '主岗',
    label: '组织部门'
  },
  position: {
    name: '名称',
    code: '编码',
    gradeName: '职级',
    dataPermissionType: '数据权限',
    isVirtual: '虚拟岗位',
    gradeValue: '职级',
    gradeNameAlias: '职级头衔',
    label: '岗位',
    placeholder: {
      name: '请输入名称',
      code: '请输入编码',
      gradeName: '请选择职级',
      dataPermissionType: '请选择数据权限',
      label: '请选择岗位'
    }
  },
  user: {
    type: '用户类型',
    orgId: '所属部门',
    orgIdAlias: '部门',
    userNum: '员工编号',
    userNumAlias: '员工编号',
    realname: '姓名',
    username: '用户名',
    password: '密码',
    role: '角色',
    accountStatus: '账号状态',
    gender: '性别',
    status: '状态',
    birthday: '生日',
    mobilePhone: '电话',
    email: '邮箱',
    sortId: '排序号',
    general: '普通用户',
    system: '系统用户',
    modifyPassword: '修改密码',
    onJob: '在职',
    dimission: '离职',
    partTimeJob: '兼职',
    rules: {
      email: '请输入正确的邮箱地址',
      mobilePhone: '请输入正确的手机号'
    },
    placeholder: {
      orgId: '请选择所属部门',
      userNum: '请输入员工编号',
      realname: '请输入姓名',
      username: '请输入用户名',
      password: '请输入密码',
      role: '请选择角色',
      birthday: '请选择生日',
      accountStatus: '请选择账号状态',
      sortId: '请输入排序号',
      mobilePhone: '请输入电话',
      email: '请输入邮箱'
    }
  }
}

export type Locale = typeof zhCN

export default zhCN
