const zhCN = {
  tenantInfo: {
    name: '租户名称',
    code: '租户编码',
    validDate: '有效期',
    startDate: '有效期起始',
    endDate: '有效期截止',
    manager: '负责人',
    phone: '联系电话',
    status: '租户状态',
    description: '描述',
    rules: {
      email: '请输入正确的邮箱地址',
      mobilePhone: '请输入正确的手机号',
      phone: '请输入正确的联系电话'
    },
    placeholder: {
      name: '请输入租户名称',
      code: '请输入租户编码',
      status: '请选择租户状态',
      manager: '请输入负责人',
      phone: '请输入联系电话',
      description: '请输入描述',
      startDate: '起始日期',
      endDate: '截止日期'
    },
    resourceForm: {
      success: '权限配置成功',
      title: '权限配置'
    },
    adminForm: {
      title: '管理员配置'
    }
  }
}

export type Locale = typeof zhCN

export default zhCN
