import type { Locale } from './zhCN'

const en: Locale = {
  language: 'English',
  operation: {
    create: 'Create',
    add: 'Add',
    createTenantAdmin: 'Admin Configuration',
    createTenantResource: 'Permission Configuration',
    detail: 'Detail',
    update: 'Edit',
    delete: 'Delete',
    batchDelete: 'Batch Delete',
    import: 'Import',
    export: 'Export',
    more: 'More',
    search: 'Search',
    reset: 'Reset',
    label: 'Operation'
  },
  rules: {
    notnull: 'field is required',
    nonpass: 'Validation failed',
    pass: 'Validation passed'
  },
  button: {
    submit: 'Submit',
    save: 'Save',
    continueAdd: 'Save and Continue',
    cancel: 'Cancel',
    confirm: 'Confirm',
    select: 'Select',
    close: 'Close'
  },
  title: {
    reset: 'Reset Search Conditions',
    detail: 'Details',
    update: 'Update',
    create: 'Create'
  },
  bool: {
    yes: 'Yes',
    no: 'No'
  },
  placeholder: {
    select: 'Please Select',
    input: 'Please Enter',
    filter: 'Enter Content Filter'
  },
  baseField: {
    createTime: 'Creation Time',
    updateTime: 'Last Updated',
    createBy: 'Created By',
    updateBy: 'Updated By'
  },
  password: {
    strong: 'Strong',
    general: 'General',
    weak: 'Weak'
  },
  router: {
    dashboard: 'Dashboard',
    personal: 'Personal Center'
  },
  searchState: {
    up: 'Collapse',
    down: 'Expand'
  }
}

export default en
