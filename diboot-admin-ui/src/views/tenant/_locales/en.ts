import type { Locale } from './zhCN'
const en: Locale = {
  tenantInfo: {
    name: 'Tenant Name',
    code: 'Tenant Code',
    validDate: 'Validity Period',
    startDate: 'Validity Start Date',
    endDate: 'Validity End Date',
    manager: 'Manager',
    phone: 'Contact Phone',
    status: 'Tenant Status',
    description: 'Description',
    rules: {
      email: 'Please enter a correct email address',
      mobilePhone: 'Please enter a correct mobile phone number',
      phone: 'Please enter a correct contact phone number'
    },
    placeholder: {
      name: 'Please enter the tenant name',
      code: 'Please enter the tenant code',
      status: 'Please select the tenant status',
      manager: 'Please enter the manager',
      phone: 'Please enter the contact phone',
      description: 'Please enter a description',
      startDate: 'Start Date',
      endDate: 'End Date'
    },
    resourceForm: {
      success: 'Permission configuration successful',
      title: 'Permission Configuration'
    },
    adminForm: {
      title: 'Administrator Configuration'
    }
  }
}

export default en
