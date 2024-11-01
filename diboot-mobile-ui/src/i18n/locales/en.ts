import type { Locale } from './zhCN'

const en: Locale = {
  language: 'English',
  operation: {
    submit: 'Submit',
    detail: 'Detail',
    create: 'Create',
    update: 'Edit',
    delete: 'Delete',
    copy: 'Copy'
  },
  bool: {
    yes: 'Yes',
    no: 'No'
  },
  placeholder: {
    select: 'Please Select',
    input: 'Please Enter'
  },
  rules: {
    notnull: 'field is required',
    validationFailed: 'Form verification fails',
    valueNotUnique: 'Content duplicates, {0} already exists!'
  },
  msg: {
    noMore: 'No more',
    requestErr: 'If the request fails, click Reload',
    loading: 'loading...',
    uploadErr: 'The upload file is abnormal, please try again later!'
  },
  select: {
    key: 'Enter keyword',
    search: 'Search options',
    noOpt: 'No options'
  }
}

export default en
