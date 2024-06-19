import type { Locale } from './zhCN'

const en: Locale = {
  components: {
    di: {
      input: {
        uploadFormatError: 'Please upload a file of {0} format!',
        fileLarge: 'File too large, exceeding the limit of {0} MB, please adjust your file!',
        uploadFile: 'Upload File',
        limit: { prefix: 'Up to', suffix: 'files' },
        accept: 'Files of type {0}',
        size: 'Single file must be less than'
      },
      selector: { label: 'Select' },
      tree: { searchFilter: 'Search Filter' },
      table: {
        total: 'Total on current page',
        avg: 'Average on current page',
        config: { title: 'List Config', show: 'Show', name: 'Name', width: 'Width', sort: 'Sort', fixed: 'Fixed' },
        border: 'Vertical Border',
        stripe: 'Striped'
      }
    },
    document: { fetchFileFailed: 'Failed to fetch the file', emptyContent: 'File content is empty' },
    download: 'Download',
    excel: {
      dataUpload: 'Data Upload',
      uploadData: 'Upload Data',
      downloadExample: 'Download Example File',
      chooseFile: 'Choose File',
      previewData: 'Preview Data',
      checkError: 'Please check the Excel file for errors',
      description: 'Remarks',
      excelParsePrefix: 'Excel file parsed successfully, total of',
      data: 'rows of data',
      canUpload: 'can be uploaded',
      errorMsg: 'There are {0} rows of abnormal data',
      uploadDataTipPrefix: 'After uploading data, you can',
      uploadDataTipSuffix: 'export error data',
      exportColumnNull: 'Export columns should not be empty',
      selectColumnExport: 'Select Columns to Export',
      selectExportColumn: 'Choose Columns to Export',
      ignoreColumn: 'Ignore Columns',
      exportColumn: 'Export Columns'
    },
    i18n: { language: 'Language Identifier', config: 'Internationalization Configuration' },
    icon: { reelect: 'Re-Select', choose: 'Choose', clear: 'Clear', selector: 'Icon Selector' },
    numberRange: { startPlaceholder: 'Start', endPlaceholder: 'End' },
    rich: { placeholder: 'Enter content...', uploadError: 'Upload failed, please try again later!' },
    watermark: 'Your browser does not support watermark functionality'
  }
}

export default en
