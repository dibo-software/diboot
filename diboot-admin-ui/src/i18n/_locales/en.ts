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
  },
  hooks: {
    fetchDetailFailed: 'Failed to retrieve details',
    saveFailed: 'Save failed',
    fetchListFailed: 'Failed to retrieve list data',
    confirmDelete: 'Confirm deletion of this data?',
    confirmDelete0: 'Confirm the deletion of "{0}"?',
    delete: 'Delete',
    deleteFailed: 'Deletion failed',
    deleteSuccess: 'Deletion successful',
    nonChooseData: 'No data selected',
    confirmBatchDelete: 'Confirm deletion of selected data?',
    batchDelete: 'Bulk delete',
    initOptionFailed: 'Initialization of option data failed',
    fetchOptionFailed: 'Failed to retrieve option data',
    sortFailed: 'Sorting failed',
    loginFailed: 'Login failed',
    nullSsoLoginUrl: 'Single sign-on URL is empty',
    fetchTreeFailed: 'Failed to retrieve tree list data',
    addDataFailed: 'Add data failed',
    confirmDeleteNode: 'Confirm deletion of node?',
    sortSuccess: 'Sorting successful'
  },
  copy: {
    label: 'Copy',
    success: 'Copy success',
    error: 'Failed to write to clipboard, please manually select Copy'
  },
  utils: {
    request: {
      expiredLogin: 'Login expired, please log in again',
      server500: 'The server seems to be having a hiccup, please try again!',
      server400: 'Error saving data',
      server401: 'Unauthorized',
      server403: 'Forbidden',
      server404: 'Requested resource not found',
      serverError: 'There might be a network issue'
    }
  }
}

export default en
