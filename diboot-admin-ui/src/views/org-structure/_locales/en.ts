import type { Locale } from './zhCN'
const en: Locale = {
  org: {
    name: 'Name',
    code: 'Code',
    type: 'Type',
    managerName: 'Manager',
    sortId: 'Sort ID',
    parentId: 'Parent ID',
    orgComment: 'Note',
    comp: 'Company',
    dept: 'Department',
    primaryPosition: 'Main Position',
    label: 'Organization Department'
  },
  position: {
    name: 'Name',
    code: 'Code',
    gradeName: 'Grade',
    dataPermissionType: 'Data Permission',
    isVirtual: 'Virtual Position',
    gradeValue: 'Grade Value',
    gradeNameAlias: 'Grade Title',
    label: 'Position',
    placeholder: {
      name: 'Enter name',
      code: 'Enter code',
      gradeName: 'Select grade',
      dataPermissionType: 'Select data permission',
      label: 'Select position'
    }
  },
  user: {
    type: 'User Type',
    orgId: 'Department',
    orgIdAlias: 'Department',
    userNum: 'Number',
    userNumAlias: 'Number',
    realname: 'Name',
    username: 'Username',
    password: 'Password',
    role: 'Role',
    accountStatus: 'Status',
    gender: 'Gender',
    status: 'Status',
    birthday: 'Birthday',
    mobilePhone: 'Phone',
    email: 'Email',
    sortId: 'Sort ID',
    general: 'Regular User',
    system: 'System User',
    modifyPassword: 'Change Password',
    onJob: 'Active',
    dimission: 'Inactive',
    partTimeJob: 'Part-Time',
    rules: {
      email: 'Please enter a valid email address.',
      mobilePhone: 'Please enter a valid phone number.'
    },
    placeholder: {
      orgId: 'Please select the department',
      userNum: 'Please enter employee number',
      realname: 'Please enter name',
      username: 'Please enter username',
      password: 'Please enter password',
      role: 'Please select role',
      birthday: 'Please select birthday',
      accountStatus: 'Please select account status',
      sortId: 'Please enter sort ID',
      mobilePhone: 'Please enter phone number',
      email: 'Please enter email'
    }
  }
}

export default en
